package com.example.app_oscar.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View // Importante adicionar o View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar // Importante adicionar o ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_oscar.R
import com.example.app_oscar.models.LoginRequest
import com.example.app_oscar.models.LoginResponse
import com.example.app_oscar.conf.RetrofitClient
import com.example.app_oscar.utils.PrefsHelper
import com.example.app_oscar.ui.welcome.WelcomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtLogin = findViewById<EditText>(R.id.edtLogin)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar) // Referência ao ProgressBar

        btnLogin.setOnClickListener {
            val login = edtLogin.text.toString()
            val password = edtPassword.text.toString()

            if (login.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostra o loading e desativa o botão
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            val request = LoginRequest(login, password)
            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    // Esconde o loading e reativa o botão
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        PrefsHelper.saveLoginData(this@LoginActivity, body.userId, body.token)
                        startActivity(Intent(this@LoginActivity, WelcomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login ou senha incorretos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Esconde o loading e reativa o botão em caso de erro também
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this@LoginActivity,
                        "Erro de conexão",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}