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
