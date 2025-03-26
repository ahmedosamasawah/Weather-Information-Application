import java.io.*;
import java.util.Properties;

/**
 * Application settings manager
 */
public class AppSettings {
    private static final String SETTINGS_FILE = "weather_app_settings.properties";
    private Properties properties;
    
    // Default settings
    private static final String DEFAULT_TEMPERATURE_UNIT = "Celsius";
    private static final String DEFAULT_THEME = "Light";
    private static final boolean DEFAULT_AUTO_REFRESH = false;
    
    public AppSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    private void loadSettings() {
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            properties.load(in);
        } catch (IOException e) {
            // If file doesn't exist, use defaults
            setDefaults();
        }
    }
    
    private void setDefaults() {
        properties.setProperty("temperatureUnit", DEFAULT_TEMPERATURE_UNIT);
        properties.setProperty("theme", DEFAULT_THEME);
        properties.setProperty("autoRefresh", String.valueOf(DEFAULT_AUTO_REFRESH));
    }
    
    public void saveSettings() {
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(out, "Weather App Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters and setters
    public String getTemperatureUnit() {
        return properties.getProperty("temperatureUnit", DEFAULT_TEMPERATURE_UNIT);
    }
    
    public void setTemperatureUnit(String unit) {
        properties.setProperty("temperatureUnit", unit);
    }
    
    public String getTheme() {
        return properties.getProperty("theme", DEFAULT_THEME);
    }
    
    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
    }
    
    public boolean isAutoRefresh() {
        return Boolean.parseBoolean(properties.getProperty("autoRefresh", 
                                                         String.valueOf(DEFAULT_AUTO_REFRESH)));
    }
    
    public void setAutoRefresh(boolean autoRefresh) {
        properties.setProperty("autoRefresh", String.valueOf(autoRefresh));
    }
    
    // Temperature conversion utilities
    public double convertTemperature(double celsius) {
        if ("Fahrenheit".equals(getTemperatureUnit())) {
            return (celsius * 9/5) + 32;
        }
        return celsius;
    }
    
    public String getTemperatureSymbol() {
        return "Fahrenheit".equals(getTemperatureUnit()) ? "°F" : "°C";
    }
}
