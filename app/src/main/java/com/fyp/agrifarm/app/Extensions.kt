package com.fyp.agrifarm.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.news.ui.NewsDetailsFragment

fun Context.toast(msg: String, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}

fun Fragment.toastFrag(msg: String, len: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(msg, len)
}

fun log(msg: String) {
    Log.i("😎💕🌀👀😉😘", msg)
}

fun AppCompatActivity.fragTransaction(fragment: Fragment): FragmentTransaction {
    return supportFragmentManager.beginTransaction()
            .replace(R.id.container_details_activity, fragment)
}