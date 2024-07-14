package com.shruti.codsofttodoapp

import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.shruti.codsofttodoapp.databinding.ActivityMainBinding
import com.shruti.codsofttodoapp.databinding.BottomsheetTodoBinding
import java.util.Calendar


class MainActivity : AppCompatActivity(),TodoInterface {
    lateinit var binding : ActivityMainBinding
   var firestore = FirebaseFirestore.getInstance()
    var item = arrayListOf<TodoDataClass>()
    lateinit var linearLayout: LinearLayoutManager
    lateinit var adapter : TodoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = TodoAdapter(item,this)
        binding.recycler.adapter = adapter
        linearLayout = LinearLayoutManager(this)
        binding.recycler.layoutManager = linearLayout
        getCollectionTodo()
        binding.fab.setOnClickListener {
                val dialogBindingTodo = BottomsheetTodoBinding.inflate(layoutInflater)
                var bottomSheettodo = BottomSheetDialog(this).apply {
                    setContentView(dialogBindingTodo.root)
                    show()
                }
                dialogBindingTodo.btnsetdata.setOnClickListener{
                    var dialog = DatePickerDialog(this)
                    dialog.setOnDateSetListener{view,year,month,dayOfMonth ->
                        var simpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")
                        var calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR,year)
                        calendar.set(Calendar.MONTH,month)
                        calendar.set(Calendar.DATE,dayOfMonth)
                        var stringDate = simpleDateFormat.format(calendar.time)
                        dialogBindingTodo.btnsetdata.setText(stringDate)
                    }
                    dialog.show()
                }
                dialogBindingTodo.tvdone.setOnClickListener{
                    if(dialogBindingTodo.ettodo.text.isNullOrEmpty()){
                        dialogBindingTodo.ettodo.error = "Enter task"
                    }
                    else{
                        item.clear()
                        firestore.collection("todo").add(
                            TodoDataClass(title = dialogBindingTodo.ettodo.text.toString(),
                                time = dialogBindingTodo.btnsetdata.text.toString()),
                        )
                            .addOnSuccessListener {
                                getCollectionTodo()
                                Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Data failure", Toast.LENGTH_SHORT).show()
                            }
                            .addOnCanceledListener{
                                Toast.makeText(this, "Data cancel", Toast.LENGTH_SHORT).show()
                            }
                        adapter.notifyDataSetChanged()
                        bottomSheettodo.dismiss()
                    }
                }
            }

        }

    override fun delete(todoDataClass: TodoDataClass, position: Int) {
        item.clear()
        firestore.collection("todo").document(todoDataClass.id ?: "")
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,"Data delete",Toast.LENGTH_SHORT).show()
                getCollectionTodo()
            }
            .addOnCanceledListener {
                Toast.makeText(this,"Data Cancel",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this,"Data fail",Toast.LENGTH_SHORT).show()
            }
        adapter.notifyDataSetChanged()
        }

    override fun update(todoDataClass: TodoDataClass, position: Int) {
        val dialogBindingTodo = BottomsheetTodoBinding.inflate(layoutInflater)
        var bottomSheettodo = BottomSheetDialog(this).apply {
            setContentView(dialogBindingTodo.root)
            show()
        }
        dialogBindingTodo.ettodo.setText(todoDataClass.title)
        dialogBindingTodo.btnsetdata.setText(todoDataClass.time)
        dialogBindingTodo.btnsetdata.setOnClickListener{
            var dialog = DatePickerDialog(this)
            dialog.setOnDateSetListener{view,year,month,dayOfMonth ->
                var simpleDateFormat = SimpleDateFormat("dd,MMM,yyyy")
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR,year)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.DATE,dayOfMonth)
                var stringDate = simpleDateFormat.format(calendar.time)
                dialogBindingTodo.btnsetdata.setText(stringDate)
            }
            dialog.show()
        }
        dialogBindingTodo.tvdone.setOnClickListener{
            if(dialogBindingTodo.ettodo.text.isNullOrEmpty()){
                dialogBindingTodo.ettodo.error = "Enter task"
            }
            else{
                item.clear()
                var updateTodo =  TodoDataClass(title = dialogBindingTodo.ettodo.text.toString(),
                    time = dialogBindingTodo.btnsetdata.text.toString())
                firestore.collection("todo").document(todoDataClass.id ?:"")
                    .set(updateTodo)
                    .addOnSuccessListener {
                        getCollectionTodo()
                        Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data failure", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCanceledListener{
                        Toast.makeText(this, "Data cancel", Toast.LENGTH_SHORT).show()
                    }
                adapter.notifyDataSetChanged()
                bottomSheettodo.dismiss()
            }
        }
    }


    override fun getCollectionTodo() {
        item.clear()
        firestore.collection("todo").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            for (dc in snapshot!!) {
                val firestoreClass = dc.toObject(TodoDataClass::class.java)
                firestoreClass.id = dc.id
                item.add(firestoreClass)
            }
            adapter.notifyDataSetChanged()
        }
        }


    override fun todoMark(todoDataClass: TodoDataClass, position: Int) {
        item.clear()
        firestore.collection("todo").document(todoDataClass.id ?: "")
            .update(mapOf("completed" to todoDataClass.completed))
            .addOnSuccessListener {
                item[position].completed = todoDataClass.completed
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Todo successfully updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error occurred", e)
            }
    }
}

