package com.example.hk_pc.gmf;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hk_pc.gmf.Map.Common;
import com.example.hk_pc.gmf.Model.PlaceDetail;
import com.example.hk_pc.gmf.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activity_viewPlaces extends AppCompatActivity {

    ImageView photo;

    IGoogleAPIService mService;
    RatingBar ratingBar;
    TextView opening_hour,place_address,place_name;
    Button viewMap, showDirection;
    PlaceDetail mPlaceDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_places);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mService = Common.getGoogleAPIService();

        photo = (ImageView)findViewById(R.id.photo);

        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        opening_hour = (TextView)findViewById(R.id.place_open_hour);
        place_address = (TextView)findViewById(R.id.place_address);
        place_name = (TextView)findViewById(R.id.place_name);
        viewMap = (Button)findViewById(R.id.btn_show_map);
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlaceDetail.getResult().getUrl()));
                startActivity(mapIntent);
            }
        });

        showDirection = (Button)findViewById(R.id.btn_show_direction);
        showDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(activity_viewPlaces.this,activity_viewDirection.class);
                startActivity(mapIntent);
            }
        });

        place_name.setText("");
        place_address.setText("");
        opening_hour.setText("");

        if(Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0){
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }

        if(Common.currentResult.getRating()!=null && !TextUtils.isEmpty(Common.currentResult.getRating())){
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else {
            ratingBar.setVisibility(View.GONE);
        }

        if(Common.currentResult.getOpening_hour()!=null){
            opening_hour.setText("Open now: "+Common.currentResult.getOpening_hour().getOpen_now());
        }
        else {
            opening_hour.setVisibility(View.GONE);
        }

        mService.getDetailPlaces(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlaceDetail = response.body();
                        place_address.setText(mPlaceDetail.getResult().getFormatted_address());
                        place_name.setText(mPlaceDetail.getResult().getName());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });

    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("place detail",url.toString());
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photo_reference);
        url.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("photourl",url.toString());
        return url.toString();

    }
}


