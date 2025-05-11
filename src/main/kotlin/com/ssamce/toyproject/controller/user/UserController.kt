package com.ssamce.toyproject.controller.user

import com.ssamce.toyproject.domain.user.dto.UserDto
import com.ssamce.toyproject.security.JwtTokenProvider
import com.ssamce.toyproject.service.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
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
    fun myPage(request: HttpServletRequest): ResponseEntity<UserDto.FormResponse> {
        val token = jwtTokenProvider.getToken(request)
        val response = userService.getMyPage(token)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/update")
    fun updateUser(
        request: HttpServletRequest,
        @RequestBody updateRequest: UserDto.UpdateRequest
    ): ResponseEntity<UserDto.FormResponse> {
        val token = jwtTokenProvider.getToken(request)
        val updatedUser = userService.updateUser(token, updateRequest)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUserByAdmin(
        @PathVariable id: Long,
        @RequestBody updateRequest: UserDto.UpdateForAdminRequest
    ): ResponseEntity<UserDto.FormResponse> {
        val updatedUser = userService.updateUserByAdmin(id, updateRequest)
        return ResponseEntity.ok(updatedUser)
    }
}