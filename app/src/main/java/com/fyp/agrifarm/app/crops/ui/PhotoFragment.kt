package com.fyp.agrifarm.app.crops.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.fyp.agrifarm.R
import com.squareup.picasso.Picasso
import java.io.File


class PhotoFragment internal constructor() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = ImageView(context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments ?: return
        val resource = args.getString(FILE_NAME_KEY)
        if (resource != null) { // Load recent Image if it exists
            Picasso.get().load(File(resource)).into(view as ImageView)
        } else { // Otherwise load a placeholder image
            Picasso.get().load(R.drawable.ic_photo).into(view as ImageView)
        }
    }

    companion object {
        private const val FILE_NAME_KEY = "file_name"

        fun create(image: File) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putString(FILE_NAME_KEY, image.absolutePath)
            }
        }
    }
}