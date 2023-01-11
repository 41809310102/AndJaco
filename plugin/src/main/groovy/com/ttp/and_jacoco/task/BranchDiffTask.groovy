package com.ttp.and_jacoco.task

import com.alibaba.fastjson.JSONObject
import com.ttp.and_jacoco.util.OkHttpTemplate
import com.ttp.and_jacoco.util.Ziputil
import okhttp3.Call
import okhttp3.ResponseBody
import org.gradle.api.tasks.Internal
import org.jacoco.core.diff.GetinjutClass


import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference

import com.ttp.and_jacoco.extension.JacocoExtension
import com.ttp.and_jacoco.report.ReportGenerator
import com.ttp.and_jacoco.result.CodeDiffResultVO
import com.ttp.and_jacoco.result.MethodInfoResultVO
import com.ttp.and_jacoco.util.Juiutil

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jacoco.core.data.MethodInfo
import org.jacoco.core.diff.DiffAnalyzer

import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


class BranchDiffTask extends DefaultTask {
    @Internal
    JacocoExtension jacocoExtension
    @TaskAction
    def checkDifClass() {
        if (jacocoExtension.execDir == null) {
            jacocoExtension.execDir = "${project.buildDir}/jacoco/code-coverage/"
        }
        def dataDir = jacocoExtension.execDir
        new File(dataDir).mkdirs()
        if(!jacocoExtension.isLocal){
            println "downloadEcData start..................."
            downloadEcDatas()
            println "downloadEcData end!"
        }else{
            println("now choose local ec file........")
        }
        DiffAnalyzer.injutlist = []
        //生成差异报告
        println "pullDiffClasses start"
        pullDiffadmin()
        println "pullDiffClasses end!!!!!!"
        if (jacocoExtension.reportDirectory == null) {
            jacocoExtension.reportDirectory = "${project.buildDir.getAbsolutePath()}/outputs/report/"+jacocoExtension.reportid
        }
        ReportGenerator generator = new ReportGenerator(jacocoExtension.execDir, toFileList(jacocoExtension.classDirectories),
                toFileList(jacocoExtension.sourceDirectories), new File(jacocoExtension.reportDirectory));
        generator.create();
        println("the project found ADD codes of java-files or kt-files to show that..... ")
        for(GetinjutClass ob: DiffAnalyzer.injutlist){
            println(ob.toString())
        }
        //开始上传本次编译文件
        toZip()
    }

    def toFileList(List<String> path) {
        List<File> list = new ArrayList<>(path.size())
        for (String s : path)
            list.add(new File(s))
        return list
    }

    //从deff平台获取
    def pullDiffadmin() {
        //发送http请求,url 参数
        if(jacocoExtension.isdiff){
            createDiffMethods(syncUploadFiles())
        }else{
            createDiffMethods(syncUploadFiles0flocal())
        }
        //获取差异方法
    }


    def  syncUploadFiles(){
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(300, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)//写入超时(单位:秒)
                .build();
        System.out.println(("now get diffadmin send http message letter start"));
        //RequestBody.create(MediaType.get("application/json"));
        String baseVersion = jacocoExtension.branchName
        String nowVersion = jacocoExtension.nowVersion
        String gitUrl = jacocoExtension.giturl
        String gitdiffurl = jacocoExtension.gitdiffurl
        //http://127.0.0.1:8085/api/code/diff/git/list
        String url = gitdiffurl+"?baseVersion="+baseVersion+"&gitUrl="+gitUrl+"&nowVersion="+nowVersion;
        //builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
        Response response = client.newCall(new Request.Builder()
                .url(url)
                .get()
                .build()).execute();
        //解析json对象
        String str = response.body().string();
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(str);
        str = JSON.toJSONString(jsonObject.get("data"))
        List<CodeDiffResultVO> passengerDetailsVOS = JSON.parseObject(str, new TypeReference<List<CodeDiffResultVO>>(){});
        List<MethodInfo> getdifflist = new LinkedList<>()
        for(CodeDiffResultVO codeDiffResultVO : passengerDetailsVOS){
            System.out.println(codeDiffResultVO.getClassFile())
            String classname = codeDiffResultVO.getClassFile()
            if(codeDiffResultVO.getMethodInfos().size()>0){
                for(MethodInfoResultVO res: codeDiffResultVO.getMethodInfos()){
                    MethodInfo methodInfo = new MethodInfo()
                    methodInfo.setClassName(classname);
                    methodInfo.setMethodName(res.getMethodName())
                    methodInfo.setDesc(Juiutil.diffadminTran(res.getParameters())) //这里保存类的参数信息
                    getdifflist.add(methodInfo)
                }
            }else{
                MethodInfo methodInfo = new MethodInfo()
                methodInfo.setClassName(classname);
                methodInfo.setMethodName("Class") //默认是新的方法，全部标记
                methodInfo.setDesc("()") //默认无数据
                getdifflist.add(methodInfo)
            }
        }
        System.out.println("now get dif fadmin send http message letter over")
        System.out.println("difflist size="+getdifflist.size())
      //  print("all data to see:" + getdifflist.toString())
        return getdifflist
    }


