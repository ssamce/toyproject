package com.ssamce.toyproject.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.ssamce.toyproject.common.enums.ActionType
import com.ssamce.toyproject.security.JwtTokenProvider
import com.ssamce.toyproject.service.user.LogService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val SENSITIVE_KEYS = setOf("password", "pw", "passwd", "accessToken", "refreshToken")

fun removeSensitive(node: JsonNode): JsonNode {
    if (node.isObject) {
        val obj = node.deepCopy<ObjectNode>()
        SENSITIVE_KEYS.forEach { obj.remove(it) }
        obj.fieldNames().forEachRemaining { key ->
            obj.replace(key, removeSensitive(obj.get(key)))
        }
        return obj
    }
    if (node.isArray) {
        val arr = node.deepCopy<ArrayNode>()
        for (i in 0 until arr.size()) {
            arr.set(i, removeSensitive(arr.get(i)))
        }
        return arr
    }
    return node
}


class ApiLogFilter(
    private val jwt: JwtTokenProvider,
    private val logService: LogService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val start = System.currentTimeMillis()

        val wrappedReq = ContentCachingRequestWrapper(req)
        val wrappedRes = ContentCachingResponseWrapper(res)

        chain.doFilter(wrappedReq, wrappedRes)

        val duration = System.currentTimeMillis() - start
        val method = req.method
        val uri = req.requestURI
        val remoteIp = req.remoteAddr

        val reqBodyRaw = wrappedReq.contentAsByteArray
        val reqBodyJson = reqBodyRaw
            .takeIf { it.isNotEmpty() && req.contentType?.contains("application/json") == true }
            ?.let { bytes ->
                val node = objectMapper.readTree(bytes)
                removeSensitive(node).toString()
            }

        wrappedRes.contentAsByteArray
        val resBodyNode = wrappedRes.contentAsByteArray
            .takeIf { it.isNotEmpty() && res.contentType?.contains("application/json") == true }
            ?.let { objectMapper.readTree(it) }

        val resBodyJson = resBodyNode?.let { removeSensitive(it).toString() }


        val tokenFromHeader = jwt.resolveToken(req)
        val tokenFromLogin =
            if (tokenFromHeader == null
                && req.requestURI == "/api/users/login"
                && res.status == HttpServletResponse.SC_OK
            ) {
                resBodyNode?.path("accessToken")?.asText(null)
            } else null

        val token = tokenFromHeader ?: tokenFromLogin
        if (token != null && jwt.validateToken(token)) {
            val userId = jwt.getUserId(token)
            val role = jwt.getUserRole(token)

            logService.write(
                userId = userId,
                action = ActionType.valueOf(method),
                api = uri,
                duration = duration,
                requestParams = reqBodyJson ?: "",
                responseParams = resBodyJson ?: "",
                userAgent = req.getHeader("User-Agent"),
                remoteIp = remoteIp,
                role = role
            )
        }

        wrappedRes.copyBodyToResponse()
    }
}