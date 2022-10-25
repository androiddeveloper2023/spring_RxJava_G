package com.example.spring_mvvm_rxjava.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class Student (


    val name: String,
    val course: String,
    val score: Int


        ): Parcelable