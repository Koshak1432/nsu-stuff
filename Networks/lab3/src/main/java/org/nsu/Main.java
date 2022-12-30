package org.nsu;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    private static List<Location> handleException(Throwable t) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Enter place");
        String input = null;
        Scanner scanner = new Scanner(System.in);
        while (input == null) {
            if ((input = IO.parseLine(scanner)) == null) {
                System.out.println("Couldn't parse input, try again");
            }
        }
        IO io = new IO();
        Locations locations = new Locations();
        String finalInput = input;
        CompletableFuture<Void> mainFuture = CompletableFuture.supplyAsync(
                () -> locations.findLocations(finalInput))
                .exceptionally((t) -> {
                    t.printStackTrace();
                    return null;
                    })
                .thenAccept(io::printLocations).thenRun(() -> {
                    if (locations.getLocations() == null) {
                        return;
                    }
                    int idx = io.askLocationChoice(scanner, locations.getLocations().size());
                    LocationCoordinates coordinates = locations.getLocations().get(idx - 1).coordinates();
                    CompletableFuture<Weather> weather = CompletableFuture.supplyAsync(() ->
                                Weather.findWeather(coordinates));

                    InterestingPlacesWithDescription places = new InterestingPlacesWithDescription();
                    CompletableFuture<List<InterestingPlace>> placesFuture = CompletableFuture.supplyAsync(() ->
                            InterestingPlacesWithDescription.findInterestingPlacesXIDs(coordinates))
                            .thenApply(places::findPlacesDescriptions);

                    try {
                        io.printWeather(weather.get());
                        io.printInterestingPlaces(placesFuture.get());
                        scanner.close();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
        mainFuture.join();
    }
}