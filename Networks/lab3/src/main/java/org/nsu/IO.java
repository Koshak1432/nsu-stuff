package org.nsu;

import java.util.List;
import java.util.Scanner;

public class IO {
    public IO() {

    }

    public void printLocations(List<Location> locations) {
        if (locations == null) {
            return;
        }
        for (int i = 0; i < locations.size(); ++i) {
            Location location = locations.get(i);
            System.out.println(i + 1 + ": longitude " + location.coordinates().longitude() + ", latitude " + location.coordinates().latitude());
            if (location.country() != null) {
                System.out.println("Country: " + location.country());
            }
            if (location.city() != null) {
                System.out.println("City: " + location.city());
            }
            if (location.name() != null) {
                System.out.println("Name: " + location.name());
            }
            if (location.osmValue() != null) {
                System.out.println("Osm value: " + location.osmValue());
            }
            System.out.println("------------------------------");
        }
    }

    public void printWeather(Weather weather) {
        if (weather == null) {
            return;
        }
        System.out.println("Weather: ");
        String description = weather.getDescription();
        if (description != null) {
            System.out.println("Status: " + description);
        }
        System.out.println("Temperature: " + weather.getTemp());
        System.out.println("Feels like: " + weather.getFeelsLike());
        System.out.println("Pressure: " + weather.getPressure());
        System.out.println("Humidity: " + weather.getHumidity());
        System.out.println("Visibility: " + weather.getVisibility());
        System.out.println("Wind speed: " + weather.getWindSpeed());
        System.out.println("------------------------------");
    }

    public void printInterestingPlaces(List<InterestingPlace> places) {
        System.out.println("Interesting places:\n");
        for (InterestingPlace place : places) {
            if (place.name() != null) {
                System.out.println("Name: " + place.name());
            }
            if (place.country() != null) {
                System.out.println("Country: " + place.country());
            }
            if (place.city() != null) {
                System.out.println("City: " + place.city());
            }
            if (place.road() != null && place.houseNumber() != null) {
                System.out.println("Street: " + place.road() + ", " + place.houseNumber());
            }
            if (place.cityDistrict() != null) {
                System.out.println("District: " + place.cityDistrict());
            }
            System.out.println("------------------------------");
        }
    }

    public int askLocationChoice(Scanner scanner, int locationsSize) {
        System.out.println("Enter number of location to observe");
        int idx = -1;
        String line;
        do {
            if ((line = parseLine(scanner)) != null) {
                idx = Integer.parseInt(line);
                if (idx < 1 || idx > locationsSize) {
                    System.out.println("Invalid idx, try again");
                    continue;
                }
                break;
            }
        } while (idx == -1);
        return idx;
    }

    public static String parseLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }
}
