package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by 신정민 on 2016-10-14.
 */
public class Register extends AppCompatActivity {
    String id = "";
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.inst_reg);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        TextView tv = (TextView)findViewById(R.id.professor_Number);
        tv.setText(id); //인텐트로 받아온 교수 아이디를 저장하고 텍스트뷰로 띄움
        sendToJsp();
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/Register_Student.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
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
            String realData = parsing[1];
            String[] parsed = realData.split("[/]");

//버튼 과목명 과목번호 교수명 강의계획서 조회

            //받은 데이터를 파싱해서 액티비티에 띄워주는 부분
            for (int i = 0; i < parsed.length; i++) {
                String[] completed = parsed[i].split("[|]");
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;

                final LinearLayout layout = (LinearLayout) findViewById(R.id.layout_reg);
                final LinearLayout linear = new LinearLayout(this);
                final int count = i;
                linear.setOrientation(LinearLayout.HORIZONTAL);
                linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                Button reg_btn = new Button(this);
                reg_btn.setLayoutParams(p);
                reg_btn.setText("등록");
                //강의 등록 버튼을 주면 강의 등록 액티비티로 넘어감
                reg_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Register.this, update_plan.class);
                        intent.putExtra("coursenum", String.valueOf(count+1));
                        startActivity(intent);
                    }
                });

                TextView coursename = new TextView(Register.this);
                coursename.setText(completed[0]);
                coursename.setLayoutParams(p);  //강의명

                TextView coursenum = new TextView(Register.this);
                coursenum.setText(completed[1]);
                coursenum.setLayoutParams(p);   //강의번호

                TextView instname = new TextView(Register.this);
                instname.setText(completed[2]);
                instname.setLayoutParams(p);    //교수명

                Button planbtn = new Button(Register.this);
                planbtn.setText("강의계획서");
                planbtn.setLayoutParams(p);
                planbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Register.this, Course_Plan.class);
                        intent.putExtra("coursenum", String.valueOf(count+1));
                        startActivity(intent);
                    }
                }); //강의계획서 조회 액티비티로 넘어감

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
                //받은 데이터들이 추가된 LinearLayout을 액티비티에 추가
            }

            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
