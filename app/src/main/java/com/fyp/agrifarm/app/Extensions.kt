package com.fyp.agrifarm.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
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

fun Context.startAnActivity(cls: Class<*>) {
    startActivity(Intent(this, cls))
}

fun Fragment.startAnActivity(cls: Class<*>) {
    startActivity(Intent(requireContext(), cls))
}

// region Views

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.getSharedPrefs(): SharedPreferences {
    return getSharedPreferences(HomeFragment.KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
}

// endregion