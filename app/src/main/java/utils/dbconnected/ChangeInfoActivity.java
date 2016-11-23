package utils.dbconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import utils.model.UserInfo;
import main.MainActivity;
import psj.hahaha.R;

public class ChangeInfoActivity extends AppCompatActivity {
    String URL="http://210.91.76.33:8080/user/changeinfo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        TextView idtv = (TextView) findViewById(R.id.change_id);
        idtv.setText(UserInfo.UserEntry.USER_ID);
        EditText nameet = (EditText) findViewById(R.id.change_name);
        nameet.setText(UserInfo.UserEntry.USER_NAME);

    }

    public void changeInfo(View view){
        class ChangeAsync extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ChangeInfoActivity.this, "잠시만요.", "로딩중...");
            }

            @Override
            protected String doInBackground(String... params) {
                String userid = params[0];
                String uname = params[1];
                String pass = params[2];

                try {
                    String id = (String)params[0];
                    String name = (String) params[1];
                    String password = (String) params[2];

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

                    // Read Server Response
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
                    SharedPreferences sp = getSharedPreferences("autologin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", UserInfo.UserEntry.USER_NAME);
                    editor.putString("pwd", UserInfo.UserEntry.USER_PWD);
                    editor.commit();
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
        if(!name.equals("")&&!pwd.equals("")&&!pwd_check.equals("")&&pwd.equals(pwd_check)){
            ChangeAsync task = new ChangeAsync();
            task.execute(UserInfo.UserEntry.USER_ID,name,pwd);
        }else{
            Toast.makeText(this,"입력이 올바르지 않습니다.",Toast.LENGTH_LONG).show();
        }
    }

    public void cancelChange(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
