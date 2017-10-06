package com.infullmobile.voicetempo

import android.util.Base64

class TokenGenerator {

    companion object {
        fun generateToken(username: String, password: String): String =
                "Basic ${Base64.encodeToString("$username:$password".toByteArray(), Base64.DEFAULT)}".dropLast(1)
    }
}