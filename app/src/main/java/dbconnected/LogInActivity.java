package dbconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import utils.model.UserInfo;
import intro.MainActivity;
import psj.hahaha.R;


public class LogInActivity extends AppCompatActivity {
    String URL="http://210.91.76.33:8080/user/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
    public void login_act_login(View view){
        EditText et = (EditText) findViewById(R.id.login_id);
        String id = et.getText().toString();
        et = (EditText) findViewById(R.id.login_password);
        String pwd = et.getText().toString();

        if(!id.equals("")&&!pwd.equals("")){
            class LoginAsync extends AsyncTask<String, Void, String> {

                ProgressDialog loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(LogInActivity.this, "잠시만요.", "로딩중...");
                }

                @Override
                protected String doInBackground(String... params) {
                    String userid = params[0];
                    String pass = params[1];
                    try{
                        String id = (String)params[0];
                        String password = (String)params[1];

                        String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                        data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                        java.net.URL url = new URL(URL);
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

                @Override
                protected void onPostExecute(String result){
                    super.onPostExecute(result);
                    loading.dismiss();

                    if(!result.equalsIgnoreCase("failure")){
                        try {
                            JSONObject root = new JSONObject(result);

                            JSONArray ja = root.getJSONArray("result");

                            String[] usrinfo = new String[]{
                                    ja.getJSONObject(0).getString("id"),
                                    ja.getJSONObject(0).getString("name"),
                                    ja.getJSONObject(0).getString("password")
                            };
                            UserInfo.UserEntry.USER_ID = usrinfo[0];
                            UserInfo.UserEntry.USER_NAME = usrinfo[1];
                            UserInfo.UserEntry.USER_PWD = usrinfo[2];
                            UserInfo.UserEntry.IS_LOGIN = true;

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호가 잘못되었습니다.",Toast.LENGTH_LONG).show();
                    }
                }
            }
            LoginAsync task = new LoginAsync();
            task.execute(id,pwd);
        }else{
            Toast.makeText(this,"아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        }

    }
    public void login_act_signin(View view){
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);
        finish();
    }
    public void goBack(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
