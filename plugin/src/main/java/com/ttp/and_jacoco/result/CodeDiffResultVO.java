package com.ttp.and_jacoco.result;


import java.util.List;

/**
 * @date:2021/1/9
 * @className:CodeDiffResultVO
 * @author:Administrator
 * @description: 差异代码结果集
 */
public class CodeDiffResultVO {

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * java文件
     */
    private String classFile;

    /**
     * 类中的方法
     */
    private List<MethodInfoResultVO> methodInfos;
    /**
     * 修改类型
     */
    private String type;
    /**
     * 变更行
     */
    private List<ChangeLineVO> lines;


    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getClassFile() {
        return classFile;
    }

    public void setClassFile(String classFile) {
        this.classFile = classFile;
    }

    public List<MethodInfoResultVO> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(List<MethodInfoResultVO> methodInfos) {
        this.methodInfos = methodInfos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ChangeLineVO> getLines() {
        return lines;
    }

    public void setLines(List<ChangeLineVO> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "CodeDiffResultVO{" +
                "moduleName='" + moduleName + '\'' +
                ", classFile='" + classFile + '\'' +
                ", methodInfos=" + methodInfos +
                ", type='" + type + '\'' +
                ", lines=" + lines +
                '}';
    }
}
