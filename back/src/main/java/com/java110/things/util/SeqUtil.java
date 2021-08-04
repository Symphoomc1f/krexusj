package com.java110.things.util;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * @ClassName SeqUtil
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 21:36
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class SeqUtil {

    private static int MACHINE_SEQ = 1;

    /**
     * 获取流水
     *
     * @return
     */
    public static String getId() {
        return UUID.randomUUID().toString();
    }

    public static synchronized String getMachineSeq() {
        MACHINE_SEQ++;
        DecimalFormat decimalFormat = new DecimalFormat("000");
        String machineSeq = DateUtil.getCurrentDay() + "" + DateUtil.getCurrentMin() + "" + decimalFormat.format(MACHINE_SEQ);
        return machineSeq;
    }

    public static void main(String[] args) {
        System.out.printf(getMachineSeq());
    }
}
