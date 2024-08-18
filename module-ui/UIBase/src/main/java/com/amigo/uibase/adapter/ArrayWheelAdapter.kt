package com.amigo.uibase.adapter

import com.amigo.uibase.wheelview.WheelAdapter

class ArrayWheelAdapter<T>(val data: List<T>) : WheelAdapter<T> {

    override fun getItemsCount(): Int = data.size

    override fun getItem(index: Int): T = data[index]

    override fun indexOf(o: T): Int = data.indexOf(o)

}