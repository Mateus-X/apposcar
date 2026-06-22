package com.example.app_oscar.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_oscar.R
import com.example.app_oscar.models.Filme
import com.example.app_oscar.conf.RetrofitClient
import com.example.app_oscar.adapters.MovieAdapter
import com.example.app_oscar.ui.movie.detail.MovieDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val recyclerMovies = findViewById<RecyclerView>(R.id.recyclerMovies)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        recyclerMovies.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val adapter = MovieAdapter(response.body()!!) { filme ->
                        val intent =
                            Intent(this@MovieActivity, MovieDetailActivity::class.java).apply {
                                putExtra("ID", filme.id)
                                putExtra("NOME", filme.nome)
                                putExtra("GENERO", filme.genero)
                                putExtra("FOTO", filme.foto)
                            }
                        startActivity(intent)
                    }
                    recyclerMovies.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Filme>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MovieActivity, "Erro ao carregar filmes", Toast.LENGTH_SHORT).show()
            }
        })
    }
}