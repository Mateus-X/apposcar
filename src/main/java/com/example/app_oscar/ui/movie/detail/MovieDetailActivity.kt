package com.example.app_oscar.ui.movie.detail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.app_oscar.R
import com.example.app_oscar.utils.PrefsHelper

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val imgDetailPoster = findViewById<ImageView>(R.id.imgDetailPoster)
        val txtDetailName = findViewById<TextView>(R.id.txtDetailName)
        val txtDetailGenre = findViewById<TextView>(R.id.txtDetailGenre)
        val btnVoteThisMovie = findViewById<Button>(R.id.btnVoteThisMovie)

        val id = intent.getStringExtra("ID") ?: ""
        val nome = intent.getStringExtra("NOME") ?: ""
        val genero = intent.getStringExtra("GENERO") ?: ""
        val foto = intent.getStringExtra("FOTO") ?: ""

        txtDetailName.text = nome
        txtDetailGenre.text = genero
        Glide.with(this).load(foto).into(imgDetailPoster)

        if (PrefsHelper.isVotingLocked(this)) {
            btnVoteThisMovie.isEnabled = false
            btnVoteThisMovie.text = "Votação Encerrada"
        }

        btnVoteThisMovie.setOnClickListener {
            PrefsHelper.saveMovieVote(this, id.toInt(), nome)
            Toast.makeText(this, "Voto para filme registrado localmente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}