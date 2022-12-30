package org.nsu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InterestingPlacesWithDescription {
    private static List<String> getXIDsFrom(String response) {
        List<String> ids = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray features = jsonResponse.getJSONArray("features");
        for (int i = 0; i < features.length(); ++i) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");
            ids.add(properties.getString("xid"));
        }
        return ids;
    }

    public static List<String> findInterestingPlacesXIDs(LocationCoordinates coordinates) {
        String url = "https://api.opentripmap.com/0.1/ru/places/radius?" +
                "radius=10000&" +
                "lon=" + coordinates.longitude() +
                "&lat=" + coordinates.latitude() +
                "&rate=2" +
                "&limit=5" +
                "&apikey=" + apiKey;
        String jsonResponse = ApiCaller.getResponseAsJSON(url);
        return getXIDsFrom(jsonResponse);
    }

    private InterestingPlace addDescriptionFrom(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        String name = (jsonResponse.has("name")) ? jsonResponse.getString("name") : null;
        String country = null;
        String city = null;
        String road = null;
        String houseNumber = null;
        String cityDistrict = null;
        if (jsonResponse.has("address")) {
            JSONObject address = jsonResponse.getJSONObject("address");
            if (address.has("city")) {
                city = address.getString("city");
            }
            if (address.has("road")) {
                road = address.getString("road");
            }
            if (address.has("house_number")) {
                houseNumber = address.getString("house_number");
            }
            if (address.has("city_district")) {
                cityDistrict = address.getString("city_district");
            }
            if (address.has("country")) {
                country = address.getString("country");
            }
        }
        InterestingPlace place = new InterestingPlace(name, country, city, road, houseNumber, cityDistrict);
        interestingPlaces.add(place);
        return place;
    }


    public List<InterestingPlace> findPlacesDescriptions(List<String> xids) {
        List<CompletableFuture<InterestingPlace>> requests = Collections.synchronizedList(new ArrayList<>());
        for (String xid : xids) {
            String url = "https://api.opentripmap.com/0.1/ru/places/xid/" + xid + "?apikey=" + apiKey;
            String response = ApiCaller.getResponseAsJSON(url);
            CompletableFuture<InterestingPlace> request = CompletableFuture.supplyAsync(() -> addDescriptionFrom(response));
            requests.add(request);
        }
        CompletableFuture.allOf(requests.toArray(new CompletableFuture[0]));
        return interestingPlaces;
    }






    private final List<InterestingPlace> interestingPlaces = new ArrayList<>();
    private static final String apiKey = "5ae2e3f221c38a28845f05b672dd765fbc7217af2834d2149f4e28cb";
}
