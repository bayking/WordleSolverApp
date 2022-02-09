package com.bay.wordlesolver.domain

class Letter(letter: Char, pos: Int, isCorrectPos: Boolean, isIncorrectPos: Boolean) {
    var letter = letter
    var pos = pos
    var isCorrectPos = isCorrectPos
    var isIncorrectPos = isIncorrectPos

    override fun toString(): String {
        return "Letter{" +
                "letter=" + letter +
                ", pos=" + pos +
                ", correctPos=" + isCorrectPos +
                ", incorrectPos=" + isIncorrectPos +
                '}'
    }
}