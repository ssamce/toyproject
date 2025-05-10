package com.ssamce.toyproject.domain.user.entity

import com.ssamce.toyproject.common.enums.Role
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    val email: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(nullable = false, length = 10)
    val name: String,

    @Column(
        name = "role",
        nullable = false,
        columnDefinition = "ENUM('USER','ADMIN')"
    )
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,

    @Column(name = "access_token", columnDefinition = "TEXT")
    var accessToken: String? = null,

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    var refreshToken: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateToken(access: String?, refresh: String?) {
        this.accessToken = access
        this.refreshToken = refresh
    }
}