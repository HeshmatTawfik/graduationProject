package com.heshmat.doctoreta.utils;

import android.os.AsyncTask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;


public class NTPServerConnect extends AsyncTask<Void, Void, Long> {
    private NTPServerListener listener;

    public NTPServerConnect(NTPServerListener listener) {
        this.listener = listener;
    }

    @Override
    protected Long doInBackground(Void... params) {



                //Connection OK
                return getCurrentNetworkTime();

    }
    private static final String TIME_SERVER = "time.google.com";

    @Override
    protected void onPostExecute(Long timeInMs) {
        super.onPostExecute(timeInMs);
        listener.onInternetConnect(timeInMs);
    }
    public  long getCurrentNetworkTime() {
        try {


            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            //long returnTime = timeInfo.getReturnTime();   //local device time
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time

           // new AlertDialog.Builder(ProfileInfo.this).setMessage(time.toString()).show();


            return returnTime;
        }
        catch (Exception e){
            String ss=e.getMessage();
          //  new AlertDialog.Builder(ProfileInfo.this).setMessage(e.getMessage()).show();
            return -1;
        }
    }
}
