package com.java110.things.factory;

import com.java110.things.config.Java110Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * 图片工厂类
 */
public class ImageFactory {

    private static Logger logger = LoggerFactory.getLogger(ImageFactory.class);

    public static void main(String[] args) {
        String req = "data:image/jpeg;base64,AQSkZJRgABAQAAAQABAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEB";
        req = req.substring(req.indexOf("base64,") + 7);

        System.out.printf("req" + req);
    }

    public static String GenerateImage(String imgStr, String imagePath) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) { //图像数据为空
            return "";
        }
        imgStr = imgStr.replaceAll("\r|\n", "");
        System.out.printf("imgStr=" + imgStr);
        BASE64Decoder decoder = new BASE64Decoder();
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }

            InputStream is = new ByteArrayInputStream(b);
            BufferedImage img = ImageIO.read(is);
            b = bufferedImageTobytes(img, 0.8F);
            //生成jpeg图片
            String filename = Java110Properties.getCloudFacePath() + imagePath;//新生成的图片
            File file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(filename);
            out.write(b);
            out.flush();
            out.close();
            return encoder.encode(b);
        } catch (Exception e) {
            logger.error("存储人脸失败", e);
            return imgStr;
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // 取得内存输出流
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
        }
        return byteArrayOutputStream.toByteArray();
    }


}
