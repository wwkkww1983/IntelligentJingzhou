package com.zack.intelligent.bean;

/**
 * Created by Administrator on 2018/6/20.
 */

public class ObjectType {

   private String typeName;
   private int typeId;
   private int typeNum;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(int typeNum) {
        this.typeNum = typeNum;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "ObjectType{" +
                "typeName='" + typeName + '\'' +
                ", typeId=" + typeId +
                ", typeNum=" + typeNum +
                '}';
    }
}
