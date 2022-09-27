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


}
