package com.example.users.model.domain.utils

import javax.inject.Inject

interface Mapper<I, O> {
    fun map(input: I): O
    fun unmap(input: O): I
}

interface DBMapper<DTO, DOMAIN> {
    fun map(input: DTO): DOMAIN
    fun unmap(input: DOMAIN): DTO
}

class ListMapper<I, O> @Inject constructor(private val mapper: Mapper<I, O>) {
    fun map(input: List<I>): List<O> {
        return input.map { mapper.map(it) }
    }
    fun unmap(input: List<O>):List<I> {
        return input.map { mapper.unmap(it) }
    }
}

//todo определить нужно ли мне это вовсе
//class NullableInputListMapper<I, O>(private val mapper: Mapper<I, O>) {
//    fun map(input: List<I>?): List<O> {
//        return input?.map { mapper.map(it) }.orEmpty()
//    }
//}