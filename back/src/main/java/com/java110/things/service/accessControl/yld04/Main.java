package com.java110.things.service.accessControl.yld04;

import java.util.Scanner;

public class Main {

 public static Function fun=new Function();
   public static boolean   cameraconntect =false;
    public static void main(String[] args)
    {
       //*******************************************************************
        //这是一个示例程序，函数输入输出用控制台打印
        fun.Init();
        boolean  stop =true;
        while(stop)
        {
            stop= maninterface();
            if(stop ==false)
                break ;
        }
    }
    public static boolean maninterface( )
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("**************************************");
        System.out.println("请输入整数标号");
        System.out.println("0：连接相机  1:断开相机 ,2连接/断开视频");
        System.out.println("3：添加人脸  4:查询及删除人脸 ,5搜索相机");
        System.out.println("6：设置韦根  7:灯控设置 ,8: 去重复");
        System.out.println("9：相似度设计  10:标题设置 ,11: 时间设置");
        System.out.println("12：系统升级，13： 串口设置，14： 网络设置");
            System.out.println("其它：退出 ");
        System.out.println("**********请输入要选择的操作*********");
        int chooice=999;
        try {chooice = sc.nextInt();}
        catch(Exception e){System.out.println("输入有误");}
      //  libFaceRecognition.INSTANCE.lib_clsClear();
        System.out.println(chooice);

        switch(chooice){
            case 0 :  getcameraip();break;
            case 1 :  fun.DisConnectCamera("192.168.1.23");break;
            case 2 :  fun.StartStream("192.168.1.23");break;
            //case 3:  fun.AddFace("192.168.1.23");break;
            case 4 :  fun.degregmng("192.168.1.23");break;
            case 5 :  fun.searchcerme();break;
            case 6 :  fun.SettingWG("192.168.1.23");break;
            case 7 :  fun.SettingLight("192.168.1.23");break;
            case 8:  fun. Repetition ("192.168.1.23");break;
            case 9 :  fun.SimilaritySetting("192.168.1.23");break;
            case 10 :  fun.TagSetting("192.168.1.23");break;
            case 11 :  fun.TimeSetting("192.168.1.23");break;
            case 12 :  fun.upgrade("192.168.1.23");break;
            case 13 :  fun.GorgelineSetting("192.168.1.23");break;
            case 14 :  fun.webSetting("192.168.1.23");break;
            default : return false;
                //语句
        }
        return  true;
    }

    public static void getcameraip()
    {
       // Scanner sc = new Scanner(System.in);
      //  System.out.println("请输入相机ip");
       // String str = sc.nextLine();
        String str="192.168.1.23";
        fun.connectCamera(str);


    }





}
