package com.haohua.plugin.mixed;

class MixedBuild {
    boolean enable
    File propertiesFile
    Closure featureBuild
    Closure mixedAfter

    String toString() {
        return "BuildConfig -->> enable = ${enable}, propertiesFile = ${propertiesFile}, featureBuild = ${featureBuild.toString()}, mixedAfter = ${mixedAfter.toString()}"
    }
}