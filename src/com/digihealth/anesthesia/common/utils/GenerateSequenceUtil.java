package com.digihealth.anesthesia.common.utils;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.digihealth.anesthesia.common.config.Global;


public class GenerateSequenceUtil {
 
    /** The FieldPosition. */
    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);
 
    /** This Format for format the data to special format. */
    private final static Format dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
 
    /** This Format for format the number to special format. */
    private final static NumberFormat numberFormat = new DecimalFormat("0000");
 
    /** This int is the sequence number ,the default value is 0. */
    private static int seq = 0;
 
    private static final int MAX = 9999;
 
    /**
     * 时间格式生成序列
     * @return String
     */
    public static synchronized String generateSequenceNo() {
  
        Calendar rightNow = Calendar.getInstance();
 
        StringBuffer sb = new StringBuffer();
 
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
 
        numberFormat.format(seq, sb, HELPER_POSITION);
 
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
// System.out.println(sb.toString());
        //logger.info("THE SQUENCE IS :" + sb.toString());
 
        return sb.toString();
    }
    
    /**
     * 根据roomId生成主键
     * @param roomId
     * @return
     */
    public static synchronized String generateSequenceNo(String roomId) {
          
        Calendar rightNow = Calendar.getInstance();
 
        StringBuffer sb = new StringBuffer();
 
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
 
        numberFormat.format(seq, sb, HELPER_POSITION);
 
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
        
        sb.append(roomId);
        
        System.out.println(sb.toString());
        //logger.info("THE SQUENCE IS :" + sb.toString());
 
        return sb.toString();
    }
    
    public static String getRoomId(){
        int rLength = 2;
        String roomId = Global.getConfig("roomId").toString();
        if(StringUtils.isBlank(roomId) || roomId.length()<rLength){
            for (int i = 0; i <= rLength - roomId.length(); i++) {
                roomId = "0"+roomId;
            }
        }
        return roomId;
    }
    
    public static void main(String[] args) {
		GenerateSequenceUtil g = new GenerateSequenceUtil();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
		g.generateSequenceNo();
	}
}
