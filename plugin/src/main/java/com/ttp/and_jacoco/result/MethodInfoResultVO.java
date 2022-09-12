package com.ttp.and_jacoco.result;

import java.util.List;

/**
 * @date:2021/1/9
 * @className:MethodInfoResultVO
 * @author:Administrator
 * @description: 方法对象
 */

public class MethodInfoResultVO {


//    /**
//     * 方法的md5
//     */
//    @ApiModelProperty(name = "md5", value = "方法的md5", dataType = "string", example = "13E2BFB69F7D987A6DB4272400C94E9B")
//    public String md5;
    /**
     * 方法名
     */
    public String methodName;

    /**
     * 方法参数
     */
    public List<String> parameters;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }


    @Override
    public String toString() {
        return "MethodInfoResultVO{" +
                "methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
