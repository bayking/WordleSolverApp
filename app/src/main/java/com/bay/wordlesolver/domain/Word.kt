package com.bay.wordlesolver.domain

import java.util.*

class Word(word: String, letters: MutableList<Letter>) {
    var word = word
    var letters = letters

    operator fun contains(c: Char): Boolean {
        return letters.any{ x: Letter -> x.letter == c }

    }

    fun add(letter: Letter) {
        letters.add(letter);
    }

    override fun toString(): String {
        return "Word{" +
                "word='" + word + '\'' +
                ", letters=" + letters.toString() +
                '}'
    }
}