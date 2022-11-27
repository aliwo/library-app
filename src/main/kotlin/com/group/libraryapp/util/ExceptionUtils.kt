package com.group.libraryapp.util

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import java.lang.IllegalArgumentException

fun <T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T {
    return this.findByIdOrNull(id) ?: throw IllegalArgumentException()
}
