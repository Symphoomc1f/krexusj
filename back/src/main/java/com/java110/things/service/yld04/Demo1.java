package com.java110.things.service.yld04;

import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.util.Base64Convert;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Demo1 {
    // 定义组件
    //  public JFrame JF;

    public JLabel jl1;

    public Demo1() {

        //    d1.setVisible(true);
        //  mainPanelLeft= new Panel();

        //  mainPanelLeft.setBounds(100, 100, 400, 400);
        //  jl1 =new JLabel();
        //  jl1.setBounds(100, 100, 400, 400);   // 功能与上面相
        //  mainPanelLeft.add(jl1);       				// 添加了按钮会把背景颜色挡住，可以通过面板来调节
        //     mainPanelLeft.setDefaultCloseOperation(mainPanelLeft.EXIT_ON_CLOSE);
        //  mainPanelLeft.setVisible(true);
        // 创建组件
        // JLabel可以放置图片
        jl1 = new JLabel();
        ImageIcon i = new ImageIcon("C:\\Users\\Administrator\\Desktop\\c\\20190509140419.jpg");
        //设置ImageIcon
        jl1.setIcon(i);
        //label的大小设置为ImageIcon,否则显示不完整
        jl1.setBounds(0, 0, i.getIconWidth(), i.getIconHeight());
        // 拆分窗格
        //  JF.add(jl1);
        // 可以手动伸缩变化
        // 设置布局管理器，它本身就是borderLayout布局，就不用再设置了
        // 添加组件
        //    JF.setSize(400, 300);
        //  JF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jl1.setVisible(true);
        //JF.setBounds(100, 100, 400, 400);   // 功能与上面相
    }

    public static void main(String[] args) throws Exception{
        String image = "VORK5CYII=";
        GenerateImage(image);
        //        byte[] imagedata = Base64Convert.base64ToByte(image);
//        //faceimgarray[0].img_len = imagedata.length;
//        Memory a = new Memory(imagedata.length);
//        a.write(0, imagedata, 0, imagedata.length);
//        //faceimgarray[0].img.setPointer(a);
//        //faceimgarray[0].img_fmt = 0;
//        try {
//            byte[] imagedata1 = new byte[imagedata.length];
//            a.read(0, imagedata1, 0, imagedata.length);
//            String filename = "F:\\face\1.jpg";
//            // 转换成图片
//            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imagedata1));
//
//            //保存图片
//           // writeImageFile(bi, filename);
//        } catch (Exception e) {
//           // logger.debug("数据有误", e);
//        }
    }

    public static boolean GenerateImage(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "F:\\face\\neew.jpg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
