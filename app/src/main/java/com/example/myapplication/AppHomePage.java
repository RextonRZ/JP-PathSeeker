package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class AppHomePage extends AppCompatActivity {

    private ImageSlider imageSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home_page);

        imageSlider = findViewById(R.id.imageSlider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();


        slideModels.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


    }
}