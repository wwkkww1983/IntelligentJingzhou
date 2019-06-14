package com.zack.intelligent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zack.intelligent.Constants;
import com.zack.intelligent.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-15.
 */

public class SharedUtils {

    private static String Name = "intelligent";


    private static SharedPreferences getShared() {
        SharedPreferences sp = App.getContext().getSharedPreferences(Name,
                Context.MODE_PRIVATE);
        return sp;
    }

    public static Editor getEditor() {
        return getShared().edit();
    }

    public static void putString(String key, String content) {
        getEditor().putString(key, content).commit();
    }

    public static String getString(String key) {
        return getShared().getString(key, "");
    }

    public static void putFloat(String key, float content) {
        getEditor().putFloat(key, content).apply();
    }

    public static Float getFloat(String key) {
        return getShared().getFloat(key, 0f);
    }

    public static void putInt(String key, int val) {
        getEditor().putInt(key, val).apply();
    }

    public static int getInt(String key) {
        return getShared().getInt(key, 0);
    }

    public static void putBoolean(String key, boolean val) {
        getEditor().putBoolean(key, val).apply();
    }

    public static boolean getBoolean(String key) {
        return getShared().getBoolean(key, false);
    }

    public static boolean saveArray(List<String> list) {
        Editor editor = getShared().edit();
        editor.putInt("list_size", list.size());

        for (int i = 0; i < list.size(); i++) {
            editor.remove("Status_" + i);
            editor.putString("Status_" + i, list.get(i));
        }
        return editor.commit();
    }

