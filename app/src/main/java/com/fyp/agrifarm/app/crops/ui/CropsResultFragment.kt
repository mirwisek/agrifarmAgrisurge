package com.fyp.agrifarm.app.crops.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.toastFrag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_crops_result.*
import kotlinx.android.synthetic.main.fragment_crops_result.view.*
import java.io.File
import java.lang.Exception

class CropsResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crops_result, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = view.findViewById(R.id.progressCropDiagnosis)
        labelFeedback = view.findViewById(R.id.tvCropFeedback)

        val imageView = view.findViewById<ImageView>(R.id.ivCropSubject)
        arguments?.let {
            val args = it ?: return
            val path = args.getString(KEY_PHOTO_PATH)
            val file = File(path!!)
            Picasso.get().load(file).into(imageView, object: Callback {

                override fun onSuccess() {

                    uploadImageToFirebase(file)

                    Handler().postDelayed({
                        labelFeedback.visibility = View.VISIBLE
                        Handler().postDelayed({
                            progress.visibility = View.VISIBLE
                        }, 300)
                    }, 800)
                }

                override fun onError(e: Exception?) {
                }

            })

        }

    }

    private fun uploadImageToFirebase(photoFile: File) {

        val file = Uri.fromFile(photoFile)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        storageReference
                .child(uid)
                .child(file.lastPathSegment.toString())
                .putFile(file)
                .addOnFailureListener {
                    Log.d("uploadS", " Done")
                }.addOnSuccessListener {
                    progress.visibility = View.GONE
                    labelFeedback.text = "Image uploaded successfully 😘"
                }
    }

    companion object {
        const val KEY_PHOTO_PATH = "leafPhoto"

        private lateinit var progress: ProgressBar
        private lateinit var labelFeedback: TextView
        private var storageReference = FirebaseStorage.getInstance().getReference("CropImages/")

        fun create(photoPath: String) = CropsResultFragment().apply {
            log("Photo path is : $photoPath")
            arguments = Bundle().apply {
                putString(KEY_PHOTO_PATH, photoPath)
            }
        }
    }
}
