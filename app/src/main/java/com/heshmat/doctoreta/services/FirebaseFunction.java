package com.heshmat.doctoreta.services;

import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class FirebaseFunction {
   // stripeChargeCall
    public static Task<HashMap<String,Object>> callFirebaseFunction(String functionName, Map<String,Object> map) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable(functionName)
                .call(map)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        try {
                            HashMap<String,Object> result = (HashMap<String, Object>) task.getResult().getData();
                            return result;
                        }
                        catch (Exception e) {
                            String ex=e.getMessage();
                            return null;
                        }
                    }
                });
    }

}
