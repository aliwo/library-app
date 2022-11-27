package com.group.libraryapp.domain.book

import javax.persistence.*

@Entity
class Book constructor(
    val name: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null,
) {
    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
    }
}
