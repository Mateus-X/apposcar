package com.example.app_oscar.models

data class LoginRequest(val login: String, val password: String)

data class LoginResponse(val message: String, val userId: Int, val token: Int)

data class Filme(
    val id: String,
    val nome: String,
    val genero: String,
    val foto: String
)

data class Diretor(
    val id: String,
    val nome: String
)

data class VoteRequest(
    val userId: Int,
    val movieId: Int,
    val directorId: Int,
    val token: Int
)

data class VoteResponse(val message: String, val error: String?)

data class VoteStatusRequest(
    val userId: Int,
    val token: Int
)

data class VoteStatusResponse(
    val hasVoted: Boolean,
    val movieId: Int?,
    val directorId: Int?
)