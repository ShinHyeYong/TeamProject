package utils.dbconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import main.MainActivity;
import psj.hahaha.R;
import utils.model.UserInfo;

public class ChangeInfoActivity extends AppCompatActivity {
    String URL="http://210.91.76.33:8080/user/changeinfo.php";
    EditText nameet;
    EditText pwdet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        TextView idtv = (TextView) findViewById(R.id.change_id);
        idtv.setText(UserInfo.UserEntry.USER_ID);
        nameet = (EditText) findViewById(R.id.change_name);
        nameet.setText(UserInfo.UserEntry.USER_NAME);
        pwdet = (EditText) findViewById(R.id.change_password);
    }

    public void changeInfo(View view){
        class ChangeAsync extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ChangeInfoActivity.this, "잠시만 기다려주세요.", "로딩중...");
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String id = (String)params[0];
                    String name = (String) params[1];
                    String password = (String) params[2];
                    //id, password를 php서버에 전송 php에서는 POST로 받음
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    java.net.URL url = new URL(URL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    //php서버에서 echo하는 데이터를 받음 (php서버에서 json형태로 parsing)
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                if (result.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), "정보 변경이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    //변경된 이름과 password 저장
                    UserInfo.UserEntry.USER_NAME = nameet.getText().toString();
                    UserInfo.UserEntry.USER_PWD = pwdet.getText().toString();
                    //preference에도 사용자 정보 업데이트
                    SharedPreferences sp = getSharedPreferences("autologin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", UserInfo.UserEntry.USER_NAME);
                    editor.putString("pwd", UserInfo.UserEntry.USER_PWD);
                    editor.commit();
                    //변경 완료 후 메인 액티비티로 이동
                    Intent intent = new Intent(ChangeInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (result.equalsIgnoreCase("failure")) {
                    Toast.makeText(getApplicationContext(), "정보변경 오류.", Toast.LENGTH_LONG).show();
                }
            }
        }
        EditText et = (EditText) findViewById(R.id.change_name);
        String name = et.getText().toString();
        et = (EditText) findViewById(R.id.change_password);
        String pwd = et.getText().toString();
        et = (EditText) findViewById(R.id.change_password_check);
        String pwd_check = et.getText().toString();
        //이름과 password를 입력하고 password가 check되었을 경우에만 정보 변경 실행
        if(!name.trim().equals("")&&!pwd.trim().equals("")
                &&!pwd_check.trim().equals("")&&pwd.equals(pwd_check)){
            //asynctask 실행
            ChangeAsync task = new ChangeAsync();
            task.execute(UserInfo.UserEntry.USER_ID,name,pwd);
        }else{
            Toast.makeText(this,"입력이 올바르지 않습니다.",Toast.LENGTH_LONG).show();
        }
    }

    //메인 액티비티로 이동
    public void cancelChange(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //휴대폰의 back버튼을 탭 했을 경우 앱이 종료되지 않고 메인 액티비티로 이동
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
