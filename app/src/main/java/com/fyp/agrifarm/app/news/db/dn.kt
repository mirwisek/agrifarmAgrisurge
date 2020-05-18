package com.fyp.agrifarm.app.news.db//package com.fyp.agrifarm.app.news.db
//
//import android.content.Context
//import android.os.AsyncTask
//import android.util.Log
//import com.google.firebase.firestore.CollectionReference
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//
//class DownloadNews(context: Context?) : AsyncTask<Void?, Void?, Void?>() {
//    private lateinit var newsref : CollectionReference
//    override fun onPreExecute() {
//        Log.i("Download NEWS", "onPreExecute: Working on it")
//        val currentdate = java.util.Calendar.getInstance()
//        val timestamp = SimpleDateFormat("yyyy_MM_dd").format(currentdate)
//        newsref =  FirebaseFirestore.getInstance().collection("news").document("2020").collection(timestamp)
//    }
//
//    override fun doInBackground(vararg params: Void?): Void? {
//
//        newsref.get().addOnSuccessListener { documentSnapshot ->
//
//            for (document in documentSnapshot.documents)
//            {
//                val newsarray = document.get("news") as List<Map<String, Any>>?
//            }
//
//        }.addOnFailureListener { exception ->
//
//        }
//
//        return null
//    }
//
//}