package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by 신정민 on 2016-10-14.
 */

public class update_plan extends AppCompatActivity {
    EditText text = null;
    String coursenum = null;
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.course_plan_register);
        text = (EditText)findViewById(R.id.plan_editText);
        Intent intent = getIntent();
        coursenum = intent.getStringExtra("coursenum");
    }

    public void onClick(View v)
    {
        sendToJsp();
        finish();
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/plan_update.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("coursenum", coursenum));
            nameValuePairs.add(new BasicNameValuePair("content", text.getText().toString()));
            //강의 명과 교수가 입력했던 강의계획서 내용을 jsp에 전달함
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entityRequest =
                    new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");

            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);

            String line = null;
            String page = "";
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(
                            responsePost.getEntity().getContent(), "EUC-KR"));
            // 버퍼의 웹문서 소스를 줄 단위로 읽어(line), page에 저장함
            while ((line = bufreader.readLine()) != null) {
                page += line;
            }

            String[] parsing = page.split("[$]");
            boolean result = parsing[1].trim().compareTo("1") == 0;
            //값을 1,0으로 받아와서 강의 등록 성공이면 1, 실패면 0으로 boolean result를 셋팅함
            if(result)//result에 따른 결과 출력
                Toast.makeText(this, "등록 성공", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "등록 실패", Toast.LENGTH_LONG).show();

            Log.i("jungmin", page);
        }catch(Exception e){e.printStackTrace();
        }
    }
}
