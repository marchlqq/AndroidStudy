package com.haohua.plugin.mixed

import org.gradle.api.Plugin
import org.gradle.api.Project

class MixedPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("mixed plugin apply and this project is " + project.getName())

        project.getExtensions().create("mixed", MixedConfig)
        project.getExtensions().create("mixedBuild", MixedBuild)

        project.afterEvaluate {
            if (project['mixed'].enable) {
                println(project['mixed'].toString())

                def propertiesTask = project.task("AndroidProperties", type: AndroidPropertiesTask) {
                    group "mixed"
                    urlFrom = project['mixed'].propertiesFrom
                    fileTo = project['mixed'].propertiesTo
                }

                def zipTask = project.task("AndroidZip", type: AndroidZipTask) {
                    group "mixed"
                    urlFrom = project['mixed'].zipFrom
                    fileTo = project['mixed'].zipTo
                    unzipTo = project['mixed'].unzipDes
                    themeAble = project['mixed'].themeAble
                }

                propertiesTask.actions.each {
                    it.execute(propertiesTask)
                }

                zipTask.actions.each {
                    it.execute(zipTask)
                }
            } else {
                println('mixed fetch is not enable')
            }

            if (project['mixedBuild'].enable) {
                println(project['mixedBuild'].toString())

                File propertiesFile = project['mixedBuild'].propertiesFile

                if (propertiesFile == null || !propertiesFile.exists() || !propertiesFile.canRead()) {
                    throw new MixedException('can not load properties file ~!!')
                }
                def Params = new Properties()
                def inputStream = new BufferedInputStream(new FileInputStream(propertiesFile))
                Params.load(new InputStreamReader(inputStream, "UTF-8"))

                project.ext.Params = Params

                Closure mixedAfter = project['mixedBuild'].mixedAfter
                Closure featureBuild = project['mixedBuild'].featureBuild


                project.allprojects {
//                    println "mixed configuring:" + it.name
                    if(it.name != "flutter"){
                        it.afterEvaluate {
                            def androidExtension = it.extensions.findByName("android")
                            //只在 android application 或者 android lib 项目中生效
                            if (androidExtension != null) {
                                if (mixedAfter != null) {
                                    mixedAfter(it)
                                }
                                if (featureBuild != null) {
                                    featureBuild(androidExtension)
                                }
                            }
                        }
                    }
                }
            } else {
                println('mixedBuild is not enable')
            }
        }
    }


}