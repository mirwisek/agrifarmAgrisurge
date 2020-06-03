package com.fyp.agrifarm.app.crops.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.CropsViewModel
import com.fyp.agrifarm.app.log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.services.discovery.Discovery
import com.google.api.services.discovery.model.JsonSchema
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ModelRequest {

    private lateinit var httpTransport: HttpTransport
    private lateinit var jsonFactory: JsonFactory
    private lateinit var credential: GoogleAccountCredential
    private lateinit var inputFile: File

    companion object {
        private const val PROJECT_ID = "agrifarm-58a88"
        // Default Values
        private var MODEL_ID: String = "agrifarmResnet"
        private var VERSION_ID: String = "v1"

        private val instance = ModelRequest()

        fun getInstance() : ModelRequest {
            return instance
        }
    }

    private constructor()

    // Initialize from Firestore
    fun init(modelName: String, version: String) {
        MODEL_ID = modelName
        VERSION_ID = version
    }

    fun formatInput(resources: Resources): String {
        val bitmap = BitmapFactory.decodeResource(resources, R.raw.lb1)
        val os = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        val byteArray = os.toByteArray()
//        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        return Base64.encodeBase64String(byteArray)
    }

    fun setCredentials(credential: GoogleAccountCredential) {
        this.credential = credential
    }
    
    fun writeInputFile(context: Context, storagePath: String) {
        val json = JSONObject()
        val array = JSONArray()
        array.put(storagePath)
        json.put("instances", array)
        log("input json $json")

        val file = File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS)?.absolutePath +  "/jsonInput.txt")
        file.writeText(json.toString())
        inputFile = file
    }

    @Throws(IOException::class)
    fun predict(): String? {

        httpTransport = NetHttpTransport()
        jsonFactory = JacksonFactory.getDefaultInstance()
        val discovery = Discovery.Builder(httpTransport, jsonFactory, null).build()
        val api = discovery.apis().getRest("ml", "v1").execute()
        val method = api.resources["projects"]!!.methods["predict"]
        val param = JsonSchema()
        param["name"] = "projects/${PROJECT_ID}/models/${MODEL_ID}/versions/${VERSION_ID}"
        val url = GenericUrl(UriTemplate.expand(api.baseUrl + method!!.path, param, true))
        println(url)
        val contentType = "application/json"

//        File requestBodyFile = new File(is);
//        FileContent(contentType, input)
        val content: HttpContent = FileContent(contentType, inputFile)
//        val scopes = mutableListOf(MainActivity.GCP_ML_SCOPE)

        val requestFactory: HttpRequestFactory = httpTransport.createRequestFactory(credential)
        val request = requestFactory.buildRequest(method.httpMethod, url, content)
        return request.execute().parseAsString()
    }

}
