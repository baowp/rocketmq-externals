package org.apache.rocketmq.console.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: DingService
 * @Athor: baowp
 * @CreateTime: 2021/7/21 15:17
 * @Description:
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "ding")
public class DingService implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    //请求地址以及access_token
    String webhook = "https://oapi.dingtalk.com/robot/send?access_token=";

    String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    //密钥
    String secret;
    RestTemplate restTemplate = new RestTemplate();
    /*
     ** 生成时间戳和验证信息
     */

    public String encode() throws Exception {
        //获取时间戳
        Long timestamp = System.currentTimeMillis();
        //把时间戳和密钥拼接成字符串，中间加入一个换行符
        String stringToSign = timestamp + "\n" + secret;
        //声明一个Mac对象，用来操作字符串
        Mac mac = Mac.getInstance("HmacSHA256");
        //初始化，设置Mac对象操作的字符串是UTF-8类型，加密方式是SHA256
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        //把字符串转化成字节形式
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        //新建一个Base64编码对象
        Base64.Encoder encoder = Base64.getEncoder();
        //把上面的字符串进行Base64加密后再进行URL编码
        String sign = URLEncoder.encode(new String(encoder.encodeToString(signData)), "UTF-8");
        String result = "&timestamp=" + timestamp + "&sign=" + sign;
        return result;
    }

    public void dingtalk(String message) {
        String url = null;
        try {
            url = webhook + accessToken + encode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf8");
        Map requestBody = new HashMap<>();
        Map<String, String> text = new HashMap<>();
        text.put("content", message);
        requestBody.put("text", text);
        requestBody.put("msgtype", "text");
        logger.info("sent to dingtalk: {}", message);
        HttpEntity httpEntity = new HttpEntity(requestBody, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(url, httpEntity, HashMap.class);
        response.getStatusCode();
        logger.info("dingtalk responded: {},{}", response.getStatusCode(), response.getBody());
    }

    public static void main(String[] args) {
        new DingService().dingtalk("测试b test");
//        dingRequest("dingRequest");
    }

    private static DingService dingService;

    public static DingService getInstance() {
        return dingService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        dingService = applicationContext.getBean(DingService.class);
    }
}
