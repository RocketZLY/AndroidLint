/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tools.lint.client.api

import com.android.SdkConstants
import com.android.SdkConstants.DOT_CLASS
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Project
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.describeApi
import com.android.utils.SdkUtils
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.SoftReference
import java.net.URLClassLoader
import java.util.HashMap
import java.util.jar.Attributes
import java.util.jar.JarFile

/**
 *  An [IssueRegistry] for a custom lint rule jar file. The rule jar should provide a
 * manifest entry with the key `Lint-Registry` and the value of the fully qualified name of an
 * implementation of [IssueRegistry] (with a default constructor).
 *
 *  NOTE: The custom issue registry should not extend this file; it should be a plain
 * IssueRegistry! This file is used internally to wrap the given issue registry.
 */
class JarFileIssueRegistry
private constructor(
    client: LintClient,
    /** The jar file the rules were loaded from */
    val jarFile: File,
    /** The custom lint check's issue registry that this [JarFileIssueRegistry] wraps */
    registry: IssueRegistry
) : IssueRegistry() {

    override fun cacheable(): Boolean = LintClient.isStudio
    override val issues: List<Issue> = registry.issues.toList()
    private var timestamp: Long = jarFile.lastModified()

    override val isUpToDate: Boolean
        get() = timestamp == jarFile.lastModified()

    init {
        val loader = registry.javaClass.classLoader
        if (loader is URLClassLoader) {
            loadAndCloseURLClassLoader(client, jarFile, loader)
        }
    }

    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API

    companion object Factory {
        /** Service key for automatic discovery of lint rules */
        private const val SERVICE_KEY =
            "META-INF/services/com.android.tools.lint.client.api.IssueRegistry"

        /**
         * Manifest constant for declaring an issue provider.
         *
         * Example: Lint-Registry-v2: foo.bar.CustomIssueRegistry
         */
        private const val MF_LINT_REGISTRY = "Lint-Registry-v2"

        /** Older key: these are for older custom rules */
        private const val MF_LINT_REGISTRY_OLD = "Lint-Registry"

        /** Cache of custom lint check issue registries */
        private var cache: MutableMap<File, SoftReference<JarFileIssueRegistry>>? = null

        /**
         * Loads custom rules from the given list of jar files and returns a list
         * of [JarFileIssueRegistry} instances.
         *
         * It will also deduplicate issue registries, since in Gradle projects with
         * local lint.jar's it's possible for the same lint.jar to be handed back
         * multiple times with different paths through various separate dependencies.
         */
        fun get(
            client: LintClient,
            jarFiles: Collection<File>,
            currentProject: Project?
        ): List<JarFileIssueRegistry> {
            val registryMap = try {
                findRegistries(client, jarFiles)
            } catch (e: IOException) {
                client.log(e, "Could not load custom lint check jar files: ${e.message}")
                return emptyList()
            }

            if (registryMap.isEmpty()) {
                return emptyList()
            }

            val capacity = jarFiles.size + 1
            val registries = ArrayList<JarFileIssueRegistry>(capacity)

            for ((registryClass, jarFile) in registryMap) {
                try {
                    val registry = get(client, registryClass, jarFile, currentProject) ?: continue
                    registries.add(registry)
                } catch (e: Throwable) {
                    client.log(e, "Could not load custom lint check jar file %1\$s", jarFile)
                }
            }

            return registries
        }

        /**
         * Returns a [JarFileIssueRegistry] for the given issue registry class name
         * and jar file, with caching
         */
        private fun get(
            client: LintClient,
            registryClassName: String,
            jarFile: File,
            currentProject: Project?
        ):
                JarFileIssueRegistry? {
            if (cache == null) {
                cache = HashMap()
            } else {
                val reference = cache!![jarFile]
                if (reference != null) {
                    val registry = reference.get()
                    if (registry != null && registry.isUpToDate) {
                        return registry
                    }
                }
            }

            // Ensure that the scope-to-detector map doesn't return stale results
            IssueRegistry.reset()

            val userRegistry = loadIssueRegistry(
                client, jarFile, registryClassName,
                currentProject
            )
            return if (userRegistry != null) {
                val jarIssueRegistry = JarFileIssueRegistry(client, jarFile, userRegistry)
                cache!![jarFile] = SoftReference(jarIssueRegistry)
                jarIssueRegistry
            } else {
                null
            }
        }

        /** Combine one or more issue registries into a single one */
        fun join(vararg registries: IssueRegistry): IssueRegistry {
            return if (registries.size == 1) {
                registries[0]
            } else {
                CompositeIssueRegistry(registries.toList())
            }
        }

        /**
         * Given a jar file, create a class loader for it and instantiate
         * the named issue registry.
         *
         * TODO: Add a custom class loader architecture here such that
         * custom rules can have dependent jars without needing to jar-jar them!
         */
        private fun loadIssueRegistry(
            client: LintClient,
            jarFile: File,
            className: String,
            currentProject: Project?
        ): IssueRegistry? {
            // Make a class loader for this jar
            val url = SdkUtils.fileToUrl(jarFile)
            return try {
                val loader = client.createUrlClassLoader(
                    arrayOf(url),
                    JarFileIssueRegistry::class.java.classLoader
                )
                val registryClass = Class.forName(className, true, loader)
                val registry = registryClass.newInstance() as IssueRegistry

                val issues = try {
                    registry.issues
                } catch (e: Throwable) {
                    val stacktrace = StringBuilder()
                    LintDriver.appendStackTraceSummary(e, stacktrace)
                    val message = "Lint found one or more custom checks that could not " +
                            "be loaded. The most likely reason for this is that it is using an " +
                            "older, incompatible or unsupported API in lint. Make sure these " +
                            "lint checks are updated to the new APIs. The issue registry class " +
                            "is $className. The class loading issue is ${e.message}: $stacktrace"

                    LintClient.report(
                        client = client, issue = OBSOLETE_LINT_CHECK,
                        message = message, file = jarFile, project = currentProject
                    )
                    return null
                }

                try {
                    val apiField = registryClass.getDeclaredMethod("getApi")
                    val api = apiField.invoke(registry) as Int
                    if (api < CURRENT_API) {
                        val message = "Lint found an issue registry (`$className`) which is " +
                                "older than the current API level; these checks may not work " +
                                "correctly.\n" +
                                "\n" +
                                "Recompile the checks against the latest version. " +
                                "Custom check API version is $api (${describeApi(api)}), " +
                                "current lint API level is $CURRENT_API " +
                                "(${describeApi(CURRENT_API)})"
                        LintClient.report(
                            client = client, issue = OBSOLETE_LINT_CHECK,
                            message = message, file = jarFile, project = currentProject
                        )
                        // Not returning here: try to run the checks
                    } else {
                        try {
                            val minApi = registry.minApi
                            if (minApi > CURRENT_API) {
                                val message = "Lint found an issue registry (`$className`) which " +
                                        "requires a newer API level. That means that the custom " +
                                        "lint checks are intended for a newer lint version; please " +
                                        "upgrade"
                                LintClient.report(
                                    client = client, issue = OBSOLETE_LINT_CHECK,
                                    message = message, file = jarFile, project = currentProject
                                )
                                return null
                            }
                        } catch (ignore: Throwable) {
                        }
                    }
                } catch (e: Throwable) {
                    var message = "Lint found an issue registry (`$className`) which did not " +
                            "specify the Lint API version it was compiled with.\n" +
                            "\n" +
                            "**This means that the lint checks are likely not compatible.**\n" +
                            "\n" +
                            "If you are the author of this lint check, make your lint " +
                            "`IssueRegistry` class contain\n" +
                            "\u00a0\u00a0override val api: Int = com.android.tools.lint.detector.api.CURRENT_API\n" +
                            "or from Java,\n" +
                            "\u00a0\u00a0@Override public int getApi() { return com.android.tools.lint.detector.api.ApiKt.CURRENT_API; }"

                    val issueIds = issues.map { it.id }.sorted()
                    if (issueIds.any()) {
                        message += ("\n" +
                                "\n" +
                                "If you are just using lint checks from a third party library " +
                                "you have no control over, you can disable these lint checks (if " +
                                "they misbehave) like this:\n" +
                                "\n" +
                                "    android {\n" +
                                "        lintOptions {\n" +
                                "            disable ${issueIds.joinToString(
                                    separator = ",\n                    "
                                ) { "\"$it\"" }}\n" +
                                "        }\n" +
                                "    }\n").replace(
                            // Force indentation
                            "    ",
                            "\u00a0\u00a0\u00a0\u00a0"
                        )
                    }

                    LintClient.report(
                        client = client, issue = OBSOLETE_LINT_CHECK,
                        message = message, file = jarFile, project = currentProject
                    )
                    // Not returning here: try to run the checks
                }

                registry
            } catch (e: Throwable) {
                client.log(e, "Could not load custom lint check jar file %1\$s", jarFile)
                null
            }
        }

        /**
         * Returns a map from issue registry qualified name to the corresponding jar file
         * that contains it
         */
        private fun findRegistries(
            client: LintClient,
            jarFiles: Collection<File>
        ): Map<String, File> {
            val registryClassToJarFile = HashMap<String, File>()
            for (jarFile in jarFiles) {
                JarFile(jarFile).use { file ->
                    val manifest = file.manifest
                    val attrs = manifest.mainAttributes
                    var attribute: Any? = attrs[Attributes.Name(MF_LINT_REGISTRY)]
                    var isLegacy = false
                    if (attribute == null) {
                        attribute = attrs[Attributes.Name(MF_LINT_REGISTRY_OLD)]
                        if (attribute != null) {
                            isLegacy = true
                        }
                    }
                    if (attribute is String) {
                        val className = attribute

                        // Store class name -- but it may not be unique (there could be
                        // multiple separate jar files pointing to the same issue registry
                        // (due to the way local lint.jar files propagate via project
                        // dependencies) so only store this file if it hasn't already
                        // been found, or if it's a v2 version (e.g. not legacy)
                        if (!isLegacy || registryClassToJarFile[className] == null) {
                            registryClassToJarFile[className] = jarFile
                        }
                    } else {
                        // Load service keys. We're reading it manually instead of using
                        // ServiceLoader because we don't want to put these jars into
                        // the class loaders yet (since there can be many duplicates
                        // when a library is available through multiple dependencies)
                        val services = file.getJarEntry(SERVICE_KEY)
                        if (services != null) {
                            file.getInputStream(services).use {
                                val reader = InputStreamReader(it, Charsets.UTF_8)
                                reader.useLines {
                                    for (line in it) {
                                        val comment = line.indexOf("#")
                                        val className = if (comment >= 0) {
                                            line.substring(0, comment).trim()
                                        } else {
                                            line.trim()
                                        }
                                        if (!className.isEmpty() &&
                                            registryClassToJarFile[className] == null
                                        ) {
                                            registryClassToJarFile[className] = jarFile
                                        }
                                    }
                                }
                            }
                        } else {
                            client.log(
                                Severity.ERROR, null,
                                "Custom lint rule jar %1\$s does not contain a valid " +
                                        "registry manifest key (%2\$s).\n" +
                                        "Either the custom jar is invalid, or it uses an outdated " +
                                        "API not supported this lint client",
                                jarFile.path, MF_LINT_REGISTRY
                            )
                        }
                    }
                }
            }

            return registryClassToJarFile
        }

        /**
         * Work around http://bugs.java.com/bugdatabase/view_bug.do?bug_id=5041014 :
         * URLClassLoader, on Windows, locks the .jar file forever.
         * As of Java 7, there's a workaround: you can call close() when you're "done"
         * with the file. We'll do that here. However, the whole point of the
         * [JarFileIssueRegistry] is that when lint is run over and over again
         * as the user is editing in the IDE and we're background checking the code, we
         * don't want to keep loading the custom view classes over and over again: we want to
         * cache them. Therefore, just closing the URLClassLoader right away isn't great
         * either. However, it turns out it's safe to close the URLClassLoader once you've
         * loaded the classes you need, since the URLClassLoader will continue to serve
         * those classes even after its close() methods has been called.
         *
         * Therefore, if we can call close() on this URLClassLoader, we'll proactively load
         * all class files we find in the .jar file, then close it.
         *
         * @param client the client to report errors to
         * @param file the .jar file
         * @param loader the URLClassLoader we should close
         */
        private fun loadAndCloseURLClassLoader(
            client: LintClient,
            file: File,
            loader: URLClassLoader
        ) {
            if (SdkConstants.CURRENT_PLATFORM != SdkConstants.PLATFORM_WINDOWS) {
                // We don't need to close the class loader on other platforms than Windows
                return
            }

            // Before closing the jar file, proactively load all classes:
            try {
                JarFile(file).use { jar ->
                    val enumeration = jar.entries()
                    while (enumeration.hasMoreElements()) {
                        val entry = enumeration.nextElement()
                        val path = entry.name
                        // Load non-inner-classes
                        if (path.endsWith(DOT_CLASS) && path.indexOf('$') == -1) {
                            // Strip .class suffix and change .jar file path (/)
                            // to class name (.'s).
                            val name = path.substring(0, path.length - DOT_CLASS.length)
                                .replace('/', '.')
                            try {
                                val cls = Class.forName(name, true, loader)
                                // Actually, initialize them too to make sure basic classes
                                // needed by the detector are available
                                if (!(cls.isAnnotation || cls.isEnum || cls.isInterface)) {
                                    try {
                                        val defaultConstructor = cls.getConstructor()
                                        defaultConstructor.isAccessible = true
                                        defaultConstructor.newInstance()
                                    } catch (ignore: NoSuchMethodException) {
                                    }
                                }
                            } catch (e: Throwable) {
                                client.log(
                                    Severity.ERROR, e,
                                    "Failed to prefetch $name from $file"
                                )
                            }
                        }
                    }
                }
            } catch (ignore: Throwable) {
            } finally {
                // Finally close the URL class loader
                try {
                    loader.close()
                } catch (ignore: Throwable) {
                    // Couldn't close. This is unlikely.
                }
            }
        }
    }
}
