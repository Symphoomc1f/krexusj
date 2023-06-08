package com.java110.things.adapt.car.zhenshi;


import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author zhouhe
 * @Date 2019/10/14 17:41
 */
public class pingxian extends Thread {

    //定义一个Socket对象
    Socket socket = null;
    private static String host = "192.168.1.110";
    private static int port = 80;

    public pingxian() {
        try {
            //需要服务器的IP地址和端口号，才能获得正确的Socket对象
            socket = new Socket(host, port);
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        //客户端一连接就可以写数据个服务器了
        super.run();
        try {
            // 读Sock里面的数据
            InputStream s = socket.getInputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            oos.writeObject("00 64 FF FF 62 23 01 01 01 00 05 01 01 03 00 00 \n" +
                    "FF 00 00 00 00 00 00 10 00 BC F5 CB D9 C2 FD \n" +
                    "D0 D0 C7 EB CE F0 B8 FA B3 B5 D9 2F \n");
            oos.flush();

            for(;;) {
                while ((len = s.read(buf)) != -1) {
                    System.out.println(new String(buf, 0, len));
                }
            }
        } catch (Exception e) {
            System.out.println("socket连接断开！");
        }
    }

    //函数入口
    public static void main(String[] args) {
        //需要服务器的正确的IP地址和端口号

        pingxian clientTest = new pingxian();
        clientTest.start();

    }
}
