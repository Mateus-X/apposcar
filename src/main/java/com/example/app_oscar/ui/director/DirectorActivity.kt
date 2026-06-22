package com.example.app_oscar.ui.director

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_oscar.R
import com.example.app_oscar.models.Diretor
import com.example.app_oscar.conf.RetrofitClient
import com.example.app_oscar.utils.PrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectorActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private var diretoresList: List<Diretor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director)

        radioGroup = findViewById(R.id.radioGroupDirectors)
        val btnSaveDirectorVote = findViewById<Button>(R.id.btnSaveDirectorVote)

        if (PrefsHelper.isVotingLocked(this)) {
            btnSaveDirectorVote.isEnabled = false
            btnSaveDirectorVote.text = "Votação Encerrada"
        }

        loadDirectors()

        btnSaveDirectorVote.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedDirector = diretoresList.find { it.id.toInt() == selectedId }
                if (selectedDirector != null) {
                    PrefsHelper.saveDirectorVote(this, selectedDirector.id.toInt(), selectedDirector.nome)
                    Toast.makeText(this, "Voto para diretor registrado localmente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Selecione um diretor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDirectors() {
        RetrofitClient.instance.getDiretores().enqueue(object : Callback<List<Diretor>> {
            override fun onResponse(call: Call<List<Diretor>>, response: Response<List<Diretor>>) {
                if (response.isSuccessful && response.body() != null) {
                    diretoresList = response.body()!!
                    val currentSavedId = PrefsHelper.getDirectorVoteId(this@DirectorActivity)

                    for (diretor in diretoresList) {
                        val rb = RadioButton(this@DirectorActivity).apply {
                            text = diretor.nome
                            id = diretor.id.toInt()
                            isEnabled = !PrefsHelper.isVotingLocked(this@DirectorActivity)
                        }
                        radioGroup.addView(rb)
                        if (diretor.id.toInt() == currentSavedId) {
                            radioGroup.check(rb.id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Diretor>>, t: Throwable) {
                Toast.makeText(this@DirectorActivity, "Erro ao carregar diretores", Toast.LENGTH_SHORT).show()
            }
        })
    }
}