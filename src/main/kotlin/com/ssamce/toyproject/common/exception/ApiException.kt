package com.ssamce.toyproject.common.exception

class ApiException(
    val code: Int,
    val description: String,
    override val message: String
) : RuntimeException(message)