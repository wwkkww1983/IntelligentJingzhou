package com.zack.intelligent.event;

/**
 * Created by Administrator on 2017-09-05.
 */

public class EventConsts {

    public static final String GET_CABS_SUCCESS ="1001"; //获取枪柜数据成功
    public static final String GET_MEMBERS_SUCCESS ="1002"; //获取警员数据成功
    public static final String SYNC_TIME_SUCCESS ="1003"; //同步时间成功
    public static final String SYNC_TIME_FAILED ="1004"; //同步时间失败
    public static final String UPDATE_CABS_DATA ="1005"; //更新枪柜数据
    public static final String EVENT_POWER_NORMAL ="2001"; //市电电源正常
    public static final String EVENT_POWER_ABNORMAL ="2002"; //备用电源供电

    public static final String EVENT_FINGER_TIME_OUT ="10000";


    public static final String KEEP_CURRENT_STATUS ="0x00";
    public static final String ADJUST_DISTANCE ="0x01";
    public static final String WATCH_MIRROR ="0x02";
    public static final String CLOSE_TO ="0x03";
    public static final String OPEN_EYES ="0x04";
    public static final String DONT_LOOK_AWRY ="0x05";


}
