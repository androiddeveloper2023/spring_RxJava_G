package com.example.spring_mvvm_rxjava.addStudent

import com.example.spring_mvvm_rxjava.model.MainRepository
import com.example.spring_mvvm_rxjava.model.Student
import io.reactivex.Completable

class AddStudentViewModel (private val mainRepository: MainRepository) {
    fun insertNewStudent(student: Student) :Completable {
        return mainRepository.insertStudent(student)
    }

    fun updateStudent(student: Student) : Completable {
        return mainRepository.updateStudent(student)
    }

}