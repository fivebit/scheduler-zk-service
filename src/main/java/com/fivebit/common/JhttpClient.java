package com.fivebit.common;

import com.alibaba.fastjson.JSONObject;
import com.fivebit.errorhandling.AppException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fivebit on 2017/5/19.
 * 这个类，是支持ajax http post 发送
 */
public class JhttpClient {
    private static String content_type ="application/x-www-form-urlencoded";
    private static Logger log = LoggerFactory.getLogger(JhttpClient.class);

    public static JSONObject httpPost(String url,JSONObject jsonParam)throws Exception {
        return JhttpClient.httpPost(url, jsonParam, false);
    }

    /**
     * 删除资源，使用GET方法。不需要返回值
     * @param url
     * @param params
     * @throws Exception
     */
    public static void httpsAjaxDel(String url,Map<String,String> params)throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        CloseableHttpClient client = HttpClients.custom().setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        try {
            String subfix = "?";
            StringBuffer subfix_buff = new StringBuffer();
            for (Map.Entry<String, String> entity : params.entrySet()) {
                subfix_buff.append(subfix);
                subfix_buff.append(entity.getKey());
                subfix_buff.append("=");
                subfix_buff.append(entity.getValue());
                subfix = "&";
            }
            url += subfix_buff.toString();
            HttpGet request = new HttpGet(url);
            client.execute(request);
            Jlog.info("https ajax delurl:" + url);
        }catch ( Exception ee){
            log.error("get request error:" + url, ee.getMessage());
            throw new AppException("0","del ajax error:"+url);
        }
    }

    /**
     * 请求sslhost 的ajax GET 。把params组装成url一部分，放在host后面
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static JSONObject httpsAjaxGet(String url,Map<String,String> params)throws Exception {
        JSONObject jsonResult = null;
        SSLContext sslContext = new SSLContextBuilder() .loadTrustMaterial(null, (certificate, authType) -> true).build();
        CloseableHttpClient client = HttpClients.custom() .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier()) .build();
        try {
            String subfix = "?";
            StringBuffer subfix_buff = new StringBuffer();
            for(Map.Entry<String,String> entity:params.entrySet()) {
                subfix_buff.append(subfix);
                subfix_buff.append(entity.getKey());
                subfix_buff.append("=");
                subfix_buff.append(entity.getValue());
                subfix = "&";
            }
            url+=subfix_buff.toString();
            Jlog.info("https ajax get url:"+url);
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                jsonResult = JSONObject.parseObject(strResult);
            } else {
                log.error("get request error:" + url);
            }
        } catch (IOException e) {
            log.error("get request error:" + url, e);
            throw new AppException("0","https ajax get error");
        }
        return jsonResult;
    }
    public static JSONObject httpsAjaxPost(String url, Map<String,String> params ) throws AppException {
        SSLContext sslContext ;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        }catch (Exception ee){
            log.error("ssl builder error:"+url,ee);
            throw new AppException("0","ajax post error");

        }
        CloseableHttpClient client = HttpClients.custom() .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier()) .build();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        method.setHeader("Content-Type", content_type);
        method.setHeader("X-Requested-With", "XMLHttpRequest");
        try {

            List<NameValuePair> params_body = new ArrayList<NameValuePair>(3);
            for(Map.Entry<String,String> entity:params.entrySet()) {
                params_body.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
            }
            method.setEntity(new UrlEncodedFormEntity(params_body,"UTF-8"));
            HttpResponse result = client.execute(method);
            Jlog.debug("request:"+url+" and return:"+result.getStatusLine());
            url = URLDecoder.decode(url, "UTF-8");
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    str = EntityUtils.toString(result.getEntity(),"UTF-8");
                    Jlog.info("request return entity :"+str);
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    log.error("post request error:" + url, e);
                }
            }
        } catch (IOException e) {
            log.error("post request error:" + url, e);
        }
        Jlog.debug("http post return:"+jsonResult.toJSONString());
        return jsonResult;
    }
    public static Boolean httpsUploadFile(String url,Map<String,String> params,String file_path)throws AppException,Exception {
        SSLContext sslContext ;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        }catch (Exception ee){
            log.error("ssl builder error:"+url,ee);
            throw new AppException("0","ajax post error");

        }
        CloseableHttpClient client = HttpClients.custom() .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier()) .build();
        HttpPost post = new HttpPost(url);
        //post.setHeader("Content-Type","multipart/mixed");
        InputStream inputStream = new FileInputStream(file_path);
       Jlog.info("file:"+file_path+" cc:"+inputStream.toString());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for(Map.Entry<String,String> entity:params.entrySet()) {
            builder.addTextBody(entity.getKey(), entity.getValue(), ContentType.TEXT_PLAIN);
        }
        builder.addBinaryBody ("file", inputStream, ContentType.create("application/zip"), "job.zip");
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        String str = EntityUtils.toString(response.getEntity(),"UTF-8");
        Jlog.info(str);
        return true;
    }
    public static JSONObject httpPost(String url,JSONObject params,Boolean noNeedResponse) throws Exception {
        //CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        DefaultHttpClient client = new DefaultHttpClient();
        /*SSLContext sslContext = new SSLContextBuilder() .loadTrustMaterial(null, (certificate, authType) -> true).build();
        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
                */
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        method.setHeader("Content-Type", content_type);
        method.setHeader("X-Requested-With", "XMLHttpRequest");
        try {
            if ( null != params ) {
                StringEntity entity = new StringEntity(params.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                method.setEntity(entity);
                Jlog.info("method:"+method.getEntity().toString());
            }
            HttpResponse result = client.execute(method);
            Jlog.info("request:"+url+" and return:"+result.getStatusLine());
            url = URLDecoder.decode(url, "UTF-8");
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    str = EntityUtils.toString(result.getEntity(),"UTF-8");
                    Jlog.info("request return entity :"+str);
                    if (noNeedResponse) {
                        return null;
                    }
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    log.error("post request error:" + url, e);
                }
            }
        } catch (IOException e) {
            log.error("post request error:" + url, e);
        }
        Jlog.debug("http post return:"+jsonResult.toJSONString());
        return jsonResult;
    }
    public static JSONObject httpGet(String url){
        JSONObject jsonResult = null;
        try {
            CloseableHttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            //DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                jsonResult = JSONObject.parseObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                log.error("get request error:" + url);
            }
        } catch (IOException e) {
            log.error("get request error:" + url, e);
        }
        return jsonResult;
    }
    public void givenIgnoringSSLClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpGet httpGet = new HttpGet("");
        httpGet.setHeader("Accept", "application/xml");
        HttpResponse response = client.execute(httpGet);
    }
}
