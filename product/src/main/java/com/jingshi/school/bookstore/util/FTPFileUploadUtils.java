/**
 * FileName: FTPFileUploadUtils
 * Author:   sky
 * Date:     2020/6/12 14:50
 * Description:
 */
package com.jingshi.school.bookstore.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 *
 * @author sky
 * @create 2020/6/12
 * @since 1.0.0
 */
public class FTPFileUploadUtils {

    //ftp服务器ip地址
    private static final String FTP_ADDRESS = "101.200.176.41";
    //端口号
    private static final int FTP_PORT = 21;
    //用户名
    private static final String FTP_USERNAME = "jingshi";
    //密码
    private static final String FTP_PASSWORD = "chenyangno1";
    //图片路径
    private static final String FTP_BASEPATH = "/home/jingshi";

    public static boolean uploadFile(String originFileName, InputStream input){
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            ftp.connect(FTP_ADDRESS, FTP_PORT);// 连接FTP服务器
            ftp.login(FTP_USERNAME, FTP_PASSWORD);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(FTP_BASEPATH );
            ftp.changeWorkingDirectory(FTP_BASEPATH );
            success = ftp.storeFile(originFileName,input);
            System.out.println(ftp.getReplyCode());
            input.close();
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}