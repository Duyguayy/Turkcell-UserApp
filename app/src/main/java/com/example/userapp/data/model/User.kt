package com.example.userapp.data.model
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val company: String
)

data class Company(
  val name: String? = null,
  val catchPhrase: String? = null,
  val bs: String? = null
)