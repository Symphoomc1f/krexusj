package com.java110.things.service.yld04;


import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.INotifyAccessControlService;
import com.java110.things.util.Base64Convert;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.text.*;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Function {
    private static Logger logger = LoggerFactory.getLogger(Function.class);
    public static libFaceRecognition m_FaceRecognition = libFaceRecognition.INSTANCE;
    public static Map<String, IntByReference> cameraPoints = new HashMap<>();
    public static IntByReference cameraPoint = new IntByReference(0);
    public static boolean BOEStream;
    public static java.util.Map<String, String> mapcamreip = new HashMap<String, String>();
    byte[] m_rs485_protocal_no = new byte[1];

    private static ZBX_FaceRecoCb_t_Realize m_RecoCb_t = null;// new ZBX_FaceRecoCb_t_Realize();
    private static ZBX_FaceQueryCb_t_Realize m_FaceQueryCb_t = null;
    private static discover_ipscan_cb_t_Realize m_discover_ipscan = null;
    private static ZBX_ConnectEventCb_t_Realize m_ConnectEventCb = null;
    private static Httpresult_Realize m_HTTPRESULT_PROCESS = null;

    Function() {
        BOEStream = false;
    }

    public static void Init() {
        m_FaceRecognition.ZBX_Init();
        m_FaceRecognition.ZBX_SetNotifyConnected(1);
        m_ConnectEventCb = new ZBX_ConnectEventCb_t_Realize();
        m_FaceRecognition.ZBX_RegConnectEventCb(m_ConnectEventCb, 0);
    }


    public static void clsClear() {
        m_FaceRecognition.lib_clsClear();
    }

    //连接相机
    public static void connectCamera(String machineIp) {
        IntByReference err_code = new IntByReference(0);
        if (cameraPoints.containsKey(machineIp)) {
            cameraPoint = cameraPoints.get(machineIp);
        }
        if (m_FaceRecognition.ZBX_Connected(cameraPoint) == 1) {
            logger.debug("相机已经连接，若要改变连接相机请先断开相机");
            return;
        }
        cameraPoint = m_FaceRecognition.ZBX_ConnectEx(machineIp, (short) 8099, "", "", err_code,
                0, 1);
        if (m_FaceRecognition.ZBX_Connected(cameraPoint) != 1) {
            logger.debug("连接相机失败");
        } else {
            cameraPoints.put(machineIp, cameraPoint);
            m_RecoCb_t = new ZBX_FaceRecoCb_t_Realize();
            m_FaceRecognition.ZBX_RegFaceRecoCb(cameraPoints.get(machineIp), m_RecoCb_t, Pointer.NULL);
            logger.debug("连接相机成功");
        }

    }

    //断开相机
    public static void DisConnectCamera(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) == 0) {
            logger.debug("相机已经断开，无需重复操作");
            return;
        }
        m_FaceRecognition.ZBX_DisConnect(cameraPoints.get(machineIp));
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) == 0) {
            logger.debug("断开成功");
            return;
        } else logger.debug("断开失败请稍后重试");
    }


    private static WinDef.HWND createHWNDByComponent(JLabel jlabel) {
        return new WinDef.HWND(Native.getComponentPointer(jlabel));
    }
    //  public static JFrame jf ;

    public static Demo1 d1;

    // 开始/断开视频流
    public static void StartStream(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }

        if (!BOEStream) {
            d1 = new Demo1();
            //传入窗口句柄
            m_FaceRecognition.ZBX_StartStream(cameraPoints.get(machineIp), createHWNDByComponent(d1.jl1));
            logger.debug("连接视频流成功");
            BOEStream = true;
        } else {
            //传入窗口句柄
            m_FaceRecognition.ZBX_StopStreamEx(cameraPoints.get(machineIp), createHWNDByComponent(d1.jl1));
            logger.debug("断开视频流成功");
            BOEStream = true;
        }
    }

    public static void AddFace(String machineIp, String faceName, String faceId, String image) throws IOException {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        libFaceRecognition.FaceFlags.ByValue flag = new libFaceRecognition.FaceFlags.ByValue();
        libFaceRecognition.FaceImage.ByValue faceimg = new libFaceRecognition.FaceImage.ByValue();
        libFaceRecognition.FaceImage.ByValue[] faceimgarray = (libFaceRecognition.FaceImage.ByValue[]) faceimg.toArray(1);
        libFaceRecognition.FaceFlags.ByValue[] flagarray = (libFaceRecognition.FaceFlags.ByValue[]) flag.toArray(1);

        flagarray[0].faceName = faceName;//.getBytes();
        flagarray[0].faceID = faceId;//.getBytes();

        byte[] imagedata = Base64Convert.base64ToByte(image);
        faceimgarray[0].img_len = imagedata.length;
        Memory a = new Memory(imagedata.length);
        a.write(0, imagedata, 0, imagedata.length);
        faceimgarray[0].img.setPointer(a);
        faceimgarray[0].img_fmt = 0;
        try {
            byte[] imagedata1 = new byte[imagedata.length];
            a.read(0, imagedata1, 0, imagedata.length);
            String filename = MappingCacheFactory.getValue("ASSESS_CONTROL_PATH") + faceId + ".jpg";
            // 转换成图片
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imagedata1));
            //保存图片
            writeImageFile(bi, filename);
        } catch (Exception e) {
            logger.debug("数据有误", e);
        }
        int k = m_FaceRecognition.ZBX_AddJpgFaces(cameraPoints.get(machineIp), flagarray,
                faceimgarray, 1, 1);

        if (k == 0) {
            logger.debug("添加成功");
        } else {
            logger.debug("添加失败");
        }
    }


    /**
     * 删除人脸
     *
     * @param machineIp 设备Ip
     * @param faceId    人脸ID
     */
    public static void deleteFace(String machineIp, String faceId) {
        m_FaceRecognition.ZBX_DeleteFaceDataByPersonID(cameraPoints.get(machineIp), faceId);
    }

    /**
     * 清空人脸
     *
     * @param machineIp 设备Ip
     */
    public static void clearFace(String machineIp) {
        int tag = m_FaceRecognition.ZBX_DeleteFaceDataAll(cameraPoints.get(machineIp));
        if (tag == 0) {
            logger.debug("删除所有成功  ");
        } else {
            logger.debug("删除所有失败  ");
        }
    }


    //查询删除实现
    public static void degregmng(String machineIp) {
        logger.debug("请输入操作:  1：查询 2：删除 3：删除所有  ");
        int chooice = -1;
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        m_FaceQueryCb_t = new ZBX_FaceQueryCb_t_Realize();
        m_FaceRecognition.ZBX_RegFaceQueryCb(cameraPoints.get(machineIp), m_FaceQueryCb_t, Pointer.NULL);

        int idx = 1;//查询页码
        int num = 10;//每页条数
        m_FaceRecognition.ZBX_QueryByRole(cameraPoints.get(machineIp), -1, idx, num, '0', '0');


    }

    //搜索相机
    public static void searchcerme() {
        m_discover_ipscan = new discover_ipscan_cb_t_Realize();
        m_FaceRecognition.ZBX_RegDiscoverIpscanCb(m_discover_ipscan, 0);
        m_FaceRecognition.ZBX_DiscoverIpscan();
    }

    //设置韦根
    public static void SettingWG(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：设置 2：获取  ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
            return;
        }
        if (chooice == 1) {
            logger.debug("请输入操作韦根号  ");
            int temp = 0;
            try {
                temp = sc.nextInt();
            } catch (Exception e) {
                logger.debug("输入有误");
                return;
            }
            int tag = m_FaceRecognition.ZBX_SetWiegandType(cameraPoints.get(machineIp), temp);
            if (tag == 0) logger.debug("设置成功");
            else logger.debug("设置失败");
        } else if (chooice == 2) {
            IntByReference type = new IntByReference(0);
            if (m_FaceRecognition.ZBX_GetWiegandType(cameraPoints.get(machineIp), type) == 0) {
                //logger.debug(type.getValue());

            } else {
                logger.debug("获取失败");
            }

        } else {
            logger.debug("输入有误");
            return;
        }
    }

    //灯控设置
    void SettingLight(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：设置 2：获取  ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
            return;
        }
        if (chooice == 1) {
            logger.debug("请输入灯控模式 ");
            sc = new Scanner(System.in);
            byte led_mode = (byte) sc.nextInt();
            m_FaceRecognition.ZBX_SetLedMode(cameraPoints.get(machineIp), led_mode);
            logger.debug("请输入灯亮度 ");
            sc = new Scanner(System.in);
            byte led_level = (byte) sc.nextInt();
            m_FaceRecognition.ZBX_SetLedLevel(cameraPoints.get(machineIp), led_level);
        } else if (chooice == 2) {
            Pointer a = new Memory(2);
            Pointer led_mode = new Memory(10);

            Pointer led_level = new Memory(10);
            m_FaceRecognition.ZBX_GetLedMode(cameraPoints.get(machineIp), led_mode);
            for (int i = 0; i < 10; ++i) {
                if ((char) led_mode.getByteArray(0, 10)[i] == '\0') break;
                System.out.print((char) led_mode.getByteArray(0, 10)[i]);
            }
            logger.debug("\n");
            //logger.debug(led_mode);
            m_FaceRecognition.ZBX_GetLedLevel(cameraPoints.get(machineIp), led_level);
            for (int i = 0; i < 10; ++i) {
                if ((char) led_level.getByteArray(0, 10)[i] == '\0') break;
                System.out.print((char) led_level.getByteArray(0, 10)[i]);
            }
            logger.debug("\n");
        } else {
            logger.debug("输入有误");
            return;
        }
    }

    //去重复设置
    void Repetition(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：查询 2：设置 ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {
            IntByReference interval = new IntByReference(-1);
            int succ = m_FaceRecognition.ZBX_GetClusterTimesInterval(cameraPoints.get(machineIp), interval);
            if (succ == 0) {
                logger.debug("查询成功 ");
                //logger.debug(interval.getValue());
            } else {
                logger.debug("查询失败 ");
            }
        } else if (chooice == 2) {
            int interval = -1;
            logger.debug("请输入重复人员间隔");
            try {
                interval = sc.nextInt();
            } catch (Exception e) {
                logger.debug("输入有误");
            }
            int succ = m_FaceRecognition.ZBX_SetClusterTimesInterval(cameraPoints.get(machineIp), interval);
            if (succ == 0) {
                logger.debug("设置成功 ");
            } else {
                logger.debug("设置失败 ");
            }
        } else {
            logger.debug("输入有误");
            return;
        }

    }

    //相似度设置
    void SimilaritySetting(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：查询 2：设置 ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {
            IntByReference score = new IntByReference(-1);
            int succ = m_FaceRecognition.ZBX_GetMatchScore(cameraPoints.get(machineIp), score);
            if (succ == 0) {
                logger.debug("查询成功 ");
                //logger.debug(score.getValue());
            } else {
                logger.debug("查询失败 ");
            }
        } else if (chooice == 2) {
            int score = -1;
            logger.debug("请输入相似度");
            try {
                score = sc.nextInt();
            } catch (Exception e) {
                logger.debug("输入有误");
            }
            if (score < 0 || score > 100) {
                logger.debug("请输入正确的相似度 应为0~100");
            }
            int succ = m_FaceRecognition.ZBX_SetMatchScore(cameraPoints.get(machineIp), score);
            if (succ == 0) {
                logger.debug("设置成功 ");
            } else {
                logger.debug("设置失败 ");
            }
        } else {
            logger.debug("输入有误");
            return;
        }

    }

    //标题设置
    void TagSetting(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：查询 2：设置 ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {
            byte[] screen_title = new byte[64];

            int succ = m_FaceRecognition.ZBX_GetScreenOsdTitle(cameraPoints.get(machineIp), screen_title);
            if (succ == 0) {
                try {
                    String title = new String(getfullbyte(screen_title), "gb2312");
                    logger.debug(title);
                } catch (Exception e) {
                    logger.debug("数据有误");
                }
                logger.debug("查询成功 ");

            } else {
                logger.debug("查询失败 ");
            }
        } else if (chooice == 2) {
            byte[] title = new byte[64];
            logger.debug("请输入相标题");
            try {
                Scanner sc1 = new Scanner(System.in);
                String t = sc1.nextLine();
                title = (t).getBytes();
            } catch (Exception e) {
                logger.debug("输入有误");
            }

            int succ = m_FaceRecognition.ZBX_SetScreenOsdTitle(cameraPoints.get(machineIp), title);
            if (succ == 0) {
                logger.debug("设置成功 ");
            } else {
                logger.debug("设置失败 ");
            }
        } else {
            logger.debug("输入有误");
            return;
        }

    }

    //时间设置
    public static void TimeSetting(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：获取相机时间 2：设置 ");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {

            IntByReference second = new IntByReference();
            int succ = m_FaceRecognition.ZBX_GetSysTime(cameraPoints.get(machineIp), second);
            if (succ == 0) {
                Date date2 = new Date();
                long t = 1000;
                date2.setTime(second.getValue() * t);
                System.out.print(date2);
                //logger.debug('\n');
            } else {
                logger.debug("查询失败 ");
            }
        } else if (chooice == 2) {

            logger.debug("下面是一个设置时间示例");

            int succ = m_FaceRecognition.ZBX_SetSysTimeEx(cameraPoints.get(machineIp), 2019, 4, 1, 8, 0, 0);
            if (succ == 0) {
                logger.debug("设置成功 ");
            } else {
                logger.debug("设置失败 ");
            }
        } else {
            logger.debug("输入有误");
            return;
        }

    }

    //系统升级
    void upgrade(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("系统升级文件路径+文件名 ");

        String filename = null;
        try {
            Scanner sc = new Scanner(System.in);
            filename = sc.nextLine();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        Pointer user_data = null;
        IntByReference second = new IntByReference();
        m_HTTPRESULT_PROCESS = new Httpresult_Realize();
        m_FaceRecognition.ZBX_SetUpdateSystem_CB(cameraPoints.get(machineIp), m_HTTPRESULT_PROCESS
                , user_data);
        int iRet = m_FaceRecognition.ZBX_UpdateSystem(cameraPoints.get(machineIp),
                filename.getBytes());

        if (iRet == 0) {
            logger.debug("升级完成 设备即将重启");
            if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) == 1)
                m_FaceRecognition.ZBX_DisConnect(cameraPoints.get(machineIp));
            //cameraPoint = (null);
            cameraPoints.put(machineIp, null);
        } else {
            logger.debug("升级失败 请重试");
        }
    }


    //串口设置
    void GorgelineSetting(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }
        logger.debug("请输入操作:  1：获取参数 2：设置 ，3： 发送串口消息");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {


            int succ = m_FaceRecognition.ZBX_GetRS485ProtocalNo(cameraPoints.get(machineIp), m_rs485_protocal_no);
            System.out.print(deCode(new String(getfullbyte(m_rs485_protocal_no))).trim());
            //logger.debug(' ');
            IntByReference baudrate = new IntByReference();
            IntByReference parity = new IntByReference();
            IntByReference databit = new IntByReference();
            IntByReference stopbit = new IntByReference();

            succ = m_FaceRecognition.ZBX_GetTSerial(cameraPoints.get(machineIp), 0, baudrate,
                    parity, databit, stopbit);
            if (succ == 0) {
                System.out.print(baudrate.getValue());
                //logger.debug(' ');
                System.out.print(parity.getValue());
                //logger.debug(' ');
                System.out.print(databit.getValue());
                //logger.debug(' ');
                System.out.print(stopbit.getValue());
                //logger.debug(' ');

            } else {
                logger.debug("查询失败 ");
            }
        } else if (chooice == 2) {

            //输入示例
            int index = 0, baudrate = 115200,
                    parity = 0, databit = 8, stopbit = 1;
            char rs485_protocal_no = '0';
            int tag = m_FaceRecognition.ZBX_OpenTSerial(cameraPoints.get(machineIp), index, baudrate,
                    parity, databit, stopbit);
            if (tag == 0) {
                logger.debug("设置串口成功");
            } else {
                logger.debug("设置串口失败");
            }
            tag = m_FaceRecognition.ZBX_SetRS485ProtocalNo(cameraPoints.get(machineIp), rs485_protocal_no);
            if (tag == 0) {
                logger.debug("设置串口协议成功");
            } else {
                logger.debug("设置串口协议失败");
            }
        } else if (chooice == 3) {
            String t = "";
            logger.debug("请输入要发送的串口消息");
            try {
                Scanner sc1 = new Scanner(System.in);
                t = sc1.nextLine();

            } catch (Exception e) {
                logger.debug("输入有误");
            }
            int succ = m_FaceRecognition.ZBX_WriteTSerial(cameraPoints.get(machineIp), (int) m_rs485_protocal_no[0],
                    t.getBytes(), t.length());
            if (succ == 0) {
                logger.debug("发送成功");
            } else {
                logger.debug("发送失败");
            }
        } else {
            logger.debug("输入有误");
            return;
        }

    }

    void webSetting(String machineIp) {
        if (m_FaceRecognition.ZBX_Connected(cameraPoints.get(machineIp)) != 1) {
            logger.debug("相机未连接");
            return;
        }

        logger.debug("请输入操作:  1：获取网络信息 2：设置，3：修改ip");
        Scanner sc = new Scanner(System.in);
        int chooice = -1;
        try {
            chooice = sc.nextInt();
        } catch (Exception e) {
            logger.debug("输入有误");
        }
        if (chooice == 1) {

            libFaceRecognition.SystemNetInfo.ByReference SystemNet = new libFaceRecognition.SystemNetInfo.ByReference();
            int succ = m_FaceRecognition.ZBX_GetNetConfig(cameraPoints.get(machineIp), SystemNet);
            if (succ == 0) {
                logger.debug("获取网络信息成功");
                logger.debug(deCode(new String(getfullbyte((SystemNet.ip_addr))).trim()));
                logger.debug(deCode(new String(((SystemNet.mac_addr))).trim()));
                logger.debug(deCode(new String(getfullbyte((SystemNet.netmask))).trim()));
                logger.debug(deCode(new String(((SystemNet.gateway))).trim()));
            } else {
                logger.debug("获取网络信息失败 ");
            }
        } else if (chooice == 2) {

            //输入示例
            libFaceRecognition.SystemNetInfo.ByReference netInfo = new libFaceRecognition.SystemNetInfo.ByReference();
            logger.debug("请输入ip");
            Scanner sc1 = new Scanner(System.in);
            netInfo.ip_addr = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入mac");
            netInfo.mac_addr = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入子网掩码");
            netInfo.netmask = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入默认网关");
            netInfo.gateway = sc1.nextLine().getBytes();//.getBytes();
            int tag = m_FaceRecognition.ZBX_SetNetConfig(cameraPoints.get(machineIp), netInfo);

            if (tag == 0) {
                logger.debug("设置成功");
            } else {
                logger.debug("设置失败");
            }

        } else if (chooice == 3) {
            logger.debug("请输入原相机的mac");
            Scanner sc1 = new Scanner(System.in);
            byte[] mac_addr = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入原相机的netmask");
            byte[] netmask = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入原相机的gateway");
            byte[] gateway = sc1.nextLine().getBytes();//.getBytes();
            logger.debug("请输入新ip");
            byte[] ip_addr = sc1.nextLine().getBytes();//.getBytes();
            m_FaceRecognition.ZBX_SetIpBymac(mac_addr, ip_addr,
                    netmask, gateway);

        } else {
            logger.debug("输入有误");
            return;
        }

    }


    //抓拍回调实现
    public static class ZBX_FaceRecoCb_t_Realize implements libFaceRecognition.ZBX_FaceRecoCb_t {
        public void Status(IntByReference cam, libFaceRecognition.FaceRecoInfo.ByReference cb,
                           Pointer usrParam) {
            String savepath = "E:\\face";
            File myPath = new File(savepath);
            if (!myPath.exists()) {
                myPath.mkdirs();
            }
            //logger.debug(cb.tvSec);
            long t = 1000;
            SimpleDateFormat lFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date date2 = new Date();
            date2.setTime(cb.tvSec * t);
            String datetime = lFormat.format(date2);

            if (cb.matched == -1) {

                logger.debug("未识别人员");
                logger.debug(datetime);
                String filepath = savepath + "\\unidentified\\";
                File myfilepath = new File(filepath);
                if (!myfilepath.exists()) {
                    myfilepath.mkdirs();
                }

                String filename = filepath + datetime + ".jpg";
                byte[] data = cb.faceImg.getPointer().getByteArray(0, cb.faceImgLen);
                try {
                    // 转换成图片
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
                    //保存图片
                    writeImageFile(bi, filename);
                } catch (Exception e) {
                    logger.debug("数据有误");
                    return;
                }

            } else {
                //保存人员信息
                try {
                    String personname = new String(getfullbyte(cb.matchPersonName), "gb2312");
                    logger.debug(personname);
                } catch (Exception e) {
                    logger.debug("数据有误");
                }
                logger.debug(datetime);
                String filepath = savepath + "\\identified\\";
                File myfilepath = new File(filepath);
                if (!myfilepath.exists()) {
                    myfilepath.mkdirs();
                }
                String filename = filepath + datetime + ".jpg";
                byte[] data = cb.faceImg.getPointer().getByteArray(0, cb.faceImgLen);
                try {
                    // 转换成图片
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
                    //保存图片
                    writeImageFile(bi, filename);
                } catch (Exception e) {
                    logger.debug("数据有误");
                    logger.debug(e.toString());
                    return;
                }


//保存模板图像信息
                String modepath = savepath + "\\mode\\";
                File mymodepath = new File(modepath);
                if (!mymodepath.exists()) {
                    mymodepath.mkdirs();
                }
                String path_mode = modepath + datetime + ".jpg";
                byte[] data_mode = cb.modelFaceImg.getPointer().getByteArray(0, cb.modelFaceImgLen);
                try {
                    // 转换成图片
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data_mode));
                    //保存图片
                    writeImageFile(bi, path_mode);
                } catch (Exception e) {
                    logger.debug("数据有误1");
                    logger.debug(e.toString());
                    return;
                }
            }
            return;
        }
    }

    //人员查询回调实现
    public static class ZBX_FaceQueryCb_t_Realize implements libFaceRecognition.ZBX_FaceQueryCb_t {
        public void Status(IntByReference cam, libFaceRecognition.QueryFaceInfo.ByReference QueryFaceIn,
                           Pointer usrParam) {
            try {
                String personname = new String(getfullbyte(QueryFaceIn.personName), "gb2312");
                logger.debug(personname);
            } catch (Exception e) {
                logger.debug("数据有误");
            }

            String personId = deCode(new String(QueryFaceIn.personID)).trim();// deCode(matchPersonID).trim();
            logger.debug(personId);
            //将data 数据转成byte【】
            byte[] data = QueryFaceIn.imgBuff[0].getPointer().getByteArray(0, QueryFaceIn.imgSize[0]);

            //
            SimpleDateFormat lFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date date = new Date();
            date.getTime();
            String path = "D:\\camera";   //保存文件路径
            File myPath = new File(path);
            if (!myPath.exists()) {
                myPath.mkdir();
            }
            String filename = path + lFormat.format(date) + ".jpg";

            logger.debug(filename);

            try {
                // 转换成图片
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
                //保存图片
                writeImageFile(bi, filename);
            } catch (Exception e) {
                logger.debug("数据有误");
                return;
            }
        }
    }


    //搜索相机回调函数
    public static class discover_ipscan_cb_t_Realize implements libFaceRecognition.discover_ipscan_cb_t {
        public void Status(libFaceRecognition.ipscan_t.ByReference ips, int usr_param) {
            if (mapcamreip.containsKey(deCode(new String(ips.mac)).trim()) == false) {
                //  输出查到的ip
                logger.debug(deCode(new String(ips.ip)).trim());
                logger.debug(deCode(new String(ips.mac)).trim());
                logger.debug(deCode(new String(ips.manufacturer)).trim());
            } else {
                mapcamreip.put(deCode(new String(ips.mac)).trim(), deCode(new String(ips.ip)).trim());
            }
            //设备上报
            INotifyAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getINotifyAccessControlService();
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineId(UUID.randomUUID().toString());
            machineDto.setMachineIp(deCode(new String(ips.ip)).trim());
            machineDto.setMachineMac(deCode(new String(ips.mac)).trim());
            machineDto.setMachineCode(deCode(new String(ips.mac)).trim());
            machineDto.setMachineName(deCode(new String(ips.ip)).trim());
            machineDto.setMachineVersion("v1.0");
            machineDto.setOem("伊兰度");
            notifyAccessControlService.uploadMachine(machineDto);
        }
    }


    /* 连接事件回调函数 */
