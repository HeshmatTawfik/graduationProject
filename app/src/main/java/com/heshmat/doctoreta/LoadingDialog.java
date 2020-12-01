package com.heshmat.doctoreta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView textView;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void setText(String text) {
        if (textView!=null)
            textView.setText(text);

    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_dialog, null);
        ImageView imageView = view.findViewById(R.id.loading_anim);
        textView=view.findViewById(R.id.loadingDialogTv);
        builder.setView(view);
        animatedLogo(imageView, activity);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();

        int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, activity.getResources().getDisplayMetrics());
        int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, activity.getResources().getDisplayMetrics());

        Objects.requireNonNull(dialog.getWindow()).setLayout(w, h);

    }

    public void dismissDialog() {

        if (dialog!=null)
        dialog.dismiss();
    }

    private void animatedLogo(ImageView imageView, Context activity) {
        final AnimatedVectorDrawable vectorDrawable = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.animated_logo);
        imageView.setImageDrawable(vectorDrawable);
        assert vectorDrawable != null;
        vectorDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                vectorDrawable.start();
            }
        });
        vectorDrawable.start();
    }

}
