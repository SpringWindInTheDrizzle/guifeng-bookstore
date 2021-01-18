/**
 * FileName: PhoneCodeUtil
 * Author:   sky
 * Date:     2020/4/12 14:38
 * Description:
 */
package com.jingshi.school.bookstore.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.jingshi.school.bookstore.config.StaticPeram;

/**
 * @author sky
 * @create 2020/4/12
 * @since 1.0.0
 */
public class PhoneCodeUtil {
    /**
     * 阿里云短信服务配置
     *
     * @param mobile
     * @return
     */
    public static String getPhonemsg(String mobile, String code) {
        System.setProperty(StaticPeram.defaultConnectTimeout, StaticPeram.Timeout);
        System.setProperty(StaticPeram.defaultReadTimeout, StaticPeram.Timeout);
        final String product = StaticPeram.product;
        final String domain = StaticPeram.domain;
        final String accessKeyId = StaticPeram.accessKeyId;
        final String accessKeySecret = StaticPeram.accessKeySecret;
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e1) {
            e1.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(mobile);
        request.setSignName(StaticPeram.SignName);
        request.setTemplateCode(StaticPeram.TemplateCode);
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                System.out.println("获取验证码成功！！！");
                return "true";
            } else {
                System.out.println(sendSmsResponse.getCode());
                System.out.println("获取验证码失败...");
                return "false";
            }
        } catch (ServerException e) {
            e.printStackTrace();
            return "由于系统维护，暂时无法注册！！！";
        } catch (ClientException e) {
            e.printStackTrace();
            return "由于系统维护，暂时无法注册！！！";
        }
    }

    /**
     * 生成4位随机数验证码
     *
     * @return
     */
    public static String vcode() {
        String vcode = "";
        for (int i = 0; i < 4; i++) {
            vcode = vcode + (int) (Math.random() * 9);
        }
        return vcode;
    }

}