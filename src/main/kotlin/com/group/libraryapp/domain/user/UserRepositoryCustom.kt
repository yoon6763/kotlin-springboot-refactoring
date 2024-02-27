package com.group.libraryapp.domain.user

interface UserRepositoryCustom {
    fun findAllWithLoanHistories(): List<User>
}