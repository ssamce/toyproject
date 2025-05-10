package com.ssamce.toyproject.security

import com.ssamce.toyproject.domain.user.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(private val env: Environment) {
    private val secretKey: String = env.getRequiredProperty("JWT_SECRET")
    private val accessTokenExpireTime = 1000 * 60 * 60 // 1시간
    private val refreshTokenExpireTime = 1000 * 60 * 60 * 24 * 7 // 7일

    fun createAccessToken(user: User): String {
        val claims = mapOf(
            "id" to user.id,
            "email" to user.email,
            "name" to user.name,
            "role" to user.role.name
        )
        return createToken(claims, accessTokenExpireTime)
    }

    fun createRefreshToken(user: User): String {
        val claims = mapOf("id" to user.id)
        return createToken(claims, refreshTokenExpireTime)
    }

    private fun createToken(claims: Map<String, Any?>, expirationTime: Int): String {
        val now = Date()
        val expiry = Date(now.time + expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getUserId(claims: Claims): Long {
        return claims["id"].toString().toLong()
    }

    fun getUserId(token: String): Long {
        val claims = getClaims(token)
        return claims["id"].toString().toLong()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userId = getUserId(claims)
        val role = claims["role"].toString()
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        val principal = org.springframework.security.core.userdetails.User(
            userId.toString(), "", authorities
        )
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}