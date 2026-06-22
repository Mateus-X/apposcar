package com.example.app_oscar.ui.confirm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_oscar.R
import com.example.app_oscar.models.VoteRequest
import com.example.app_oscar.models.VoteResponse
import com.example.app_oscar.conf.RetrofitClient
import com.example.app_oscar.utils.PrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val txtSelectedMovie = findViewById<TextView>(R.id.txtSelectedMovie)
        val txtSelectedDirector = findViewById<TextView>(R.id.txtSelectedDirector)
        val edtConfirmToken = findViewById<EditText>(R.id.edtConfirmToken)
        val btnFinalConfirm = findViewById<Button>(R.id.btnFinalConfirm)

        val movieId = PrefsHelper.getMovieVoteId(this)
        val directorId = PrefsHelper.getDirectorVoteId(this)

        txtSelectedMovie.text = "Filme: ${PrefsHelper.getMovieVoteName(this)}"
        txtSelectedDirector.text = "Diretor: ${PrefsHelper.getDirectorVoteName(this)}"

        if (PrefsHelper.isVotingLocked(this)) {
            btnFinalConfirm.isEnabled = false
            edtConfirmToken.isEnabled = false
            btnFinalConfirm.text = "Votação já enviada e bloqueada"
        }

        btnFinalConfirm.setOnClickListener {
            val tokenInput = edtConfirmToken.text.toString()

            if (movieId == -1 || directorId == -1) {
                showDialog("Erro", "Você precisa selecionar um filme e um diretor.")
                return@setOnClickListener
            }

            if (tokenInput.isBlank()) {
                showDialog("Erro", "Informe o token para confirmar o voto.")
                return@setOnClickListener
            }

            val userId = PrefsHelper.getUserId(this)
            val request = VoteRequest(userId, movieId, directorId, tokenInput.toInt())

            RetrofitClient.instance.confirmVote(request).enqueue(object : Callback<VoteResponse> {
                override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                    if (response.isSuccessful) {
                        PrefsHelper.setVotingLocked(this@ConfirmActivity, true)
                        btnFinalConfirm.isEnabled = false
                        edtConfirmToken.isEnabled = false
                        showDialog("Sucesso", "Seu voto foi registrado definitivamente.")
                    } else {
                        showDialog("Erro", "Token inválido")
                    }
                }

                override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                    showDialog("Erro de Rede", "Não foi possível conectar ao servidor.")
                }
            })
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}