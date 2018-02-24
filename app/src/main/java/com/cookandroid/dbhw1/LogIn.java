package com.cookandroid.dbhw1;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LogIn extends AppCompatActivity {
    EditText editText_ID;   //id가 입력될 EditText
    EditText editText_PW;   //pw가 입력될 EditText
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editText_ID = (EditText) findViewById(R.id.id);
        editText_PW = (EditText) findViewById(R.id.pw);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); //쓰레드 정책 무시
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.studentButton:
                url = "http://172.20.10.7:8080/DBHW1/Server.jsp";
                sendToJsp(url); //학생버튼이면 학생로그인 jsp로 들어가고
                break;
            case R.id.instructorButton:
                url = "http://172.20.10.7:8080/DBHW1/inst_login.jsp";
                sendToJsp(url); //교수버튼이면 교수로그인 jsp로 연결한다
                break;
        }
    }

    /*웹에 연결하여 request를 주고
    jsp 소스를 받아와 파싱하여 결과값을 뿌리는 메소드*/

    private void sendToJsp(String url) {
        HttpClient http = new DefaultHttpClient();
        try {

            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", editText_ID.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("pw", editText_PW.getText().toString()));
            //서버에 전달할 id와 pw 값을 key와 함께 pair로 넘김
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);    //연결 지연 시간 제한

            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entityRequest =
                    new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");
                                            //서버에 값을 전달할 때 EUC-KR로 전달
            httpPost.setEntity(entityRequest);
                                            //entity의 포맷 설정을 적용해줌
            HttpResponse responsePost = http.execute(httpPost);
                                            //값 전달, response 받음
            String line = null;
            String page = "";
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(
                            responsePost.getEntity().getContent(), "utf-8"));
            // 버퍼의 웹문서 소스를 줄 단위로 읽어(line), page에 저장함
            while ((line = bufreader.readLine()) != null) {
                if(line.compareTo("<body>") == 0)   //소스 전체가 오기 때문에 body부분 다음부터 읽음
                {
                    page = "";
                    continue;
                }
                if(line.compareTo("</body>") == 0)  //body가 끝나면 읽기를 중단함
                    break;
                page += line;
            }

            //page = EntityUtils.toString(responsePost.getEntity());

            int result = Integer.parseInt(page.trim()); //로그인 성공은 1, 실패는 0이다.
                                                        //trim()메소드를 통해 공백을 제거한다.

            if(result == 1) //파싱해온 값이 1이면 로그인 성공
            {
                //Toast.makeText(this, "로그인 성공 " + page.trim(), Toast.LENGTH_SHORT).show();
                Intent intent = null;
                //위에서 설정된 url이 학생 로그인 페이지면 인텐트를 학생페이지로 명시
                if(url.compareTo("http://172.20.10.7:8080/DBHW1/Server.jsp") == 0)
                    intent = new Intent(this, Student_Main.class);
                else
                    intent = new Intent(this, Instructor_Main.class);
                intent.putExtra("id", editText_ID.getText().toString());
                //웹에서 session으로 유저 정보를 유지했던 것처럼 intent를 통해서 id를 전달
                startActivity(intent);  //activity를 시작하고
                finish();               //로그인 액티비티는 다시 돌아올 필요 없으므로 종료한다
            }
            else        //만약 파싱해온 값이 1이 아니라면 로그인 실패이므로
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
                //아무 동작도 하지 않고 로그인 실패만 띄워준다.
        }catch(Exception e){e.printStackTrace();}
    }
}
