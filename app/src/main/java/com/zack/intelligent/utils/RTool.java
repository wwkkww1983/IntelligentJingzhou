package com.zack.intelligent.utils;


import android.util.Log;

import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.GunTypeBean;

import java.util.List;
import java.util.Map;

/**
 * 转换类
 */

public class RTool {

    public static String convertCabType(int cabType) {

        String type = null;
        switch (cabType) {
            case 1:
                type = "长枪柜";
                break;
            case 2:
                type = "弹药柜";
                break;
            case 3:
                type = "综合柜";
                break;
            default:
                type = "短枪柜";
                break;
        }
        return type;
    }

    public static String convertSubCabType(int type) {
        String SubCabType = null;
        switch (type) {
            case 1:
                SubCabType = "子弹位置";
                break;
            case 2:
                SubCabType = "弹夹位置";
                break;
            case 3:
                SubCabType = "枪支位置";
                break;
            default:
                break;
        }
        return SubCabType;
    }

    public static String convertObjectStatus(int status) {

        String ObjectStatus = null;
        switch (status) {
            case 1:
                ObjectStatus = "正常在库";
                break;
            case 2:
                ObjectStatus = "出警领出";
                break;
            case 3:
                ObjectStatus = "保养领出";
                break;
            case 4:
                ObjectStatus = "紧急出警领出";
                break;
            case 5:
                ObjectStatus = "临时存放";
                break;
            case 99:
                ObjectStatus = "异常不在位";
                break;
            default:
                break;
        }
        return ObjectStatus;
    }

    public static int convertobjTypeToImageId(int type) {
        int objType = 0;
        switch (type) {
            case 1002:
                objType = R.drawable.ic_pistol_54;
                break;
            case 1003:
                objType = R.drawable.ic_pistol_64;
                break;
            case 1004:
                objType = R.drawable.ic_pistol_77;
                break;
            case 1005:
                objType = R.drawable.ic_pistol_92;
                break;
            case 1006:
                objType = R.drawable.ic_pistol_revolver;
                break;
            case 1007:
                objType = R.drawable.ic_riot_gun_38;
                break;
            case 1008:
                objType = R.drawable.ic_riot_gun_97_1;
                break;
            case 1009:
                objType = R.drawable.ic_rifle_56;
                break;
            case 1010:
                objType = R.drawable.ic_sniper_rifle_85;
                break;
            case 1011:
                objType = R.drawable.ic_rifle_95;
                break;
            case 1012:
                objType = R.drawable.ic_sniper_rifle_88;
                break;
            case 1013:
                objType = R.drawable.ic_submachine_gun_56;
                break;
            case 1014:
                objType = R.drawable.ic_submachine_gun_79;
                break;
            case 1015:
                objType = R.drawable.ic_class_machine_guns;
                break;
            case 1016:
                objType = R.drawable.ic_class_machine_guns;
                break;
            default:
                objType = R.drawable.short_gun_null;
                break;
        }
        return objType;
    }

    /*
    objTypyId转为灰色图片资源
     */
    public static int convertGunToGreyImageId(int type) {
        int objType = 0;
        switch (type) {
            case 1002:
                objType = R.drawable.ic_pistol_54_grey;
                break;
            case 1003:
                objType = R.drawable.ic_pistol_64_grey;
                break;
            case 1004:
                objType = R.drawable.ic_pistol_77_grey;
                break;
            case 1005:
                objType = R.drawable.ic_pistol_92_grey;
                break;
            case 1006:
                objType = R.drawable.ic_pistol_revolver_grey;
                break;
            case 1007:
                objType = R.drawable.ic_riot_gun_38_grey;
                break;
            case 1008:
                objType = R.drawable.ic_riot_gun_97_1_grey;
                break;
            case 1009:
                objType = R.drawable.ic_rifle_56_grey;
                break;
            case 1010:
                objType = R.drawable.ic_sniper_rifle_85_grey;
                break;
            case 1011:
                objType = R.drawable.ic_rifle_95_grey;
                break;
            case 1012:
                objType = R.drawable.ic_sniper_rifle_88_grey;
                break;
            case 1013:
                objType = R.drawable.ic_submachine_gun_56_grey;
                break;
            case 1014:
                objType = R.drawable.ic_submachine_gun_79_grey;
                break;
            case 1015:
                objType = R.drawable.ic_class_machine_guns_grey;
                break;
            case 1016:
                objType = R.drawable.ic_class_machine_guns_grey;
                break;
            default:
                objType = R.drawable.ic_pistol_54_grey;
                break;
        }
        return objType;
    }

