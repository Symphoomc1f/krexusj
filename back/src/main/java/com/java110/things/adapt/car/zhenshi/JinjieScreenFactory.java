package com.java110.things.adapt.car.zhenshi;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.util.Base64Convert;
import com.java110.things.util.SeqUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 深圳金颉 屏显
 * https://item.taobao.com/item.htm?spm=a1z10.5-c-s.w4002-17920172376.16.375f4dbaFsvPzC&id=575197111280
 *
 * @Author zhouhe
 * @Date 2019/10/14 17:41
 */
public class JinjieScreenFactory {
    private static Logger logger = LoggerFactory.getLogger(JinjieScreenFactory.class);

    /**
     * 0x00 连接 PSWD[6~32] RET + HVR[3] + SVR[3] + CPVR[3]
     * 1.0.0
     * 0x02 修改密码 PSWD[6~32] ACK(成功为 0，失败返回非 0 的值)
     * 0x05 同步时间 Y[2] + M + D + W + H + N + S ACK(成功为 0，失败返回非 0 的值)
     * 0x07 更改通信地址 NDA ACK(成功为 0，失败返回非 0 的值)
     * 0x0A 更改波特率 BAUD[4] ACK(成功为 0，失败返回非 0 的值)
     * 0x0C 调整显示亮度 LIGHT ACK(成功为 0，失败返回非 0 的值)
     * 0x0D 调整音量 VOL ACK(成功为 0，失败返回非 0 的值)
     * 0x0F 设置继电器状态 CH+OF+RESERVED[3] + OT ACK(成功为 0，失败返回非 0 的值)
     * 0x1A 设置 485 模式 MODE ACK(成功为 0，失败返回非 0 的值)
     * 0x1E 恢复出厂设置 无 ACK(成功为 0，失败返回非 0 的值)
     * 0xD3 配置临时车权限 TCA ACK(成功为 0，失败返回非 0 的值)
     * 0xD4 获取 GPS 位置信息 无 MSG
     * <p>
     * 0x30 播放语音 OPT + TEXT[MAX 254] ACK(成功为 0，失败返回非 0 的值)
     * 0x31 停止播放语音 无 ACK(成功为 0，失败返回非 0 的值)
     * 显 示 接 口
     * 0x62 下载临时信息 TWID + ETM + ETS + DM + DT + EXM + EXS + FINDEX + DRS + TC[4] + BC[4] + TL[2] + TEXT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0x67 下载广告语 TWID + FID + AF + ETM + ETS + DM + DT + EXM + EXS + FINDEX + TC[4] + BC[4] + TL[2] + TEXT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0x68 显示广告语 TWID + FID ACK(成功为 0，失败返回非 0 的值)
     * 0x6E 多行单包显示带语音 SAVE_FLAG + TEXT_CONTEXT_NUMBER + TEXT_CONTEXT[…]+ VF+ VTL + VT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0xE1 扫码支付界面 SF + EM + ETM + ST + NI + TIME + MONEY + ML + TL + FLAGS + QRSIZE + RESERVED[15] + MSG[ML + 1] + TEXT[TL + 1] ACK(成功为 0，失败返回非 0 的值)
     * 0xE3 余位显示界面 SF + EM + ETM + ST + NI + NUM ACK(成功为 0，失败返回非 0 的值)
     * 0xE5 扫码支付界面 SF + EM + ETM + ST + NI + VEN + TL + TEX
     */

    public static final byte[] CMD_CONNECT = {0x00};
    public static final byte[] CMD_CHANGE_PWD = {0x02};
    public static final byte[] CMD_SYNC_TIME = {0x05};
    public static final byte[] CMD_CHANGE_ADDRESS = {0x07};
    public static final byte[] CMD_CHANGE_BRIGHTNESS = {0x0C};
    public static final byte[] CMD_SETTING_485 = {0x1A};
    public static final byte[] CMD_PLAY = {0x30};
    public static final byte[] CMD_STOP_PLAY = {0x31};
    public static final byte[] CMD_DOWNLOAD_TEMP_INFO = {0x62};//下载临时信息
    public static final byte[] CMD_DOWNLOAD_AD = {0x67};//下载广告语
    public static final byte[] CMD_VIEW_AD = {0x68};//下载广告语
    public static final byte[] CMD_VIEW_QRCODE = {(byte) 0xE1};//显示二维码
    public static final byte[] CMD_VIEW_PLAY = {(byte) 0x6E};//显示并播放

