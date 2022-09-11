package com.ttp.and_jacoco.extension;

public class Methodinfo {
    private String classname;
    private String methodname;
    private String desc;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Methodinfo{" +
                "classname='" + classname + '\'' +
                ", methodname='" + methodname + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
