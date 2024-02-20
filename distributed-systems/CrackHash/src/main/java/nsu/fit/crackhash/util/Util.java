package nsu.fit.crackhash.util;

import nsu.fit.crackhash.config.Constants;
import nsu.fit.crackhash.model.dto.Alphabet;

import java.util.Arrays;

public class Util {
    public static Alphabet getAlphabetFromString(String alfabetString) {
        Alphabet alphabet = new Alphabet();
        alphabet.setSymbols(Arrays.asList(alfabetString.split("")));
        return alphabet;
    }

    public static int countAllCombinations(String alphabet, int maxLen) {
        int res = 0;
        int alphabetSymbols = alphabet.length();
        for (int i = 1; i <= maxLen; ++i) {
            res += (int) Math.pow(alphabetSymbols, i);
        }
        return res;
    }
}
