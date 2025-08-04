import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherByCity {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter City Name (e.g., Chennai, Mumbai): ");
        String city = scanner.nextLine().trim();
        scanner.close();

        try {
            // Step 1: Get latitude and longitude from city name
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest geoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(geoUrl))
                    .build();

            HttpResponse<String> geoResponse = client.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            String geoJson = geoResponse.body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode geoRoot = mapper.readTree(geoJson);
            JsonNode location = geoRoot.path("results").get(0);

            if (location == null) {
                System.out.println(" City not found. Please try again with a valid name.");
                return;
            }

            double latitude = location.path("latitude").asDouble();
            double longitude = location.path("longitude").asDouble();
            String resolvedCity = location.path("name").asText();

            // Step 2: Get weather data using the coordinates
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude
                    + "&longitude=" + longitude + "&current_weather=true";

            HttpRequest weatherRequest = HttpRequest.newBuilder()
                    .uri(URI.create(weatherUrl))
                    .build();

            HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode weatherRoot = mapper.readTree(weatherResponse.body());
            JsonNode weather = weatherRoot.path("current_weather");

            // Step 3: Display structured data
            System.out.println("\nWeather Report for " + resolvedCity + ":");
            System.out.println("Temperature : " + weather.path("temperature").asText() + " °C");
            System.out.println("Wind Speed  : " + weather.path("windspeed").asText() + " km/h");
            System.out.println("Time        : " + weather.path("time").asText());

        } catch (IOException | InterruptedException e) {
            System.out.println(" Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong. Please check your city name and try again.");
        }
    }
}
