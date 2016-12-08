package utils.dbconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import main.MainActivity;
import psj.hahaha.R;

public class SignInActivity extends AppCompatActivity {
    String URL="http://210.91.76.33:8080/user/signin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }
    public void singin_act_signin(View view){
        EditText et = (EditText) findViewById(R.id.signin_id);
        String id = et.getText().toString();
        et = (EditText) findViewById(R.id.signin_name);
        String name = et.getText().toString();
        et = (EditText) findViewById(R.id.signin_password);
        String pwd = et.getText().toString();
        et = (EditText) findViewById(R.id.signin_password_check);
        String pwd_check = et.getText().toString();
        if(!id.trim().equals("")&&!name.trim().equals("")&&!pwd.trim().equals("")&&pwd.equals(pwd_check)){
            class InsertData extends AsyncTask<String, Void, String> {
                ProgressDialog loading;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(SignInActivity.this, "잠시만요.", "로딩중...");
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    loading.dismiss();
                    if(result.equalsIgnoreCase("success")) {
                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignInActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(result.equalsIgnoreCase("failure")){
                        Toast.makeText(getApplicationContext(), "이미 가입된 학번입니다.", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "회원가입 오류.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(String... params) {

                    try{
                        String id = (String)params[0];
                        String name = (String)params[1];
                        String password = (String)params[2];

                        String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                        data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                        data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                        URL url = new URL(URL);
                        URLConnection conn = url.openConnection();

                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                        wr.write( data );
                        wr.flush();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        // Read Server Response
                        while((line = reader.readLine()) != null)
                        {
                            sb.append(line);
                            break;
                        }
                        return sb.toString();
                    }
                    catch(Exception e){
                        return new String("Exception: " + e.getMessage());
                    }

                }
            }
            InsertData task = new InsertData();
            task.execute(id,name,pwd);

        }else{
            Toast.makeText(this,"입력이 올바르지 않습니다.",Toast.LENGTH_LONG).show();
        }
    }
    public void signin_act_back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
