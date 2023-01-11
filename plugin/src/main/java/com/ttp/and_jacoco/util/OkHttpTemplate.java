package com.ttp.and_jacoco.util;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

/**
 * OkHttp请求工具服务类
 *
 * @description:
 * @author: hujunjie
 * @date: 2023/1/11 17:50
 */

public class OkHttpTemplate {

    public void uploadFile(String url, String path) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(3000, TimeUnit.SECONDS)
                .readTimeout(1200, TimeUnit.SECONDS)
                .writeTimeout(1200, TimeUnit.SECONDS)//写入超时(单位:秒)
                .build();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        File  files= new File(path);
        //第一个参数要与Servlet中的一致
        builder.addFormDataPart("file", files.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), files));
        MultipartBody multipartBody = builder.build();
        Response response = client.newCall(new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build()).execute();
        String str = response.body().string();
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(str);
        if(response.code()==200){
            System.out.println("***************************************************************************");
            System.out.println(" The Report is send to color-admin is successful and Marge file is  successful");
            System.out.println("         coverTotal%:"+JSON.toJSONString(jsonObject.get("cover"))+"       brashTotal%:"+JSON.toJSONString(jsonObject.get("brach")));
            System.out.println("***************************************************************************");
        }
    }


    public static void main(String[] args) throws IOException {
      OkHttpTemplate okHttpTemplate = new OkHttpTemplate();
      String url = "http://10.23.181.252:8345/file/Report?brash=test";
      String path = "D:\\github\\AndJaco\\Report.zip";
      okHttpTemplate.uploadFile(url,path);
    }
}