    def syncUploadFiles0flocal(){
        String path = jacocoExtension.diffpath
        println("start read json file......")
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();

        System.out.println(JSON.parseObject(jsonStr));
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonStr);
        jsonStr = JSON.toJSONString(jsonObject.get("data"))
        List<CodeDiffResultVO> passengerDetailsVOS = JSON.parseObject(jsonStr, new TypeReference<List<CodeDiffResultVO>>(){});
        List<MethodInfo> getdifflist = new LinkedList<>()
        for(CodeDiffResultVO codeDiffResultVO : passengerDetailsVOS){
            System.out.println(codeDiffResultVO.getClassFile())
            String classname = codeDiffResultVO.getClassFile()
            if(codeDiffResultVO.getMethodInfos().size()>0){
                for(MethodInfoResultVO res: codeDiffResultVO.getMethodInfos()){
                    MethodInfo methodInfo = new MethodInfo()
                    methodInfo.setClassName(classname);
                    methodInfo.setMethodName(res.getMethodName())
                    methodInfo.setDesc(Juiutil.diffadminTran(res.getParameters())) //这里保存类的参数信息
                    getdifflist.add(methodInfo)
                }
            }else{
                MethodInfo methodInfo = new MethodInfo()
                methodInfo.setClassName(classname);
                methodInfo.setMethodName("Class") //默认是新的方法，全部标记
                methodInfo.setDesc("()") //默认无数据
                getdifflist.add(methodInfo)
            }
        }
        System.out.println("now get diff is ok")
        System.out.println("difflist size="+getdifflist.size())
        return getdifflist

    }


    void readFiles(String dirPath, Closure closure) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return
        }
        File[] files = file.listFiles();
        for (File classFile : files) {
            if (classFile.isDirectory()) {
                readFiles(classFile.getAbsolutePath(), closure);
            } else {
                if (classFile.getName().endsWith(".class")) {
                    if (!closure.call(classFile)) {
                        classFile.delete()
                    }
                } else {
                    classFile.delete()
                }
            }
        }
    }

    //重写差异方法 2022-09-12 honyyi
    def createDiffMethods(List<MethodInfo> list){
        DiffAnalyzer.getInstance().reset() //将检测对象属性置为空
        DiffAnalyzer.getInstance().creatediff(list)
        //excludeMethod  这里获取配置删除不需要检测的方法名称
        println("excludeMethod before diff.size=${DiffAnalyzer.getInstance().getDiffList().size()}") //打印差别方法的数量
        if (jacocoExtension.excludeMethod != null) {
            Iterator<MethodInfo> iterator = DiffAnalyzer.getInstance().getDiffList().iterator()
            while (iterator.hasNext()) {
                MethodInfo info = iterator.next();
                if (jacocoExtension.excludeMethod.call(info))
                    iterator.remove()
            }
        }
        //这里输出筛选后的方法名称
        println("excludeMethod after diff.size=${DiffAnalyzer.getInstance().getDiffList().size()}")
    }
    def downloadEcDatas(){
        println("get downloadFile of  Ec  files  loading..................................")
        if (jacocoExtension.execDir == null) {
            jacocoExtension.execDir = "${project.buildDir}/jacoco/code-coverage/"
        }
        def dataDir = jacocoExtension.execDir
        new File(dataDir).mkdirs()
        def downEchost = jacocoExtension.downEchost
        saveUrlAs(downEchost)
    }
    def saveUrlAs(String url){
        ResponseBody result =null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            //            System.out.println(response.headers());
            //            System.out.println(response.body().string());
            result = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取InputStream
        InputStream is = result.byteStream();
        WriteFile4InputStream(is)
    }
    //将InputStream写入到文件，成功返回true 失败返回false
    def  WriteFile4InputStream(InputStream inputStream) {
        //默认为flase 即失败
        boolean result = false;
        try {
            OutputStream os = new FileOutputStream(jacocoExtension.execDir+"/code.zip");
            os.write(inputStream.readAllBytes());
            os.close();
            result = true;
        }catch (IOException e)
        {
            e.printStackTrace();
            result = false;
        }
        //将压缩包解压
        isopenZip()
        if(result){
            println ("the ec file is down ok!")
        }else{
            println ("the ec file is down error!")
        }
    }

    //解压zip文件
    def isopenZip(){
        String sourcePath = jacocoExtension.execDir+"/code.zip"
        String desPath = jacocoExtension.execDir
        try {
            ZipFile zipFile = new ZipFile(sourcePath,Charset.forName("utf-8"));
            System.out.println("found the ECZIP path:"+sourcePath);
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry;
            while(enumeration.hasMoreElements()){
                zipEntry = (ZipEntry) enumeration.nextElement();
                System.out.println("the Ec file=>:"+zipEntry.getName());
                if(zipEntry.isDirectory()){
                    continue;
                }
                File file = new File(desPath+"\\"+zipEntry.getName());
                if(!file.exists()){
                    file.createNewFile();
                }
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                int len;
                byte[] bytes = new byte[1024];
                while ((len=is.read())!=-1){
                    fos.write(len);
                }
                fos.close();
                is.close();
            }
            zipFile.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    //压缩覆盖率报告并且上传服务接口


    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
     def toZip(){
         String path =  jacocoExtension.reportDirectory
         String zippath =jacocoExtension.execDir
         FileOutputStream fos1 = new FileOutputStream(new File(zippath+"/"+"Report.zip"));
         Ziputil.toZip(path,fos1,true)
         OkHttpTemplate okHttpTemplate = new OkHttpTemplate();
         //将报告上传
         okHttpTemplate.uploadFile(jacocoExtension.host,zippath+"/"+"Report.zip");
    }





    boolean deleteEmptyDir(File dir) {
        if (dir.isDirectory()) {
            boolean flag = true
            for (File f : dir.listFiles()) {
                if (deleteEmptyDir(f))
                    f.delete()
                else
                    flag = false
            }
            return flag
        }
        return false
    }




}