package com.amigo.im.cutom

import org.json.JSONObject

abstract class CustomMessage {

    abstract fun toJson(): JSONObject?

    abstract fun parseJson(json: String?)

    abstract fun identity(): Int

    abstract fun intoDb(): Boolean

    abstract fun shortContent(): String

    abstract fun identityString(): String
}