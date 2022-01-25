package com.haohua.plugin.mixed;

import okhttp3.OkHttpClient
import okhttp3.Request

class Utils {

    static void downloadGet(String urlFrom, File fileTo) {

        if (urlFrom == null || urlFrom.isEmpty()) {
            throw new Exception('downloadGet but urlFrom is no value !!!')
        }

        if (fileTo == null) {
            throw new Exception('downloadGet but fileTo is no value !!!')
        }

        if (fileTo.exists()) {
            fileTo.delete()
        }

        println("start download get ---->>>> url: ${urlFrom}")

        def client = new OkHttpClient()
        def request = new Request.Builder().url(urlFrom).build()
        def response = client.newCall(request).execute()

        if (response.code() == 200) {

            println("download success ---->>>> write file： ${fileTo.getAbsolutePath()}")

            fileTo.withOutputStream { osm ->
                osm.write(response.body().bytes())
            }
        } else {
            throw new Exception("download get fail ！！！ -->> code: ${response.code()},${response.message()}")
        }
        response.close()
    }

    static void deleteDir(File dir) {
        if (dir && dir.listFiles()) {
            dir.listFiles().sort().each { File file ->
                if (file.isFile()) {
                    file.delete()
                } else {
                    file.deleteDir()
                }
            }
        }
    }
}