    public static String convertObjectType(int type) {
        List<GunTypeBean> gunTypeList = DataCache.gunTypeBeanList;
        if(!gunTypeList.isEmpty()){
            for (int i = 0; i < gunTypeList.size(); i++) {
                GunTypeBean gunTypeBean = gunTypeList.get(i);
                int typeno = gunTypeBean.getTypeno();
                if(typeno == type){
                    return gunTypeBean.getType();
                }
            }
        }
        return "";
    }
//    public static String convertObjectType(int type) {
//        String objType = null;
//        switch (type) {
//            case 1002:
//                objType = "54式手枪";
//                break;
//            case 1003:
//                objType = "64式手枪";
//                break;
//            case 1004:
//                objType = "77式手枪";
//                break;
//            case 1005:
//                objType = "92式手枪";
//                break;
//            case 1006:
//                objType = "左轮手枪";
//                break;
//            case 1007:
//                objType = "38mm防暴枪";
//                break;
//            case 1008:
//                objType = "97-1式防暴枪";
//                break;
//            case 1009:
//                objType = "56式步枪";
//                break;
//            case 1010:
//                objType = "85狙击步枪";
//                break;
//            case 1011:
//                objType = "95式自动步枪";
//                break;
//            case 1012:
//                objType = "88式狙击步枪";
//                break;
//            case 1013:
//                objType = "56式冲锋枪";
//                break;
//            case 1014:
//                objType = "79式微冲";
//                break;
//            case 1015:
//                //Class machine guns
//                objType = "95式班用机枪";
//                break;
//            case 1016:
//                //Class machine guns
//                objType = "18.4mm防暴枪";
//                break;
//            case 2001:
//                objType = "9mm手枪弹";
//                break;
//            case 2002:
//                objType = "5.54mm手枪弹";
//                break;
//            case 2003:
//                objType = "5.8mm手枪弹";
//                break;
//            case 2004:
//                objType = "5.56mm步枪弹";
//                break;
//            case 2005:
//                objType = "5.8mm步枪弹";
//                break;
//            case 2006:
//                objType = "7.62mm步枪弹";
//                break;
//            case 2007:
//                objType = "防爆枪弹";
//                break;
//            case 3001:
//                objType = "54式手枪弹夹";
//                break;
//            case 3002:
//                objType = "64式手枪弹夹";
//                break;
//            case 3003:
//                objType = "77式手枪弹夹";
//                break;
//            case 3004:
//                objType = "92式手枪弹夹";
//                break;
//            case 3005:
//                objType = "转轮手枪弹夹";
//                break;
//            case 3006:
//                objType = "38mm防暴枪弹夹";
//                break;
//            case 3007:
//                objType = "97-1式防暴枪弹夹";
//                break;
//            case 3008:
//                objType = "56式步枪弹夹";
//                break;
//            case 3009:
//                objType = "85狙击步枪弹夹";
//                break;
//            case 3010:
//                objType = "95式自动步枪弹夹";
//                break;
//            case 3011:
//                objType = "88式狙击步枪弹夹";
//                break;
//            case 3012:
//                objType = "56式冲锋枪弹夹";
//                break;
//            case 3013:
//                objType = "79式微冲弹夹";
//                break;
//            case 3014:
//                objType = "95式班用机枪弹夹";
//                break;
//            default:
//                break;
//        }
//        return objType;
//    }

    public static String convertRank(String rank) {
        String sRank = null;
        switch (rank) {
            case "0":
                sRank = "无警衔";
                break;
            case "JY_1":
                sRank = "一级警员";
                break;
            case "JY2":
                sRank = "二级警员";
                break;
            case "JY3":
                sRank = "三级警员";
                break;
            case "JS_1":
                sRank = "一级警司";
                break;
            case "JS_2":
                sRank = "二级警司";
                break;
            case "JS_3":
                sRank = "三级警司";
                break;
            case "JD_1":
                sRank = "一级警督";
                break;
            case "JD_2":
                sRank = "二级警督";
                break;
            case "JD_3":
                sRank = "三级警督";
                break;
            case "JJ_1":
                sRank = "一级警监";
                break;
            case "JJ_2":
                sRank = "二级警监";
                break;
            case "JJ_3":
                sRank = "三级警监";
                break;

            default:
                break;
        }
        return sRank;

    }

