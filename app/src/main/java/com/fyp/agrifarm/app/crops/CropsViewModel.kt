package com.fyp.agrifarm.app.crops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.prices.model.LoadState
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonObjectParser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class CropsViewModel : ViewModel() {

//    private val TIME_OUT = 500_000L
    private val TIME_OUT = 10_000L

    val currentState = MutableLiveData<ModelResultState>().apply {
        postValue(ModelResultState.UNSET)
    }

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
        try {
            viewModelScope.launch(Dispatchers.IO) {
                currentState.postValue(ModelResultState.LOADING)
                val result = viewModelScope.async(Dispatchers.IO) {
                    ModelRequest.getInstance().predict()
                }
                withTimeout(TIME_OUT) {
                    result.await()?.let { json ->
                        log("RES:: $json")
                        val body = JSONObject(json)
                        val arr: JSONArray = body.getJSONArray("predictions")
                        val predictions = hashMapOf<String, Double>()

                        for(i in 0 until arr.length()) {
                            val item = arr.getJSONObject(i)
                            // Should sort with cast to double first but it works with string anyway
                            // That might delete precision
                            predictions[item.getString("class")] = item.getString("prob").toDouble()
                        }

                        val maxProbability = Collections.max(predictions.values)
                        val maxClass = predictions.filterValues { it == maxProbability }
                        modelOutput.postValue(maxClass.keys.elementAt(0))
                        currentState.postValue(ModelResultState.SUCCESS)
                    }
                }
            }
        } catch (e: IOException) {
            currentState.postValue(ModelResultState.ERROR)
            e.printStackTrace()
        }
    }

    fun getModelOutput(): LiveData<String> {
        return modelOutput
    }

}