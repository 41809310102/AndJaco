package com.ttp.and_jacoco;

import com.alibaba.fastjson.*;
import com.ttp.and_jacoco.result.CodeDiffResultVO;


import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Deno {
    public void startScript() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        System.out.println(("now get diffadmin send http message letter start"));
        //   RequestBody.create(MediaType.get("application/json"));
        String baseVersion = "main";
        String nowVersion = "debug";
        String gitUrl = "https://git.bilibili.co/hujunjie02/test_android_demo.git";
        String url = "http://127.0.0.1:8085/api/code/diff/git/list?baseVersion="+baseVersion+"&gitUrl="+gitUrl+"&nowVersion="+nowVersion;
        //builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
        Response response = client.newCall(new Request.Builder()
                .url(url)
                .get()
                .build()).execute();
        //解析json对象
        String str = response.body().string();
        JSONObject jsonObject = JSON.parseObject(str);
        str = JSON.toJSONString(jsonObject.get("data"));
        List<CodeDiffResultVO> passengerDetailsVOS = JSON.parseObject(str, new TypeReference<List<CodeDiffResultVO>>(){});
        for(CodeDiffResultVO codeDiffResultVO : passengerDetailsVOS){
            System.out.println(codeDiffResultVO.getClassFile());
        }
        System.out.println(("now get dif fadmin send http message letter over"));
    }

    public static void main(String[] args) {
        try {
            new Deno().startScript();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