    public static String convertDeparty(String departy) {
        String sDeparty = null;
        switch (departy) {
            case "DY":
                sDeparty = "中共党员";
                break;
            case "TY":
                sDeparty = "共青团员";
                break;
            case "QZ":
                sDeparty = "群众";
                break;
            default:
                sDeparty = "";
                break;
        }
        return sDeparty;
    }

    public static String convertDeparty(int departy) {
        String sDeparty = null;
        switch (departy) {
            case 1:
                sDeparty = "中共党员";
                break;
            case 2:
                sDeparty = "共青团员";
                break;
            case 3:
                sDeparty = "群众";
                break;

            default:
                break;
        }
        return sDeparty;
    }

    public static String convertPoliceType(int type) {
        String PoliceType = null;
        switch (type) {
            case 0:
                PoliceType = "超级管理员";
                break;
            case 1:
                PoliceType = "枪管员";
                break;
            case 2:
                PoliceType = "警员";
                break;
            case 3:
                PoliceType = "领导";
                break;

            default:
                break;
        }
        return PoliceType;
    }

    /**
     * 0、超级管理员   ROLE_ADMINISTRATOR
     * 1、枪械管理员   ROLE_GUN_MAN
     * 2、普通警员     ROLE_POLICE_CONSTABLE
     * 3、领导        ROLE_BRANCH_OFFICE_LEADER
     */
    public static String convertPoliceType(String type) {
        String PoliceType = null;
        switch (type) {
            case "ROLE_ADMINISTRATOR":
                PoliceType = "超级管理员";
                break;
            case "ROLE_GUN_MAN":
                PoliceType = "枪械管理员";
                break;
            case "ROLE_POLICE_CONSTABLE":
                PoliceType = "普通警员";
                break;
            case "ROLE_BRANCH_OFFICE_LEADER":
                PoliceType = "领导";
                break;
            default:
                PoliceType = "";
                break;
        }
        return PoliceType;
    }

    public static String convertBioType(int type) {
        String BioType = null;
        switch (type) {
            case 1:
                BioType = "左手大拇指";
                break;
            case 2:
                BioType = "左手食指";
                break;
            case 3:
                BioType = "左手中指";
                break;
            case 4:
                BioType = "左手无名指";
                break;
            case 5:
                BioType = "左手小指";
                break;
            case 6:
                BioType = "右手大拇指";
                break;
            case 7:
                BioType = "右手食指";
                break;
            case 8:
                BioType = "右手中指";
                break;
            case 9:
                BioType = "右手无名指";
                break;
            case 10:
                BioType = "右手小指";
                break;
            case 11:
                BioType = "左眼虹膜";
                break;
            case 12:
                BioType = "右眼虹膜";
                break;
            case 13:
                BioType = "其他";
                break;

            default:
                break;
        }
        return BioType;
    }

    public static String convertBioCheck(int check) {
        String BioCheck = null;
        switch (check) {
            case 1:
                BioCheck = "正常验证";
                break;
            case 2:
                BioCheck = "非正常验证";
                break;

            default:
                break;
        }
        return BioCheck;
    }

    /**
     * 1.领取枪支
     * 2.领取弹/弹夹
     * 3.存放枪支
     * 4.存放弹/弹夹
     *
     * @param type
     * @return
     */
    public static String convertTaskItemType(int type) {
        String itemType = null;
        switch (type) {
            case 1:
                itemType = "领取枪支";
                break;
            case 2:
                itemType = "领取弹/弹夹";
                break;
            case 3:
                itemType = "存放枪支";
                break;
            case 4:
                itemType = "存放弹/弹夹";
                break;
            default:
                break;
        }
        return itemType;
    }

    public static String convertTaskItemStatus(int type, int status) {

        String taskStatus = null;
        switch (status) {
            case 1:
                if (type == 1 || type == 2) {
                    taskStatus = "领取中";
                }
                break;
            case 2:
                taskStatus = "执行中";
                break;
            case 3:
                if (type == 1 || type == 2) { //领取
                    taskStatus = "归还中";
                } 
                break;
            case 4:
                taskStatus = "完成";
                break;
            default:
                break;
        }
        return taskStatus;
    }

