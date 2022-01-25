package com.haohua.plugin.mixed;

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.io.ZipInputStream
import net.lingala.zip4j.model.FileHeader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class AndroidZipTask extends DefaultTask {

    @Input
    String urlFrom

    @Input
    boolean themeAble

    @OutputFile
    File fileTo

    @OutputDirectory
    File unzipTo

    @TaskAction
    void androidZip() {
        println("androidZip TaskAction")
        if (!fileTo.getParentFile().exists()) {
            fileTo.mkdirs()
        }
        if (fileTo.exists()) {
            fileTo.delete()
        }
        Utils.downloadGet(urlFrom, fileTo)
        unZip(fileTo, unzipTo)
    }

    void unZip(File androidZip, File zipDir) {
        // 解压根目录
        def destDir = zipDir

        println "upZipFile start: ${androidZip.getAbsolutePath()} and destDir: ${destDir.getAbsolutePath()}"

        Utils.deleteDir(destDir)

        long startUnZipTimeMinis = System.currentTimeMillis()

        if (androidZip.exists()) {
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(androidZip)
            // 设置文件名编码，在GBK系统中需要设置
            zFile.setFileNameCharset("GBK")
            // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
            if (!zFile.isValidZipFile()) {
                throw new ZipException("压缩文件不合法,可能被损坏！")
            }

            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir()
            }

            def fileHeaderList = zFile.getFileHeaders()

            def themeDir = fileHeaderList.find() {
                it.getFileName().startsWith('android/res-theme')
            }

            if (themeDir != null && !themeAble) {
                throw new Exception('非法的换肤构建操作！请先购买换肤服务。')
            }

            def filter1 = fileHeaderList.findAll {
                FileHeader fileHeader = (FileHeader) it
                def fName = fileHeader.getFileName()
                !fName.endsWith('.DS_Store')
            }

            def filter2 = filter1.findAll {
                FileHeader fileHeader = (FileHeader) it
                def fName = fileHeader.getFileName()
                fName.startsWith('android/assets/menu/') ||
                        fName.startsWith('android/assets/default_emoji/') ||
                        fName.startsWith('android/assets/config/') ||
                        fName.startsWith('android/assets/todo/') ||
                        fName.startsWith('android/res/drawable-hdpi/') ||
                        fName.startsWith('android/res/drawable-xhdpi/') ||
                        fName.startsWith('android/res/drawable-xxhdpi/') ||
                        fName.startsWith('android/res/raw/') ||
                        fName.startsWith('android/java/') ||
                        fName.startsWith('android/assets/cert') ||
                        fName.endsWith('/strings.xml') ||
                        fName.startsWith('android/res-theme') ||
                        fName == 'android/assets/featureConfigs.json'
            }

            //由于压缩文件目录结构问题，顾需要以流的形式单个解压到目标位置
            filter2.each {
                FileHeader fileHeader = (FileHeader) it

                if (fileHeader != null) {
                    def fName = fileHeader.getFileName()
                    println("fName = ${fName}")
                    def finalDest = destDir.getAbsolutePath() + fName.replaceFirst('android', '')
                    println("finalDest = ${finalDest}")
                    def outFile = new File(finalDest)

                    if (fileHeader.isDirectory()) {
                        outFile.mkdirs()
                        //在 groovy 语法中 return 相当于 continue
                        return
                    }

                    File parentDir = outFile.getParentFile()

                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }

                    ZipInputStream is = zFile.getInputStream(fileHeader)
                    OutputStream os = new FileOutputStream(outFile)

                    def readLen
                    byte[] buff = new byte[4096]

                    while ((readLen = is.read(buff)) != -1) {
                        os.write(buff, 0, readLen)
                    }
                    os.close()
                    is.close()
                }
            }

////        filter2.each {
////            FileHeader fileHeader = (FileHeader) it
////            def fName = fileHeader.getFileName()
////            println(fName)
////            def finalDest = destDir.getAbsolutePath() + fName.replaceFirst('android', '')
////            println("finalDest = ${finalDest}")
////            // 将文件抽出到解压目录(解压)
////            zFile.extractFile(fileHeader, buildDir.getAbsolutePath())
////        }
            println("upZipFile finished !!! 共耗时 ：" + (System.currentTimeMillis() - startUnZipTimeMinis) + "ms")
        } else {
            println "upZipFile zipFile is not exists !!! "
        }
    }

}