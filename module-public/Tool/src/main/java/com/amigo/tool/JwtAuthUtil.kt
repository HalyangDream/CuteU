package com.amigo.tool

import com.auth0.jwt.algorithms.Algorithm


object JwtAuthUtil {

    fun jwtGenerate(content: String?): String {
        val headers: MutableMap<String, Any> = HashMap()
        headers["alg"] = "HS256"
        headers["typ"] = "JWT"

        return JWTCreator.init().withHeader(headers).withClaim("did", content)
            .sign(Algorithm.HMAC256(getJwtSecret()))
    }

    private fun getJwtSecret(): String {
        return "37bf92251cd5c121c6466493da87cea5"
    }
}