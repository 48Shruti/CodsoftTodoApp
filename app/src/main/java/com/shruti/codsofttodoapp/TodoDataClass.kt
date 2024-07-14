package com.shruti.codsofttodoapp

data class TodoDataClass(
    var id : String ?= ""
    ,var title : String ?= "",
    var time : String ?= "",
    var completed : Boolean = false
)
