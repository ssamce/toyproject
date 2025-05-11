package com.ssamce.toyproject.repository.user

import com.ssamce.toyproject.domain.user.entity.UserLog
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository : JpaRepository<UserLog, Long>