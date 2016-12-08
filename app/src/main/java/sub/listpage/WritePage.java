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
        //어떤 프래그먼트에서 글을 작성하는지 구분
        //프래그먼트에 따라 URL도 다름
        fragmentType = i.getStringExtra("fragmentType");
        if(fragmentType.equals("market"))
            URL = "http://210.91.76.33:8080/context/writemarketcontext.php";
        else
            URL = "http://210.91.76.33:8080/context/writeexchangecontext.php";
    }

    //메인 액티비티로 이동
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
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = df.format(c.getTime());
            //php서버에서 echo할 때 \n이 포함되어 있을 경우에 데이터를 받아오지 못해서
            //앱애서 서버로 전송할 때 \n을 특정 string으로 변경
            //앱이 서버에서 이 데이터를 다시 받을때는 반대로 string 변경
            String body = eMain.getText().toString().replace("\n","이것은줄바꿈이다!!!");

            WriteContextAsync task = new WriteContextAsync();
            task.execute(eTitle.getText().toString(), body, formattedDate, UserInfo.UserEntry.USER_ID,imgString);

        }else{
            Toast.makeText(this,"제목 또는 본문 내용을 입력하십시오.",Toast.LENGTH_SHORT).show();
        }
    }

    //휴대폰의 back버튼을 탭 했을 경우 앱이 종료되지 않고 메인 액티비티로 이동
    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(WritePage.this, MainActivity.class);
        if(fragmentType.equals("market"))
            mainIntent.putExtra("write","mk");
        else
            mainIntent.putExtra("write","ex");
        startActivity(mainIntent);
        finish();

    }

    //dialogoptionmenu의 layout을 지정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_main,menu);
        return true;
    }


    //이미지 첨부(사진 or 앨범)
    public void attachImage(View v){
        selectImage();
    }

    private void selectImage() {

        final CharSequence[] options = { "사진 촬영", "앨범","취소" };

        AlertDialog.Builder builder = new AlertDialog.Builder(WritePage.this);
        builder.setTitle("사진 첨부");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("사진 촬영"))
                {
                    //이미지 캡쳐(카메라)를 실행하기 위한 intent 생성 및 실행
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg"); //임시로 temp.jpg라고 이름을 지정
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("앨범"))
                {
                    //앨범에서 이미지를 선택하기 위한 intent 생성 및 실행
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show(); //alertdialog 실행
    }

    //dialog에서 이미지 캡쳐 또는 앨범에서 이미지 선택한 후 실행하는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { //이미지 캡쳐(카메라)
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) { //카메라로 찍을 때 temp.jpg로 이름을 지정했기 때문에 temp.jpg 파일을 찾음
                        f = temp;
                        break;
                    }
                }
                try {
                    //카메라 화질이 너무 좋으면 imageview에 넣을 수가 없어서
                    //이미지 크기 축소 (이미지 비트맵으로 수정)
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 4;
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1680, 1680, true);

                    //이미지 비트맵을 bytearray로 변환하고 Base64를 이용해 string으로 인코딩
                    //php서버에 string을 전송하고 php서버에서 디코딩하여 이미지를 파일로 저장
                    //데이터베이스(mysql)에는 이미지 uri를 저장
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    imgString = Base64.encodeToString(b, Base64.DEFAULT);

                    //이미지 뷰에 이미지 삽입
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
            } else if (requestCode == 2) { //앨범에서 이미지 선택

                //cursor 및 contentResolver를 이용하여 선택한 이미지의 파일 경로를 가져옴
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                //카메라 화질이 너무 좋으면 imageview에 넣을 수가 없어서
                //이미지 크기 축소 (이미지 비트맵으로 수정)
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;
                bitmap = BitmapFactory.decodeFile(picturePath, bitmapOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 1680, 1680, true);


                //이미지 비트맵을 bytearray로 변환하고 Base64를 이용해 string으로 인코딩
                //php서버에 string을 전송하고 php서버에서 디코딩하여 이미지를 파일로 저장
                //데이터베이스(mysql)에는 이미지 uri를 저장
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                imgString = Base64.encodeToString(b, Base64.DEFAULT);

                //이미지 뷰에 이미지 삽입
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
                String context_img = (String) params[4];

                //글 제목, 내용, 작성 시간, 작성자, 이미지를 서버에 전송 php에서는 POST로 받음
                String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(context_title, "UTF-8");
                data += "&" + URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(context_body, "UTF-8");
                data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(context_time, "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(context_usrid, "UTF-8");
                data += "&" + URLEncoder.encode("img", "UTF-8") + "=" + URLEncoder.encode(context_img, "UTF-8");

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
            if (!result.equalsIgnoreCase("failure")) {
                try {
                    JSONObject root = new JSONObject(result);
                    String number = root.getString("result").toString();

                    Toast.makeText(getApplicationContext(), "글 작성이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    //글이 작성되었을 경우에는 작성한 글을 볼 수 있는 contentpage 액티비티로 이동
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