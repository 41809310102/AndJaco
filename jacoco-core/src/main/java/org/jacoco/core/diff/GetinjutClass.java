package org.jacoco.core.diff;

/**
 * desc:定义被识别插入探针的类
 * actor:hungyi*/
public class GetinjutClass {
    private String classname;
    private String mothod;
    private String desc;
    private String jacodesc;

    public GetinjutClass(String classname, String mothod, String desc, String jacodesc) {
        this.classname = classname;
        this.mothod = mothod;
        this.desc = desc;
        this.jacodesc = jacodesc;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMothod() {
        return mothod;
    }

    public void setMothod(String mothod) {
        this.mothod = mothod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getJacodesc() {
        return jacodesc;
    }

    public void setJacodesc(String jacodesc) {
        this.jacodesc = jacodesc;
    }

    @Override
    public String toString() {
        return "GetinjutClass{" +
                "classname='" + classname + '\'' +
                ", mothod='" + mothod + '\'' +
                ", desc='" + desc + '\'' +
                ", jacodesc='" + jacodesc + '\'' +
                '}';
    }
}
