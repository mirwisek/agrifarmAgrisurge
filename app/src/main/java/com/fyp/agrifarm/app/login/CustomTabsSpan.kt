package com.fyp.agrifarm.app.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import com.fyp.agrifarm.R
import java.lang.ref.WeakReference

/*
 * Opens up URLs in ChromeTabs (with Customized Behaviour)
 */
class CustomTabsSpan(context: Context, url: String) : URLSpan(url) {

    private val mContext: WeakReference<Context> = WeakReference(context)
    private val mUrl: String = url
    private val customTabInent: CustomTabsIntent

    override fun onClick(widget: View) {
        mContext.get()?.let {  context ->
            customTabInent.launchUrl(context, Uri.parse(url))
        }
    }

    init {

        // Getting default color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        @ColorInt val color = typedValue.data

        customTabInent = CustomTabsIntent.Builder()
                .setToolbarColor(color)
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build()

        customTabInent.intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    }
}