    public static final byte[] DA = {0x00};
    public static final byte[] VR = {0x64};
    public static final byte[] PN = {(byte) 0xFF, (byte) 0xFF};
    public static final byte[] COLOR_RED = {(byte) 0xFF, 0x00, 0x00, 0x00};
    public static final byte[] GB_COLOR = {0x00, 0x00, 0x00, 0x00};
    public static final byte[] COLOR_GREEN = {0x00, (byte) 0xFF, 0x00, 0x00};


    //语音变量

    /**
     * 0x00 添加到语音队列但是不开始播放
     * 0x01 添加到语音队列并且开始播放
     * 0x02 先清除队列，再添加新语音到队列，然后开始播放
     */

    public static final byte CMD_PLAY_ADD = 0x00;
    public static final byte CMD_PLAY_AUTO = 0x01;
    public static final byte CMD_PLAY_DELETE_ADD = 0x02;

    /**
     * 播放声音
     */
    public static void pay(MachineDto machineDto, String msg) {
        try {
            byte[] data = msg.getBytes("GBK");
            int textLen = data.length;
            byte[] datalength = {(byte) (1 + textLen), 0x01};
            data = ArrayUtils.addAll(datalength, data);
            sendData(machineDto, CMD_PLAY, data);
        } catch (Exception e) {
            logger.error("发送语音失败", e);
        }
    }

    public static void viewText(MachineDto machineDto, String[] msgs) {
        List<String> tmpMsgs = new ArrayList<>();
        for (String msg : msgs) {
            tmpMsgs.add(msg);
        }
        try {
            viewText(machineDto, tmpMsgs);
        } catch (Exception e) {
            logger.error("发送文字失败", e);
        }
    }

    /**
     * 下载临时文本的功能是用来显示临时文字信息。
     * 请求格式: DA + VR + PN[2] + 0x62+ DL +TWID + ETM + ETS + DM + DT + EXM + EXS + FINDEX + DRS +
     * TC[4] + BC[4] + TL[2] + TEXT[...]+ CRC[2]
     * 请求参数描述: DL 等于 19 个字节再加上文本长度。
     * TWID:为窗口的 ID(表示第几行),用于标识创建的窗口身份。
     * ETM:文字进入窗口的方式。 取值范围及含义见下表:
     * ETM 取值含义
     * 编码 描述
     * 0x00 立即显示
     * 0x01 从右向左移动
     * 0x02 从左向右移动
     * 0x03 从下向上移动
     * 0x04 从上向下移动
     * 0x05 向下拉窗
     * 0x06 向上拉窗
     * 0x07 向左拉窗
     * 0x08 向右拉窗
     * 0x0D 逐字显示
     * 0x15 连续左移
     * ETS:为文字进入的速度。取值范围为 1~32。时基取决于当前的扫描周期。
     * DM:为文字停留的方式。 目前作为保留值固定为 0。
     * DT:为文字停留的时间。取值范围为 0 ~255。
     * EXM:为文字退场的方式。取值范围及含义参考 ETM:
     * EXS:为文字退场的速度。取值范围为 1~32。时基取决于当前的扫描周期。
     * FINDEX:为文字的字体索引值,目前作为保留值固定为 3。
     * DRS:为显示的次数。取值范围为 0~255,当为 0 的时，表示无限循环显示。
     * TC:为文字的颜色值。 存储结构为 R G B A 三基色，各占 8 位， R 表示红色分量， G 表示
     * 绿色分量,B 表示蓝色分量， A 目前没用使用，作为保留字。各取值范围为 0~255。
     * BC: 为背景的颜色值。存储结构为 R G B A 三基色，各占 8 位， R 表示红色分量， G 表
     * 示绿色分量,B 表示蓝色分量， A 目前没用使用，作为保留字。各取值范围为 0~255。
     * TL:为文字的长度。 16 位数据类型，小端模式。 目前最大长度为 2K 字节。可以存储 1000
     * 个汉字信息。
     * TEXT:为显示的文字内容，支持 ASCII 和 GBK2312 编码。 单包最大长度为 255,超出单包
     * 最大长度时，需要分包传输。
     * 回复格式: DA + VR + PN[2] + 0x62+ DL + ACK + CRC[2]
     * 回复参数描述: 包含 1 个字节的参数， DL 取值为 1， ACK 是显示屏返回的结果，取值为 0 表示成
     * 功，非 0 表示不成功。
     * 使用例子： 设置地址为 0 的显示屏的 0 号窗口的文本为"欢迎光临请入场停车"，进入模式为连续
     * 移动，进入速度为 1，停留模式为正常停留，停留时间为 2 秒，退出模式为连续移动，退出速度
     * 为 1，字体为宋体 16，字体颜色为红色，背景颜色为黑色。显示屏通信协议-v1.08
     * 主机发送: 00 64 FF FF 62 25 00 15 01 00 02 15 01 03 00 FF 00 00 00 00 00 00 00 12 00 BB B6 D3 AD
     * B9 E2 C1 D9 C7 EB C8 EB B3 A1 CD A3 B3 B5 F4 F5
     * 设备回复: 00 64 FF FF 62 01 00 97 6A
     *
     * @param machineDto
     * @param msgs
     * @throws UnsupportedEncodingException
     */
    public static void downloadTempTexts(MachineDto machineDto, List<String> msgs) throws UnsupportedEncodingException {
        for (int msgIndex = 0; msgIndex < msgs.size(); msgIndex++) {
            downloadTempTexts(machineDto, msgIndex, msgs.get(msgIndex));
        }
    }

