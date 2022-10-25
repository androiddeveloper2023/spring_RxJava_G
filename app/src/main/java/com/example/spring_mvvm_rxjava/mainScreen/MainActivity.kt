package com.example.spring_mvvm_rxjava.mainScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spring_mvvm_rxjava.addStudent.AddStudentActivity
import com.example.spring_mvvm_rxjava.databinding.ActivityMainBinding
import com.example.spring_mvvm_rxjava.databinding.DialogDeleteItemBinding
import com.example.spring_mvvm_rxjava.model.MainRepository
import com.example.spring_mvvm_rxjava.model.Student
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

const val BASE_URL = "http://192.168.1.5:8080"
class MainActivity : AppCompatActivity(), StudentAdapter.StudentEvent {

    lateinit var binding: ActivityMainBinding
    lateinit var myAdapter: StudentAdapter
    private val compositeDisposable = CompositeDisposable()
    lateinit var mainScreenViewModel: MainScreenViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        mainScreenViewModel = MainScreenViewModel(MainRepository())
        binding.btnAddStudent.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }

        compositeDisposable.add(
            mainScreenViewModel.progressBarSubject.subscribe {
                if (it) {
                    runOnUiThread {
                        binding.progressMain.visibility = View.VISIBLE
                        binding.recyclerMain.visibility = View.INVISIBLE
                    }
                } else {
                    runOnUiThread {
                        binding.progressMain.visibility = View.INVISIBLE
                        binding.recyclerMain.visibility = View.VISIBLE
                    }
                }
            }
        )

    }

    override fun onResume() {
        super.onResume()

        mainScreenViewModel
            .getAllStudents()
            . subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Student>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: List<Student>) {
                    setDataToRecycler(t)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(this@MainActivity, "error -> " + e.message ?: "null", Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onItemClicked(student: Student, position: Int) {
        val intent = Intent(this, AddStudentActivity::class.java)
        intent.putExtra("student", student)
        startActivity(intent)
    }

    override fun onItemLongClicked(student: Student, position: Int) {
        val dialog = AlertDialog.Builder(this).create()

        val dialogBinding = DialogDeleteItemBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.show()

        dialogBinding.btnNo.setOnClickListener{
            dialog.dismiss()
        }

        dialogBinding.btnDelete.setOnClickListener{
            deleteDataFromServer(student, position)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteDataFromServer(student: Student, position: Int) {

        mainScreenViewModel
            .removeStudent(student.name)
            . subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onComplete() {
                    Toast.makeText(this@MainActivity, "student removed :)", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(this@MainActivity, "error -> " + e.message ?: "null", Toast.LENGTH_SHORT).show()                }

            })


        myAdapter.removeItem(student, position)

    }

    private fun setDataToRecycler(data: List<Student>) {
        val myData = ArrayList(data)
        myAdapter = StudentAdapter(myData, this)
        binding.recyclerMain.adapter = myAdapter
        binding.recyclerMain.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}