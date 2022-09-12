package org.jacoco.core.data;

import java.util.Arrays;
import java.util.Objects;

public class MethodInfo {
    public String className;
    public String methodName;
    public String desc;
    public String signature;
    public String[] exceptions;
    public String md5;//有方法本体，注解构成的md5

    @Override
    public int hashCode() {
        int result = Objects.hash(className, methodName, desc, signature, md5);
        result = 31 * result + Arrays.hashCode(exceptions);
        return result;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", desc='" + desc + '\'' +
                ", signature='" + signature + '\'' +
                ", exceptions=" + Arrays.toString(exceptions) +
                ", md5='" + md5 + '\'' +
                '}';
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
