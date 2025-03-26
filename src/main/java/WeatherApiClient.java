import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Client for fetching weather data from an API
 */
public class WeatherApiClient {
    // Replace with your actual API key if using OpenWeatherMap or similar service
    private static final String API_KEY = "bd5e378503939ddaee76f12ad7a97608";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    
    /**
     * Fetches current weather data for a location
     */
    public String fetchWeatherData(String location) throws Exception {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
        String urlString = WEATHER_API_URL + "?q=" + encodedLocation + "&units=metric&appid=" + API_KEY;
        
        return fetchData(urlString);
    }
    
    /**
     * Fetches forecast data for a location
     */
    public String fetchForecastData(String location) throws Exception {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
        String urlString = FORECAST_API_URL + "?q=" + encodedLocation + "&units=metric&appid=" + API_KEY;
        
        return fetchData(urlString);
    }
    
    /**
     * Generic method to fetch data from an API
     */
    private String fetchData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Failed to fetch data. Response code: " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * For testing without an API key - current weather
     */
    public String fetchMockWeatherData(String location) {
        // Create a mock response
        JSONObject mockData = new JSONObject();
        
        JSONObject main = new JSONObject();
        main.put("temp", 22.5);
        main.put("humidity", 65);
        main.put("pressure", 1012);
        main.put("temp_min", 20.1);
        main.put("temp_max", 24.3);
        
        JSONObject wind = new JSONObject();
        wind.put("speed", 5.2);
        
        JSONObject weather = new JSONObject();
        weather.put("main", "Clouds");
        weather.put("description", "scattered clouds");
        
        JSONObject sys = new JSONObject();
        sys.put("country", "US");
        
        JSONArray weatherArray = new JSONArray();
        weatherArray.add(weather);
        
        mockData.put("name", location);
        mockData.put("main", main);
        mockData.put("wind", wind);
        mockData.put("weather", weatherArray);
        mockData.put("sys", sys);
        mockData.put("dt", System.currentTimeMillis() / 1000);
        
        return mockData.toJSONString();
    }
    
    /**
     * For testing without an API key - forecast
     */
    public String fetchMockForecastData(String location) {
        // Create a mock response
        JSONObject mockData = new JSONObject();
        
        JSONArray list = new JSONArray();
        long currentTime = System.currentTimeMillis() / 1000;
        
        // Create forecast entries for the next 5 days
        for (int day = 0; day < 5; day++) {
            for (int hour = 0; hour < 24; hour += 6) {
                JSONObject entry = new JSONObject();
                entry.put("dt", currentTime + (day * 86400) + (hour * 3600));
                
                JSONObject main = new JSONObject();
                main.put("temp", 20 + (Math.random() * 10));
                main.put("temp_min", 18 + (Math.random() * 5));
                main.put("temp_max", 23 + (Math.random() * 8));
                main.put("pressure", 1010 + (Math.random() * 10));
                main.put("humidity", 60 + (Math.random() * 30));
                
                JSONObject weather = new JSONObject();
                String[] conditions = {"Clear", "Clouds", "Rain", "Snow"};
                weather.put("main", conditions[(int)(Math.random() * conditions.length)]);
                
                JSONArray weatherArray = new JSONArray();
                weatherArray.add(weather);
                
                entry.put("main", main);
                entry.put("weather", weatherArray);
                
                list.add(entry);
            }
        }
        
        mockData.put("list", list);
        mockData.put("city", createMockCity(location));
        
        return mockData.toJSONString();
    }
    
    private JSONObject createMockCity(String location) {
        JSONObject city = new JSONObject();
        city.put("name", location);
        city.put("country", "US");
        
        JSONObject coord = new JSONObject();
        coord.put("lat", 40.7);
        coord.put("lon", -74.0);
        city.put("coord", coord);
        
        return city;
    }
} 