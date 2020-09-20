#!/bin/bash

chmod 777 $1
reportPath=$(cat $1 | grep "reportPath" | awk -F '=' '{print $2}')
userName=$(cat $1 | grep "userName" | awk -F '=' '{print $2}')
moduleName=$(cat $1 | grep "moduleName" | awk -F '=' '{print $2}')
errorCount=$(cat $1 | grep "errorCount" | awk -F '=' '{print $2}')

echo $reportPath
echo $userName
echo $moduleName
echo $errorCount
