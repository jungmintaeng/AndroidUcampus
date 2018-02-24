package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.view.View;
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
public class Course_Plan extends AppCompatActivity {
    String course_num;  //조회버튼에서 넘겨준 인텐트로 강의번호가 저장될 변수
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.course_plan);
        Intent intent = getIntent();
        course_num = intent.getStringExtra("coursenum");    //인텐트 받아옴
        sendToJsp();
    }

    public void click(View v)
    {
        finish();
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/course_Plan.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("coursenum", course_num));
            //jsp에 강의번호를 넘겨주면
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entityRequest =
                    new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");

            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);
            //해당하는 과목의 강의계획서를 받아옴 varchar(300)형
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
            String realData = parsing[1];   //받은 데이터를 파싱하는 과정
            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show();

            TextView tv = (TextView)findViewById(R.id.plan_TextView);
            //파싱한 강의계획서를 띄워줌
            if(realData.trim().compareTo("NULL") == 0)
            {   //만약 강의계획서가 null이라면 body에 $NULL$이 들어가도록 해놓음
                tv.setText("강의계획서가 등록되지 않았습니다");
                return;
            }

            tv.setText(realData);

            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()
        }catch(Exception e){e.printStackTrace();}
    }
}
