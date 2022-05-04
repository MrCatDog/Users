package com.example.users.mainfragment.model.mappers

import javax.inject.Inject

interface Mapper<I, O> {
    fun map(input: I): O
}

class ListMapper<I, O> @Inject constructor(private val mapper: Mapper<I, O>) {
    fun map(input: List<I>): List<O> {
        return input.map { mapper.map(it) }
    }
}

//todo определить нужно ли мне это вовсе
class NullableInputListMapper<I, O>(private val mapper: Mapper<I, O>) {
    fun map(input: List<I>?): List<O> {
        return input?.map { mapper.map(it) }.orEmpty()
    }
}