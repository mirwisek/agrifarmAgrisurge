package com.fyp.agrifarm.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.login.CustomTabsSpan
import com.fyp.agrifarm.app.login.UserRegistrationActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val ppURL = "http://agrifarm.website/privacy_policy.html"
        private const val tosURL = "http://agrifarm.website/terms.html"
        private var signInClient: GoogleSignInClient? = null
        const val KEY_USER_REGISTRATION_COMPLETE = "userRegistrationComplete"
        const val REQUEST_LOGIN = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        custom_tos_pp.text = getSpannedText()
        custom_tos_pp.movementMethod = LinkMovementMethod.getInstance()


//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken(getString(R.string.google_signin_token))
//                .requestProfile()
//                .build()

        // Build a GoogleSignInClient with the options specified by gso.

        // Build a GoogleSignInClient with the options specified by gso.
//        signInClient = GoogleSignIn.getClient(this@LoginActivity, gso)


        btnLogin.setOnClickListener {
//            startActivityForResult(signInClient?.signInIntent, REQUEST_LOGIN)
            launchLoginActivity()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            REQUEST_LOGIN -> {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                try {
//                    task.getResult(ApiException::class.java)?.let {
//                        onLoginSuccess()
//                    }
//                } catch (e: ApiException) {
//                    toast("Sign in failed")
//                    e.printStackTrace()
//                }
//            }
            REQUEST_LOGIN -> {
                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
//                    FirebaseAuth.getInstance().currentUser?.let {  user ->
//                        if (response!!.isNewUser) {
//                            // No need for registration
//                            startActivity(Intent(this, UserRegistrationActivity::class.java))
//                        } else {
                            startActivity(Intent(this, MainActivity::class.java))
//                        }
                        finish()
//                    }

                } else {
                    snackbarFallback(currentFocus, "Sign in Failed!", Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().currentUser?.let {
            onLoginSuccess()
        }
//        GoogleSignIn.getLastSignedInAccount(this)?.let {
//            onLoginSuccess()
//        }
    }

    private fun onLoginSuccess() {
//        sharedPrefs.getString(KEY_USER_REGISTRATION_COMPLETE, null)?.let {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//            return
//        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun launchLoginActivity() {
        val providers: List<IdpConfig> = listOf(
                GoogleBuilder().build()
        )
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_logo)
                .setTheme(R.style.LoginTheme)
                .build(),
                REQUEST_LOGIN)
    }

    private fun getSpannedText(): SpannableStringBuilder {
        val string = getString(R.string.tos_statement)
        val spanned = SpannableStringBuilder(string)

        val tos = getString(R.string.terms_of_service)
        val pp = getString(R.string.privacy_policy)

        getSpannedItem(spanned, string, tos, tosURL)
        getSpannedItem(spanned, string, pp, ppURL)

        return spanned
    }

    private fun getSpannedItem(spannable: SpannableStringBuilder,
                               statement: String, text: String, url: String) {

        val startIndex = statement.indexOf(text)
        val endIndex = startIndex + text.length
        spannable.setSpan(CustomTabsSpan(this, url), startIndex, endIndex, 0)
    }
}