    public static void downloadTempTexts(MachineDto machineDto, int line, String msg) {
        try {
            //0x00, 0x04
            byte[] data = new byte[]{};
            //演示 红色 绿色
            byte[] color = line % 2 == 0 ? COLOR_RED : COLOR_GREEN;
            // 设置  TWID + ETM + ETS + DM + DT + EXM + EXS + FINDEX + DRS + TC[4] + BC[4] + TL[2] + TEXT[...]
            byte[] tmpDate = new byte[]{(byte) line, 0x01, 0x01, 0x00, 0x05, 0x01, 0x01, 0x03, 0x01};
            tmpDate = ArrayUtils.addAll(tmpDate, color); //tc
            tmpDate = ArrayUtils.addAll(tmpDate, GB_COLOR); //BC
            tmpDate = ArrayUtils.addAll(tmpDate, intCovertByte(msg.getBytes("GBK").length));
            tmpDate = ArrayUtils.addAll(tmpDate, msg.getBytes("GBK"));
            data = ArrayUtils.addAll(data, tmpDate);
            int textLen = data.length;
            byte[] datalength = {(byte) (textLen)};
            data = ArrayUtils.addAll(datalength, data);

            sendData(machineDto, CMD_DOWNLOAD_TEMP_INFO, data);
        } catch (Exception e) {
            logger.error("发送文件失败", e);
        }

    }

