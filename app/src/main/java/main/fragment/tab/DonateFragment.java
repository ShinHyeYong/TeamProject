package main.fragment.tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.mikepenz.iconics.utils.Utils;

import main.MainActivity;
import psj.hahaha.R;

import static android.R.attr.data;
import static android.R.attr.onClick;
import static android.R.attr.value;


public class DonateFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public double lati=0,longi=0;
    TextView tv;
    ImageView imageView;
    Bitmap photo;
    String value;

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
                        getPosition();
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

                // Get LocationManager object from System Service LOCATION_SERVICE

                LatLng MyongJi = new LatLng(37.2222614, 127.187636);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(MyongJi).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }

        });

        return view;
    }




    public void getPosition(){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        lati = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();

        LatLng myPosi = new LatLng(lati, longi);
        googleMap.addMarker(new MarkerOptions().position(myPosi));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosi).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());


//        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//
//            public void onInfoWindowClick(Marker arg0) {
//                AlertDialog alert2 = new AlertDialog.Builder(getActivity())
//                        .setTitle("식권은 여기에")
//                        .setMessage("식권왕 골드로져씀")
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).show();
//            };
//        });

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



    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        @Override
        public View getInfoWindow(Marker arg0)
        {
            View v = getActivity().getLayoutInflater().inflate(R.layout.mapinfo, null);
            tv = (TextView) v.findViewById(R.id.tvMap);


            imageView = (ImageView) v.findViewById(R.id.imageView);
            imageView.setImageBitmap(photo);
            tv.setText(value);

            return v;
        }


        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
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


}

