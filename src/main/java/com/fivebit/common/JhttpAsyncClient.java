package com.fivebit.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fivebit on 2017/5/19.
 */
public class JhttpAsyncClient {

    private static String host = "";

    public static Object sendReq(){
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        HttpGet request = new HttpGet(host);
        final  CountDownLatch latch = new CountDownLatch(1);
        httpclient.execute(request,new FutureCallback<HttpResponse>(){

            @Override
            public void completed(HttpResponse httpResponse) {
                latch.countDown();
            }

            @Override
            public void failed(Exception e) {
                latch.countDown();
            }

            @Override
            public void cancelled() {
                latch.countDown();
            }
        });
        try{
            latch.await();
        }catch (InterruptedException ee){

        }
        try{
            httpclient.close();
        }catch (IOException ee){

        }
        return null;
    }
}
