package com.zack.intelligent.bean;


/**
 * 警员指纹信息
 */
public class PoliceBiosBean {

    private String id;
    private String policeId;   //警员ID
    //设备类型:1:指纹，2:指静脉，3:虹膜，4:人脸，5:掌静脉，6其他
    private int deviceType;
    /**
     1、  左手大拇指
     2、  左手食指
     3、  左手中指
     4、  左手无名指
     5、  左手小指
     6、  右手大拇指
     7、  右手食指
     8、  右手中指
     9、  右手无名指
     10、 右手小指
     **/
    private int bioType;       //生物特征类型
    /**
     * 1、正常验证
     * 2、非正常验证(劫持)
     */
    private int bioCheckType;  //生物特征检查方式
    private int fingerprintId; //生物特征Id
    private String key;        //生物特征模版（Base64数据流）
    private String templateType;//生物特征模版类型 (9900)

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPoliceId() {
        return this.policeId;
    }
    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }
    public int getBioType() {
        return this.bioType;
    }
    public void setBioType(int bioType) {
        this.bioType = bioType;
    }
    public int getBioCheckType() {
        return this.bioCheckType;
    }
    public void setBioCheckType(int bioCheckType) {
        this.bioCheckType = bioCheckType;
    }
    public int getFingerprintId() {
        return this.fingerprintId;
    }
    public void setFingerprintId(int fingerprintId) {
        this.fingerprintId = fingerprintId;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getTemplateType() {
        return this.templateType;
    }
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
    public int getDeviceType() {
        return this.deviceType;
    }
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

}
