package org.nsu;

public record Location(LocationCoordinates coordinates, String country, String city, String name, String osmValue) {}
