package com.heshmat.doctoreta;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseInstance {
   private static FirebaseFirestore firebaseFirestore;


    public static FirebaseFirestore getInstance()
   {
       if (firebaseFirestore==null)
           firebaseFirestore = FirebaseFirestore.getInstance();
       return firebaseFirestore;
   }

}
