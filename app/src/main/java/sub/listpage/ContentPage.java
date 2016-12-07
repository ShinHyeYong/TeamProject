package sub.listpage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import main.MainActivity;
import psj.hahaha.R;
import utils.Element;
import utils.adapter.ListViewAdapter;
import utils.model.UserInfo;

/**
 * Created by user on 2016-11-11.
 */

public class ContentPage extends Activity {
    String fragmentType;
    String number;
    String URL;
    String WCURL = "http://210.91.76.33:8080/comment/writecomment.php";
    String CLISTURL = "http://210.91.76.33:8080/comment/getcommentlist.php";

    GetCommentListAsync clistTask = null;
    ArrayList<Element> elements;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Intent intent = getIntent();
        fragmentType = intent.getStringExtra("fragmentType");
        number = intent.getStringExtra("contentnum");
        if(fragmentType.equals("market"))
            URL = "http://210.91.76.33:8080/context/getmarketcontext.php";
        else
            URL = "http://210.91.76.33:8080/context/getexchangecontext.php";

        listView = (ListView) findViewById(R.id.commentList);

        clistTask = new GetCommentListAsync();
        clistTask.execute(fragmentType,number);
        GetContextAsync task = new GetContextAsync();
        task.execute(number);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    //댓글
    public void submitComment(View v) {
        //프래그먼트 타입, 글번호, 사용자이름, 내용 시간

        String body = ((EditText) findViewById(R.id.commentary)).getText().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        WriteCommentAsync task = new WriteCommentAsync();
        task.execute(fragmentType, number, UserInfo.UserEntry.USER_NAME, body, formattedDate);
        Intent i = new Intent(this,ContentPage.class);
        i.putExtra("fragmentType",fragmentType);
        i.putExtra("contentnum",number);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent Mainintent = new Intent(ContentPage.this, MainActivity.class);
        if(fragmentType.equals("market"))
            Mainintent.putExtra("write","mk");
        else
            Mainintent.putExtra("write","ex");
        startActivity(Mainintent);
        finish();
    }

    private void setListView(){
        // 리스트 뷰 세팅( 커스텀 리스트뷰어댑터 )
        ListViewAdapter adapter = new ListViewAdapter(ContentPage.this, elements);
        listView.setAdapter(adapter);
    }

    class GetContextAsync extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ContentPage.this, "잠시만요.", "로딩중...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String context_no = (String)params[0];

                String data = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(context_no, "UTF-8");

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

                    JSONArray ja = root.getJSONArray("result");
                    if(ja.length()!=0) {
                        String[] context = new String[]{
                                ja.getJSONObject(0).getString("title"),
                                ja.getJSONObject(0).getString("body"),
                                ja.getJSONObject(0).getString("time"),
                                ja.getJSONObject(0).getString("userid"),
                                ja.getJSONObject(0).getString("content_no")
                        };
                        TextView titleTv = (TextView) findViewById(R.id.titleTV);
                        TextView mainTv = (TextView) findViewById(R.id.mainTV);
                        TextView timeTv = (TextView) findViewById(R.id.timeTV);
                        TextView writerTv = (TextView) findViewById(R.id.writerTV);
                        ImageView cImage = (ImageView) findViewById(R.id.imageContent);

                        titleTv.setText(context[0]);
                        mainTv.setText(context[1]);
                        timeTv.setText("작성 날짜 : "+context[2]);
                        writerTv.setText("작성자 : "+context[3]);

//                        Bitmap imgbitmap;
//                        if(!context[5].equals("noImg")) {
//                            try {
//                                byte[] encodeByte = Base64.decode(context[5], Base64.DEFAULT);
//                                imgbitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//                                cImage.setImageBitmap(imgbitmap);
//                            } catch (Exception e) {
//                                e.getMessage();
//                            }
//                        }else{
//                            cImage.setVisibility(GONE);
//                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class WriteCommentAsync extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ContentPage.this, "잠시만요.", "로딩중...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String type = (String)params[0];
                String context_no = (String)params[1];
                String writer = (String)params[2];
                String body = (String)params[3];
                String time = (String)params[4];


                String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                data += "&" + URLEncoder.encode("context_no", "UTF-8") + "=" + URLEncoder.encode(context_no, "UTF-8");
                data += "&" + URLEncoder.encode("writer", "UTF-8") + "=" + URLEncoder.encode(writer, "UTF-8");
                data += "&" + URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(body, "UTF-8");
                data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

                java.net.URL url = new URL(WCURL);

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
                Toast.makeText(getApplicationContext(),"댓글이 작성되었습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"작성 오류.",Toast.LENGTH_SHORT).show();
            }
        }
    }


    class GetCommentListAsync extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ContentPage.this, "잠시만요.", "로딩중...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String type = (String)params[0];
                String context_no = (String)params[1];

                String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                data += "&" + URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(context_no, "UTF-8");

                java.net.URL url = new URL(CLISTURL);

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
            System.out.println(result);
            loading.dismiss();
            if (!result.equalsIgnoreCase("failure")) {
                try {
                    elements = new ArrayList<Element>();
                    JSONObject root = new JSONObject(result);

                    JSONArray ja = root.getJSONArray("result");
                    if(ja.length()!=0) {
                        for(int i=ja.length()-1;i>=0;i--){
                            elements.add(new Element("- "+ja.getJSONObject(i).getString("writer")+" -\n\n"+ ja.getJSONObject(i).getString("body"), ja.getJSONObject(i).getString("time")));
                        }
                    }
                    setListView();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
