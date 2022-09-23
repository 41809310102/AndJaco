package com.ttp.and_jacoco.task

import com.ttp.and_jacoco.extension.JacocoExtension
import com.ttp.and_jacoco.report.ReportGenerator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
class CreateReport extends DefaultTask {
    @Internal
    JacocoExtension jacocoExtension
    @TaskAction
    def getDiffClass() {
        if (jacocoExtension.execDir == null) {
            jacocoExtension.execDir = "${project.buildDir}/jacoco/code-coverage/"
        }
        def dataDir = jacocoExtension.execDir
        new File(dataDir).mkdirs()
        if(!jacocoExtension.isLocal){
            println "downloadEcData start..................."
            //downloadEcDatas()
            println "downloadEcData end!"
        }else{
            println("now choose local ec file........")
        }
        //生成差异报告
        println "pullDiffClasses start"
        //pullDiffadmin()
        println "pullDiffClasses end!!!!!!"

        if (jacocoExtension.reportDirectory == null) {
            jacocoExtension.reportDirectory = "${project.buildDir.getAbsolutePath()}/outputs/report"
        }
        ReportGenerator generator = new ReportGenerator(jacocoExtension.execDir, toFileList(jacocoExtension.classDirectories),
                toFileList(jacocoExtension.sourceDirectories), new File(jacocoExtension.reportDirectory));
        generator.create();
    }

}