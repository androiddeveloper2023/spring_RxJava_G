package com.example.spring_mvvm_rxjava.model

import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @GET("/student")
    fun getAllStudents(): Single<List<Student>>

    @POST("/student")
    fun insertStudent(@Body body: JsonObject): Completable

    @PUT("/student/updating{name}")
    fun updateStudent(@Path("name") name:String, @Body body :JsonObject ) :Completable

    @DELETE("/student/deleting{name}")
    fun deleteStudent( @Path("name") name:String ) :Completable



}