    public static List<String> loadArray() {
        int size = getShared().getInt("list_size", 0);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(getShared().getString("Status_" + i, ""));
        }
        return list;
    }

    public static void saveSerialPath(String path) {
        getEditor().putString("serial_path", path).apply();
    }

    public static String getSerialPath() {

        return getShared().getString("serial_path", "");
    }

    public static void savePathPos(int pos) {
        getEditor().putInt("device_position", pos).apply();
    }

    public static int getPathPos() {
        return getShared().getInt("device_position", 0);
    }

    public static void saveBaudrate(String baudrate) {
        getEditor().putString("serial_baudrate", baudrate).apply();
    }

    public static String getBaudrate() {
        return getShared().getString("serial_baudrate", "-1");
    }

    public static void saveBaudPos(int pos) {
        getEditor().putInt("baudrate_position", pos).apply();
    }

    public static int getBaudPos() {
        return getShared().getInt("baudrate_position", 0);
    }

    public static void saveSelectedCabPosition(int val) {
        getEditor().putInt("cab_position", val).apply();
    }

    public static int getSelectedCabPosition() {
        return getShared().getInt("cab_position", 0);
    }

    public static void saveGunCabNo(String val) {
        getEditor().putString("cab_no", val).apply();
    }

    public static String getGunCabNo() {
        return getShared().getString("cab_no", "1");
    }

    public static void saveGunCabId(String val) {
        getEditor().putString("cab_id", val).apply();
    }

    public static String getGunCabId() {
        return getShared().getString("cab_id", "");
    }

    public static void saveGunCabType(int val) {
        getEditor().putInt("cab_type", val).apply();
    }

    public static int getGunCabType() {
        return getShared().getInt("cab_type", 1001);
    }

    public static void saveRoomName(String val) {
        getEditor().putString("room_name", val).apply();
    }

    public static String getRoomName() {
        return getShared().getString("room_name", "监控室名称");
    }

    public static void saveRoomNo(String val) {
        getEditor().putString("room_no", val).apply();
    }

    public static String getRoomNo() {
        return getShared().getString("room_no", "001");
    }

    public static void saveLeaderName(String val) {
        getEditor().putString("leader_name", val).apply();
    }

    public static String getLeaderName() {
        return getShared().getString("leader_name", "领导姓名");
    }

    public static void saveLeaderNo(String val) {
        getEditor().putString("leader_no", val).apply();
    }

    public static String getLeaderNo() {
        return getShared().getString("leader_no", "001");
    }

    public static void saveServerIp(String url) {
        getEditor().putString("server_ip", url).apply();
    }

    public static String getServerIp() {
        return getShared().getString("server_ip", Constants.IP);
    }

    public static void saveServerPort(String url) {
        getEditor().putString("server_port", url).apply();
    }

    public static String getServerPort() {
        return getShared().getString("server_port", Constants.PORT);
    }

    /**
     * 枪库id
     * @param roomId
     */
    public static void saveRoomId(String roomId) {
        getEditor().putString("room_id", roomId).apply();
    }

    public static String getRoomId() {
        return getShared().getString("room_id", "");
    }

    //代领枪弹
    public static void saveMultiple(boolean isMutiple) {
        getEditor().putBoolean("is_multiple", isMutiple).apply();
    }

    public static boolean getMultiple() {
        return getShared().getBoolean("is_multiple", false);
    }

    // 指纹仪
    public static void saveFingerOpen(boolean isOpen){
        getEditor().putBoolean("finger_open", isOpen).apply();
    }

    public static boolean getFingerOpen(){
       return getShared().getBoolean("finger_open", true);
    }

    // 指静脉
    public static void saveVeinOpen(boolean isOpen){
        getEditor().putBoolean("vein_open", isOpen).apply();
    }

    public static boolean getVeinOpen(){
        return getShared().getBoolean("vein_open", false);
    }
    // 虹膜
    public static void saveIrisOpen(boolean isOpen){
        getEditor().putBoolean("iris_open", isOpen).apply();
    }

    public static boolean getIrisOpen(){
        return getShared().getBoolean("iris_open", false);
    }

    // 人脸识别
    public static void saveFaceOpen(boolean isOpen){
        getEditor().putBoolean("face_open", isOpen).apply();
    }

    public static boolean getFaceOpen(){
        return getShared().getBoolean("face_open", false);
    }
   //    系统时间
    public static void saveSystemTime(long time){
        getEditor().putLong("system_time",time).apply();
    }
    //获取系统时间
    public static long getSystemTime(){
        return getShared().getLong("system_time", System.currentTimeMillis());
    }
    //酒精检测
    public static void saveAlcoholDetect(boolean val){
        getEditor().putBoolean("alcohol_detect", val).apply();
    }

    public static boolean getAlcoholDetect(){
        return getShared().getBoolean("alcohol_detect",false);
    }

    //温湿度检测
    public static void saveHumitureOpen(boolean val){
        getEditor().putBoolean("humiture_open", val).apply();
    }
    public static boolean getHumitureOpen(){
        return getShared().getBoolean("humiture_open",false);
    }

    public static void saveOpenCabStatus(int status){
        getEditor().putInt("open_cab_status", status).apply();
    }

    public static int getOpenCabStatus(){
        return getShared().getInt("open_cab_status", 0);
    }

    public static void saveOperGunStatus(int status){
        getEditor().putInt("oper_gun_status", status).apply();
    }

    public static int getOperGunStatus(){
        return getShared().getInt("oper_gun_status", 0);
    }

    public static void saveOutTimeStatus(int status){
        getEditor().putInt("out_time_status", status).apply();
    }

    public static int getOutTimeStatus(){
        return getShared().getInt("out_time_status", 0);
    }

    public static void savePowerStatus(int status){
        getEditor().putInt("power_status", status).apply();
    }

    public static int getPowerStatus(){
        return getShared().getInt("power_status", 0);
    }

    public static void saveNetworkStatus(int status){
        getEditor().putInt("network_status", status).apply();
    }

    public static int getNetworkStatus(){
        return getShared().getInt("network_status", 0);
    }

    public static void saveBackupOpenCabStatus(int status){
        getEditor().putInt("backup_open_status", status).apply();
    }

    public static int getBackupOpenCabStatus(){
        return getShared().getInt("backup_open_status", 0);
    }

    public static void saveBackup2OpenCabStatus(int status){
        getEditor().putInt("backup_open2_status", status).apply();
    }

    public static int getBackup2OpenCabStatus(){
        return getShared().getInt("backup_open2_status", 0);
    }

    public static void saveTempStatus(int status){
        getEditor().putInt("temp_status", status).apply();
    }

    public static int getTempStatus(){
        return getShared().getInt("temp_status", 0);
    }
    //酒精浓度报警
    public static void saveAlcoholStatus(int status){
        getEditor().putInt("alcohol_status", status).apply();
    }

    public static int getAlcoholStatus(){
        return getShared().getInt("alcohol_status", 0);
    }

    public static void setFingerLogin(boolean fingerLogin){
        getEditor().putBoolean("finger_login",fingerLogin).apply();
    }

    public static boolean getFingerLogin(){
        return getShared().getBoolean("finger_login", true);
    }

    public static void setUserLogin(boolean userLogin){
        getEditor().putBoolean("user_login", userLogin).apply();
    }

    public  static boolean getUserLogin(){
        return getShared().getBoolean("user_login",false);
    }

    public static void setVibration(int val){
        getEditor().putInt("vibration", val).apply();
    }

    public  static int getVibration(){
        return getShared().getInt("vibration",0);
    }
    //设置报警开关
    public static void setAlarmOpen(boolean val){
        getEditor().putBoolean("alarm_open", val).apply();
    }

    //获取报警开关状态
    public  static boolean getAlarmOpen(){
        return getShared().getBoolean("alarm_open",false);
    }

    //生物特征登录验证
    public static void setBioLogin(boolean val){
        getEditor().putBoolean("bio_login", val).apply();
    }

    public  static boolean getBioLogin(){
        return getShared().getBoolean("bio_login",false);
    }
    //是否打开抓拍
    public static void setOpenCapture(boolean isCapture){
        getEditor().putBoolean("open_capture", isCapture).apply();
    }

    public static boolean getOpenCapture(){
        return getShared().getBoolean("open_capture", false);
    }

    //是否在抓拍
    public static void setIsCapturing(boolean isCapture){
        getEditor().putBoolean("is_capture", isCapture).apply();
    }

    public static boolean getIsCapturing(){
        return getShared().getBoolean("is_capture", false);
    }

    //是否综合柜
    public static void setIsSynthesisCab(boolean val){
        getEditor().putBoolean("is_synthesis", val).apply();
    }

    public static Boolean getIsSynthesisCab(){
        return getShared().getBoolean("is_synthesis", false);
    }
    //左柜门编号
    public static void saveLeftCabNo(String val){
        getEditor().putString("left_cab_no", val).apply();
    }

    public static String getLeftCabNo(){
        return getShared().getString("left_cab_no", "0");
    }
    //右柜门编号
    public static void saveRightCabNo(String val){
        getEditor().putString("right_cab_no", val).apply();
    }

    public static String getRightCabNo(){
        return getShared().getString("right_cab_no", "0");
    }
    //保存值班领导id
    public static void saveDutyLeaderId(String manageId){
        getEditor().putString("duty_leader_id", manageId).apply();
    }

    public static String getDutyLeaderId(){
        return getShared().getString("duty_leader_id", "");
    }

    //保存第一名值班管理员id
    public static void saveDutyManagerId(String manageId){
        getEditor().putString("duty_manage_id", manageId).apply();
    }

    public static String getDutyManagerId(){
        return getShared().getString("duty_manage_id", "");
    }

    //保存第二名值班管理员id
    public static void saveDutyManagerId2(String manageId){
        getEditor().putString("duty_manage_id2", manageId).apply();
    }

    public static String getDutyManagerId2(){
        return getShared().getString("duty_manage_id2", "");
    }

    // 温度
    public static void saveTemperatureValue(String temp){
        getEditor().putString("temperature_value", temp).apply();
    }

    public static String getTemperatureValue(){
        return getShared().getString("temperature_value", "23.00");
    }
    // 湿度
    public static void saveHumidityValue(String manageId){
        getEditor().putString("humidity_value", manageId).apply();
    }

    public static String getHumidityValue(){
        return getShared().getString("humidity_value", "60.00");
    }

    // 第一识别方式
    public static void saveFirstVerify(int val){
        getEditor().putInt("first_verify", val).apply();
    }

    public static int getFirstVerify(){
        return getShared().getInt("first_verify", Constants.DEVICE_FINGER);
    }

    // 第二识别方式
    public static void saveSecondVerify(int val){
        getEditor().putInt("second_verify", val).apply();
    }

    public static int getSecondVerify(){
        return getShared().getInt("second_verify", Constants.DEVICE_FINGER);
    }
    // 第三识别方式
    public static void saveThirdVerify(int val){
        getEditor().putInt("third_verify", val).apply();
    }

    public static int getThirdVerify(){
        return getShared().getInt("third_verify", Constants.DEVICE_FINGER);
    }

    // 解除报警 交接班 验证方式
    public static void saveBiosVerify(int val){
        getEditor().putInt("bios_verify", val).apply();
    }

    public static int getBiosVerify(){
        return getShared().getInt("bios_verify", Constants.DEVICE_FINGER);
    }

    // 保存是否第一次验证
    public static void saveIsSecondVerify(boolean val){
        getEditor().putBoolean("is_second_verify", val).apply();
    }

    //获取是否第一次验证
    public static boolean getIsSecondVerify(){
        return getShared().getBoolean("is_second_verify", false);
    }
}
