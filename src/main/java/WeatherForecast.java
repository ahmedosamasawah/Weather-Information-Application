import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class for weather forecast data
 */
public class WeatherForecast {
    private List<DailyForecast> forecasts;
    
    public WeatherForecast() {
        forecasts = new ArrayList<>();
    }
    
    public static WeatherForecast fromJson(String jsonData) {
        WeatherForecast forecast = new WeatherForecast();
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);
            
            JSONArray list = (JSONArray) json.get("list");
            if (list == null) return forecast;
            
            // Group forecasts by day
            String currentDay = "";
            DailyForecast currentForecast = null;
            
            for (Object obj : list) {
                JSONObject item = (JSONObject) obj;
                
                // Get date
                long timestamp = ((Number) item.get("dt")).longValue() * 1000;
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
                String day = dayFormat.format(new Date(timestamp));
                
                // Start a new day if needed
                if (!day.equals(currentDay)) {
                    currentDay = day;
                    currentForecast = new DailyForecast();
                    currentForecast.setDate(day);
                    forecast.forecasts.add(currentForecast);
                    
                    // Set initial values for the day
                    JSONObject main = (JSONObject) item.get("main");
                    currentForecast.setMinTemp(((Number) main.get("temp_min")).doubleValue());
                    currentForecast.setMaxTemp(((Number) main.get("temp_max")).doubleValue());
                    
                    JSONArray weatherArray = (JSONArray) item.get("weather");
                    if (weatherArray != null && !weatherArray.isEmpty()) {
                        JSONObject weather = (JSONObject) weatherArray.get(0);
                        currentForecast.setCondition((String) weather.get("main"));
                    }
                }
                
                // Update min/max temp for the day
                if (currentForecast != null) {
                    JSONObject main = (JSONObject) item.get("main");
                    double tempMin = ((Number) main.get("temp_min")).doubleValue();
                    double tempMax = ((Number) main.get("temp_max")).doubleValue();
                    
                    if (tempMin < currentForecast.getMinTemp()) {
                        currentForecast.setMinTemp(tempMin);
                    }
                    
                    if (tempMax > currentForecast.getMaxTemp()) {
                        currentForecast.setMaxTemp(tempMax);
                    }
                }
            }
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return forecast;
    }
    
    public List<DailyForecast> getForecasts() {
        return forecasts;
    }
    
    /**
     * Inner class representing a daily forecast
     */
    public static class DailyForecast {
        private String date;
        private double minTemp;
        private double maxTemp;
        private String condition;
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public double getMinTemp() { return minTemp; }
        public void setMinTemp(double minTemp) { this.minTemp = minTemp; }
        
        public double getMaxTemp() { return maxTemp; }
        public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }
        
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
    }
}
