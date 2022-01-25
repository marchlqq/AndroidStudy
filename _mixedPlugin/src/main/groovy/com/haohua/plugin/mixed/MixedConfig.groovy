package com.haohua.plugin.mixed;

class MixedConfig {
    boolean enable
    String propertiesFrom
    String zipFrom
    File propertiesTo
    File zipTo
    File unzipDes
    boolean themeAble

    String toString() {
        return "MixedConfig -->> enable = ${enable}, propertiesFrom = ${propertiesFrom}, zipFrom = ${zipFrom}, propertiesTo = ${propertiesTo}, zipTo = ${zipTo}, unzipDes = ${unzipDes}, themeAble = ${themeAble}"
    }
}