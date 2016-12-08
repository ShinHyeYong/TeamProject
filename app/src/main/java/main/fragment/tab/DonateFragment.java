package main.fragment.tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import psj.hahaha.R;
import sub.listpage.ContentPage;
import utils.Constants;
import utils.model.UserInfo;


public class DonateFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public double lati=0,longi=0;
    String place,place_hint;
    String MAKEURL = "http://210.91.76.33:8080/marker/makemarker.php";
    String GETURL = "http://210.91.76.33:8080/marker/getmarkers.php";
    ArrayList<String> markerList;

    public DonateFragment() {}

    //프래그먼트 인스턴스 생성
    public static DonateFragment newInstance(String param1, String param2) {
        DonateFragment fragment = new DonateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        //fusedlocation 사용을 위해 googleapiclient와 연결
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markerList = new ArrayList<String>(); //마커 리스트 객체 생성
        GetMarkersThread thread = new GetMarkersThread(); //마커 리스트를 받아오는 쓰레드
        Constants.tpexecutor.execute(thread); //쓰레드 풀로 쓰레드 실행

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트의 레이아웃
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.dfab);
        //마커 생성 버튼에 리스너 세팅
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //레이아웃을 생성하고 레이아웃에 editText 2개를 추가한다
                //dialog에 레이아웃 자체를 세팅하면 editText가 2개 있는 dialog가 된다
                final EditText where = new EditText(getActivity());
                where.setHint("장소");
                final EditText hint = new EditText(getActivity());
                hint.setHint("힌트");

                Context context = mMapView.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(where);
                layout.addView(hint);

                builder.setTitle("장소 및 힌트를 입력하세요");

                builder.setView(layout);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { //ok를 눌렀을 경우 마커를 생성
                    public void onClick(DialogInterface dialog, int whichButton) {
                        place = where.getText().toString();
                        place_hint = hint.getText().toString();
                        LatLng position = getPosition();

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String formattedDate = df.format(c.getTime());

                        //마커 생성 사용자, 생성 시간, 장소, 장소에 대한 힌트, 위도 경도를 서버에 전송한다
                        MakeMarkAsync task =  new MakeMarkAsync();
                        task.execute(UserInfo.UserEntry.USER_NAME,formattedDate,place,place_hint,String.valueOf(position.latitude),String.valueOf(position.longitude));

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                builder.show();


            }
        });

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // 맵을 바로 보여주기 위해 필요

        //맵 초기화
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //맵을 비동기식으로 불러옴
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                googleMap = mMap;
                //마커 클릭 시 생성했을 때 입력한 정보를 보여주는 액티비티로 이동하는 리스너 설정
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String no = marker.getTitle();
                        Intent i = new Intent(getActivity(),ContentPage.class);
                        i.putExtra("fragmentType","donate");
                        i.putExtra("contentnum",no);
                        startActivity(i);
                        getActivity().finish();

                        return true;
                    }
                });

                //서버에서 받아온 데이터로 마커 생성
                for(int i=0;i<markerList.size()-2;i+=3){
                    googleMap.addMarker(new MarkerOptions().title(markerList.get(i+2)).position(new LatLng(Double.parseDouble(markerList.get(i)),Double.parseDouble(markerList.get(i+1)))));
                }

                googleMap.setMyLocationEnabled(true); //현재 자신의 위치로 카메라를 이동하는 버튼 활성화
                googleMap.getUiSettings().setMapToolbarEnabled(false); //맵 툴바 비활성화

                // gecoder 생성
                Geocoder geo = new Geocoder(getActivity(), Locale.KOREAN);

                // Geocoder를 통한 위치 확인
                try{
                    List<android.location.Address> addr = geo.getFromLocationName("명지대학교 자연캠퍼스", 2);
                    lati = addr.get(0).getLatitude();
                    longi = addr.get(0).getLongitude();
                }catch (IOException e){}
                LatLng MyongJi = new LatLng(lati, longi);

                //카메라 줌 설정 및 맵의 위치를 카메라를 초기 위치(명지대학교 자연캠퍼스)로 이동
                CameraPosition cameraPosition = new CameraPosition.Builder().target(MyongJi).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return view;
    }




    public LatLng getPosition(){
        //사용자 권한 확인
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        //현재 위치를 받아옴
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        lati = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();

        LatLng myPosi = new LatLng(lati, longi);
        //받아온 위치로 마커 생성
        googleMap.addMarker(new MarkerOptions().title(String.valueOf(markerList.size()/3)).position(myPosi));
        //카메라 줌 설정 및 맵의 위치를 마커를 생성한 위치로 이동
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosi).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return myPosi;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }



    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //마커 생성 asynctask
    class MakeMarkAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String user = (String)params[0];
                String time = (String)params[1];
                String place = (String)params[2];
                String hint = (String)params[3];
                String lat = (String) params[4];
                String lng = (String) params[5];
                //마커 생성 사용자, 생성 시간, 생성 장소(입력한 텍스트), 장소에 대한 힌트(입력한 텍스트), 위도, 경도를 서버에 전송
                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
                data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                data += "&" + URLEncoder.encode("place", "UTF-8") + "=" + URLEncoder.encode(place, "UTF-8");
                data += "&" + URLEncoder.encode("hint", "UTF-8") + "=" + URLEncoder.encode(hint, "UTF-8");
                data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
                data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");

                java.net.URL url = new URL(MAKEURL);

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
            if (result.equalsIgnoreCase("success")) {
                Toast.makeText(getActivity(), "기부가 완료되었습니다.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("failure")) {
                Toast.makeText(getActivity(), "글 작성 오류.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //데이터베이스에 저장되어있는 마커들 받아오는 쓰레드
    class GetMarkersThread extends Thread{
        @Override
        public void run(){
            try {
                java.net.URL url = new URL(GETURL);

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                //php서버에서 echo하는 데이터를 받음 (php서버에서 json형태로 parsing)
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                String result =  sb.toString();
                if (!result.equalsIgnoreCase("failure")) {
                    try {
                        JSONObject root = new JSONObject(result);
                        JSONArray ja = root.getJSONArray("result");
                        if(ja.length()!=0) {
                            for(int i=0;i<ja.length();i++){
                                //서버에서 받아온 결과를 list에 저장
                                markerList.add(ja.getJSONObject(i).getString("lat"));
                                markerList.add(ja.getJSONObject(i).getString("lng"));
                                markerList.add(ja.getJSONObject(i).getString("no"));
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

