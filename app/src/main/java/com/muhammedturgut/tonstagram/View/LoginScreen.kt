package com.muhammedturgut.tonstagram.View

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth referansını al
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@LoginScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun loginClick(view: View) {
        val email = binding.emailAndUserNameEditText.text.toString()
        val password = binding.paswordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                showCustomToast("Merhaba", layoutInflater)

                android.os.Handler().postDelayed({
                    val intent = Intent(this@LoginScreen, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000)
            }.addOnFailureListener {
                Toast.makeText(this@LoginScreen, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@LoginScreen, "Enter Email and Password", Toast.LENGTH_LONG).show()
        }
    }

    fun signUpClick(view: View) {
        val intentSignUpScreen = Intent(this@LoginScreen, SignUpScreen::class.java)
        startActivity(intentSignUpScreen)
    }

    // Custom Toast Mesaj Kodları
    private fun showCustomToast(message: String, layoutInflater: LayoutInflater) {
        // Özel düzeni şişir (inflate)
        val customView: View = layoutInflater.inflate(R.layout.custom_toast, null)

        // Toast oluştur
        val toast = Toast(customView.context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customView
        toast.setGravity(Gravity.TOP or Gravity.RIGHT, 0, 100)
        toast.show()
    }
}