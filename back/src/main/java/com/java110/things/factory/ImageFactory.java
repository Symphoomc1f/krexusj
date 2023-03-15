package com.java110.things.factory;

import com.java110.things.config.Java110Properties;
import com.java110.things.util.Base64Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * 图片工厂类
 */
public class ImageFactory {

    private static Logger logger = LoggerFactory.getLogger(ImageFactory.class);



    public static String GenerateImage(String imgStr, String imagePath) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) { //图像数据为空
            return "";
        }
        imgStr = imgStr.replaceAll("\r|\n", "");
        System.out.printf("imgStr=" + imgStr);
        BASE64Decoder decoder = new BASE64Decoder();
        BASE64Encoder encoder = new BASE64Encoder();
        OutputStream out = null;
        InputStream is = null;
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }

            is = new ByteArrayInputStream(b);
            BufferedImage img = ImageIO.read(is);
            b = bufferedImageTobytes(img, 0.8F);
            //生成jpeg图片
            String filename = Java110Properties.getCloudFacePath() + imagePath;//新生成的图片
            File file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            out = new FileOutputStream(filename);
            out.write(b);
            out.flush();
            return encoder.encode(b);
        } catch (Exception e) {
            logger.error("存储人脸失败", e);
            return imgStr;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否存在 文件
     *
     * @param imagePath
     * @return true 存在 false 不存在
     */
    public static boolean existsImage(String imagePath) {
        File file = new File(Java110Properties.getCloudFacePath() + imagePath);
        boolean exists = file.exists();

        logger.debug("判断文件是否存在" + exists + " , 文件路径" + Java110Properties.getCloudFacePath() + imagePath);

        return exists;
    }

    /**
     * 删除文件
     *
     * @param imagePath
     */
    public static void deleteImage(String imagePath) {
        try {
            if (!existsImage(imagePath)) {
                return;
            }

            File file = new File(Java110Properties.getCloudFacePath() + imagePath);

            file.delete();

        } catch (Exception e) {
            logger.error("删除图片失败", e);
        }
    }

    /**
     * 清空图片
     *
     * @param imageDirectoryPath 文件目录
     */
    public static void clearImage(String imageDirectoryPath) {
        if (!existsImage(imageDirectoryPath)) {
            return;
        }
        File file = new File(Java110Properties.getCloudFacePath() + imageDirectoryPath);

        if (!file.isDirectory()) {
            return;
        }

        File[] files = file.listFiles();

        for (File tmpFile : files) {

            if (!tmpFile.isFile()) {
                continue;
            }
            tmpFile.delete();
        }
    }

    /**
     * 自己设置压缩质量来把图片压缩成byte[]
     *
     * @param image   压缩源图片
     * @param quality 压缩质量，在0-1之间，
     * @return 返回的字节数组
     */
    private static byte[] bufferedImageTobytes(BufferedImage image, float quality) {
        // 如果图片空，返回空
        /*if (image == null) {
            return null;
        } */
        byte[] imgByte = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            // 得到指定Format图片的writer
            Iterator<ImageWriter> iter = ImageIO
                    .getImageWritersByFormatName("jpg");// 得到迭代器
            ImageWriter writer = (ImageWriter) iter.next(); // 得到writer

            // 得到指定writer的输出参数设置(ImageWriteParam )
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // 设置可否压缩
            iwp.setCompressionQuality(quality); // 设置压缩质量参数

            iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);

            ColorModel colorModel = image.getColorModel();
            // 指定压缩时使用的色彩模式
            iwp.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel,
                    colorModel.createCompatibleSampleModel(16, 16)));

            // 开始打包图片，写入byte[]
            byteArrayOutputStream = new ByteArrayOutputStream(); // 取得内存输出流
            IIOImage iIamge = new IIOImage(image, null, null);
            try {
                // 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput
                // 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput
                writer.setOutput(ImageIO
                        .createImageOutputStream(byteArrayOutputStream));
                writer.write(null, iIamge, iwp);

            } catch (IOException e) {
                System.out.println("write errro");
                e.printStackTrace();
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imgByte = byteArrayOutputStream.toByteArray();

            return imgByte;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getBase64ByImgUrl(String url) {
        String suffix = url.substring(url.lastIndexOf(".") + 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            URL urls = new URL(url);

            Image image = Toolkit.getDefaultToolkit().getImage(urls);
            BufferedImage biOut = toBufferedImage(image);
            ImageIO.write(biOut, suffix, baos);
            String base64Str = encoder.encode(baos.toByteArray());
            return base64Str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }


    /**
     * 将网络图片编码为base64
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static String encodeImageToBase64(String urlStr) throws Exception {

        URL url = new URL(urlStr);
//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        System.out.println("图片的路径为:" + url.toString());
//打开链接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
//设置请求方式为"GET"
            conn.setRequestMethod("GET");
//超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
//通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
//得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
            System.out.println("网络文件[{}]编码成base64字符串:[{}]"+url.toString()+base64);
            return base64;//返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("图片上传失败,请联系客服!");
        }
    }

    public static void main(String[] args) throws Exception {
        String imgFilePath="https://bisen-temp-iot.oss-cn-beijing.aliyuncs.com/bisen-temp-iot/bs/face/40230b92623d42b59446a07185a1df2f.jpg";
        String base64_str = encodeImageToBase64(imgFilePath);//将网络图片编码为base64
//String base64_str = Base64Util.getImageBinary("D:/test.jpg","jpg");//将本地图片编码为base64
        System.out.println(base64_str);
    }
}
