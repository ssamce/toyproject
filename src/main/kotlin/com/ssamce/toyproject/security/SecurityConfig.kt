package com.ssamce.toyproject.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssamce.toyproject.config.ApiLogFilter
import com.ssamce.toyproject.service.user.LogService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.SecurityContextHolderFilter


@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val logService: LogService,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            authorizeHttpRequests {
                authorize("/api/users/signup", permitAll)
                authorize("/api/users/login", permitAll)
                authorize(anyRequest, authenticated)
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(
                JwtAuthenticationFilter(jwtTokenProvider)
            )

            addFilterAfter<SecurityContextHolderFilter>(
                ApiLogFilter(jwtTokenProvider, logService, objectMapper)
            )

            exceptionHandling {
                authenticationEntryPoint = customAuthenticationEntryPoint
            }
        }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}