# Android Lint

## 简介

这是一个通用Android Lint库，你可以用它来检查代码规范、bug、资源命名等。

本库最大特点是通用，相较于其他Lint库（规则直接在代码写死），最大的不同是，规则全靠配置生成，更加灵活，即便不知道LintApi，也能添加对应规则。

具体详情可以参考[Android Lint代码检查实践]()。如果有感兴趣的大佬，欢迎一起开发、交流。



## 项目结构
> .<br>
  ├── app												// demo工程用来展示lint检查效果<br>
  ├── checks										  // lint规则代码<br>
  ├── custom_lint_config.json			// 自定义配置文件<br>
  ├── lintlibrary									// 空项目，依赖了checks用来生成aar包<br>
  ├── lintplugin									// lint插件用来简化lint配置，并加入日志输出<br>



## 依赖

**需要Android Gradle在3.5.0以上**



## 使用

添加依赖后，并在项目根目录下添加custom_lint_config.json配置文件（关于配置可以看下一小节）

### 编码实时提示

添加依赖和配置文件后Rebuild一下（如果还不行则执行次`./gradlew :${moduleName}:lint${flavor}${buildType}`）即可编码实时提示。

![](http://rocketzly.androider.top/lint_as_effect.png)

**有一点需要注意，AS对于Lint实时提示支持的不算特别好，少数情况下会出现编码时提示不了的情况，但是命令行执行结果都是正常的，所以以命令行执行结果为准。**

### 命令行执行

添加依赖和配置文件后，Terminal执行`./gradlew :${moduleName}:lint${flavor}${buildType}`就可以看到检查结果。

以本库为例，执行`./gradlew :app:lintDebug`结果如下：

![](http://rocketzly.androider.top/lint_plugin_log.png)

LintConfig和LintResult中的信息是在LintPlugin添加的日志信息。

查看生成的Html文件可以查看详细lint报告

![](http://rocketzly.androider.top/lint_report_github.png)

![](http://rocketzly.androider.top/lint_report_github2.png)

**最新版本代码日志和报告可能稍有不同但不影响结果**



## 配置

目前支持五大类规则（后期会继续增加、完善规则，欢迎有兴趣大佬一起开发）

- 避免使用Api（又可细分为三类）
  - 避免使用的方法
  - 避免创建的类
  - 避免继承或实现的类
- 需要处理异常的方法
- 有依赖关系Api（即trigger_method方法调用后，必须调用dependency_method方法）
- 资源命名规范
- Serializerable对象的引用类型成员变量也必须要实现Serializerable接口

具体配置规则和说明如下：

```json
{
  "avoid_usage_api": {//避免使用Api
    "method": [//避免使用方法
      {
        "name":"",//方法名，匹配方法全路径，以Toast#show()为例，则应填入android.widget.Toast.show
        "name_regex": "",//方法名正则匹配，以Log#i()为例，则应填入android\.util\.Log\.(v|d|i|w|e)
        "message": "",//提示信息
        "exclude": [//需要排除检查的类，匹配全路径类名，例如com.rocketzly.androidlint.Test
          ""
        ],
        "exclude_regex": "",//需要排除检查的类正则匹配
        "severity": "error"//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
      }
    ],
    "construction": [//避免创建类
      {
        "name": "",//构造函数名，匹配方法全路径，以Thread构造函数为例java.lang.Thread
        "name_regex": "",//方法名正则匹配
        "message": "",//提示信息
        "exclude": [//需要排除检查的类，匹配全路径类名，例如com.rocketzly.androidlint.Test
          ""
        ],
        "exclude_regex": "",//需要排除检查的类正则匹配
        "severity": "error"//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
      }
    ],
    "inherit": [//避免继承或实现的类
      {
        "name":"",//类名，匹配全路径类名，例如com.rocketzly.androidlint.Test
        "name_regex": "",//类名正则匹配，例如\.(AppCompat|Main)?Activity$
        "message": "",//提示信息
        "exclude": [//需要排除检查的类，匹配全路径类名，例如com.rocketzly.androidlint.Test
          ""
        ],
        "exclude_regex": "",//需要排除检查的类正则匹配
        "severity": "error"//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
      }
    ]
  },
  "handle_exception_method": [//需要处理异常的方法
    {
        "name":"",//方法名，匹配方法全路径，以Toast#show()为例，则应填入android.widget.Toast.show
        "name_regex": "",//方法名正则匹配，以Log#i()为例，则应填入android\.util\.Log\.(v|d|i|w|e)
      	"exception": "",//需要处理的异常全路径类名，如java.lang.IllegalArgumentException
        "message": "",//提示信息
        "exclude": [//需要排除检查的类，匹配全路径类名，例如com.rocketzly.androidlint.Test
          ""
        ],
        "exclude_regex": "",//需要排除检查的类正则匹配
        "severity": "error"//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
    }
  ],
  "dependency_api": [//有依赖关系api
    {
      "trigger_method": "",//触发方法，匹配方法全路径，以Toast#show()为例，则应填入android.widget.Toast.show
      "dependency_method": "",//依赖方法，匹配方法全路径，即当调用trigger_method后需要调用dependency_method方法
      "message": "",//提示信息
      "severity": ""//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
    }
  ],
  "resource_name": {//资源命名
    "drawable": {
      "name_regex": "",//资源名正则规范
      "message": "",//提示信息
      "severity": ""//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
    },
    "layout": {
      "name_regex": "",//资源名正则规范
      "message": "",//提示信息
      "severity": ""//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
    }
  },
  "serializable_config": {//Serializable配置
    "name_regex": "",//检查哪些包下的类，例如只检查自己包下的类^(com\\.rocketzly\\.checks|com\\.rocketzly\\.androidlint)
    "message": "",//提示信息
    "severity": ""//lint错误严重程度默认error，可输入fatal、error、warning、informational、ignore
  }
}

```

匹配规则是先排除后匹配，exclude > exclude_regex > name > name_regex。

message会影响到AS实时提示展示的信息和report中展示信息。

severity则是对应AS实时提示的错误等级和report中错误等级。

具体demo可以看项目根目录下custom_lint_config.json。



## 协议

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).


