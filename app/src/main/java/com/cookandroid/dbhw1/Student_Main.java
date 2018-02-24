package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class Student_Main extends AppCompatActivity {
    String id;      //로그인 페이지에서 인텐트로 넘어온 학생의 id
    TextView student;//학번이 표시될 TextView
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.student_main);
        student = (TextView)findViewById(R.id.student_number);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        student.setText(id);    //Intent를 받아서 학번을 TextView에 설정하고
        sendToJsp();            //jsp와 연결해서 학생의 수강 과목 목록을 가져옴
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, Register_Course.class);
        intent.putExtra("id", id);
        startActivity(intent);      //다음 액티비티로 넘어감. id를 넘김
    }

    private void sendToJsp() {
        String url = "http://172.20.10.7:8080/DBHW1/Stu_Main.jsp";
        HttpClient http = new DefaultHttpClient();
        try {
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", student.getText().toString()));
            //학번을 request로 전달
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
            //jsp 소스 전체가 넘어오기 때문에 jsp에서 body부분에 $내용$ 이런식으로 넣어놓았다.(JSON 미사용)
            String realData = parsing[1];
            //<html> ...... ~ <body>까지가 parsing[0], parsing[1]은 받아온 내용, parsing[2]는 </body> ~ 끝까지
            String[] parsed = realData.split("[|]");
            //받은 내용을 과목을 구분하기 위해서 C1|C2|C3 이런 식으로 저장되어 있는 것이기 때문에
            //|로 split함

            LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
            //수강 과목 목록이 들어갈 LinearLayout
            for(int i = 0; i < parsed.length;i++)
            {
                TextView tv = new TextView(this);
                tv.setText(parsed[i]);
                layout.addView(tv);//layout에 과목별로 새로운 텍스트뷰 추가
            }
            //Toast.makeText(this, realData, Toast.LENGTH_LONG).show()
        }catch(Exception e){e.printStackTrace();}
    }
}
