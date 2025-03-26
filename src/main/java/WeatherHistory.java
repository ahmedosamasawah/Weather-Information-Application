import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Manages the weather search history
 */
public class WeatherHistory {
    private List<HistoryEntry> entries;
    
    public WeatherHistory() {
        entries = new ArrayList<>();
    }
    
    public void addEntry(String location, String weatherData) {
        entries.add(new HistoryEntry(location, weatherData, new Date()));
    }
    
    public List<HistoryEntry> getHistory() {
        return new ArrayList<>(entries);
    }
    
    public void clearHistory() {
        entries.clear();
    }
    
    static class HistoryEntry {
        private String location;
        private String weatherData;
        private Date timestamp;
        
        public HistoryEntry(String location, String weatherData, Date timestamp) {
            this.location = location;
            this.weatherData = weatherData;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getLocation() { return location; }
        public String getWeatherData() { return weatherData; }
        public Date getTimestamp() { return timestamp; }
    }
}
