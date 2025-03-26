import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages weather icons for the application
 */
public class WeatherIconManager {
    private Map<String, ImageIcon> iconCache;
    private static final String ICON_PATH = "/icons/";
    
    public WeatherIconManager() {
        iconCache = new HashMap<>();
        preloadCommonIcons();
    }
    
    private void preloadCommonIcons() {
        // Preload commonly used icons
        loadIcon("sunny");
        loadIcon("clear");
        loadIcon("cloudy");
        loadIcon("rainy");
        loadIcon("snow");
        loadIcon("thunderstorm");
        loadIcon("foggy");
        loadIcon("unknown");
    }
    
    public ImageIcon getIcon(String weatherCondition) {
        if (weatherCondition == null || weatherCondition.isEmpty()) {
            return getDefaultIcon();
        }
        
        weatherCondition = weatherCondition.toLowerCase();
        
        // Map OpenWeatherMap condition names to our icon names
        String iconName = mapConditionToIcon(weatherCondition);
        
        // Check if the icon is already loaded
        if (iconCache.containsKey(iconName)) {
            return iconCache.get(iconName);
        }
        
        // Try to load the icon
        ImageIcon icon = loadIcon(iconName);
        if (icon != null) {
            return icon;
        }
        
        // If we couldn't load the icon, return the default
        return getDefaultIcon();
    }
    
    private String mapConditionToIcon(String condition) {
        switch (condition.toLowerCase()) {
            case "clear":
            case "sunny":
                return "sunny";
                
            case "clouds":
            case "cloudy":
            case "partly cloudy":
            case "overcast":
                return "cloudy";
                
            case "rain":
            case "rainy":
            case "drizzle":
            case "shower rain":
                return "rainy";
                
            case "thunderstorm":
            case "thunder":
                return "thunderstorm";
                
            case "snow":
            case "snowy":
            case "sleet":
                return "snow";
                
            case "mist":
            case "fog":
            case "haze":
                return "foggy";
                
            default:
                return "unknown";
        }
    }
    
    private ImageIcon loadIcon(String iconName) {
        try {
            String resourcePath = ICON_PATH + iconName + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
            
            // Ensure the icon was loaded properly
            if (icon.getIconWidth() <= 0) {
                System.err.println("Failed to load icon for: " + iconName);
                return null;
            }
            
            iconCache.put(iconName, icon);
            return icon;
        } catch (Exception e) {
            System.err.println("Failed to load icon for: " + iconName + " - " + e.getMessage());
            return null;
        }
    }
    
    private ImageIcon getDefaultIcon() {
        // Try to get the unknown icon first
        if (iconCache.containsKey("unknown")) {
            return iconCache.get("unknown");
        }
        
        // If that fails, create a simple default icon
        return new ImageIcon(new byte[0]);
    }
}
