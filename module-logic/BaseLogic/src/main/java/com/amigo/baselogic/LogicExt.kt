package com.amigo.baselogic

import android.content.Context
import com.amigo.baselogic.storage.DeviceDataStore
import com.amigo.baselogic.storage.PromptDataStore
import com.amigo.baselogic.storage.StatusDataStore
import com.amigo.baselogic.storage.UserDataStore


val Context.userDataStore: UserDataStore
    get() = UserDataStore.get(this)

val Context.deviceDataStore: DeviceDataStore
    get() = DeviceDataStore.get(this)

val Context.statusDataStore: StatusDataStore
    get() = StatusDataStore.get(this)

val Context.promptDateStore: PromptDataStore
    get() = PromptDataStore.get(this)