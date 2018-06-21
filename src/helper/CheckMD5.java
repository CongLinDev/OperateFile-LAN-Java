package helper;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class CheckMD5 {
	/**
	 * 获得文件的MD5编码
	 * MD5用于检查文件内容是否一致
	 * @param file
	 * @return
	 */
    public static String getMd5ByFile(File file){
        // 计算文件的 MD5 值
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest =MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 判断两个路径对应文件是否内容相同
     * @param sourceFile
     * @param destinationFile
     * @return
     */
    public static boolean filesAreEqual(File sourceFile, File destinationFile){
        return (getMd5ByFile(sourceFile).equals(getMd5ByFile(destinationFile)));
    }

}