    /**
     * 请 求 格 式 : DA + VR + PN[2] + 0x6E+ DL + SAVE_FLAG + TEXT_CONTEXT_NUMBER + TEXT_CONTEXT[…]+ VF+ VTL + VT[...] + CRC[2]
     * 可变长参数，最大为 255 字节。
     * SAVE_FLAG: 为保存标志，取值为 1 时表示下载到存储区，取值为 0 时表示下载到 临时区，频繁修改的内容建议下载到临时区。
     * TEXT_CONTEXT_NUMBER:为文本参数数量，即几行文本。目前版本最大支持 4 行。
     * TEXT_CONTEXT:为文本参数，每个文本参数控制一行，用 0X0D 分开，最后一个文
     * 本参数用 0X00 结束，最多 4 个文本参数。文本参数的结构为 LID + DM + DS + DT  + DR + TC[4]+TL +TEXT[...]+0X0D/0X00 各参数取值含义如下。
     * LID:为显示行号。 0 表示第 1 行， 1 表示第 2 行，以此类推。
     * DM:为显示模式  DM 取值含义
     * 编码 描述
     * 0x00 立即显示 0x01 从右向左移动  0x02 从左向右移动 0x03 从下向上移动  0x04 从上向下移动 0x05 向下拉窗 0x06 向上拉窗  0x07 向左拉窗
     * 0x08 向右拉窗  0x0D 逐字显示  0x15 连续左移
     * DS:为显示速度，建议取值为 0；
     * DT:停留时间，单位为秒，最大为 255 秒；
     * DR：为显示次数， 0 为无限循环显示。
     * TC：为文本颜色， 32 位数据类型，存储结构为 RGBA， R 为红色分
     * 量， G 为绿色分量， B 为蓝色分量， A 为透明值目前保留为 0，没个
     * 颜色分量占用一个字节即 8 位。
     * TL:为文本长度。
     * TEXT:为文本内容，最大 32 字节。
     * VF:语音标志，固定取值为 0X0A。
     * VTL:语音文本长度。
     * VOICE:语音文本内容，最大 64 字节。
     *
     * @param machineDto
     * @param msgs
     * @throws UnsupportedEncodingException
     */
    public static void viewText(MachineDto machineDto, List<String> msgs) throws UnsupportedEncodingException {
        byte[] data = new byte[]{0x00, 0x04};
        String msg = "";
        for (int msgIndex = 0; msgIndex < msgs.size(); msgIndex++) {
            msg = msgs.get(msgIndex);
            //演示 红色 绿色
            byte[] color = msgIndex % 2 == 0 ? COLOR_RED : COLOR_GREEN;
            // 设置
            byte[] tmpDate = new byte[]{(byte) msgIndex, 0x01, 0x01, 0x05, 0x01};
            tmpDate = ArrayUtils.addAll(tmpDate, color);
            tmpDate = ArrayUtils.addAll(tmpDate, (byte) (msg.getBytes("GBK").length));
            tmpDate = ArrayUtils.addAll(tmpDate, msg.getBytes("GBK"));
            //每行的内容用‘ \n’ 换行符分隔，最后一行用‘ \0’ 结束，
            if (msgIndex != msgs.size() - 1) {
                tmpDate = ArrayUtils.addAll(tmpDate, new byte[]{0x0D});
            } else {
                tmpDate = ArrayUtils.addAll(tmpDate, new byte[]{0x00});
            }
            data = ArrayUtils.addAll(data, tmpDate);

        }
        int textLen = data.length;
        byte[] datalength = new byte[]{(byte)(textLen)};
        data = ArrayUtils.addAll(datalength, data);
        sendData(machineDto, CMD_VIEW_PLAY, data);
    }

    /**
     * int类型转换成byte数组
     *
     * @param param int int类型的参数
     */

    public static byte[] intCovertByte(int param) {
        byte[] arr = new byte[2];
        arr[0] = (byte) ((param >> 8) & 0xff);
        arr[1] = (byte) (param & 0xff);
        return arr;
    }


