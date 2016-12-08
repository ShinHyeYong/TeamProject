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

import com.bumptech.glide.Glide;

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

import static android.view.View.GONE;

/**
 * Created by user on 2016-11-11.
 */

public class ContentPage extends Activity {
    String fragmentType;
    String number;
    String URL;
    String WCURL = "http://210.91.76.33:8080/comment/writecomment.php";
    String CLISTURL = "http://210.91.76.33:8080/comment/getcommentlist.php";

    ImageView image;

    GetCommentListAsync clistTask = null;
    ArrayList<Element> elements;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Intent intent = getIntent();
        image = (ImageView) findViewById(R.id.imageContent);

        //어떤 프래그먼트의 글인지 구분
        //프래그먼트에 따라 URL도 다름
        fragmentType = intent.getStringExtra("fragmentType");
        number = intent.getStringExtra("contentnum");
        if(fragmentType.equals("market"))
            URL = "http://210.91.76.33:8080/context/getmarketcontext.php";
        else if(fragmentType.equals("exchange"))
            URL = "http://210.91.76.33:8080/context/getexchangecontext.php";
        else {
            URL = "http://210.91.76.33:8080/marker/getmarkercontext.php";
            image.setVisibility(GONE); //마커 생성할때는 이미지 첨부 없음
        }

        //댓글 리스트
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

    //댓글 작성 함수
    public void submitComment(View v) {
        String body = ((EditText) findViewById(R.id.commentary)).getText().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        WriteCommentAsync task = new WriteCommentAsync();
        //프래그먼트 타입, 글번호, 사용자이름, 내용, 시간을 서버에 전송
        task.execute(fragmentType, number, UserInfo.UserEntry.USER_NAME, body, formattedDate);
        Intent i = new Intent(this,ContentPage.class);
        i.putExtra("fragmentType",fragmentType);
        i.putExtra("contentnum",number);
        startActivity(i);
        finish();
    }

    //휴대폰의 back버튼을 탭 했을 경우 앱이 종료되지 않고 메인 액티비티로 이동
    @Override
    public void onBackPressed() {
        Intent Mainintent = new Intent(ContentPage.this, MainActivity.class);
        if(fragmentType.equals("market"))
            Mainintent.putExtra("write","mk");
        else if(fragmentType.equals("exchange"))
            Mainintent.putExtra("write","ex");
        else
        Mainintent.putExtra("write","dn");
        startActivity(Mainintent);
        finish();
    }

    // 댓글 리스트에 어댑터 세팅, 리스트에 elements(리스트 내용)세팅
    private void setListView(){
        ListViewAdapter adapter = new ListViewAdapter(ContentPage.this, elements);
        listView.setAdapter(adapter);
    }

    //글 내용을 서버에서 받아오는 asynctask
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

                //글 번호를 서버에 전송 php서버에서는 POST로 받음, 글 번호가 게시물 데이터베이스의 primary key
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
                    //서버에서 받은 데이터를 jsonobject에 저장
                    JSONObject root = new JSONObject(result);

                    //result jsonarray에 받아야할 데이터가 들어있으므로
                    //jsonarray를 따로 저장
                    JSONArray ja = root.getJSONArray("result");
                    if(ja.length()!=0) {
                        String[] context = new String[]{
                                ja.getJSONObject(0).getString("title"), //제목
                                ja.getJSONObject(0).getString("body"), //내용
                                ja.getJSONObject(0).getString("time"), //작성 시간
                                ja.getJSONObject(0).getString("userid"), //작성자
                                ja.getJSONObject(0).getString("content_no"), //글 번호
                                ja.getJSONObject(0).getString("img"), //이미지 uri
                                ja.getJSONObject(0).getString("cnum") //댓글 개수
                        };
                        TextView titleTv = (TextView) findViewById(R.id.titleTV);
                        TextView mainTv = (TextView) findViewById(R.id.mainTV);
                        TextView timeTv = (TextView) findViewById(R.id.timeTV);
                        TextView writerTv = (TextView) findViewById(R.id.writerTV);
                        TextView cnumTv = (TextView) findViewById(R.id.textView5);

                        titleTv.setText(context[0]); //글 제목 세팅
                        //php서버에서 echo할 때 \n이 포함되어 있을 경우에 데이터를 받아오지 못해서
                        //앱애서 서버로 전송할 때 \n을 특정 string으로 변경
                        //앱이 서버에서 이 데이터를 다시 받을때는 반대로 string 변경
                        mainTv.setText(context[1].replace("이것은줄바꿈이다!!!","\n"));
                        timeTv.setText("작성 날짜 : "+context[2]); //작성 날짜 세팅
                        writerTv.setText("작성자 : "+context[3]); //작성자 세팅
                        cnumTv.setText("댓글("+context[6]+")"); //댓글 개수 세팅
                        //이미지 uri string이 noImg가 아니면 uri가 있음 -> 글 작성 시 이미지 첨부
                        if(!context[5].equals("noImg")){
                            Glide.with(ContentPage.this).load(context[5]).into(image);
                        }else{ //이미지를 첨부하지 않았을 경우
                            image.setVisibility(GONE);
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    //작성한 댓글을 서버에 전송하는 asynctask
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

                //게시글의 종류(마켓,교환,기부), 글 번호, 작성자, 내용, 작성시간을 서버에 전송, php서버에서는 POST로 받음
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
                Toast.makeText(getApplicationContext(),"댓글이 작성되었습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"작성 오류.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //댓글 리스트를 서버에서 받는 asynctask
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
                //게시글의 종류(마켓,교환,기부), 글 번호를 서버에 전송, php서버에서는 POST로 받음
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
                    elements = new ArrayList<Element>();
                    JSONObject root = new JSONObject(result);
                    JSONArray ja = root.getJSONArray("result");

                    //서버에서 받아온 결과를 ArrayList<Element>에 저장
                    if(ja.length()!=0) {
                        for(int i=ja.length()-1;i>=0;i--){
                            elements.add(new Element("- "+ja.getJSONObject(i).getString("writer")+" -\n\n"+ ja.getJSONObject(i).getString("body"), ja.getJSONObject(i).getString("time")));
                        }
                    }
                    //elements를 리스트뷰에 세팅
                    setListView();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
