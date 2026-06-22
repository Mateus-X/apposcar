package com.example.app_oscar.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {
    private const val PREFS_NAME = "oscar_prefs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginData(context: Context, userId: Int, token: Int) {
        getPrefs(context).edit().apply {
            putInt("USER_ID", userId)
            putInt("TOKEN", token)
            apply()
        }
    }

    fun getUserId(context: Context): Int = getPrefs(context).getInt("USER_ID", -1)
    fun getToken(context: Context): Int = getPrefs(context).getInt("TOKEN", -1)

    fun saveMovieVote(context: Context, movieId: Int, movieName: String) {
        getPrefs(context).edit().apply {
            putInt("MOVIE_ID", movieId)
            putString("MOVIE_NAME", movieName)
            apply()
        }
    }

    fun getMovieVoteId(context: Context): Int = getPrefs(context).getInt("MOVIE_ID", -1)
    fun getMovieVoteName(context: Context): String? = getPrefs(context).getString("MOVIE_NAME", "Nenhum filme selecionado")

    fun saveDirectorVote(context: Context, directorId: Int, directorName: String) {
        getPrefs(context).edit().apply {
            putInt("DIRECTOR_ID", directorId)
            putString("DIRECTOR_NAME", directorName)
            apply()
        }
    }

    fun getDirectorVoteId(context: Context): Int = getPrefs(context).getInt("DIRECTOR_ID", -1)
    fun getDirectorVoteName(context: Context): String? = getPrefs(context).getString("DIRECTOR_NAME", "Nenhum diretor selecionado")

    fun setVotingLocked(context: Context, locked: Boolean) {
        getPrefs(context).edit().putBoolean("VOTING_LOCKED", locked).apply()
    }

    fun isVotingLocked(context: Context): Boolean = getPrefs(context).getBoolean("VOTING_LOCKED", false)

    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}