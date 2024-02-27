package com.group.libraryapp.repository

import com.group.libraryapp.domain.book.QBook
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class BookQuerydslRepository(
    private val querydslFactory: JPAQueryFactory,
) {

    fun getStatus(): List<BookStatResponse> {
        return querydslFactory.select(
            Projections.constructor(
                BookStatResponse::class.java,
                QBook.book.type,
                QBook.book.count()
            )
        )
            .from(QBook.book)
            .groupBy(QBook.book.type)
            .fetch()
    }

}