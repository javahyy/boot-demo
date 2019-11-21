package boot.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @description:rsa 签名加密
 * @author: hyy
 * @time: 2019-09-27 13:32
 */
public class RsaUtils {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    private static final int KEY_SIZE = 2048;

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA"; // MD5withRSA  SHA256WithRSA

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();
    }

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        //             return new String(Base64.decodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData, "UTF-8");
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(key);
        signature.update(srcData.getBytes());
//        byte[] dataBytes = Base64.encodeBase64();
//        dataBytes = Base64.decodeBase64();
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static void main(String[] args) {
        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data = "待加密的文字内容";
            String encryptData = encrypt(data, getPublicKey(publicKey));
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, getPrivateKey(privateKey));
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, getPrivateKey(privateKey));
            // RSA验签
            boolean result = verify(data, getPublicKey(publicKey), sign);
            System.out.print("验签结果:" + result);


            //=====
            test();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }


    public static void test() {
        System.out.println("=======1111=========" + System.currentTimeMillis() / 1000);
        String enterpriseCode = "test02";
        String billMonth = "201803";
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", "69aec9edc0687e19cc2dca0e4dd73d4b");
        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("enterprise_code", enterpriseCode);
        params.put("bill_month", billMonth);
        String signStr = getSignStr(params);


        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCfaqXxB/9vPDyPBqjTM5Z/u/V8MVQwDQas+/ZZhZ9AfQsrQqOSdlJMNYytS9fJmq6EVcG5g7SWzvyFVDY/o+IgNdfw8RHOc1GKkiawo3z5u0F6w2gw0ztV+Q1Mg4jike5NO5O8UG/iHqrm8gUXp2N/irvWxpUZ9SPDe8iAAAvOL3f4DhxJX2tZHfQCc26t2XSGGLOT/EzHcUDN5D4/zNHHFJEeSDexhRgIp36YWmrg4Qs07xfI4CQJ0BKoWI9insrMw1gUCKPYnqW8MAFPGOOdHnXf46coW1nb2AXD8xfc2DWKeQsvxmOlVJ853s9FSthu/bQNgzoQ5HsfSeMfaQNTAgMBAAECggEAZAVG7m7FLmaN3HMiM7k3YRduE6jjmG5j1N8Vlt+GprCN9GuQM5G3WwdyJzzhvCUn//HKZXokMUbXG1RV4SIS+4Cs5whp0q156MaALe21uP5rO/okghXWMb/cPzh8ByPmb2DE9a9GM4poUhRha5DjlZ9W8Y+Q90NeWAVvLBhbT9ZrmWyq8byOVQS4MwTgr+mCGPcGlfW5Cbf1I31IlnJ7CHah200dCyzrTXU7F9cgtfIXSeKKJSoarKbAtKNmNUV7jaoiUTePOM7XULJ/C6U2wn7fH0LEhRTEc8fky2Ii/2NzJm88z3TmPnD/dox0MKytdY8hdQZOz4QK4vvZS3kVgQKBgQDhbUUtLIHOupNTXRE1E0D7QTIsfhhg6NnxvVuGXyjUF1wRLo37//matZgu69GcXip9Q+hSRSmanbLYCE17irQaEkemJ8NaoKpgvxl78PnQbI0KIb9yIO+a6ZUQByUoQeKu2SXVJsW33ncyaT7911Hsne/iWSME5dC+EXFnlHnVkwKBgQC1CYda5FA108WlpaHa14BvipxBnI2bP5ocgg86FhOUbTZT+4NedofLbnANDp76y2NzWeOjlE8+ue82LwgD7qfJ5HDmR63YnBJG+KgGyHz1PspHDZcIErzXGvnyLLOl61ggpVG4uThrQaMImCTg0ax0ZCd+kFkvnBVGFaErqcyzQQKBgQCIzMHtLxQ7OzhKn7Dgj6WglCj5nS/4J3aTYAFZ4weo5eAmQSVC5003GLqYxtz2UqLgT8R9oKElxkuRFEDJFxxCshMu/vRrXBLpES9K6qr/U9CndGF0xg4B6XxfNKbU7l69EuQFnTjhYyzct8fVTcTWneXEa6l3wGtT3MwAOrdQ5wKBgQCUbtmh9myllZsKNAG/Es5lpfpz+U5Jqh5LnX+qmos3OUxAE4okm36/ly8cJY7IBpbl7x+r8WAACCdP/8PMpZLBACJpTQAHCAAcXKg3145M7kyfcU8R+XsHy0CABZLOSWYabjHRCVxu5IQt+cDS7Fc/nxaauPPHC2yN6BIbYL9LgQKBgD3zXmBwXFp2IaPnGpPRjXiKHIRtt5mVZTWw0ZbPQbbdbD4ALNXsDHp72MBI3MiPbEMZTsfZ/rPBxU0gH/y1RjWXl12pph5hLsJ9H+ZaYVbEm9wZwDymc8Nup2UplT7zsMd1s1vUBDUKXpDegDITQgzBPLJStz7nG8GgmL3M9nLQ";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn2ql8Qf/bzw8jwao0zOWf7v1fDFUMA0GrPv2WYWfQH0LK0KjknZSTDWMrUvXyZquhFXBuYO0ls78hVQ2P6PiIDXX8PERznNRipImsKN8+btBesNoMNM7VfkNTIOI4pHuTTuTvFBv4h6q5vIFF6djf4q71saVGfUjw3vIgAALzi93+A4cSV9rWR30AnNurdl0hhizk/xMx3FAzeQ+P8zRxxSRHkg3sYUYCKd+mFpq4OELNO8XyOAkCdASqFiPYp7KzMNYFAij2J6lvDABTxjjnR513+OnKFtZ29gFw/MX3Ng1inkLL8ZjpVSfOd7PRUrYbv20DYM6EOR7H0njH2kDUwIDAQAB";

        privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC13p104FbV3jMxN7p2i50Jyl2KESj8Q8plxNhiX9hwvGxubiyBS88Da5CagKr30eKjKYMRuETDVcKvMxy7FJWnDnMviGd/pUHXHHAsd1/fwAvYzAUUCk2lDXxnIwXF7JWGN0/FmceWmSvFdABPhDlYyLKW0gs4IB+BawcMawuf44MjxhgnNuTaC7mcw0P1A+5eZ0UhJS26ll+1aZZOp6H1EGsXe0UtRorWW660irx1zSxq3u0m3VWODcLvZW/eQd3SvAEi+npvR0s+MCyyKxk8KHnbOjDBMJuggjCaVCC2HLJWcoLqVTo+6gcDvpdlUtVnq0AFaxU6YX/+/0O36GLvAgMBAAECggEBALQxheZ4I/Ewf54jLg8oUwaRR051pBsVe6KLfy0w83z3Y6Oia1wL5WbpG1PJahuyC5BHoAJdhVvYS9VUmgHN6lyjl4Kf7pPtA6gDGC0mdYp2eAZsVhe+AaIoE3sImz64IonhoZsRu0u+JT00qt4eDselyjRelhkc0rpidZBbOyBFGk4V+TN6lcsNrs470SRfqsOqevcYlrI+eOSkFv/pohDr6bu7eDGncJ4DoH+o4OF0ixK2qQZFZhvKfYRIbQgNKwkg4Zq0M14/oGlQ6X05sph83inNjHCqUnTRfi1Ytid6M3uil6lPpEdzkNQF9RK3u2MywDJgkbpgAsa4MxeoNUkCgYEA3DX10heLRr3FdQIJunnO7MOi1lxpKWX+kB8+aoXbWm0zOFvrauW6OtmaP9HJgAad/Erk8ijmEwXFqLnptS2PHvhd7KtJIGqgmkBslurtxHFkWOiY7xSN2OqhYZxl+X6R8sO0ijCYBPM4UGEUQ8bcXbADbIz/+SK0A/y99NU7WBMCgYEA021wnXQRUGGL9edumJWXWBkq64sPnXNuR5/M4AhdYFK1Amoax5dnx6oyujvy9AA8bcj1Ry18xaLnNaxm5U2YHAaJM029uxrjEo6igArnhtyEel6W1uTniZcxlUIZ1p1/jDr4JneUEMugNR8CbgwJw5BhLKheScldR/DhaC5nHTUCgYByfRd+EEGYGOgFKncWXWgIzrnliwwduup70dKonUlrHSUpIxklbaTCHgpFLfP+PPJ8jg+0GzFrKHAYhfgM+0/SwTxL5M3TLbysFdyfJitSG8YZN2zzuBL7eRlIX4DhEz4cIkXGJveSc0WO/LoFdulxSCavlCstQy6meAvms8nzwwKBgDtSNYq4CjOXawGv0aQWNMSPKy+zcMoonQSTnZQuRKTGD+uBgcoGfSPUaS8/qsczWcEj5U33QU7iiiTRHn2v68HRQ6WVZmqc/KE0S/vJE3UPYkxDRec9iolcVpkeVsmUnzjlQsLoQi8EMws0jCX8UrEATYOOKgZh7T5GI7hrbJsRAoGAIugfwe/6+80obuQZJsXFNA+2SUxvQD0I2AavXN5Z1GIgQOElewWPGpv4FhAWZY3xSThMnR1TopYyaPJGnugJFV5Gj1ytMl2+F2gSuPUdUr7gOxy5qzasi8Rc5WyMjxcxld9pNB2M0j2Z/nZX903N9c/TUt8NGUuOciCbEW/69dw=";
        publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtd6ddOBW1d4zMTe6doudCcpdihEo/EPKZcTYYl/YcLxsbm4sgUvPA2uQmoCq99HioymDEbhEw1XCrzMcuxSVpw5zL4hnf6VB1xxwLHdf38AL2MwFFApNpQ18ZyMFxeyVhjdPxZnHlpkrxXQAT4Q5WMiyltILOCAfgWsHDGsLn+ODI8YYJzbk2gu5nMND9QPuXmdFISUtupZftWmWTqeh9RBrF3tFLUaK1luutIq8dc0sat7tJt1Vjg3C72Vv3kHd0rwBIvp6b0dLPjAssisZPCh52zowwTCboIIwmlQgthyyVnKC6lU6PuoHA76XZVLVZ6tABWsVOmF//v9Dt+hi7wIDAQAB";

        try {
            // RSA签名
            String sign = sign(signStr, getPrivateKey(privateKey));
            // RSA验签
            boolean result = verify(signStr, getPublicKey(publicKey), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSignStr(Map<String, ? extends Object> params) {
        if (params == null) {
            return null;
        } else {
            params.remove("sign");
            params.remove("sign_type");
            StringBuffer content = new StringBuffer();
            List<String> keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i) {
                String key = (String) keys.get(i);
                String value = (String) params.get(key);
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }

            return content.toString();
        }
    }

}
