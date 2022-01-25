package com.haohua.plugin.mixed;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class AndroidPropertiesTask extends DefaultTask {

    @Input
    String urlFrom

    @OutputFile
    File fileTo

    @TaskAction
    void androidProperties() {
        println("androidProperties TaskAction")
        if (!fileTo.getParentFile().exists()) {
            fileTo.mkdirs()
        }
        if(fileTo.exists()){
            fileTo.delete()
        }
        Utils.downloadGet(urlFrom, fileTo)
    }

}