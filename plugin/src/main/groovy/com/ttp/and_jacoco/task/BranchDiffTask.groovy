package com.ttp.and_jacoco.task

import okhttp3.Call
import okhttp3.ResponseBody
import org.gradle.api.tasks.Internal

import java.net.HttpURLConnection
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.android.utils.FileUtils
import com.ttp.and_jacoco.extension.JacocoExtension
import com.ttp.and_jacoco.report.ReportGenerator
import com.ttp.and_jacoco.result.CodeDiffResultVO
import com.ttp.and_jacoco.result.MethodInfoResultVO
import com.ttp.and_jacoco.util.Juiutil
import com.ttp.and_jacoco.util.Utils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.codehaus.groovy.runtime.IOGroovyMethods
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jacoco.core.data.MethodInfo
import org.jacoco.core.diff.DiffAnalyzer
import java.util.concurrent.TimeUnit

class BranchDiffTask extends DefaultTask {

    @Internal
    JacocoExtension jacocoExtension
    @Internal
    def currentName = 'debug'
    @Internal
    def getDiffClass() {
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
        //生成差异报告
        println "pullDiffClasses start"
        pullDiffadmin()
        println "pullDiffClasses end!!!!!!"

        if (jacocoExtension.reportDirectory == null) {
            jacocoExtension.reportDirectory = "${project.buildDir.getAbsolutePath()}/outputs/report"
        }
        ReportGenerator generator = new ReportGenerator(jacocoExtension.execDir, toFileList(jacocoExtension.classDirectories),
                toFileList(jacocoExtension.sourceDirectories), new File(jacocoExtension.reportDirectory));
        generator.create();
    }

    def toFileList(List<String> path) {
        List<File> list = new ArrayList<>(path.size())
        for (String s : path)
            list.add(new File(s))
        return list
    }

    def pullDiffClasses() {
        currentName = "git name-rev --name-only HEAD".execute().text.replaceAll("\n", "")
        if(currentName.contains("/")){
            currentName=currentName.substring(currentName.lastIndexOf("/")+1)
        }

        println "currentName:\n" + currentName
        //获得两个分支的差异文件
        def diff = "git diff origin/${jacocoExtension.branchName} origin/${currentName} --name-only".execute().text
        List<String> diffFiles = getDiffFiles(diff)

        println("diffFiles size=" + diffFiles.size())
        writerDiffToFile(diffFiles)

        //两个分支差异文件的目录
        def currentDir = "${project.rootDir.parentFile}/temp/${currentName}/app"
        def branchDir = "${project.rootDir.parentFile}/temp/${jacocoExtension.branchName}/app"

        project.delete(currentDir)
        project.delete(branchDir)
        new File(currentDir).mkdirs()
        new File(branchDir).mkdirs()

        //先把两个分支的所有class copy到temp目录
        copyBranchClass(jacocoExtension.branchName, branchDir)
        copyBranchClass(currentName, currentDir)
        //再根据diffFiles 删除不需要的class
        deleteOtherFile(diffFiles, branchDir)
        deleteOtherFile(diffFiles, currentDir)

        //删除空文件夹
        deleteEmptyDir(new File(branchDir))
        deleteEmptyDir(new File(currentDir))

        createDiffMethod(currentDir, branchDir)

        writerDiffMethodToFile()
    }

    //从deff平台获取
    def pullDiffadmin() {
        //发送http请求,url 参数
        createDiffMethods(syncUploadFiles())
        //获取差异方法
    }


   def  syncUploadFiles(){
       OkHttpClient client = new OkHttpClient.Builder()
               .callTimeout(30, TimeUnit.SECONDS)
               .readTimeout(30, TimeUnit.SECONDS)
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
       print("all data to see:" + getdifflist.toString())
       return getdifflist
    }




    def writerDiffToFile(List<String> diffFiles) {
        String path = "${project.buildDir.getAbsolutePath()}/outputs/diff/diffFiles.txt"
        File parent = new File(path).getParentFile();
        if (!parent.exists()) parent.mkdirs()

        println("writerDiffToFile size=" + diffFiles.size() + " to >" + path)
        println("--------------------------------------------------------------->jacoco")

        FileOutputStream fos = new FileOutputStream(path)
        for (String str : diffFiles) {
            fos.write((str + "\n").getBytes())
        }
        fos.close()
    }

    def writerDiffMethodToFile() {
        String path = "${project.buildDir.getAbsolutePath()}/outputs/diff/diffMethod.txt"

        println("writerDiffMethodToFile size=" + DiffAnalyzer.getInstance().getDiffList().size() + " >" + path)

        FileUtils.writeToFile(new File(path), DiffAnalyzer.getInstance().toString())
    }

