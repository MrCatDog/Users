package com.example.users.mainfragment.model.mappers

interface Mapper<I, O> {
    fun map(input: I): O
}

class ListMapper<I, O>(private val mapper: Mapper<I, O>) {
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