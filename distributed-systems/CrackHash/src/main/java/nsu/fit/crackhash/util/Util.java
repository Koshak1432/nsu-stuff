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
}
