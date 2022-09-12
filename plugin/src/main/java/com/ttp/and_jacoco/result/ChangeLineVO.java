package com.ttp.and_jacoco.result;

/**
 * @ProjectName: code-diff-parent
 * @Package: com.dr.code.diff.vo.result
 * @Description: java类作用描述
 * @Author: duanrui
 * @CreateDate: 2021/6/24 21:32
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2021
 */

public class ChangeLineVO {
    /**
     * 变更类型
     */
    private String type;

    private Integer startLineNum;

    private Integer endLineNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStartLineNum() {
        return startLineNum;
    }

    public void setStartLineNum(Integer startLineNum) {
        this.startLineNum = startLineNum;
    }

    public Integer getEndLineNum() {
        return endLineNum;
    }

    public void setEndLineNum(Integer endLineNum) {
        this.endLineNum = endLineNum;
    }

    @Override
    public String toString() {
        return "ChangeLineVO{" +
                "type='" + type + '\'' +
                ", startLineNum=" + startLineNum +
                ", endLineNum=" + endLineNum +
                '}';
    }
}
