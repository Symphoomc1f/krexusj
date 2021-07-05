package com.java110.things.service.yld04;

import com.sun.jna.Native;

import javax.swing.*;
import java.awt.*;

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

    public static void main(String[] args) {
        libFaceRecognition INSTANCE = (libFaceRecognition) Native.loadLibrary(
                "C:\\Users\\Administrator\\Documents\\project\\hc\\MicroCommunityThings\\back\\dll\\yld04\\libFaceRecognitionSDK_x64", libFaceRecognition.class);
    }
}
