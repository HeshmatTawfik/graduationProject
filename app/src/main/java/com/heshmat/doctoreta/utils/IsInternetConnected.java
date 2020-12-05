package com.heshmat.doctoreta.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class IsInternetConnected extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private InternetCheckListener listener;

    public IsInternetConnected(Context context, InternetCheckListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try{
                HttpURLConnection connection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                connection.setRequestProperty("User-Agent", "Test");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(3000); //choose your own timeframe
                connection.setReadTimeout(4000); //choose your own timeframe
                connection.connect();
                int responseCode = connection.getResponseCode();
                //Connection OK
                return responseCode == 200;
            }catch (Exception e){
                return  false; //connectivity exists, but no internet.
            }
        } else {
            return  false; //no connectivity
        }
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        super.onPostExecute(isConnected);
        listener.onInternetConnect(isConnected);
    }
}