    /**
     * 显示文字并播放
     *
     * @param machineDto
     * @param msgs
     * @param text
     * @throws UnsupportedEncodingException
     */
    public static void viewTextAndPlay(MachineDto machineDto, List<String> msgs, String text) throws UnsupportedEncodingException {
        byte[] data = new byte[]{0x00, 0x04};
        String msg = "";
        for (int msgIndex = 0; msgIndex < msgs.size(); msgIndex++) {
            msg = msgs.get(msgIndex);
            //演示 红色 绿色
            byte[] color = msgIndex % 2 == 0 ? COLOR_RED : COLOR_GREEN;
            // 设置
            byte[] tmpDate = new byte[]{(byte) msgIndex, 0x01, 0x01, 0x05, 0x01};
            tmpDate = ArrayUtils.addAll(tmpDate, color);
            tmpDate = ArrayUtils.addAll(tmpDate, (byte) (msg.getBytes("GBK").length));
            tmpDate = ArrayUtils.addAll(tmpDate, msg.getBytes("GBK"));
            //每行的内容用‘ \n’ 换行符分隔，最后一行用‘ \0’ 结束，
            if (msgIndex != msgs.size() - 1) {
                tmpDate = ArrayUtils.addAll(tmpDate, new byte[]{0x0D});
            } else {
                tmpDate = ArrayUtils.addAll(tmpDate, new byte[]{0x00});
            }
            data = ArrayUtils.addAll(data, tmpDate);
        }

        //加入声音
        byte[] play = {0x0A, (byte) text.getBytes("GBK").length};

        data = ArrayUtils.addAll(data, play);

        data = ArrayUtils.addAll(data, text.getBytes("GBK"));

        data = ArrayUtils.addAll(data, new byte[]{0x00});

        int textLen = data.length;
        byte[] datalength = {(byte) (textLen)};
        data = ArrayUtils.addAll(datalength, data);
        sendData(machineDto, CMD_VIEW_PLAY, data);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        List<String> msgs = new ArrayList<>();
        msgs.add("欢饮光临");
        msgs.add("月租车");
        msgs.add("青AGK916");
        msgs.add("剩余天数152天");
        viewTextAndPlay(null, msgs, "剩余天数152天");
    }


    /**
     * 发送数据
     *
     * @param machineDto
     * @param cmd
     * @param data
     */
    private static void sendData(MachineDto machineDto, byte[] cmd, byte[] data) {
        int textLen = data.length;
        if ((1 + textLen) >= 255) { //数据最大不能超过255长度
            throw new IllegalArgumentException("数据最大不能超过255长度");
        }
        //拼接 va vr pn  cmd dl  data  crc
        byte[] newData = ArrayUtils.addAll(DA, VR);
        newData = ArrayUtils.addAll(newData, PN);
        newData = ArrayUtils.addAll(newData, cmd);
        newData = ArrayUtils.addAll(newData, data);
        byte[] crc = getCrc(newData);
        newData = ArrayUtils.addAll(newData, crc);
        System.out.println("语音数据" + bytes2hex03(newData));
        if (machineDto == null) {
            System.out.println("设备为空，未向设备发送");
            return;
        }
        //ZhenshiByteToString.sendScreenCmd(machineDto, newData);
        String logId = SeqUtil.getMachineSeq();
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", "ttransmission");
        paramIn.put("id", logId);
        paramIn.put("subcmd", "send");
        paramIn.put("datalen", newData.length);
        paramIn.put("data", Base64Convert.byteTobase64(newData));
        paramIn.put("comm", "rs485-1");
        ZhenshiByteToString.sendCmd(machineDto, paramIn.toJSONString());

    }


    /**
     * 计算crc 16
     *
     * @param data
     * @return
     */
    private static byte[] getCrc(byte[] data) {
        byte[] crc16_h = {
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40
        };

        byte[] crc16_l = {
                (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04,
                (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8,
                (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC,
                (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12, (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10,
                (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4,
                (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38,
                (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C,
                (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7, (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0,
                (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4,
                (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68,
                (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C,
                (byte) 0xB4, (byte) 0x74, (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0,
                (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54,
                (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98,
                (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D, (byte) 0x4C, (byte) 0x8C,
                (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40
        };

        int crc = 0x0000ffff;
        int ucCRCHi = 0x00ff;
        int ucCRCLo = 0x00ff;
        int iIndex;
        for (int i = 0; i < data.length; ++i) {
            iIndex = (ucCRCLo ^ data[i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_h[iIndex];
            ucCRCHi = crc16_l[iIndex];
        }

        crc = ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
        //高低位互换，输出符合相关工具对Modbus CRC16的运算
        crc = ((crc & 0xFF00) >> 8) | ((crc & 0x00FF) << 8);
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((crc >> 8) & 0xff);
        bytes[1] = (byte) (crc & 0xff);
        return bytes;
    }

    public static String bytes2hex03(byte[] bytes) {
        final String HEX = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0F));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0F));
            sb.append(" ");
        }

        return sb.toString();
    }

}
