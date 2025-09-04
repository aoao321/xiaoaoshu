package com.aoao.framework.common.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aoao.framework.common.properties.AliSmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aoao
 * @create 2025-08-22-21:33
 */
@Slf4j
@Component
public class AliSmsHelper {

    @Autowired
    private Client client;
    @Autowired
    private AliSmsProperties smsProperties;

    // 发送短信方法
    public  void sendVerificationCode(String phoneNumber, String code) {
        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(smsProperties.getSignName()) // 需在阿里云控制台申请
                    .setTemplateCode(smsProperties.getTemplateCode()) // 需在阿里云控制台申请
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);

            if ("OK".equals(response.getBody().getCode())) {
                System.out.println("短信发送成功");
            } else {
                System.err.println("发送失败：" + response.getBody().getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
