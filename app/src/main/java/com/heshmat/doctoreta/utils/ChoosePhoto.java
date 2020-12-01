package com.heshmat.doctoreta.utils;

import android.app.Activity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ChoosePhoto {
    public static void choosePhoto(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }
}
