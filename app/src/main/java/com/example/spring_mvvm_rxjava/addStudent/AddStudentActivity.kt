package com.example.spring_mvvm_rxjava.addStudent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.spring_mvvm_rxjava.model.MainRepository
import com.example.spring_mvvm_rxjava.model.Student
import com.example.spring_mvvm_rxjava.databinding.ActivityAddStudentBinding
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddStudentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddStudentBinding
    lateinit var addStudentViewModel: AddStudentViewModel
    private val compositeDisposable = CompositeDisposable()
    var isInserting = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAddStudentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain2)
        addStudentViewModel = AddStudentViewModel(MainRepository())

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.edtFirstName.requestFocus()


        val testMode = intent.getParcelableExtra<Student>("student")
        isInserting = (testMode == null)
        if (!isInserting) {
            logicUpdateStudent()
        }

        binding.btnDone.setOnClickListener {

            if (isInserting) {
                addNewStudent()
            } else {
                updateStudent()
            }

        }


    }
    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun logicUpdateStudent() {
        binding.btnDone.text = "update"

        val dataFromIntent = intent.getParcelableExtra<Student>("student")!!
        binding.edtScore.setText(dataFromIntent.score.toString())
        binding.edtCourse.setText(dataFromIntent.course)

        val splitedName = dataFromIntent.name.split(" ")
        binding.edtFirstName.setText(splitedName[0])
        binding.edtLastName.setText(splitedName[(splitedName.size - 1)])
    }
    private fun updateStudent() {

        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {

            addStudentViewModel
                .updateStudent(
                    Student(firstName + " " + lastName, course, score.toInt())
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        Toast.makeText(this@AddStudentActivity, "student updated :)", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@AddStudentActivity, "error -> " + e.message ?: "null", Toast.LENGTH_SHORT).show()                    }
                })

        } else {
            Toast.makeText(this, "لطفا اطلاعات را کامل وارد کنید", Toast.LENGTH_SHORT).show()        }
    }
    private fun addNewStudent() {

        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {

            addStudentViewModel
                .insertNewStudent(
                    Student(firstName + " " + lastName, course, score.toInt())
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        Toast.makeText(this@AddStudentActivity, "student inserted :)", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@AddStudentActivity, "error -> " + e.message ?: "null", Toast.LENGTH_SHORT).show()                    }

                })


        } else {
            Toast.makeText(this, "لطفا اطلاعات را کامل وارد کنید", Toast.LENGTH_SHORT).show()        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return true
    }

}