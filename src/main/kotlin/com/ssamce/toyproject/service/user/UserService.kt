package com.ssamce.toyproject.service.user

import com.ssamce.toyproject.common.exception.ApiException
import com.ssamce.toyproject.domain.user.dto.UserDto
import com.ssamce.toyproject.mapper.user.toEntity
import com.ssamce.toyproject.mapper.user.toResponse
import com.ssamce.toyproject.mapper.user.updateByAdmin
import com.ssamce.toyproject.mapper.user.updateByUser
import com.ssamce.toyproject.repository.user.UserRepository
import com.ssamce.toyproject.security.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun signup(request: UserDto.FormRequest): UserDto.FormResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ApiException(
                409,
                "EMAIL_ALREADY_EXISTS",
                "이미 존재하는 email 입니다."
            )
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = request.toEntity(encodedPassword)
        val saved = userRepository.save(user)
        return saved.toResponse()
    }

    fun login(request: UserDto.LoginRequest): UserDto.TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw ApiException(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.reasonPhrase,
                "email 혹은 비밀번호를 확인해주세요."
            )

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ApiException(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.reasonPhrase,
                "email 혹은 비밀번호를 확인해주세요."
            )
        }

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = jwtTokenProvider.createRefreshToken(user)

        user.updateToken(accessToken, refreshToken)
        userRepository.save(user)

        return UserDto.TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun getMyPage(token: String): UserDto.FormResponse {
        val userId = jwtTokenProvider.getUserId(token)
        val user = userRepository.findById(userId)
            .orElseThrow {
                throw ApiException(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.reasonPhrase,
                    "잘못된 요청입니다."
                )
            }

        return user.toResponse()
    }

    fun updateUser(token: String, request: UserDto.UpdateRequest): UserDto.FormResponse {
        val userId = jwtTokenProvider.getUserId(token)
        val user = userRepository.findById(userId)
            .orElseThrow {
                throw ApiException(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.reasonPhrase,
                    "잘못된 요청입니다."
                )
            }
        val encryptedPassword = if (request.password != null) {
            passwordEncoder.encode(request.password)
        } else {
            user.password
        }

        return userRepository.save(user.updateByUser(request, encryptedPassword)).toResponse()
    }

    fun updateUserByAdmin(userId: Long, request: UserDto.UpdateForAdminRequest): UserDto.FormResponse {
        val user = userRepository.findById(userId)
            .orElseThrow {
                throw ApiException(
                    HttpStatus.UNAUTHORIZED.value(),
                    "UserNotFound",
                    "존재하지 않는 id 입니다."
                )
            }

        val encryptedPassword = if (request.password != null) {
            passwordEncoder.encode(request.password)
        } else {
            user.password
        }

        return userRepository.save(user.updateByAdmin(request, encryptedPassword)).toResponse()
    }
}