package org.nsu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Locations {
    private Location getLocationFromHit(JSONObject hit) {
        JSONObject point = hit.getJSONObject("point");
        double lng = (point.has("lng")) ? point.getDouble("lng") : 0.0;
        double lat = (point.has("lat")) ? point.getDouble("lat") : 0.0;
        LocationCoordinates coordinates = new LocationCoordinates(lat, lng);

        String country = (hit.has("country")) ? hit.getString("country") : null;
        String city = (hit.has("city")) ? hit.getString("city") : null;
        String name = (hit.has("name")) ? hit.getString("name") : null;
        String osmValue = (hit.has("osm_value")) ? hit.getString("osm_value") : null;
        return new Location(coordinates, country, city, name, osmValue);
    }

    private List<Location> getLocationsFromJsonString(String stringResponse) {
        if (stringResponse == null) {
            return null;
        }
        List<Location> locations = new ArrayList<>();
        JSONObject response = new JSONObject(stringResponse);
        if (response.has("hits")) {
            JSONArray hits = new JSONArray(response.getJSONArray("hits"));
            for (int i = 0; i < hits.length(); ++i) {
                locations.add(getLocationFromHit(hits.getJSONObject(i)));
            }
        } else {
            System.err.println("Couldn't find hits");
        }
        return locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Location> findLocations(String locationName) {
        String url = "https://graphhopper.com/api/1/geocode?q=" + locationName +
                "&locale=en&limit=5" + "&key=" + apiKey;
        String response = ApiCaller.getResponseAsJSON(url);
        locations = getLocationsFromJsonString(response);
        return locations;
    }

    private final String apiKey = "45d0dff3-d737-4c5c-b67f-e0f403b4257a";
    private List<Location> locations;
}
