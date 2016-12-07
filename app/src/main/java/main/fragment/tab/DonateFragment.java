package main.fragment.tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import psj.hahaha.R;
import utils.Constants;


public class DonateFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public double lati=0,longi=0;
    TextView tv;
    ImageView imageView;
    Bitmap photo;
    String value;
    String MAKEURL = "http://210.91.76.33:8080/marker/makemarker.php";
    String GETURL = "http://210.91.76.33:8080/marker/getmarkers.php";
    String GETTEXTURL = "http://210.91.76.33:8080/marker/getmarkertext.php";
    ArrayList<String> markerList;

    static final int ACTION_REQUEST_CAMERA = 1;


    public DonateFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DonateFragment newInstance(String param1, String param2) {
        DonateFragment fragment = new DonateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        markerList = new ArrayList<String>();
        GetMarkersThread thread = new GetMarkersThread();
        Constants.tpexecutor.execute(thread);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.dfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {        //현재는 +를 누르면 현재위치가 맵에 표시되게. 더 기능 필요
                Dialog dialog = new Dialog(getActivity());
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("힌트를 입력하세요");
                final EditText input = new EditText(getActivity());
                builder.setView(input);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        value = input.getText().toString();
                        LatLng position = getPosition();

                        MakeMarkAsync task =  new MakeMarkAsync();
                        task.execute(value,String.valueOf(position.latitude),String.valueOf(position.longitude));

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });


                builder.setItems(new CharSequence[] {"Camera" },
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {

                                    case 0:


                                        Intent cameraIntent = new Intent(
                                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                        startActivityForResult(
                                                cameraIntent,
                                                ACTION_REQUEST_CAMERA);


                                        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                        final EditText input = new EditText(getActivity());
                                        input.setSingleLine();

                                        alert.setTitle("식권");
                                        alert.setMessage("힌트 입력:");
                                        alert.setView(input);
                                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                value = input.getText().toString().trim();
                                                getPosition();
                                            }
                                        });

                                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.cancel();
                                            }
                                        });
                                        alert.show();

                                        break;

                                    default:
                                        break;
                                }
                            }
                        });

                builder.show();


            }
        });

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                for(int i=0;i<markerList.size()-2;i+=3){
                    googleMap.addMarker(new MarkerOptions().title(markerList.get(i+2)).position(new LatLng(Double.parseDouble(markerList.get(i)),Double.parseDouble(markerList.get(i+1)))));

                }

                // Get LocationManager object from System Service LOCATION_SERVICE
                Geocoder geo = new Geocoder(getActivity(), Locale.KOREAN);
                // Geocoder를 통한 위치확인
                try{
                    List<android.location.Address> addr = geo.getFromLocationName("명지대학교 자연캠퍼스", 2);
                    lati = addr.get(0).getLatitude();
                    longi = addr.get(0).getLongitude();
                }catch (IOException e){}
                LatLng MyongJi = new LatLng(lati, longi);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(MyongJi).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

        });

        return view;
    }




    public LatLng getPosition(){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        lati = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();

        LatLng myPosi = new LatLng(lati, longi);
        googleMap.addMarker(new MarkerOptions().position(myPosi));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosi).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return myPosi;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == DonateFragment.ACTION_REQUEST_CAMERA) {
                photo = (Bitmap) data.getExtras()
                        .get("data");

            }
        }
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

    class MakeMarkAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String context_body = (String)params[0];
                String lat = (String) params[1];
                String lng = (String) params[2];

                String data = URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(context_body, "UTF-8");
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
            if (result.equalsIgnoreCase("success")) {
                Toast.makeText(getActivity(), "기부가 완료되었습니다.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("failure")) {
                Toast.makeText(getActivity(), "글 작성 오류.", Toast.LENGTH_LONG).show();
            }
        }
    }

    class GetMarkersThread extends Thread{
        @Override
        public void run(){
            try {
                java.net.URL url = new URL(GETURL);

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
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
                                markerList.add(ja.getJSONObject(i).getString("lat"));
                                markerList.add(ja.getJSONObject(i).getString("lng"));
                                markerList.add(ja.getJSONObject(i).getString("body"));
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


    class GetMarkTextAsync extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "잠시만요.", "로딩중...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String lat = (String) params[0];
                String lng = (String) params[1];

                String data = URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
                data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");

                java.net.URL url = new URL(GETTEXTURL);

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
//                        View v = getActivity().getLayoutInflater().inflate(R.layout.mapinfo, null);
//                        tv = (TextView) v.findViewById(R.id.tvMap);
//                        tv.setText(ja.getJSONObject(0).getString("body"));
                        value = ja.getJSONObject(0).getString("body");
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

