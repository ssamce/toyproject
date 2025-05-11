package com.ssamce.toyproject.domain.user.dto

import com.ssamce.toyproject.common.enums.Role

class UserDto {

    data class FormRequest(
        val email: String,
        val password: String,
        val name: String
    )

    data class FormResponse(
        val id: Long,
        val email: String,
        val name: String,
        val role: Role
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class TokenResponse(
        val accessToken: String,
        val refreshToken: String
    )

    data class Condition(
        val email: String? = null,
        val name: String? = null
    )

    data class ConditionResponse(
        val id: Long,
        val email: String,
        val name: String
    )

    data class UpdateRequest(
        val name: String?,
        val password: String?
    )

    data class UpdateForAdminRequest(
        val name: String?,
        val password: String?,
        val email: String?,
        val role: Role?
    )
}