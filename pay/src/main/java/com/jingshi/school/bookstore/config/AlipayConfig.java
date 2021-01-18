package com.jingshi.school.bookstore.config;

import java.io.FileWriter;
import java.io.IOException;

public class AlipayConfig {
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "";
    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "";
    // 异步通知到以POST请求发送
    public static String notify_url = "http://123.56.228.188:8090/pay/alipay/alipayNotifyNotice";
    public static String return_url = "http://123.56.228.188:8090/pay/alipay/alipayReturnNotice";
    public static String sign_type = "RSA2";
    public static String charset = "utf-8";
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";//注意：沙箱测试环境，正式环境为：https://openapi.alipay.com/gateway.do

}