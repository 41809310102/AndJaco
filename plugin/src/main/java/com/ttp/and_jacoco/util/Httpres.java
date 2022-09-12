package com.ttp.and_jacoco.util;

import java.util.ArrayList;
import java.util.List;

public class Httpres {
    private  int code;
    private  String msg;
    private  String uniqueData;
    private List<Object> data = new ArrayList<Object>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUniqueData() {
        return uniqueData;
    }

    public void setUniqueData(String uniqueData) {
        this.uniqueData = uniqueData;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Httpres{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", uniqueData='" + uniqueData + '\'' +
                ", data=" + data +
                '}';
    }
}
