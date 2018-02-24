package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class Instructor_Main extends AppCompatActivity {
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.instructor_main);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        TextView tv = (TextView) findViewById(R.id.instructor_number);
        tv.setText(id); //교수 아이디 인텐트로 받아옴
        sendToJsp();
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, Register.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    public void userclick(View v) {
        Intent intent = new Intent(this, Search.class);
        EditText text = (EditText) findViewById(R.id.search_EditText);
        intent.putExtra("coursename", text.getText().toString());
        startActivity(intent);
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/Inst_Main.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", id));
            //교수 아이디를 request로 주고 본인이 강의하는 과목 목록 받아옴
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
            String[] parsed = realData.split("[|]");
            //받은 데이터를 파싱함


            Log.i("jungmin", page);

            LinearLayout layout = (LinearLayout) findViewById(R.id.layout_inst_main);

            for (int i = 0; i < parsed.length; i++) {
                TextView tv = new TextView(this);
                tv.setText(parsed[i]);
                layout.addView(tv); //자신의 강의 목록을 텍스트뷰로 만들어서 새로 추가함
            }
            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
