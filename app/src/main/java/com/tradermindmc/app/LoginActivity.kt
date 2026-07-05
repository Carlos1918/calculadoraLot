package com.tradermindmc.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Si ya hay sesión activa, ir directo al main
        if (auth.currentUser != null) {
            goToMain()
            return
        }

        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val registerBtn = findViewById<Button>(R.id.btn_register)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val errorText = findViewById<TextView>(R.id.tv_error)
        val toggleText = findViewById<TextView>(R.id.tv_toggle)

        var isLoginMode = true

        // Toggle entre login y registro
        toggleText.setOnClickListener {
            isLoginMode = !isLoginMode
            if (isLoginMode) {
                loginBtn.visibility = View.VISIBLE
                registerBtn.visibility = View.GONE
                toggleText.text = getString(R.string.no_account)
            } else {
                loginBtn.visibility = View.GONE
                registerBtn.visibility = View.VISIBLE
                toggleText.text = getString(R.string.have_account)
            }
            errorText.visibility = View.GONE
        }

        // Login
        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = getString(R.string.fill_all_fields)
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            loginBtn.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    goToMain()
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    loginBtn.isEnabled = true
                    errorText.text = getErrorMessage(e.message)
                    errorText.visibility = View.VISIBLE
                }
        }

        // Registro
        registerBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = getString(R.string.fill_all_fields)
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (password.length < 6) {
                errorText.text = getString(R.string.password_min)
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            registerBtn.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    goToMain()
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    registerBtn.isEnabled = true
                    errorText.text = getErrorMessage(e.message)
                    errorText.visibility = View.VISIBLE
                }
        }
    }

    private fun getErrorMessage(message: String?): String {
        return when {
            message?.contains("email address is already") == true -> getString(R.string.error_email_used)
            message?.contains("password is invalid") == true -> getString(R.string.error_wrong_password)
            message?.contains("no user record") == true -> getString(R.string.error_no_user)
            message?.contains("badly formatted") == true -> getString(R.string.error_invalid_email)
            else -> getString(R.string.error_generic)
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
