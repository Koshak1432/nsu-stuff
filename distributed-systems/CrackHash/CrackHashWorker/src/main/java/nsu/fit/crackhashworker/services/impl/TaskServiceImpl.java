package nsu.fit.crackhashworker.services.impl;

import nsu.fit.crackhashworker.config.Constants;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest__1;
import nsu.fit.crackhashworker.services.TaskService;
import org.paukov.combinatorics3.Generator;
import org.paukov.combinatorics3.IGenerator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class TaskServiceImpl implements TaskService {
    private final MessageDigest md;

    public TaskServiceImpl() {
        try {
            this.md = MessageDigest.getInstance(Constants.HASH_NAME);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't get message digest");
        }
    }

    @Override
    public Void crackHash(CrackHashManagerRequest request) {
        CrackHashManagerRequest__1 packet = request.getCrackHashManagerRequest();
        long startIdx = calculateStartWordIdx(request.getCrackHashManagerRequest());
        var wordsStream = Generator.permutation(String.join("", packet.getAlphabet().getSymbols()).toCharArray())
                .withRepetitions(packet.getMaxLength())
                .stream()
                .skip(startIdx)
                .limit(calculateEndWordIdx(request.getCrackHashManagerRequest()) - startIdx)

    }

    private String calculateHash(String word) {
        md.update(word.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        md.reset();
        return hexString.toString();
    }

    private long calculateStartWordIdx(CrackHashManagerRequest__1 request) {
        int totalWords = countAllCombinations(String.join("", request.getAlphabet().getSymbols()),
                                              request.getMaxLength());
        int wordsPerWorker = totalWords / request.getPartCount();
        return (long) request.getPartNumber() * wordsPerWorker; // or (partNumber - 1)
    }

    private long calculateEndWordIdx(CrackHashManagerRequest__1 request) {
        int totalWords = countAllCombinations(String.join("", request.getAlphabet().getSymbols()),
                                              request.getMaxLength());
        int wordsPerWorker = totalWords / request.getPartCount();
        long endWordIdx = (long) (request.getPartNumber() + 1) * wordsPerWorker;
        if (endWordIdx > totalWords) {
            endWordIdx = totalWords;
        }
        return endWordIdx; // or just partNumber + 0
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
