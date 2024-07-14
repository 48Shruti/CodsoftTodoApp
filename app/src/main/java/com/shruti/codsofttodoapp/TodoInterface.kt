package com.shruti.codsofttodoapp

interface TodoInterface {
    fun delete (todoDataClass: TodoDataClass,position: Int)
    fun update(todoDataClass: TodoDataClass,position: Int)
    fun getCollectionTodo()
    fun todoMark(todoDataClass: TodoDataClass,position:Int)
}