package com.example.app_oscar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_oscar.R
import com.example.app_oscar.models.Filme

class MovieAdapter(
    private val movies: List<Filme>,
    private val onClick: (Filme) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        val txtMovieName: TextView = view.findViewById(R.id.txtMovieName)
        val txtMovieGenre: TextView = view.findViewById(R.id.txtMovieGenre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val filme = movies[position]
        holder.txtMovieName.text = filme.nome
        holder.txtMovieGenre.text = filme.genero

        Glide.with(holder.itemView.context)
            .load(filme.foto)
            .into(holder.imgPoster)

        holder.itemView.setOnClickListener { onClick(filme) }
    }

    override fun getItemCount(): Int = movies.size
}