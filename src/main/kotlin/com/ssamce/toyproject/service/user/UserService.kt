package com.ssamce.toyproject.service.user

import com.ssamce.toyproject.domain.user.dto.UserDto
import com.ssamce.toyproject.domain.user.exception.InvalidPasswordException
import com.ssamce.toyproject.domain.user.exception.UnknownRequestException
import com.ssamce.toyproject.domain.user.exception.UserAlreadyExistsException
import com.ssamce.toyproject.domain.user.exception.UserNotFoundException
import com.ssamce.toyproject.mapper.user.toEntity
import com.ssamce.toyproject.mapper.user.toResponse
import com.ssamce.toyproject.repository.user.UserRepository
import com.ssamce.toyproject.security.JwtTokenProvider
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
            throw UserAlreadyExistsException(request.email)
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = request.toEntity(encodedPassword)
        val saved = userRepository.save(user)
        return saved.toResponse()
    }

    fun login(request: UserDto.LoginRequest): UserDto.TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidPasswordException()
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
            .orElseThrow { UnknownRequestException() }

        return user.toResponse()
    }
}