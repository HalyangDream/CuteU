package com.cute.basic

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class NonInitialLiveData<T>(initialValue: T) : LiveData<T>() {

    private var isInitialValueSet = false

    init {
        // 设置初始值，但不传递给观察者
        setValue(initialValue)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { newValue ->
            if (isInitialValueSet) {
                observer.onChanged(newValue)
            } else {
                isInitialValueSet = true
            }
        })
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever {
            if (isInitialValueSet) {
                observer.onChanged(it)
            } else {
                isInitialValueSet = true
            }
        }
    }

    public override fun postValue(value: T) {
        if (isInitialValueSet) {
            super.postValue(value)
        } else {
            isInitialValueSet = true
        }
    }

    public override fun setValue(value: T) {
        if (isInitialValueSet) {
            super.setValue(value)
        } else {
            isInitialValueSet = true
        }
    }

}