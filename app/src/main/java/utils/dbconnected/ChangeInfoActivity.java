package utils.dbconnected;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import main.MainActivity;
import psj.hahaha.R;
import utils.model.UserInfo;

public class ChangeInfoActivity extends AppCompatActivity {
    String URL="http://210.91.76.33:8080/user/changeinfo.php";
    Bitmap bitmap = null;
    ImageView img;
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
       // img = (ImageView) findViewById(R.id.changeUserImage);
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
                    UserInfo.UserEntry.USER_NAME = nameet.getText().toString();
                    UserInfo.UserEntry.USER_PWD = pwdet.getText().toString();
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
        if(!name.trim().equals("")&&!pwd.trim().equals("")
                &&!pwd_check.trim().equals("")&&pwd.equals(pwd_check)){
            ChangeAsync task = new ChangeAsync();
            task.execute(UserInfo.UserEntry.USER_ID,name,pwd);
        }else{
            Toast.makeText(this,"입력이 올바르지 않습니다.",Toast.LENGTH_LONG).show();
        }
    }

    public void changeImage(View view){
        selectImage();
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeInfoActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    img.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));

                img.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelChange(View view){
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
