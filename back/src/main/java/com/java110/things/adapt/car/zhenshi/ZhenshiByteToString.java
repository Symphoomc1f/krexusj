package com.java110.things.adapt.car.zhenshi;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.netty.client.CarNettyClient;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * 臻识 道闸摄像头 字节和 字符串转换处理类
 * <p>
 * add by 吴学文 2021-01-04
 */
public class ZhenshiByteToString {


    public static String getDataByByte(byte[] bytes) {
        InputStream inputStream = null;
        String sn = "";
        try {
            inputStream = byte2Input(bytes);

            int sn_len = recvPacketSize(inputStream);
            if (sn_len > 0) {
                //接收实际数据
                byte[] data = new byte[sn_len];
                int recvLen = recvBlock(inputStream, data, sn_len);
                JSONObject dataObj = onRecv(data, recvLen);
                if (dataObj != null) {
                    sn = dataObj.toJSONString();
                }
            }
        } catch (Exception e) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sn;
    }

    public static JSONObject onRecv(byte[] data, int len) throws UnsupportedEncodingException {
        //String ivs = new String(data,"UTF-8" );
        JSONObject dataObj = null;
        if (len > 20 * 1024) {
            //带图片数据
            dataObj = onIVSResultRecv(data, len);
        } else {
            //普通的指令响应
            String ivs = new String(data, "UTF-8");

            dataObj = JSONObject.parseObject(ivs);
        }
        return dataObj;
    }

    public static JSONObject onIVSResultRecv(byte[] data, int len) {
        //json处理
        int pos = 0;
        while (true) {
            if (data[pos] == 0) {
                break;
            }
            pos++;
        }


        String ivs = new String(data, 0, pos);

        JSONObject dataObj = JSONObject.parseObject(ivs);

        String image = "";

        // 此处改为通过json来解析车牌识别结果,来获取车牌图片的大小
        int nImgSize = len - pos - 1;

        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();

            baos.write(data, pos + 1, nImgSize);
            byte[] imgData = baos.toByteArray();
            baos.flush();
            image = new BASE64Encoder().encode(imgData);
            dataObj.put("photoJpg", image);
        } catch (Exception e) {
            //读取图片失败

        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataObj;

    }

    public static int saveImage(byte[] buff, int pos, int len, String imgPath) {
        int ret = -1;
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(imgPath));
            out.write(buff, pos, len);
            out.close();
        } catch (IOException io) {
            System.out.println("save image failed " + imgPath);
        }

        return ret;
    }


    public static int recvPacketSize(byte[] bytes) {
        byte[] header = new byte[8];
        int recvLen = recvBlock(byte2Input(bytes), header, 8);
        if (recvLen <= 0) {
            return -1;
        }

        if (header[0] != 'V' || header[1] != 'Z') {
            //格式不对
            return -1;
        }

        if (header[2] == 1) {
            //心跳包
            return 0;
        }

        return convBytesToInt(header, 4);
    }

    public static int recvPacketSize(InputStream in) {
        byte[] header = new byte[8];
        int recvLen = recvBlock(in, header, 8);
        if (recvLen <= 0) {
            return -1;
        }

        if (header[0] != 'V' || header[1] != 'Z') {
            //格式不对
            return -1;
        }

        if (header[2] == 1) {
            //心跳包
            return 0;
        }

        return convBytesToInt(header, 4);
    }

    public static int convBytesToInt(byte[] buff, int offset) {
        //4bytes 转为int，要考虑机器的大小端问题
        int len, byteValue;
        len = 0;
        byteValue = (0x000000FF & ((int) buff[offset]));
        len += byteValue << 24;
        byteValue = (0x000000FF & ((int) buff[offset + 1]));
        len += byteValue << 16;
        byteValue = (0x000000FF & ((int) buff[offset + 2]));
        len += byteValue << 8;
        byteValue = (0x000000FF & ((int) buff[offset + 3]));
        len += byteValue;
        return len;
    }

    //接收指定长度的数据，收完为止
    public static int recvBlock(InputStream in, byte[] buff, int len) {
        try {
            int totleRecvLen = 0;
            int recvLen;
            while (totleRecvLen < len) {
                recvLen = in.read(buff, totleRecvLen, len - totleRecvLen);
                totleRecvLen += recvLen;
            }
            return len;
        } catch (Exception e) {
            System.out.println("recvBlock timeout!");
            // System.out.println("Error:"+e);
            return -1;
        }
    }

    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            return in2b;
        } finally {
            swapStream.close();
        }
    }

    /**
     * 发送指令
     *
     * @param machineDto
     * @param cmd
     * @return
     */
    public static boolean sendScreenCmd(MachineDto machineDto, byte[] cmd) {
        try {
            CarNettyClient.sendScreenMsg(machineDto, cmd);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 发送指令
     *
     * @param machineDto
     * @param cmd
     * @return
     */
    public static boolean sendCmd(MachineDto machineDto, String cmd) {
        try {
            int len = cmd.getBytes().length;
            byte[] header = {'V', 'Z', 0, 0, 0, 0, 0, 0};
            header[4] += (byte) ((len >> 24) & 0xFF);
            header[5] += (byte) ((len >> 16) & 0xFF);
            header[6] += (byte) ((len >> 8) & 0xFF);
            header[7] += (byte) (len & 0xFF);
            //CarNettyClient.sendMsg(machineDto, header);
            CarNettyClient.sendMsg(machineDto, header, cmd.getBytes());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean sendKeepAlive(MachineDto machineDto) {
        try {
            byte[] buff = {'V', 'Z', 1, 0, 0, 0, 0, 0};
            CarNettyClient.sendMsg(machineDto, buff);
        } catch (Exception e) {
            System.out.println("Error:" + e);
            return false;
        }
        return true;
    }

    public static boolean configFormat(MachineDto machineDto, int enable, int fmt, int image) {
        String cmd;
        cmd = String.format("{" +
                        "\"cmd\" : \"ivsresult\"," +
                        "\"enable\" : %s," +
                        "\"format\" : \"%s\"," +
                        "\"image\" : %s" +
                        "}",
                enable != 0 ? "true" : "false",
                fmt != 0 ? "json" : "bin",
                image != 0 ? "true" : "false");

        return sendCmd(machineDto, cmd);
    }


}
