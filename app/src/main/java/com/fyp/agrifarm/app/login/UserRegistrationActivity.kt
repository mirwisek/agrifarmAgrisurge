package com.fyp.agrifarm.app.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.*
import com.fyp.agrifarm.app.profile.User
import com.fyp.agrifarm.utils.FirebaseUtils
import com.fyp.agrifarm.utils.PicassoUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.ActivityBuilder
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_user_registration.*

class UserRegistrationActivity: AppCompatActivity() {

    private var userProfileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_registration)

        var photoUri: Uri? = null
        FirebaseAuth.getInstance().currentUser?.let { user ->
            log("USER => ${user.displayName}")

            user.displayName?.let { uFullName.setText(user.displayName) }
            photoUri = user.photoUrl
            FirebaseUtils.downloadUserProfileImage(applicationContext, user, imageView, resources)

            // Downloads and Sets up the profile picture in the ImageView
//            FirebaseUtils.downloadUserProfileImage(this, user, imageView, resources)
        }

//        imageView.setOnClickListener {
//            changeProfileImage()
//        }

        btnGetStarted.setOnClickListener {
            getUserDetails(photoUri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    if (resultUri != null) {
                        userProfileUri = resultUri
                        PicassoUtils.loadCropAndSetImage(userProfileUri.toString(), imageView, resources)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    toast("Cropping Error: ${error.message}")
                }
            }
        }
    }

    private fun changeProfileImage() {
        val cropImage: ActivityBuilder = if (userProfileUri == null) {
            CropImage.activity()
        } else {
            // If picture already selected
            CropImage.activity(userProfileUri)
        }

        cropImage.setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                .setMinCropResultSize(300, 300)
                .setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setActivityTitle("Profile Photo")
//                .setMinCropResultSize(300,700)
                .start(this)
    }

    private fun getUserDetails(photoUri: Uri?) {
        val userFullName = uFullName.text.toString()
        val userOccupation = uOccupation.text.toString()
        val userAge = uAge.text.toString()
        val userLocation = uLocation.text.toString()

        if (userFullName.isEmpty() || userOccupation.isEmpty() || userAge.isEmpty() || userLocation.isEmpty()) {
            snackbarFallback(currentFocus, "Please fill all the fields...", Snackbar.LENGTH_LONG)
            return
        }

        progressBar.visible()

        val user = User(userLocation, userAge, userOccupation, userFullName, photoUri?.toString())

//        FirebaseUtils.uploadUserData(this, user, imageView) {
//            progressBar.gone()
//            saveToSharedPrefs()
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
        FirebaseUtils.storeUserDetails(applicationContext, null, user, user.age) {
            saveToSharedPrefs()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun saveToSharedPrefs() {
        sharedPrefs.edit()
                .putString(LoginActivity.KEY_USER_REGISTRATION_COMPLETE, "complete")
                .apply()
    }

}