// event 1为已连接 2为连接中断
    public static class ZBX_ConnectEventCb_t_Realize implements libFaceRecognition.ZBX_ConnectEventCb_t {
        public void Status(IntByReference cam, String ip, short port, int event, int usrParam) {
            if (event == 0) {
                if (m_FaceRecognition.ZBX_IsServer(cameraPoints.get(ip)) == 1) {
                    m_FaceRecognition.ZBX_DisConnect(cameraPoints.get(ip));
                }
                return;
            }
        }
    }

    //系统升级进度
//注意：如果注册此回调 该回调必须返回0 否则会终止传输
    public static class Httpresult_Realize implements libFaceRecognition.ZBX_HTTPRESULT_PROCESS {
        public int Status(Pointer user_data, double rDlTotal, double rDlNow, double rUlTotal, double rUlNow) {
            if (!(rUlTotal == 0) && !(rUlNow == 0)) {
                return 0;
            }
            int process = (int) (rDlNow * 100 / rUlTotal);
            logger.debug("系统升级进度为： ");
            //logger.debug(process);
            return 0;
        }

    }


    //byte数组到图片
    public static void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            logger.debug("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            logger.debug("Exception: " + ex);
            ex.printStackTrace();
        }
    }


    public static String deCode(String str) {
        try {
            return java.net.URLDecoder.decode(str, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String deCodeUtf_8(String str) {
        try {
            return java.net.URLDecoder.decode(str, "Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    //  读取图片转换成byte[]
    public static byte[] imageToByteArray(String imgPath) {
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(imgPath));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int size = 0;
            byte[] temp = new byte[1024];
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //图像读取
    public static BufferedImage readImageFile(String filename) {
        File file = new File(filename);
        try {
            BufferedImage image = ImageIO.read(file);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //图像保存
    public static void writeImageFile(BufferedImage bi, String filename) throws IOException {
        File outputfile = new File(filename);
        ImageIO.write(bi, "jpg", outputfile);


    }

    public static byte[] bufferedImageToByteArray(BufferedImage img)
            throws ImageFormatException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(img);
        return os.toByteArray();
    }

    public static byte[] getfullbyte(byte[] orgbyte) {
        int length = 0;
        for (int i = 0; i < orgbyte.length; ++i) {
            if (orgbyte[i] != '\0') ++length;
            else break;
        }
        byte[] temp = new byte[length + 1];
        for (int i = 0; i < length; ++i) {
            temp[i] = orgbyte[i];
        }
        temp[length] = '\0';
        return temp;
    }


}

