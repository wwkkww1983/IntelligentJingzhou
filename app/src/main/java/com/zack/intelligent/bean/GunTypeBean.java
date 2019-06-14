package com.zack.intelligent.bean;

/**
 * 枪弹类型
 */

public class GunTypeBean {

    private String id;
    private String type; //类型名称
    private int typeno; //类型编号
    private int typenum; //类型 1：枪支 2：子弹 3：弹匣

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeno() {
        return typeno;
    }

    public void setTypeno(int typeno) {
        this.typeno = typeno;
    }

    public int getTypenum() {
        return typenum;
    }

    public void setTypenum(int typenum) {
        this.typenum = typenum;
    }
}
