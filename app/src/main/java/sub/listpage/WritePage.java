package sub.listpage;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import main.MainActivity;
import psj.hahaha.R;
import utils.model.UserInfo;

/**
 * Created by user on 2016-11-11.
 */

public class WritePage extends Activity {
    String URL;
    ImageView eImage;
    Bitmap bitmap = null;
    String imgString = "noImg";
    String fragmentType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        eImage = (ImageView) findViewById(R.id.imageWrite);
        Intent i = getIntent();
        fragmentType = i.getStringExtra("fragmentType");
        if(fragmentType.equals("market"))
            URL = "http://210.91.76.33:8080/context/writemarketcontext.php";
        else
            URL = "http://210.91.76.33:8080/context/writeexchangecontext.php";
    }

    public void goMain(View v){

        Intent mainIntent = new Intent(WritePage.this, MainActivity.class);
        if(fragmentType.equals("market"))
            mainIntent.putExtra("write","mk");
        else
            mainIntent.putExtra("write","ex");
        startActivity(mainIntent);
        finish();
    }


    public void goContent(View v){

        // 제목 본문 이미지 등록 시간 사용자id 서버로 전송
        Intent contentIntent = new Intent(WritePage.this, ContentPage.class);
        //제목
        EditText eTitle = (EditText) findViewById(R.id.editTitle);
        //본문
        EditText eMain = (EditText) findViewById(R.id.editMain);

        if(!eTitle.getText().toString().trim().equals("") && !eMain.getText().toString().trim().equals("")) {
            //이미지
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                imgString = Base64.encodeToString(b, Base64.DEFAULT);
            }

        }else{
            Toast.makeText(this,"제목 또는 본문 내용을 입력하십시오.",Toast.LENGTH_SHORT).show();
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        WriteContextAsync task = new WriteContextAsync();
        task.execute(eTitle.getText().toString(), eMain.getText().toString(), formattedDate, UserInfo.UserEntry.USER_ID);
    }


    @Override
    public void onBackPressed() {
//        finish();
//        Intent Mainintent = new Intent(WritePage.this, MainActivity.class);
//        Mainintent.putExtra("write","ex");
//        startActivity(Mainintent);
//        finish();
//
        Intent mainIntent = new Intent(WritePage.this, MainActivity.class);
        if(fragmentType.equals("market"))
            mainIntent.putExtra("write","mk");
        else
            mainIntent.putExtra("write","ex");
        startActivity(mainIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write_main,menu);
        return true;
    }


    //이미지 첨부(사진 or 앨범)
    public void attachImage(View v){
        selectImage();
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(WritePage.this);
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

                    eImage.setImageBitmap(bitmap);

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

                eImage.setImageBitmap(bitmap);
            }
        }
    }
    class WriteContextAsync extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(WritePage.this, "잠시만요.", "로딩중...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String context_title = (String)params[0];
                String context_body = (String) params[1];
                String context_time = (String) params[2];
                String context_usrid = (String) params[3];

                String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(context_title, "UTF-8");
                data += "&" + URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(context_body, "UTF-8");
                data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(context_time, "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(context_usrid, "UTF-8");

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
            if (!result.equalsIgnoreCase("failure")) {
                try {
                    JSONObject root = new JSONObject(result);
                    String number = root.getString("result").toString();

                    Toast.makeText(getApplicationContext(), "글 작성이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(WritePage.this, ContentPage.class);
                    intent.putExtra("fragmentType",fragmentType);
                    intent.putExtra("contentnum",number);
                    startActivity(intent);
                    finish();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            } else if (result.equalsIgnoreCase("failure")) {
                Toast.makeText(getApplicationContext(), "글 작성 오류.", Toast.LENGTH_LONG).show();
            }
        }
    }
}