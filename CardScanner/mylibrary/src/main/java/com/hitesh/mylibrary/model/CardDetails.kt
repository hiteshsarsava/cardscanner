package com.hitesh.mylibrary.model

import java.io.Serializable

data class CardDetails(
    val owner: String?,
    val number: String?,
    val expirationMonth: String?,
    val expirationYear: String?
) : Serializable