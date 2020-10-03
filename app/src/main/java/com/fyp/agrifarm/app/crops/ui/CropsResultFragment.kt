package com.fyp.agrifarm.app.crops.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.CropsViewModel
import com.fyp.agrifarm.app.crops.ModelRequest
import com.fyp.agrifarm.app.crops.ModelResponse
import com.fyp.agrifarm.app.crops.ModelResultState
import com.fyp.agrifarm.app.gone
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.snackbarFallback
import com.fyp.agrifarm.app.visible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_crops_result.*
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CropsResultFragment : Fragment() {

    private var modelResultBottomSheet: ModelResultBottomSheet? = null

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

        cropViewModel = ViewModelProvider(requireActivity()).get(CropsViewModel::class.java)

        progress = view.findViewById(R.id.progressCropDiagnosis)
        labelFeedback = view.findViewById(R.id.tvCropFeedback)

        cardResult.setOnClickListener {
            modelResultBottomSheet?.let { sheet ->
                sheet.show(parentFragmentManager, sheet.tag)
            }
        }


        val imageView = view.findViewById<ImageView>(R.id.ivCropSubject)
        arguments?.let {
            val args = it ?: return
            val path = args.getString(KEY_PHOTO_PATH)
            val file = File(path!!)
            Picasso.get().load(file).into(imageView, object : Callback {

                override fun onSuccess() {

//                    sendPredictionRequest(file)
//
//                    cropViewModel.currentState.observe(viewLifecycleOwner, Observer { model ->
//                        model?.let {  state ->
//                            when(state) {
//                                ModelResultState.ERROR -> {
//                                    progress.gone()
//                                    labelFeedback.text = "Sorry couldn't process your request (Server Error)"
//                                }
//                                ModelResultState.SUCCESS -> {
//                                    cropViewModel.getModelOutput().observe(viewLifecycleOwner, Observer { out ->
//                                        out?.let { output ->
//                                            progress.visibility = View.GONE
//                                            labelFeedback.text = output
//
//                                            modelResultBottomSheet = ModelResultBottomSheet(output)
//                                            modelResultBottomSheet?.show(parentFragmentManager, modelResultBottomSheet?.tag)
//                                            cropDiseaseName.text = output
//                                            cardResult.visible()
//                                        }
//                                    })
//                                }
//                                else -> { }
//                            }
//                        }
//                    })
                    cropViewModel.getTfLiteModelOutput().observe(viewLifecycleOwner, Observer { out ->
                        out?.let { output ->
                            progress.visibility = View.GONE
                            var result = output.replace("_",  " ")
                            result = result.replace("\\s+".toRegex() ," ")
                            labelFeedback.text = result
                            modelResultBottomSheet = ModelResultBottomSheet(result)
                            modelResultBottomSheet?.show(parentFragmentManager, modelResultBottomSheet?.tag)
                            cropDiseaseName.text = result
                            cardResult.visible()
                        }
                    })

                    // Add a little animation
                    Handler().postDelayed({
                        labelFeedback.visibility = View.VISIBLE
                        Handler().postDelayed({
                            progress.visibility = View.GONE
                        }, 300)
                    }, 800)
                }

                override fun onError(e: Exception?) {
                }

            })

        }

    }

//
//    private fun sendPredictionRequest(image: File) {
//
//        ModelRequest.instance.predict(image, object : retrofit2.Callback<ModelResponse> {
//
//            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
//                log("Unsucessful <RETROFIT>:: ${t.message}")
//                cropViewModel.currentState.postValue(ModelResultState.ERROR)
//                t.printStackTrace()
//                snackbarFallback("[Error] Couldn't find disease, please try again!")
//            }
//
//            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
//                if (response.isSuccessful) {
//                    response.body()?.let { list ->
//
//                        val sortedList = list.output.sortedByDescending { x -> x.probability }
//
////                        sortedList.forEach {  log("SORT:: ${it.label} and ${it.probability}") }
//                        cropViewModel.modelOutput.postValue(sortedList[0].label)
//                        cropViewModel.currentState.postValue(ModelResultState.SUCCESS)
//                    }
//                } else {
//                    cropViewModel.currentState.postValue(ModelResultState.ERROR)
//                    snackbarFallback("Couldn't find disease, please try again!")
//                    val err = response.errorBody()?.string()
//                    log("Unsucessful <RETROFIT>:: $err")
//                }
//
//            }
//        })
//    }

    companion object {
        const val KEY_PHOTO_PATH = "leafPhoto"

        private lateinit var cropViewModel: CropsViewModel
        private lateinit var progress: ProgressBar
        private lateinit var labelFeedback: TextView

        fun create(photoPath: String) = CropsResultFragment().apply {
            log("Photo path is : $photoPath")
            arguments = Bundle().apply {
                putString(KEY_PHOTO_PATH, photoPath)
            }
        }
    }
}
