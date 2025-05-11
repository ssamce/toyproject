package com.ssamce.toyproject.mapper.user

import com.ssamce.toyproject.domain.user.dto.UserDto
import com.ssamce.toyproject.domain.user.entity.User

fun UserDto.FormRequest.toEntity(encodedPassword: String): User =
    User(
        email = this.email,
        password = encodedPassword,
        name = this.name
    )

fun User.toResponse(): UserDto.FormResponse =
    UserDto.FormResponse(
        id = this.id,
        email = this.email,
        name = this.name,
        role = this.role
    )

fun User.updateByUser(request: UserDto.UpdateRequest, encryptedPassword: String): User {
    return this.copy(
        name = request.name ?: this.name,
        password = encryptedPassword
    )
}

fun User.updateByAdmin(request: UserDto.UpdateForAdminRequest, encryptedPassword: String): User {
    return this.copy(
        name = request.name ?: this.name,
        password = encryptedPassword,
        email = request.email ?: this.email,
        role = request.role ?: this.role
    )
}
