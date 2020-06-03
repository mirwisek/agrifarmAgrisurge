package com.fyp.agrifarm.app.crops.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.MainActivity
import com.fyp.agrifarm.app.crops.CropsViewModel
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.toastFrag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_crops_result.*
import kotlinx.android.synthetic.main.fragment_crops_result.view.*
import org.json.JSONObject
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

        cropViewModel = ViewModelProvider(this).get(CropsViewModel::class.java)

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
                    cropViewModel.getModelOutput().observe(viewLifecycleOwner, Observer {
                        log("Output ${it}")
                        labelFeedback.text = it
                    })

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
                .child(file.lastPathSegment.toString())
                .putFile(file)
                .addOnFailureListener {
                    Log.d("uploadS", " Done")
                }.addOnSuccessListener { taskSnap ->
                    var path: String? = null
                    taskSnap?.metadata?.let { meta ->
                        path = "${file.lastPathSegment}"
                        ModelRequest.getInstance().writeInputFile(requireContext(), path!!)
                        cropViewModel.setPrediction()

                        cropViewModel.getModelOutput().observe(viewLifecycleOwner, Observer {
                            it?.let { output ->
                                progress.visibility = View.GONE
                                labelFeedback.text = output
                            }
                        })
                    }
                }

    }

    companion object {
        const val KEY_PHOTO_PATH = "leafPhoto"

        private lateinit var cropViewModel: CropsViewModel
        private lateinit var progress: ProgressBar
        private lateinit var labelFeedback: TextView
        private var storageReference = FirebaseStorage.getInstance().getReference("cropSamples/")

        fun create(photoPath: String) = CropsResultFragment().apply {
            log("Photo path is : $photoPath")
            arguments = Bundle().apply {
                putString(KEY_PHOTO_PATH, photoPath)
            }
        }
    }
}
