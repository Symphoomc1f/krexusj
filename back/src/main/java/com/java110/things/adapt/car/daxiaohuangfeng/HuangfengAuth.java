package com.java110.things.adapt.car.daxiaohuangfeng;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.factory.MappingCacheFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Base64;

public class HuangfengAuth {

    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS7Padding";

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";

    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }


    /**
     * DES加密字符串
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data     待加密字符串
     * @return 加密后内容
     */
    public static String encrypt(String password, String data, String ivs) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

            //JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            //Android平台可以使用android.util.Base64
            return new String(Base64.getEncoder().encode(bytes));

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data     待解密字符串
     * @return 解密后内容
     */
    public static String decrypt(String password, String data, String ivs) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(CHARSET))), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 生成 data 加密数据
     *
     * @param paramIn
     * @return
     */
    public static JSONObject encryptData(JSONObject paramIn) {
        String data = paramIn.getJSONObject("data").toJSONString();
        String key = MappingCacheFactory.getValue("HUANG_FENG_KEY");
        String iv = MappingCacheFactory.getValue("HUANG_FENG_IV");
        data = encrypt(key, data, iv);
        paramIn.put("data", data);
        return paramIn;
    }

    /**
     * 解密数据
     *
     * @param paramIn
     * @return
     */
    public static JSONObject decryptData(JSONObject paramIn) {
        String data = paramIn.getString("data");
        String key = MappingCacheFactory.getValue("HUANG_FENG_KEY");
        String iv = MappingCacheFactory.getValue("HUANG_FENG_IV");
        data = decrypt(key, data, iv);
        paramIn.put("data", JSONObject.parseObject(data));
        return paramIn;
    }
}
