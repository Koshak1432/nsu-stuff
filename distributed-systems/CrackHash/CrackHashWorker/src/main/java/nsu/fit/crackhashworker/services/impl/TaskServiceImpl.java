package nsu.fit.crackhashworker.services.impl;

import nsu.fit.crackhashworker.config.Constants;
import nsu.fit.crackhashworker.model.dto.*;
import nsu.fit.crackhashworker.services.TaskService;
import org.paukov.combinatorics3.Generator;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class TaskServiceImpl implements TaskService {
    private final MessageDigest md;
    private final RestTemplate restTemplate;

    public TaskServiceImpl(RestTemplate restTemplate) {
        try {
            this.md = MessageDigest.getInstance(Constants.HASH_NAME);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't get message digest");
        }
        this.restTemplate = restTemplate;
    }

    @Override
    @Async
    public void crackHash(CrackHashManagerRequest request) {
        CrackHashManagerRequest__1 packet = request.getCrackHashManagerRequest();
        List<String> result = processTask(packet);
        CrackHashWorkerResponse response = formResponse(result, packet.getPartNumber(), packet.getRequestId());
        System.out.println("SENDING WORDS: " + result);
        sendResult(response);
    }

    private List<String> processTask(CrackHashManagerRequest__1 packet) {
        List<String> alphabet = packet.getAlphabet().getSymbols();
        long totalWords = countAllCombinations(alphabet.size(), packet.getMaxLength());
        long startIdx = calculateStartWordIdx(totalWords, packet.getPartNumber(), packet.getPartCount());
        long endIdx = calculateEndWordIdx(totalWords, packet.getPartNumber(), packet.getPartCount());

        Stream<List<String>> allPermutations = generatePermutations(packet.getMaxLength(), alphabet);
        return allPermutations
                .skip(startIdx)
                .limit(endIdx - startIdx)
                .map(permutation -> String.join("", permutation))
                .filter(word -> calculateHash(word).equals(packet.getHash()))
                .toList();
    }

    private void sendResult(CrackHashWorkerResponse response) {
        RequestEntity<CrackHashWorkerResponse> request = RequestEntity.patch(Constants.MANAGER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(response);
        restTemplate.exchange(request, Void.class);
    }

    private static CrackHashWorkerResponse formResponse(List<String> words, int partNumber, String requestId) {
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        CrackHashWorkerResponse__1 response__1 = new CrackHashWorkerResponse__1();
        Answers answers = new Answers();
        answers.setWords(words);
        response__1.setAnswers(answers);
        response__1.setPartNumber(partNumber);
        response__1.setRequestId(requestId);
        response.setCrackHashWorkerResponse(response__1);
        return response;
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

    private long calculateStartWordIdx(long totalWords, int partNumber, int partCount) {
        long wordsPerWorker = totalWords / partCount;
        return partNumber * wordsPerWorker;
    }

    private long calculateEndWordIdx(long totalWords, int partNumber, int partCount) {
        long wordsPerWorker = totalWords / partCount;
        long endWordIdx = (partNumber + 1) * wordsPerWorker;
        return Math.min(endWordIdx, totalWords);
    }

    private static long countAllCombinations(int alphabetLen, int maxLen) {
        long res = 0;
        for (int i = 1; i <= maxLen; ++i) {
            res += (long) Math.pow(alphabetLen, i);
        }
        return res;
    }
}
