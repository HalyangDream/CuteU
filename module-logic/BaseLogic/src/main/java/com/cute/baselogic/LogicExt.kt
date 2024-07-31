package com.cute.baselogic

import android.content.Context
import com.cute.baselogic.storage.DeviceDataStore
import com.cute.baselogic.storage.PromptDataStore
import com.cute.baselogic.storage.StatusDataStore
import com.cute.baselogic.storage.UserDataStore


val Context.userDataStore: UserDataStore
    get() = UserDataStore.get(this)

val Context.deviceDataStore: DeviceDataStore
    get() = DeviceDataStore.get(this)

val Context.statusDataStore: StatusDataStore
    get() = StatusDataStore.get(this)

val Context.promptDateStore: PromptDataStore
    get() = PromptDataStore.get(this)