# Anltr4语法解析

## 环境准备
1. 下载最新版的 antlr4
```shell
curl -O https://www.antlr.org/download/antlr-4.13.1-complete.jar
```
2. 最新版的 antlr4 依赖 java11，需要配置 java11 环境。生成的代码依然可以在 java8 环境下运行。 
如果不想安装 java11，则使用版本`4.7.2`
```shell
# mac下安装 java11
brew install openjdk@11

# 并使用 jenv 管理，添加到 jenv
jenv add /usr/local/opt/openjdk@11

# 查看 jenv 当前的可用 java 版本
jenv versions

# 仅在单项目使用 java11
jenv local 11.0

# 查看 antlr4 命令可用选项
java -Xmx500M -cp "antlr-jars/antlr-4.13.1-complete.jar:$CLASSPATH" org.antlr.v4.Tool
```

## 生成解析代码

### Doris 解析代码生成
1. 执行生成命令
```shell
# 生成 Parser
java -Xmx500M -cp "antlr-jars/antlr-4.13.1-complete.jar:$CLASSPATH" org.antlr.v4.Tool -o /Users/waywee/self-improve/iantlr/gen/doris4 -package com.lark.doris -Dlanguage=Java -listener -visitor -lib /Users/waywee/self-improve/iantlr/grammer4/doris4_0_2-rc02 /Users/waywee/self-improve/iantlr/grammer4/doris4_0_2-rc02/DorisParser.g4 /Users/waywee/self-improve/iantlr/grammer4/doris4_0_2-rc02/DorisLexer.g4
```

2. 会在`gen/doris`目录下生成解析代码，同时默认会增加来源 g4 文件的目录层级
3. 解析样例见测试代码
```shell
1. 正例：src/test/java/com/lark/antlr/doris/DorisNormalTest.java
2. 反例：src/test/java/com/lark/antlr/doris/DorisExceptionTest.java
```
