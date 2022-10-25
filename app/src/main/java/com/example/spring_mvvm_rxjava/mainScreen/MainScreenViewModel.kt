package com.example.spring_mvvm_rxjava.mainScreen

import com.example.spring_mvvm_rxjava.model.MainRepository
import com.example.spring_mvvm_rxjava.model.Student
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class MainScreenViewModel( private val mainRepository: MainRepository) {


    val progressBarSubject = BehaviorSubject.create<Boolean>()

    fun getAllStudents() : Single<List<Student>> {
        progressBarSubject.onNext(true)

        return mainRepository
            .getAllStudents()

            .doFinally {
                progressBarSubject.onNext(false)
            }

    }

    fun removeStudent(studentName :String) : Completable {
        return mainRepository.removeStudent(studentName)
    }



}


