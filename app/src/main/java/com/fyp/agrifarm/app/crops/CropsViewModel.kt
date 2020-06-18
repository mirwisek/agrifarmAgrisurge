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

    val currentState = MutableLiveData<ModelResultState>().apply {
        postValue(ModelResultState.UNSET)
    }

    val modelOutput: MutableLiveData<String> = MutableLiveData<String>()

    fun getModelOutput(): LiveData<String> {
        return modelOutput
    }

}