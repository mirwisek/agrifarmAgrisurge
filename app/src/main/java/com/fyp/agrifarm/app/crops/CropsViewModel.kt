package com.fyp.agrifarm.app.crops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fyp.agrifarm.app.crops.ui.ModelRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CropsViewModel() : ViewModel() {

    private val TIME_OUT = 240_000L

    private val modelOutput: MutableLiveData<String> = MutableLiveData<String>()
    val modelMetadata = MutableLiveData<HashMap<String, String>>().apply {
        FirebaseFirestore.getInstance().document("static/model").get()
                .addOnSuccessListener { docSnap ->
                    // Default values
                    val name = docSnap.getString("name") ?: "agrifarmResnet"
                    val version = docSnap.getString("version") ?: "v1"

                    postValue(hashMapOf(
                            "name" to name,
                            "version" to version
                    ))
                }
    }

    fun setPrediction() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = viewModelScope.async(Dispatchers.IO) {
                ModelRequest.getInstance().predict()
            }
            withTimeout(TIME_OUT) {
                modelOutput.postValue(result.await())
            }
        }
    }

    fun getModelOutput(): LiveData<String> {
        return modelOutput
    }
}