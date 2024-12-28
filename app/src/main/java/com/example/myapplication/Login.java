package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView forgotPW = findViewById(R.id.forgotPW);

        forgotPW.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // Finger or mouse down
                case MotionEvent.ACTION_HOVER_ENTER: // Mouse enters
                    forgotPW.setPaintFlags(forgotPW.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    break;

                case MotionEvent.ACTION_UP: // Finger or mouse up
                case MotionEvent.ACTION_HOVER_EXIT: // Mouse exits
                    forgotPW.setPaintFlags(forgotPW.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

                    Intent intent = new Intent(getApplicationContext(), ForgotPW.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }
}