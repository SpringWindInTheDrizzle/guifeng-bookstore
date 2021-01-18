/**
 * FileName: AliPayController
 * Author:   sky
 * Date:     2020/4/10 19:17
 * Description:
 */
package com.jingshi.school.bookstore.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;

import com.jingshi.school.bookstore.model.entity.Flow;
import com.jingshi.school.bookstore.model.entity.Order;
import com.jingshi.school.bookstore.service.FlowService;
import com.jingshi.school.bookstore.service.OrderService;
import com.jingshi.school.bookstore.config.AlipayConfig;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Controller
@RequestMapping("/pay")
public class AliPayController {

    @Reference(version = "1.0.0")
    OrderService orderService;

    @Resource
    FlowService flowService;

    //退款
    @PostMapping("/alipay/refund")
    @ResponseBody
    public Result refund(@RequestParam("orderId") Long orderId) throws AlipayApiException {
        /*
        //退款查询
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();

        //商户订单号，商户网站订单系统中唯一订单号
        String out_trade_no = orderid;
        Flow flow=flowService.selectByPrimaryKey(orderid);
        //支付宝交易号
        String trade_no = flow.getFlowNum();
        //请二选一设置
        //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
        String out_request_no ="123456";
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                +"\"trade_no\":\""+ trade_no +"\","
                +"\"out_request_no\":\""+ out_request_no +"\"}");
        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        */
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();

        Order order = orderService.getOrderDetailByOrderNo(null, orderId);
        //商户订单号，商户网站订单系统中唯一订单号
        String out_trade_no = order.getOrderNo();
        orderService.cancelOrder(order.getOrderNo(), null);
        Flow flow = flowService.selectByPrimaryKey(out_trade_no);
        //支付宝交易号
        String trade_no = flow.getFlowNum();

        //请二选一设置
        //需要退款的金额，该金额不能大于订单金额，必填
        String refund_amount = String.valueOf(order.getPayment());
        //退款的原因说明
        String refund_reason = "用户申请退款";

        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
        String out_request_no = String.valueOf(flow.getId());

        alipayRequest.setBizContent("{" +
                "\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"trade_no\":\""+ trade_no +"\","
                + "\"refund_amount\":\""+ refund_amount +"\","
                + "\"refund_reason\":\""+ refund_reason +"\","
                + "\"out_request_no\":\""+ out_request_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        return ResultGenerator.genSuccessResult();
    }

    //TODO 去支付
    @GetMapping(value = "/alipay/gopay/{orderId}", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String gopay(@PathVariable("orderId") Integer orderId) throws Exception {
        Order order = orderService.getOrderDetailByOrderNo(null, Long.valueOf(orderId));

        // Product product = productService.selectByPrimaryKey(orders.getProductId());
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = order.getOrderNo();
        //付款金额，必填
        String total_amount = String.valueOf(order.getPayment());
        //订单名称，必填
        String subject = String.valueOf(order.getId());
        //商品描述，可空
        // String body = "用户订购商品个数：" + order.();

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "15m";

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"timeout_express\":\""+ timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
    }

    @RequestMapping(value = "/alipay/alipayReturnNotice")
    public String alipayReturnNotice(HttpServletRequest request) throws Exception {

        System.out.println("支付成功, 进入同步通知接口...");
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

//            //修改叮当状态，改为 支付成功，已付款;
//            Order order = orderService.getOrderDetailByOrderNo(out_trade_no, null);
//            order.setStatus((byte) 1);
//            orderService.updateOrderInfo(order);
//
//            //同时新增支付流水
//            Flow flow = new Flow();
//            flow.setFlowNum(trade_no);
//            flow.setOrderNo(out_trade_no);
//            flowService.insert(flow);

            System.out.println("********************** 支付成功(支付宝同步通知) **********************");
            System.out.println("* 订单号: {}"+ out_trade_no);
            System.out.println("* 支付宝交易号: {}"+ trade_no);
            System.out.println("* 实付金额: {}"+total_amount);
            System.out.println("***************************************************************");

            request.setAttribute("out_trade_no",out_trade_no);//订单编号
            request.setAttribute("trade_no",trade_no);//支付宝交易号
            request.setAttribute("total_amount",total_amount);//实付金额
        }else {
            System.out.println("支付, 验签失败...");
        }
        return "alipaySuccess";
    }

    @RequestMapping(value = "/alipay/alipayNotifyNotice")
    @ResponseBody
    public String alipayNotifyNotice(HttpServletRequest request) throws Exception {
        System.out.println("支付成功, 进入异步通知接口...");

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——

		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
        if(signVerified) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意： 尚自习的订单没有退款功能, 这个条件判断是进不来的, 所以此处不必写代码
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知

                // 修改叮当状态，改为 支付成功，已付款; 同时新增支付流水
                //修改叮当状态，改为 支付成功，已付款;
                Order order = orderService.getOrderDetailByOrderNo(out_trade_no, null);
                order.setStatus((byte) 1);
                order.setPaymentTime(new Date());
                orderService.updateOrderInfo(order);

                //同时新增支付流水
                Flow flow = new Flow();
                flow.setFlowNum(trade_no);
                flow.setOrderNo(out_trade_no);
                flowService.insert(flow);

                System.out.println("********************** 支付成功(支付宝异步通知) **********************");
                System.out.println("* 订单号: {}"+ out_trade_no);
                System.out.println("* 支付宝交易号: {}"+trade_no);
                System.out.println("* 实付金额: {}"+total_amount);
                System.out.println("***************************************************************");
            }
            System.out.println("支付成功...");

        }else {//验证失败
            System.out.println("支付, 验签失败...");
        }

        return "success";
    }


}