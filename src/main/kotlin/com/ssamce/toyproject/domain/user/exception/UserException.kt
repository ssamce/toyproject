package com.ssamce.toyproject.domain.user.exception

class UserAlreadyExistsException(email: String) :
    RuntimeException("이미 등록된 이메일입니다: $email")

class UserNotFoundException :
    RuntimeException("이메일 혹은 비밀번호를 확인해주세요")

class InvalidPasswordException :
    RuntimeException("이메일 혹은 비밀번호를 확인해주세요")

class UnknownRequestException :
    RuntimeException("잘못된 요청입니다.")