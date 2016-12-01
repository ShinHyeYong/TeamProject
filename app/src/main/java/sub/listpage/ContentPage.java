package sub.listpage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import main.MainActivity;
import psj.hahaha.R;

/**
 * Created by user on 2016-11-11.
 */

public class ContentPage extends Activity {
    String fragmentType;
    String number;
    String URL;

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
        GetContextAsync task = new GetContextAsync();
        task.execute(number);
    }

    //댓글
    public void submitComment(View v){

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
            System.out.println(result);
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
}
