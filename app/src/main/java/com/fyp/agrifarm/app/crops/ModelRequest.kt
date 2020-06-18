package com.fyp.agrifarm.app.crops

import com.fyp.agrifarm.api.NetworkFactory
import com.fyp.agrifarm.app.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.coroutines.CoroutineContext

class ModelRequest : CoroutineScope {

    companion object {

        private val _instance = ModelRequest()

        val instance: ModelRequest
            get() = _instance
    }

    private constructor()

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    fun predict(image: File, callback: Callback<ModelResponse>) {

        val request = MultipartBody.Part.createFormData("file", image.name,
                image.asRequestBody("image/*".toMediaTypeOrNull()))
        log("req about to send $request")
        NetworkFactory.getPrediction(request, callback)
    }

}
