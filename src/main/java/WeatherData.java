import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model class for weather data
 */
public class WeatherData {
    private String location;
    private String country;
    private double temperature;
    private double minTemp;
    private double maxTemp;
    private String condition;
    private int humidity;
    private double windSpeed;
    private int pressure;
    private int visibility;
    private String lastUpdated;
    
    public static WeatherData fromJson(String jsonData) {
        WeatherData data = new WeatherData();
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);
            
            // Extract basic information
            data.location = (String) json.get("name");
            
            // Extract country information
            JSONObject sys = (JSONObject) json.get("sys");
            if (sys != null) {
                data.country = (String) sys.get("country");
            } else {
                data.country = "";
            }
            
            // Extract main weather information
            JSONObject main = (JSONObject) json.get("main");
            data.temperature = ((Number) main.get("temp")).doubleValue();
            data.humidity = ((Number) main.get("humidity")).intValue();
            data.pressure = ((Number) main.get("pressure")).intValue();
            
            // Extract min/max temperature
            data.minTemp = ((Number) main.get("temp_min")).doubleValue();
            data.maxTemp = ((Number) main.get("temp_max")).doubleValue();
            
            // Extract wind information
            JSONObject wind = (JSONObject) json.get("wind");
            data.windSpeed = ((Number) wind.get("speed")).doubleValue();
            
            // Extract visibility
            if (json.containsKey("visibility")) {
                data.visibility = ((Number) json.get("visibility")).intValue();
            } else {
                data.visibility = 0;
            }
            
            // Extract weather condition
            JSONArray weatherArray = (JSONArray) json.get("weather");
            if (weatherArray != null && !weatherArray.isEmpty()) {
                JSONObject weather = (JSONObject) weatherArray.get(0);
                data.condition = (String) weather.get("main");
            } else {
                data.condition = "Unknown";
            }
            
            // Format the last updated time
            long timestamp = ((Number) json.get("dt")).longValue() * 1000;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            data.lastUpdated = sdf.format(new Date(timestamp));
            
        } catch (ParseException e) {
            e.printStackTrace();
            // Set default values if parsing fails
            data.location = "Unknown";
            data.country = "";
            data.temperature = 0;
            data.minTemp = 0;
            data.maxTemp = 0;
            data.condition = "Unknown";
            data.humidity = 0;
            data.windSpeed = 0;
            data.pressure = 0;
            data.visibility = 0;
            data.lastUpdated = "N/A";
        }
        
        return data;
    }
    
    // Getters
    public String getLocation() { return location; }
    public String getCountry() { return country; }
    public double getTemperature() { return temperature; }
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public String getCondition() { return condition; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public int getPressure() { return pressure; }
    public int getVisibility() { return visibility; }
    public String getLastUpdated() { return lastUpdated; }
}
