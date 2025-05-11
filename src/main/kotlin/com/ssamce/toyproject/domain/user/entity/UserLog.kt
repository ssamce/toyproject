package com.ssamce.toyproject.domain.user.entity

import com.ssamce.toyproject.common.enums.ActionType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_log")
class UserLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(
        name = "action_type",
        nullable = false,
        columnDefinition = "ENUM('POST','PUT', 'GET', 'DELETE')"
    )
    @Enumerated(EnumType.STRING)
    val action: ActionType = ActionType.GET,

    @Column(nullable = false, length = 50)
    val type: String,

    @Column(name = "request_params", columnDefinition = "TEXT")
    val requestParams: String? = null,

    @Column(name = "response_params", columnDefinition = "TEXT")
    val responseParams: String? = null,

    @Column(name = "user_agent", columnDefinition = "TEXT")
    val userAgent: String? = null,

    @Column(name = "remote_ip", length = 45)
    val remoteIp: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)