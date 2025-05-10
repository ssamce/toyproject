package com.ssamce.toyproject.controller.user

import com.ssamce.toyproject.domain.user.dto.UserDto
import com.ssamce.toyproject.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: UserDto.FormRequest): ResponseEntity<UserDto.FormResponse> {
        val response = userService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: UserDto.LoginRequest): ResponseEntity<UserDto.TokenResponse> {
        val response = userService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun myPage(@RequestHeader("Authorization") authHeader: String): ResponseEntity<UserDto.FormResponse> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = userService.getMyPage(token)
        return ResponseEntity.ok(response)
    }
}