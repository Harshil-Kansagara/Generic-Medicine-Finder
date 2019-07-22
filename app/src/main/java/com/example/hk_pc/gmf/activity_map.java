package com.example.hk_pc.gmf;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.example.hk_pc.gmf.Map.Common;
import com.example.hk_pc.gmf.Model.MyPlaces;
import com.example.hk_pc.gmf.Model.Results;
import com.example.hk_pc.gmf.Remote.IGoogleAPIService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activity_map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double latitude, longitude;
    private Location mLastLocation;
    private Marker marker;
    private LocationRequest locationRequest;

    IGoogleAPIService mService;
    private static final int My_PERMISSION_CODE = 100;

    MyPlaces currentPlaces;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mService = Common.getGoogleAPIService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_gms:
                        nearByPlace("pharmacy");
                        break;
                    default:
                        break;

                }
                return true;
            }
        });

        buildLocationCallBack();
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    @Override
    protected void onStop( )  {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void buildLocationCallBack( ) {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();

                if(marker != null){
                    marker.remove();
                }

                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                LatLng latLng = new LatLng(latitude,longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng).title("Your Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                marker = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            }
        };
    }

    private void buildLocationRequest( ) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    private void nearByPlace(String placeType) {
        mMap.clear();
        String url = getUrl(latitude,longitude,placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlaces = response.body();

                        if(response.isSuccessful()){
                            for(int i=0;i<response.body().getResults().length;i++){
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());

                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);

                                if(placeName.equalsIgnoreCase("pharmacy")){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                }

                                else {
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                }

                                markerOptions.snippet(String.valueOf(i));
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+30000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&keyword=generic");
        googlePlacesUrl.append("&keyword=jan%20aushadhi");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyBHad9ugQ69clF3Pk9Pm2kYojkgNCrz4Ww");
        Log.d("getUrl",googlePlacesUrl.toString());

        return googlePlacesUrl.toString();
    }

    private boolean checkLocationPermission( ) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, My_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, My_PERMISSION_CODE);
            }
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case My_PERMISSION_CODE:{
                if (grantResults.length>0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);

                        buildLocationCallBack();
                        buildLocationRequest();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getSnippet() != null){
                    Common.currentResult = currentPlaces.getResults()[Integer.parseInt(marker.getSnippet())];
                    startActivity( new Intent(activity_map.this,activity_viewPlaces.class));
                }
                return true;
            }
        });
    }
}
