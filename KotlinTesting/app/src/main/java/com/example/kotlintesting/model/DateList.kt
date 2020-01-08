package com.example.kotlintesting.model

class DateList {
    var dt: Long = 0
    var main: Main? = null
    var weather: MutableList<Weather> = mutableListOf<Weather>()
    var clouds: Clouds? = null
    var wind: Wind? = null
    var sys: Sys? = null
    var dt_txt: String = ""

}