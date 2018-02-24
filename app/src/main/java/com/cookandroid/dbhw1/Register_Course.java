package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Register_Course extends AppCompatActivity {//강의 등록(수강신청) 페이지
    String id;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.register_course);
        TextView idTextBox = (TextView) findViewById(R.id.register_id);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        idTextBox.setText(id);  //intent로 받은 id로 학번 설정
        sendToJsp();
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/Register_Student.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", id));
            //jsp에 id를 넘기고
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
            String[] parsed = realData.split("[/]");
            //값을 받아와서 parsing한다.
            //...<body>$과목명|과목번호|교수명/과목명|과목번호|교수명/$</body>...
            //이러한 형태로 받아온 데이터를 파싱한다


//버튼 과목명 과목번호 교수명 강의계획서 조회
            for (int i = 0; i < parsed.length; i++) {
                String[] completed = parsed[i].split("[|]");
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;

                final LinearLayout layout = (LinearLayout) findViewById(R.id.register_layout);
                final LinearLayout linear = new LinearLayout(Register_Course.this);
                final int count = i;
                linear.setOrientation(LinearLayout.HORIZONTAL);
                linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //버튼을 동적으로 생성한다
                Button reg_btn = new Button(Register_Course.this);
                reg_btn.setLayoutParams(p);
                reg_btn.setText("등록");
                reg_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String url = "http://172.20.10.7:8080/DBHW1/Register_course.jsp";
                            HttpClient http = new DefaultHttpClient();
                            try {//버튼을 누르면 jsp 페이지에 연결하여 insert문을 실행하고, 결과값을 받는다
                                ArrayList<NameValuePair> nameValuePairs =
                                        new ArrayList<NameValuePair>();
                                nameValuePairs.add(new BasicNameValuePair("coursenum", String.valueOf(count + 1)));
                                nameValuePairs.add(new BasicNameValuePair("id", id));

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
                                //Toast.makeText(Register_Course.this, page, Toast.LENGTH_LONG).show();
                                String realData = parsing[1];
                                Log.i("debug", realData);
                                //로그인 결과를 받아왔을 때와 비슷한 형태로 1, 0의 데이터를 받아서 메시지 출력
                                if(Integer.parseInt(realData.trim()) == 1)
                                    Toast.makeText(Register_Course.this, "등록 완료", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(Register_Course.this, "등록되지 않았습니다. 이미 등록한 강의이거나 정원초과입니다.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                });

                TextView coursename = new TextView(Register_Course.this);
                coursename.setText(completed[0]);
                coursename.setLayoutParams(p);//강의명 텍스트뷰 생성

                TextView coursenum = new TextView(Register_Course.this);
                coursenum.setText(completed[1]);
                coursenum.setLayoutParams(p);//강의번호 텍스트뷰 생성

                TextView instname = new TextView(Register_Course.this);
                instname.setText(completed[2]);
                instname.setLayoutParams(p);//교수명 텍스트뷰 생성

                Button planbtn = new Button(Register_Course.this);
                planbtn.setText("강의계획서");
                planbtn.setLayoutParams(p);
                planbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Register_Course.this, Course_Plan.class);
                        intent.putExtra("coursenum", String.valueOf(count+1));
                        startActivity(intent);
                    }
                });//강의계획서 조회 버튼 생성

                linear.addView(reg_btn);
                linear.addView(coursename);
                linear.addView(coursenum);
                linear.addView(instname);
                linear.addView(planbtn);

                layout.post(new Runnable() {
                    public void run() {
                        layout.addView(linear);
                    }
                });
                //버튼 과목명 과목번호 교수명 강의계획서 조회 X 과목수로 뷰들이 생성된 레이아웃을 액티비티에 추가
            }

            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
