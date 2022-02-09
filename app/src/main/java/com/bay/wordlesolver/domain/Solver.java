package com.bay.wordlesolver.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private static final ArrayList<Letter> correctPos = new ArrayList<>();
    private static final ArrayList<Letter> incorrectPos = new ArrayList<>();
    private static final ArrayList<Character> unused = new ArrayList<>();
    private static final ArrayList<Word> usedWords = new ArrayList<>();
    private static ArrayList<String> dict;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> generateGuesses(Word latestGuess) {
        updateLetterLists(latestGuess);
        usedWords.add(latestGuess);
        return dict.stream()
                .filter(Solver::filterIncorrectPos)
                .filter(Solver::filterCorrectPos)
                .filter(Solver::filterUnusedChar)
                .filter(x -> !usedWords.contains(x))
                .collect(Collectors.toList());
    }

    public static void setDict(ArrayList<String> dict) {
        Solver.dict = dict;
    }

    private static void updateLetterLists(Word latestGuess) {
        for (Letter l : latestGuess.getLetters()) {
            if (l.isCorrectPos() && !correctPos.contains(l)) {
                correctPos.add(l);
                incorrectPos.remove(l);
            }
            else if (l.isIncorrectPos() && !incorrectPos.contains(l)) {
                incorrectPos.add(l);
            } else {
                unused.add(l.getLetter());
            }
        }
    }

    private static boolean filterIncorrectPos(String str) {
        for (Letter l : incorrectPos) {
            if (str.indexOf(l.getLetter()) == -1 )
                return false;
            else if (str.indexOf(l.getLetter()) == l.getPos())
                return false;

        }
        return true;
    }

    private static boolean filterCorrectPos(String str) {
        for (Letter l : correctPos) {
            if (str.indexOf(l.getLetter(), l.getPos()) != l.getPos() )
                return false;
        }
        return true;
    }

    private static boolean filterUnusedChar(String str) {
        for (char c : unused) {
            if (str.indexOf(c) != -1)
                return false;
        }
        return true;
    }

    public static void clearLists() {
        correctPos.clear();
        incorrectPos.clear();
        unused.clear();
    }
}