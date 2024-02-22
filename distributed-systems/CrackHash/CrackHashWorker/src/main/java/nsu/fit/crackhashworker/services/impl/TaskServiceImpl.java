package nsu.fit.crackhashworker.services.impl;

import nsu.fit.crackhashworker.config.Constants;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest;
import nsu.fit.crackhashworker.model.dto.CrackHashManagerRequest__1;
import nsu.fit.crackhashworker.services.TaskService;
import org.paukov.combinatorics3.Generator;
import org.paukov.combinatorics3.IGenerator;
import org.paukov.combinatorics3.PermutationGenerator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        // тут отправить ответ через другой поток, типа всё ок?
        long startIdx = calculateStartWordIdx(request.getCrackHashManagerRequest());
        Stream<List<String>> allPermutations = generatePermutations(packet.getMaxLength(), packet.getAlphabet().getSymbols());
        List<String> workerPermutations = allPermutations
                .skip(startIdx)
                .limit(calculateEndWordIdx(packet) - startIdx)
                .map(permutation -> String.join("", permutation))
                .filter(word -> calculateHash(word).equals(packet.getHash()))
                .toList();
        // щас в листе ответы, надо patch сделать менеджеру, кинуть их
    }

    private Stream<List<String>> generatePermutations(int maxLen, List<String> alphabet) {
        List<Stream<List<String>>> streamsList = IntStream
                .rangeClosed(1, maxLen)
                .mapToObj(len -> generateStreamForLen(len, alphabet))
                .toList();
        return streamsList.stream().flatMap(s -> s);
    }

    private Stream<List<String>> generateStreamForLen(int len, List<String> alphabet) {
        return Generator
                .permutation(alphabet)
                .withRepetitions(len)
                .stream();
    }

    private String calculateHash(String word) {
        md.update(word.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : md.digest()) {
            hexString.append(String.format("%02x", b));
        }
        md.reset();
        return hexString.toString();
    }

    private long calculateStartWordIdx(CrackHashManagerRequest__1 request) {
        long totalWords = countAllCombinations(String.join("", request.getAlphabet().getSymbols()),
                                              request.getMaxLength());
        long wordsPerWorker = totalWords / request.getPartCount();
        return request.getPartNumber() * wordsPerWorker; // or (partNumber - 1)
    }

    private long calculateEndWordIdx(CrackHashManagerRequest__1 request) {
        long totalWords = countAllCombinations(String.join("", request.getAlphabet().getSymbols()),
                                              request.getMaxLength());
        long wordsPerWorker = totalWords / request.getPartCount();
        long endWordIdx = (request.getPartNumber() + 1) * wordsPerWorker;
        return Math.min(endWordIdx, totalWords);
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
