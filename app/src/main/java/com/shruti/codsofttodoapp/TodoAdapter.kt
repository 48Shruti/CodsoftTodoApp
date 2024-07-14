package com.shruti.codsofttodoapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter ( var item: ArrayList<TodoDataClass>, var todoInterface: TodoInterface) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.titletodo)
        val time = view.findViewById<TextView>(R.id.datetodo)
        val checkbox= view.findViewById<CheckBox>(R.id.checkboxtodo)
        val delete = view.findViewById<ImageView>(R.id.imgdelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(item[position].title)
        holder.time.setText(item[position].time)
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = item[position].completed
        if (item[position].completed) {
            holder.title.setTextColor(Color.GRAY)
            holder.time.setText("Completed")
        } else {
            holder.title.setTextColor(Color.BLACK)
            holder.time.setText(item[position].time)
        }
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item[position].completed = isChecked
            if (isChecked) {
                holder.title.setTextColor(Color.GRAY)
                holder.time.setText("Completed")
                todoInterface.todoMark(item[position], position)
            } else {
                holder.title.setTextColor(Color.BLACK)
                holder.time.setText(item[position].time)
                todoInterface.todoMark(item[position], position)
            }

        }
        holder.itemView.setOnClickListener {
            todoInterface.update(item[position],position)
        }

        holder.delete.setOnClickListener{
            todoInterface.delete(item[position],position)
        }

    }
}