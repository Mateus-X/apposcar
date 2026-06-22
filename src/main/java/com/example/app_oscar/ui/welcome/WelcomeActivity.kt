package com.example.app_oscar.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_oscar.R
import com.example.app_oscar.models.Diretor
import com.example.app_oscar.models.Filme
import com.example.app_oscar.models.VoteStatusResponse
import com.example.app_oscar.conf.RetrofitClient
import com.example.app_oscar.models.VoteStatusRequest
import com.example.app_oscar.ui.confirm.ConfirmActivity
import com.example.app_oscar.ui.director.DirectorActivity
import com.example.app_oscar.ui.login.LoginActivity
import com.example.app_oscar.ui.movie.MovieActivity
import com.example.app_oscar.utils.PrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnVoteMovie: Button
    private lateinit var btnVoteDirector: Button
    private lateinit var btnConfirmVote: Button
    private lateinit var progressWelcome: ProgressBar
    private lateinit var layoutVotosRegistrados: LinearLayout
    private lateinit var txtVotoFilmeStatus: TextView
    private lateinit var txtVotoDiretorStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val txtToken = findViewById<TextView>(R.id.txtToken)
        btnVoteMovie = findViewById(R.id.btnVoteMovie)
        btnVoteDirector = findViewById(R.id.btnVoteDirector)
        btnConfirmVote = findViewById(R.id.btnConfirmVote)
        progressWelcome = findViewById(R.id.progressWelcome)
        layoutVotosRegistrados = findViewById(R.id.layoutVotosRegistrados)
        txtVotoFilmeStatus = findViewById(R.id.txtVotoFilmeStatus)
        txtVotoDiretorStatus = findViewById(R.id.txtVotoDiretorStatus)

        val token = PrefsHelper.getToken(this)
        txtToken.text = "Seu Token de Votação: $token"

        btnVoteMovie.setOnClickListener {
            startActivity(Intent(this, MovieActivity::class.java))
        }

        btnVoteDirector.setOnClickListener {
            startActivity(Intent(this, DirectorActivity::class.java))
        }

        btnConfirmVote.setOnClickListener {
            startActivity(Intent(this, ConfirmActivity::class.java))
        }

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            PrefsHelper.clearAll(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkBackendVoteStatus()
    }

    private fun checkBackendVoteStatus() {

        val request = VoteStatusRequest(
            userId = PrefsHelper.getUserId(this),
            token = PrefsHelper.getToken(this)
        )

        setButtonsEnabled(false)
        progressWelcome.visibility = View.VISIBLE
        layoutVotosRegistrados.visibility = View.GONE

        RetrofitClient.instance.checkVoteStatus(request).enqueue(object : Callback<VoteStatusResponse> {
            override fun onResponse(call: Call<VoteStatusResponse>, response: Response<VoteStatusResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val status = response.body()!!

                    if (status.hasVoted && status.movieId != null && status.directorId != null) {
                        PrefsHelper.setVotingLocked(this@WelcomeActivity, true)

                        fetchNamesFromIds(status.movieId, status.directorId)
                    } else {
                        // Não votou, libera a tela
                        progressWelcome.visibility = View.GONE
                        PrefsHelper.setVotingLocked(this@WelcomeActivity, false)
                        setButtonsEnabled(true)
                    }
                } else {
                    progressWelcome.visibility = View.GONE
                    Toast.makeText(this@WelcomeActivity, "${response}", Toast.LENGTH_SHORT).show()
                    setButtonsEnabled(true)
                }
            }

            override fun onFailure(call: Call<VoteStatusResponse>, t: Throwable) {
                progressWelcome.visibility = View.GONE
                Toast.makeText(this@WelcomeActivity, "Falha de conexão ao verificar votos.", Toast.LENGTH_SHORT).show()
                val isLockedLocally = PrefsHelper.isVotingLocked(this@WelcomeActivity)
                setButtonsEnabled(!isLockedLocally)
            }
        })
    }

    private fun fetchNamesFromIds(movieId: Int, directorId: Int) {
        var movieName = "ID não encontrado"
        var directorName = "ID não encontrado"

        // Usamos um contador para saber quando as duas requisições (filmes e diretores) terminaram
        var requestsCompleted = 0

        fun checkAllCompleted() {
            requestsCompleted++
            if (requestsCompleted == 2) {
                // Ambas as requisições terminaram, podemos atualizar a UI
                progressWelcome.visibility = View.GONE
                txtVotoFilmeStatus.text = "Filme: $movieName"
                txtVotoDiretorStatus.text = "Diretor: $directorName"
                layoutVotosRegistrados.visibility = View.VISIBLE
                setButtonsEnabled(false)
            }
        }

        // 1. Busca os Filmes
        RetrofitClient.instance.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                if (response.isSuccessful && response.body() != null) {
                    val filme = response.body()!!.find { it.id.toInt() == movieId }
                    if (filme != null) {
                        movieName = filme.nome
                    }
                }
                checkAllCompleted()
            }
            override fun onFailure(call: Call<List<Filme>>, t: Throwable) {
                movieName = "Erro ao carregar nome"
                checkAllCompleted()
            }
        })

        // 2. Busca os Diretores
        RetrofitClient.instance.getDiretores().enqueue(object : Callback<List<Diretor>> {
            override fun onResponse(call: Call<List<Diretor>>, response: Response<List<Diretor>>) {
                if (response.isSuccessful && response.body() != null) {
                    val diretor = response.body()!!.find { it.id.toInt() == directorId }
                    if (diretor != null) {
                        directorName = diretor.nome
                    }
                }
                checkAllCompleted()
            }
            override fun onFailure(call: Call<List<Diretor>>, t: Throwable) {
                directorName = "Erro ao carregar nome"
                checkAllCompleted()
            }
        })
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        btnVoteMovie.isEnabled = isEnabled
        btnVoteDirector.isEnabled = isEnabled
        btnConfirmVote.isEnabled = isEnabled
    }
}