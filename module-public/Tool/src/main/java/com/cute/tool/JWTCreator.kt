package com.cute.tool

import android.util.Base64
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.SignatureGenerationException
import com.auth0.jwt.impl.ClaimsHolder
import com.auth0.jwt.impl.PayloadSerializer
import com.auth0.jwt.impl.PublicClaims
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.nio.charset.StandardCharsets
import java.util.Date

internal class JWTCreator {

    private val algorithm: Algorithm
    private var headerJson: String
    private var payloadJson: String

    @Throws(JWTCreationException::class)
    private constructor(
        algorithm: Algorithm,
        headerClaims: Map<String, Any>,
        payloadClaims: Map<String, Any>
    ) {
        this.algorithm = algorithm
        try {
            val mapper = ObjectMapper()
            val module = SimpleModule()
            module.addSerializer(ClaimsHolder::class.java, PayloadSerializer())
            mapper.registerModule(module)
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            headerJson = mapper.writeValueAsString(headerClaims)
            payloadJson = mapper.writeValueAsString(ClaimsHolder(payloadClaims))
        } catch (e: JsonProcessingException) {
            throw JWTCreationException(
                "Some of the Claims couldn't be converted to a valid JSON format.",
                e
            )
        }
    }


    companion object {
        /**
         * Initialize a JWTCreator instance.
         *
         * @return a JWTCreator.Builder instance to configure.
         */
        fun init(): Builder {
            return Builder()
        }

        /**
         * The Builder class holds the Claims that defines the JWT to be created.
         */
        class Builder internal constructor() {
            private val payloadClaims: MutableMap<String, Any>
            private var headerClaims: MutableMap<String, Any>

            init {
                payloadClaims = HashMap()
                headerClaims = HashMap()
            }

            /**
             * Add specific Claims to set as the Header.
             *
             * @param headerClaims the values to use as Claims in the token's Header.
             * @return this same Builder instance.
             */
            fun withHeader(headerClaims: Map<String, Any>): Builder {
                this.headerClaims = HashMap(headerClaims)
                return this
            }

            /**
             * Add a specific Key Id ("kid") claim to the Header.
             * If the [Algorithm] used to sign this token was instantiated with a KeyProvider, the 'kid' value will be taken from that provider and this one will be ignored.
             *
             * @param keyId the Key Id value.
             * @return this same Builder instance.
             */
            fun withKeyId(keyId: String): Builder {
                headerClaims[PublicClaims.KEY_ID] = keyId
                return this
            }

            /**
             * Add a specific Issuer ("iss") claim to the Payload.
             *
             * @param issuer the Issuer value.
             * @return this same Builder instance.
             */
            fun withIssuer(issuer: String?): Builder {
                addClaim(PublicClaims.ISSUER, issuer)
                return this
            }

            /**
             * Add a specific Subject ("sub") claim to the Payload.
             *
             * @param subject the Subject value.
             * @return this same Builder instance.
             */
            fun withSubject(subject: String?): Builder {
                addClaim(PublicClaims.SUBJECT, subject)
                return this
            }

            /**
             * Add a specific Audience ("aud") claim to the Payload.
             *
             * @param audience the Audience value.
             * @return this same Builder instance.
             */
            fun withAudience(vararg audience: String?): Builder {
                addClaim(PublicClaims.AUDIENCE, audience)
                return this
            }

            /**
             * Add a specific Expires At ("exp") claim to the Payload.
             *
             * @param expiresAt the Expires At value.
             * @return this same Builder instance.
             */
            fun withExpiresAt(expiresAt: Date?): Builder {
                addClaim(PublicClaims.EXPIRES_AT, expiresAt)
                return this
            }

            /**
             * Add a specific Not Before ("nbf") claim to the Payload.
             *
             * @param notBefore the Not Before value.
             * @return this same Builder instance.
             */
            fun withNotBefore(notBefore: Date?): Builder {
                addClaim(PublicClaims.NOT_BEFORE, notBefore)
                return this
            }

            /**
             * Add a specific Issued At ("iat") claim to the Payload.
             *
             * @param issuedAt the Issued At value.
             * @return this same Builder instance.
             */
            fun withIssuedAt(issuedAt: Date?): Builder {
                addClaim(PublicClaims.ISSUED_AT, issuedAt)
                return this
            }

            /**
             * Add a specific JWT Id ("jti") claim to the Payload.
             *
             * @param jwtId the Token Id value.
             * @return this same Builder instance.
             */
            fun withJWTId(jwtId: String?): Builder {
                addClaim(PublicClaims.JWT_ID, jwtId)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: Boolean?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: Int?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: Long?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: Double?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: String?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Claim value.
             *
             * @param name  the Claim's name.
             * @param value the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withClaim(name: String, value: Date?): Builder {
                assertNonNull(name)
                addClaim(name, value)
                return this
            }

            /**
             * Add a custom Array Claim with the given items.
             *
             * @param name  the Claim's name.
             * @param items the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withArrayClaim(name: String, items: Array<String?>?): Builder {
                assertNonNull(name)
                addClaim(name, items)
                return this
            }

            /**
             * Add a custom Array Claim with the given items.
             *
             * @param name  the Claim's name.
             * @param items the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withArrayClaim(name: String, items: Array<Int?>?): Builder {
                assertNonNull(name)
                addClaim(name, items)
                return this
            }

            /**
             * Add a custom Array Claim with the given items.
             *
             * @param name  the Claim's name.
             * @param items the Claim's value.
             * @return this same Builder instance.
             * @throws IllegalArgumentException if the name is null.
             */
            @Throws(IllegalArgumentException::class)
            fun withArrayClaim(name: String, items: Array<Long?>?): Builder {
                assertNonNull(name)
                addClaim(name, items)
                return this
            }

            /**
             * Creates a new JWT and signs is with the given algorithm
             *
             * @param algorithm used to sign the JWT
             * @return a new JWT token
             * @throws IllegalArgumentException if the provided algorithm is null.
             * @throws JWTCreationException     if the claims could not be converted to a valid JSON or there was a problem with the signing key.
             */
            @Throws(IllegalArgumentException::class, JWTCreationException::class)
            fun sign(algorithm: Algorithm?): String {
                requireNotNull(algorithm) { "The Algorithm cannot be null." }
                headerClaims[PublicClaims.ALGORITHM] = algorithm.name
                headerClaims[PublicClaims.TYPE] = "JWT"
                val signingKeyId = algorithm.signingKeyId
                signingKeyId?.let { withKeyId(it) }
                return JWTCreator(algorithm, headerClaims, payloadClaims).sign()
            }

            private fun assertNonNull(name: String?) {
                requireNotNull(name) { "The Custom Claim's name can't be null." }
            }

            private fun addClaim(name: String, value: Any?) {
                if (value == null) {
                    payloadClaims.remove(name)
                    return
                }
                payloadClaims[name] = value
            }
        }
    }


    @Throws(SignatureGenerationException::class)
    private fun sign(): String {
        val header = Base64.encodeToString(
            headerJson.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_PADDING
        )
        val payload = Base64.encodeToString(
            payloadJson.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_PADDING
        )
        var content = String.format("%s.%s", header, payload)
        content = content.replace("\n".toRegex(), "")
        val signatureBytes = algorithm.sign(content.toByteArray(StandardCharsets.UTF_8))
        val signature =
            Base64.encodeToString(signatureBytes, Base64.URL_SAFE or Base64.NO_PADDING)
        var str = String.format("%s.%s", content, signature)
        str = str.replace("\n".toRegex(), "")
        return str
    }

}