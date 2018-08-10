package com.summarized.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * http 工具类
 * @author chenjing03
 * Created on 2018-08-08
 */
public class HttpUtil {

    /**
     * post json格式数据，UTF-8编码
     * @param url 请求地址
     * @param jsonParam json格式数据
     * @return 服务端响应数据
     */
    public static String postJson(String url, String jsonParam) {
        StringBuilder result = new StringBuilder();
        CloseableHttpResponse httpResponse = null;
        try {
            CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // set header to json
            httpPost.addHeader("Content-type","application/json");
            StringEntity stringEntity = new StringEntity(jsonParam,StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
            httpResponse = httpClient.execute(httpPost);
            System.out.println(" httpResponseStatus " + httpResponse.getStatusLine().getStatusCode());
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                result.append(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
                return result.toString();
            }else{
                System.out.println(httpResponse.getStatusLine().getStatusCode() + ":" + EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return null;
    }
}

