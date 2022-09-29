package org.jacoco.core.diff;


import org.jacoco.core.data.MethodInfo;
import org.jacoco.core.tools.Juiutil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class DiffAnalyzer {
    public static final int CURRENT = 0X10;
    public static final int BRANCH = 0X11;

    Set<MethodInfo> currentList = new HashSet<>();
    Set<MethodInfo> branchList = new HashSet<>();
    Set<MethodInfo> diffList = new HashSet<>();
   public static List<GetinjutClass> injutlist = new LinkedList<>();
    //com/ttp/newcore/network/CommonDataLoader$4
    Set<String> diffClass = new HashSet<>();

    //int anim abc_tooltip_enter 0x7f01000a
    List<String> resIdLines = new ArrayList<>();

    private static DiffAnalyzer instance;

    public static DiffAnalyzer getInstance() {
        if (instance == null) {
            synchronized (DiffAnalyzer.class) {
                if (instance == null)
                    instance = new DiffAnalyzer();
            }
        }
        return instance;
    }

    public void addMethodInfo(MethodInfo methodInfo, int type) {
        if (type == CURRENT) {
            currentList.add(methodInfo);
        } else {
            branchList.add(methodInfo);
        }
    }

    public void diff() {
        if (!currentList.isEmpty() && !branchList.isEmpty()) {
            for (MethodInfo cMethodInfo : currentList) {
                boolean findInBranch = false;
                for (MethodInfo bMethodInfo : branchList) {
                    if (cMethodInfo.className.equals(bMethodInfo.className)
                            && cMethodInfo.methodName.equals(bMethodInfo.methodName)
                            && cMethodInfo.desc.equals(bMethodInfo.desc)) {
                        if (!cMethodInfo.md5.equals(bMethodInfo.md5)) {
                            diffList.add(cMethodInfo);
                        }
                        findInBranch = true;
                        break;
                    }
                }
                if (!findInBranch) {
                    diffList.add(cMethodInfo);
                }
                diffClass.add(cMethodInfo.className);
            }
        }
    }


   //调用该方法装载不同类数据
    public void creatediff(List<MethodInfo> list){
        if(list.size()== 0 ){
            return;
        }else{
            for(MethodInfo cmethodInfo : list){
                diffList.add(cmethodInfo);
                diffClass.add(cmethodInfo.className);
            }
        }
    }


    public boolean containsMethod(String className, String methodName, String desc) {
        System.out.println("now containsMethod size =>"+diffList.size());
        System.out.println("********************"+"classname:"+className+" methodName:"+methodName+" desc:"+desc);
        for (MethodInfo methodInfo : diffList) {
            if (className.equals(methodInfo.className) && methodInfo.methodName.equals("Class")) {
//                System.out.println("className:"+ className + " methodName:"+ methodName + " desc:"+"Class");
//                System.out.println("DiffList :" +methodInfo.toString());
//                System.out.println("=================================================================================");
                GetinjutClass getinjutClass = new GetinjutClass(className,methodName,desc,"new class");
                injutlist.add(getinjutClass);
                return true;
            }
            //判断无参方法是否一致
            String deschead = desc.substring(0,2);
            if(deschead.equals("()")){
                if( className.equals(methodInfo.className) && methodName.equals(methodInfo.methodName)){
                    GetinjutClass getinjutClass = new GetinjutClass(className,methodName,desc,methodInfo.desc);
                    injutlist.add(getinjutClass);
//                    System.out.println("className:"+ className + " methodName:"+ methodName + " desc:"+deschead);
//                    System.out.println("DiffList :" +methodInfo.toString());
//                    System.out.println("=================================================================================");
                    return true;
                }
            }else{  //考虑到kt文件和java文件的jui表达不兼容问题
                String desjocctran = Juiutil.JacocodescTran(desc);
                if(className.equals(methodInfo.className) && methodName.equals(methodInfo.methodName)
                        &&(desjocctran.equals(methodInfo.desc)||desjocctran.contains(methodInfo.desc))){
                    GetinjutClass getinjutClass = new GetinjutClass(className,methodName,desc,methodInfo.desc);
                    injutlist.add(getinjutClass);
//                    System.out.println("className:"+ className + " methodName:"+ methodName + " desc:"+desjocctran);
//                    System.out.println("DiffList :" +methodInfo.toString());
//                    System.out.println("=================================================================================");
                    return true;
                }
            }
        }
        return false;
    }


    public boolean containsClass(String className){
// com\example\test2\BuildConfig ->  com/example/test2/MainActivity
        boolean res = false;
        for(String s : diffClass){
            if(s.contains(checkname(className))){
              res = true;
              break;
            }
        }
        if(res){
            System.out.println("============================!======================================!=============");
            System.out.println("now is compare class is name====> "+className);
            System.out.println("============================!=======================================!============");
        }
        return res;
    }

    //改造classlist类名描述
    public String checkname(String name){
        String str = "";
        if(name.equals("") || name==null){
            return name;
        }else{
            if(name.contains("\\")){
             str =   name.replace('\\','/');
            }else{
                return name;
            }
        }
        return str;
    }



    public void reset() {
        currentList.clear();
        branchList.clear();
        diffList.clear();
    }

    public Set<MethodInfo> getDiffList() {
        return diffList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (MethodInfo mi : diffList) {
            builder.append(mi.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void readClasses(String dirPath, int type) {
        File file = new File(dirPath);
        if (!file.exists() || file.getName().equals(".git")) {
            return;
        }
        File[] files = file.listFiles();
        for (File classFile : files) {
            if (classFile.isDirectory()) {
                readClasses(classFile.getAbsolutePath(), type);
            } else {
                if (classFile.getName().endsWith(".class")) {
                    try {
                        System.out.println("sssss");
                        doClass(classFile, type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static void doClass(File fileIn, int type) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(fileIn));
            processClass(is, type);
        } finally {
            closeQuietly(is);
        }
    }


    private static void processClass(InputStream classIn, int type) throws IOException {
        ClassReader cr = new ClassReader(classIn);
        ClassVisitor cv = new DiffClassVisitor(Opcodes.ASM5, type);
        cr.accept(cv, 0);
    }

    private static void closeQuietly(Closeable target) {
        if (target != null) {
            try {
                target.close();
            } catch (Exception e) {
                // Ignored.
            }
        }
    }

    public List<String> getResIdLines() {
        return resIdLines;
    }

    public void setResIdLines(List<String> resIdLines) {
        this.resIdLines = resIdLines;
    }

}
