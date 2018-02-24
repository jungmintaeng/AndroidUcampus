package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
public class Search extends AppCompatActivity {
    String coursename;
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.search_layout);
        Intent intent = getIntent();
        coursename = intent.getStringExtra("coursename");
        sendToJsp();
        TextView tv = (TextView)findViewById(R.id.subjName);
        tv.setText(coursename);     //EditText에 입력했던 강의명을 저장함
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/Search.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("coursename", coursename));
            //강의명을 넘겨주고
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
            String realData = parsing[1];


            //해당 강의를 듣는 학생의 학번,이름을 텍스트뷰로 띄움
            Log.i("jungmin", page);
            TextView tv = (TextView)findViewById(R.id.search_result);
            tv.setText(realData);
            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()

        }catch(Exception e){e.printStackTrace();
        }
    }
}
