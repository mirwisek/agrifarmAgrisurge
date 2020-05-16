package com.fyp.agrifarm.app.crops.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fyp.agrifarm.R
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.json.JsonHttpContent
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.services.discovery.Discovery
import com.google.api.services.discovery.model.JsonSchema
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

object ModelRequestFragment {

    private lateinit var httpTransport: HttpTransport
    private lateinit var jsonFactory: JsonFactory
    var credential: GoogleAccountCredential? = null

    //    companion object {
    private const val PROJECT_ID = "agrifarm-58a88"
    private const val MODEL_ID = "agrifarm"
    private const val VERSION_ID = "v1"
//    }

    fun formatInput(resources: Resources): String {
        val bitmap = BitmapFactory.decodeResource(resources, R.raw.lb1)
        val os = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        val byteArray = os.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
//        return Base64.encodeBase64String(byteArray)
    }

    @Throws(IOException::class)
    fun predict(input: File): String? {

//        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
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
        val content: HttpContent = FileContent(contentType, input)
        println(content.length)
//        val scopes = mutableListOf(MainActivity.GCP_ML_SCOPE)

        val requestFactory: HttpRequestFactory = httpTransport.createRequestFactory(credential)
        val request = requestFactory.buildRequest(method.httpMethod, url, content)
        return request.execute().parseAsString()
    }

}
