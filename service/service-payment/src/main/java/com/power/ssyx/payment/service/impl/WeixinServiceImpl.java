package com.power.ssyx.payment.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.model.order.PaymentInfo;
import com.power.ssyx.payment.service.PaymentInfoService;
import com.power.ssyx.payment.service.WeixinService;
import com.power.ssyx.payment.utils.ConstantPropertiesUtils;
import com.power.ssyx.payment.utils.HttpClient;
import com.power.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:05
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RedisTemplate redisTemplate;


    // 调用微信支付系统生成预付单
    @Override
    public Map<String, String> createJsapi(String orderNo) {

        // 1.想payment_info支付记录表添加记录，目前支付状态，正在支付中
        PaymentInfo paymentInfo = paymentInfoService.getPaymentInfoByOrderNo(orderNo);

        if (Objects.isNull(paymentInfo)) {
            paymentInfo = paymentInfoService.savePaymentInfo(orderNo);
        }

        // 2.封装微信支付系统接口需要参数
        Map<String, String> paramMap = new HashMap<>();
        //1、设置参数
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", paymentInfo.getSubject());
        paramMap.put("out_trade_no", paymentInfo.getOrderNo());
        int totalFee = paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue();
        paramMap.put("total_fee", String.valueOf(totalFee));
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", ConstantPropertiesUtils.NOTIFYURL);
        paramMap.put("trade_type", "JSAPI");
//			paramMap.put("openid", "o1R-t5trto9c5sdYt6l1ncGmY5iY");

        Long userId = paymentInfo.getUserId();

        String key = RedisConst.USER_LOGIN_KEY_PREFIX + userId.toString();
        UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(key);

        if (!Objects.isNull(userLoginVo) && !StringUtils.isEmpty(userLoginVo.getOpenId())) {
            paramMap.put("openid", userLoginVo.getOpenId());
        } else {
            paramMap.put("openid", "oD7av4igt-00GI8PqsIlg5FROYnI"); // 只有管理员和商户才能完成支付操作
        }

        // 3.使用HttpClient调用微信支付系统接口
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

        // 设置参数，xml格式
        try {
            String xmlData = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY);
            httpClient.setXmlParam(xmlData);
            httpClient.setHttps(true);
            httpClient.post();
            // 4.调用微信支付系统接口之后，返回结果 prepay_id
            String xml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            // 5.封装需要数据-包含预付单表示 prepay_id
            String prepayId = String.valueOf(resultMap.get("prepay_id"));
            String packages = "prepay_id=" + prepayId;
            resultMap.put("appId", ConstantPropertiesUtils.APPID);
            resultMap.put("nonceStr", resultMap.get("nonce_str"));
            resultMap.put("package", packages);
            resultMap.put("signType", "MD5");
            resultMap.put("timeStamp", String.valueOf(new Date().getTime()));
            String sign = WXPayUtil.generateSignature(resultMap, ConstantPropertiesUtils.PARTNERKEY);

            //返回结果
            Map<String, String> result = new HashMap();
            result.put("timeStamp", resultMap.get("timeStamp"));
            result.put("nonceStr", resultMap.get("nonceStr"));
            result.put("signType", "MD5");
            result.put("paySign", sign);
            result.put("package", packages);

            // 6.返回结果
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 查询订单支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        // 1.封装参数
        Map<String, String> paramMap = new HashMap();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            // 3.得到返回结果
            String xmlContent = client.getContent();
            Map<String, String> stringMap = WXPayUtil.xmlToMap(xmlContent);

            return stringMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