    def deleteOtherFile(List<String> diffFiles, String dir) {

        readFiles(dir, {
            String path = ((File) it).getAbsolutePath().replace(dir, "app")
            //path= app/classes/com/example/jacoco_plugin/MyApp.class
            return diffFiles.contains(path)
        })
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

    private void copyBranchClass(String currentName, GString currentDir) {
        String[] cmds
        if (Utils.windows) {
            cmds = new String[5]
            cmds[0] = jacocoExtension.getGitBashPath()
            cmds[1] = jacocoExtension.copyClassShell
            cmds[2] = currentName
            cmds[3] = project.rootDir.getAbsolutePath()
            cmds[4] = currentDir.toString()
        } else {
            cmds = new String[4]
            cmds[0] = jacocoExtension.copyClassShell
            cmds[1] = currentName
            cmds[2] = project.rootDir.getAbsolutePath()
            cmds[3] = currentDir.toString()
        }

        println("cmds=" + cmds)
        Process pces = Runtime.getRuntime().exec(cmds)
        String result = IOGroovyMethods.getText(new BufferedReader(new InputStreamReader(pces.getIn())))
        String error = IOGroovyMethods.getText(new BufferedReader(new InputStreamReader(pces.getErr())))

        println("copyClassShell succ :" + result)
        println("copyClassShell error :" + error)

        pces.closeStreams()
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


    def createDiffMethod(def currentDir, def branchDir) {
        //生成差异方法
/*
        def path="${project.buildDir.getAbsolutePath()}/intermediates/runtime_symbol_list/${getBuildType()}/R.txt"
        def file=new File(path)
        List<String> ids=readIdList(file)

        println("createDiffMethod r=${path} exist=${file.exists()} len=${ids.size()}")
*/
        DiffAnalyzer.getInstance().reset()
//        DiffAnalyzer.getInstance().setResIdLines(ids)

        //这里读取差异方法,并且将差异方法封装
        DiffAnalyzer.readClasses(currentDir, DiffAnalyzer.CURRENT)
        DiffAnalyzer.readClasses(branchDir, DiffAnalyzer.BRANCH)
        DiffAnalyzer.getInstance().diff() //调用差别方法
        println("excludeMethod before diff.size=${DiffAnalyzer.getInstance().getDiffList().size()}") //打印差别方法的数量

        //excludeMethod  这里获取配置删除不需要检测的方法名称
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


    List<String> readIdList(File file) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader fis = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fis.readLine()) != null) {
                if (line.contains("0x7f"))
                    list.add(line)
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    List<String> getDiffFiles(String diff) {
        List<String> diffFiles = new ArrayList<>()
        if (diff == null || diff == '') {
            return diffFiles
        }
        String[] strings = diff.split("\n")
        def classes = "/classes/"
        strings.each {
            if (it.endsWith('.class')) {
                String classPath = it.substring(it.indexOf(classes) + classes.length())
                if (isInclude(classPath)) {
                    if (jacocoExtension.excludeClass != null) {
                        boolean exclude = jacocoExtension.excludeClass.call(it)
                        if (!exclude) {
                            diffFiles.add(it)
                        }
                    } else {
                        diffFiles.add(it)
                    }
                }
            }
        }
        return diffFiles
    }

    def isInclude(String classPath) {
        List<String> includes = jacocoExtension.includes
        for (String str : includes) {
            if (classPath.startsWith(str.replaceAll("\\.", "/"))) {
                return true
            }
        }
        return false
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


    def saveUrlAs(String url)
    {
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
    def  WriteFile4InputStream(InputStream inputStream)
    {
        //默认为flase 即失败
        boolean result = false;
        try {
            OutputStream os = new FileOutputStream(jacocoExtension.execDir+"/code.ec");
            os.write(inputStream.readAllBytes());
            os.close();
            result = true;
        }catch (IOException e)
        {
            e.printStackTrace();
            result = false;
        }
       if(result){
           println ("the ec file is down ok!")
       }else{
           println ("the ec file is down error!")
       }
    }

    //下载ec数据文件
    def downloadEcData() {
        if (jacocoExtension.execDir == null) {
            jacocoExtension.execDir = "${project.buildDir}/jacoco/code-coverage/"
        }
        def dataDir = jacocoExtension.execDir
        new File(dataDir).mkdirs()

        def host = jacocoExtension.host
        def android = project.extensions.android
        def appName = android.defaultConfig.applicationId.replace(".","")
        def versionCode = android.defaultConfig.versionCode
//        http://10.10.17.105:8080/WebServer/JacocoApi/queryEcFile?appName=dealer&versionCode=100

        def curl = "curl ${host}/WebServer/JacocoApi/queryEcFile?appName=${appName}&versionCode=${versionCode}"
        println "curl = ${curl}"
        def text = curl.execute().text
        println "queryEcFile = ${text}"
        text = text.substring(text.indexOf("[") + 1, text.lastIndexOf("]")).replace("]", "")

        println "paths=${text}"

        if ("".equals(text)) {
            return
        }
        String[] paths = text.split(',')
        println "下载executionData 文件 length=${paths.length}"

        if (paths != null && paths.size() > 0) {
            for (String path : paths) {
                path = path.replace("\"", '')
                def name = path.substring(path.lastIndexOf("/") + 1)
                println "${path}"
                def file = new File(dataDir, name)
                if (file.exists() && file.length() > 0) //存在
                    continue
                println "downloadFile ${host}${path}"
                println "execute curl -o ${file.getAbsolutePath()} ${host}${path}"

                "curl -o ${file.getAbsolutePath()} ${host}${path}".execute().text
            }
        }
        println "downloadData 下载完成"
    }


    //获取分支差异数据
   def getdiff(){
        println "正在获取diff平台分支不同数据"
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

    def getBuildType() {
        def taskNames = project.gradle.startParameter.taskNames
        for (tn in taskNames) {
            if (tn.startsWith("assemble")) {
                return tn.replaceAll("assemble", "").toLowerCase()
            }
        }
        return ""
    }
}