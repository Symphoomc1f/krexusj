package com.java110.things.factory;

import com.java110.things.config.Java110Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 图片工厂类
 */
public class ImageFactory {

    private static Logger logger = LoggerFactory.getLogger(ImageFactory.class);

    public static boolean GenerateImage(String imgStr, String imagePath) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        imgStr = imgStr.replaceAll("\r|\n", "");
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            String filename = Java110Properties.getCloudFacePath() + File.pathSeparator + imagePath;//新生成的图片
            OutputStream out = new FileOutputStream(filename);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否存在 文件
     *
     * @param imagePath
     * @return true 存在 false 不存在
     */
    public static boolean existsImage(String imagePath) {
        File file = new File(Java110Properties.getCloudFacePath() + File.pathSeparator + imagePath);
        return file.exists();
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

            File file = new File(Java110Properties.getCloudFacePath() + File.pathSeparator + imagePath);

            file.delete();

        } catch (Exception e) {
            logger.error("删除图片失败", e);
        }
    }

    /**
     *  清空图片
     * @param imageDirectoryPath 文件目录
     */
    public static void clearImage(String imageDirectoryPath) {
        if (!existsImage(imageDirectoryPath)) {
            return;
        }
        File file = new File(Java110Properties.getCloudFacePath() + File.pathSeparator + imageDirectoryPath);

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


}
