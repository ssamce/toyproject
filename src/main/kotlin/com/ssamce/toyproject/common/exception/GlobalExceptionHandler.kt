package com.ssamce.toyproject.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "code" to ex.code,
            "description" to ex.description,
            "message" to ex.message
        )
        return ResponseEntity.status(ex.code).body(body)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDenied(ex: AuthorizationDeniedException): ResponseEntity<Map<String, Any>> {
        val errorBody = mapOf(
            "code" to 403,
            "description" to "FORBIDDEN",
            "message" to "접근 권한이 없습니다."
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody)
    }


    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "code" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "description" to "Internal Server Error",
            "message" to (ex.message ?: "알 수 없는 오류가 발생했습니다.")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}