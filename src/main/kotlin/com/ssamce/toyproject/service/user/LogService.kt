package com.ssamce.toyproject.service.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssamce.toyproject.common.enums.ActionType
import com.ssamce.toyproject.common.enums.Role
import com.ssamce.toyproject.domain.user.entity.AdminLog
import com.ssamce.toyproject.domain.user.entity.User
import com.ssamce.toyproject.domain.user.entity.UserLog
import com.ssamce.toyproject.repository.user.AdminLogRepository
import com.ssamce.toyproject.repository.user.UserLogRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class LogService(
    private val userLogRepository: UserLogRepository,
    private val adminLogRepository: AdminLogRepository,
    private val entityManager: EntityManager
) {
    fun write(
        userId: Long,
        action: ActionType,
        api: String,
        duration: Long,
        requestParams: String?,
        responseParams: String?,
        userAgent: String?,
        remoteIp: String?,
        role: Role
    ) {
        ObjectMapper()

        val userRef = entityManager.getReference(User::class.java, userId)

        when (role) {
            Role.ADMIN -> adminLogRepository.save(
                AdminLog(
                    user = userRef,
                    action = action,
                    api = api,
                    requestParams = requestParams,
                    responseParams = responseParams,
                    userAgent = userAgent,
                    remoteIp = remoteIp,
                    duration = duration
                )
            )

            Role.USER -> userLogRepository.save(
                UserLog(
                    user = userRef,
                    action = action,
                    api = api,
                    requestParams = requestParams,
                    responseParams = responseParams,
                    userAgent = userAgent,
                    remoteIp = remoteIp,
                    duration = duration
                )
            )
        }
    }
}