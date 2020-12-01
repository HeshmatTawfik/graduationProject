package com.heshmat.doctoreta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.heshmat.doctoreta.R;

import java.io.IOException;

public class FullScreenActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        imageView=findViewById(R.id.imgDisplay);
        Intent intent=getIntent();
        String img=intent.getStringExtra("img");
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media
                    .getBitmap(this.getContentResolver(), Uri.parse(img));
            Glide.with(this).load(bitmap).into(imageView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