    /**
     * 1.紧急出警任务
     * 2.出警任务
     * 3.保养任务
     * 4.入库任务
     * 5.报废任务
     * 6.临时存放任务
     *
     * @param type
     * @return
     */
    public static String convertTaskType(int type) {
        String taskType = "";
        switch (type) {
            case 1:
                taskType = "紧急出警";
                break;
            case 2:
                taskType = "出警";
                break;
            case 3:
                taskType = "保养";
                break;
            case 4:
                taskType = "入库";
                break;
            case 5:
                taskType = "报废";
                break;
            case 6:
                taskType = "临时存放";
                break;
            case 7:
                taskType = "临时存放";
                break;

            default:
                taskType = "";
                break;
        }
        return taskType;
    }

    /**
     * * /**
     * 1.涉黄
     * 2.涉赌
     * 3.涉毒
     * 4.刑事案件
     * 99.其它
     *
     * @param subType 子任务类型
     * @return a-z
     */
    public static String convertTaskSubType(int subType) {
        String taskType = "";
        switch (subType) {
            case 1:
                taskType = "涉黄";
                break;
            case 2:
                taskType = "涉赌";
                break;
            case 3:
                taskType = "涉毒";
                break;
            case 4:
                taskType = "刑事案件";
                break;
            case 99:
                taskType = "其它";
                break;
            default:
                taskType = "";
                break;
        }
        return taskType;
    }

    public static String convertTaskStatus(int status) {
        /**
         * 1.未审批
         * 2.已审批
         * 3.审批不通过
         * 4.执行中
         * 5.结束
         */
        switch (status) {
            case 1:
                return "未审批";
            case 2:
                return "已审批";
            case 3:
                return "审批不通过";
            case 4:
                return "执行中";
            case 5:
                return "结束";
            default:
                return "";
        }
    }

    public static String convertOperType(int logType) {
        String type = null;
        switch (logType) {
            case 1:
                type = "领取枪支";
                break;
            case 2:
                type = "归还枪支";
                break;
            case 3:
                type = "领取弹药";
                break;
            case 4:
                type = "归还弹药";
                break;
            default:
                type = "";
                break;
        }
        return type;
    }

    public static String convertAlarmLogSubType(int logType) {
        String type = null;
        switch (logType) {
            case 1:
                type = "非正常开启柜门";
                break;
            case 2:
                type = "非正常领取枪支或弹药";
                break;
            case 3:
                type = "枪支或弹药未按时归还";
                break;
            case 4:
                type = "柜门超时未锁闭";
                break;
            case 5:
                type = "智能柜断电";
                break;
            case 6:
                type = "备用方式开柜门";
                break;
            case 7:
                type = "网络断开";
                break;
            case 8:
                type = "温湿度异常";
                break;
            case 9:
                type = "酒精浓度异常";
                break;
            default:
                type = "";
                break;
        }
        return type;
    }

    /**
     * 1.领枪任务
     * 2.紧急领枪任务
     * 3.申请领枪任务
     * 4.保养任务
     * 5.报废任务
     * 6.临时存放任务
     * 7.值班管理
     * 8.指纹管理
     */
    public static String convertOperTaskType(int logTaskType) {
        String typeStr = "";
        switch (logTaskType) {
            case 1:
                typeStr = "领枪";
                break;
            case 2:
                typeStr = "紧急领枪";
                break;
            case 3:
                typeStr = "申请领枪";
                break;
            case 4:
                typeStr = "枪弹保养";
                break;
            case 5:
                typeStr = "枪弹报废";
                break;
            case 6:
                typeStr = "临时存放";
                break;
            case 7:
                typeStr = "值班管理";
                break;
            case 8:
                typeStr = "指纹管理";
                break;
            default:
                typeStr = "";
                break;
        }
        return typeStr;
    }

    public static String convertLogStatus(int status) {
        switch (status) {
            case 1:
                return "未处理";
            case 2:
                return "处理成功";
            case 3:
                return "处理失败";
            default:
                return "";
        }
    }

    /**
     * 1.注册指纹
     * 2.删除指纹
     * 3.设置值班领导
     * 4.管理员交接班
     * 5.管理员上线
     * 6.管理员离班
     * 7.进入
     * 8.退出
     * 9.正常开启柜门
     * 10.正常打开枪锁
     */
    public static String convertLogSubType(int type) {
        switch (type) {
            case 1:
                return "注册指纹";
            case 2:
                return "删除指纹";
            case 3:
                return "设置值班领导";
            case 4:
                return "管理员交接班";
            case 5:
                return "管理员上线";
            case 6:
                return "管理员离班";
            case 7:
                return "进入";
            case 8:
                return "退出";
            case 9:
                return "正常开启柜门";
            case 10:
                return "正常打开枪锁";
            default:
                return "";
        }
    }

}
