import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main application class for the Weather Information App
 */
public class WeatherInfoApp {
    private WeatherHistory history;
    private WeatherIconManager iconManager;
    private WeatherApiClient apiClient;
    private AppSettings settings;
    
    private JFrame mainFrame;
    private JTextField locationField;
    private JButton searchButton;
    private JPanel weatherPanel;
    private JPanel forecastPanel;
    private JTabbedPane tabbedPane;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    private Timer autoRefreshTimer;
    
    private String currentLocation;
    
    public WeatherInfoApp() {
        history = new WeatherHistory();
        iconManager = new WeatherIconManager();
        apiClient = new WeatherApiClient();
        settings = new AppSettings();
        
        initializeUI();
        setupAutoRefresh();
    }

    private void initializeUI() {
        // Set the look and feel based on settings
        updateLookAndFeel();
        
        // Main frame setup
        mainFrame = new JFrame("Weather Information App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 700);
        mainFrame.setLayout(new BorderLayout(10, 10));

        // Create menu bar
        createMenuBar();
        
        // Search panel (top)
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        locationField = new JTextField(20);
        locationField.addActionListener(e -> performSearch());
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(new JLabel("Location: "), BorderLayout.WEST);
        searchPanel.add(locationField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Weather display with tabs (center)
        tabbedPane = new JTabbedPane();
        
        // Current weather tab
        weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane weatherScrollPane = new JScrollPane(weatherPanel);
        tabbedPane.addTab("Current Weather", weatherScrollPane);
        
        // Forecast tab
        forecastPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        forecastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane forecastScrollPane = new JScrollPane(forecastPanel);
        tabbedPane.addTab("5-Day Forecast", forecastScrollPane);
        
        // History panel (right)
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = historyList.getSelectedIndex();
                    if (index >= 0) {
                        WeatherHistory.HistoryEntry entry = history.getHistory().get(index);
                        locationField.setText(entry.getLocation());
                        loadWeatherData(entry.getLocation(), false);
                    }
                }
            }
        });
        
        JScrollPane historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Search History"));
        historyScrollPane.setPreferredSize(new Dimension(200, 0));
        
        JButton clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.addActionListener(e -> clearHistory());
        
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        historyPanel.add(clearHistoryButton, BorderLayout.SOUTH);
        
        // Add all components to the main frame
        mainFrame.add(searchPanel, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(historyPanel, BorderLayout.EAST);
        
        // Display a welcome message
        showWelcomeMessage();
        
        mainFrame.setVisible(true);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettingsDialog());
        toolsMenu.add(settingsItem);
        
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> refreshCurrentLocation());
        toolsMenu.add(refreshItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        mainFrame.setJMenuBar(menuBar);
    }
    
    private void showSettingsDialog() {
        SettingsDialog dialog = new SettingsDialog(mainFrame, settings);
        dialog.setVisible(true);
        
        // Apply new settings
        updateLookAndFeel();
        setupAutoRefresh();
        
        // Refresh data if needed
        if (currentLocation != null && !currentLocation.isEmpty()) {
            refreshCurrentLocation();
        }
    }
    
    private void updateLookAndFeel() {
        try {
            if ("Dark".equals(settings.getTheme())) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                // Apply dark color scheme
                UIManager.put("Panel.background", new Color(50, 50, 50));
                UIManager.put("Label.foreground", Color.WHITE);
                // Add more dark theme components as needed
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            
            // Update UI if frame exists
            if (mainFrame != null) {
                SwingUtilities.updateComponentTreeUI(mainFrame);
                mainFrame.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
        }
        
        if (settings.isAutoRefresh()) {
            autoRefreshTimer = new Timer(true);
            autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (currentLocation != null && !currentLocation.isEmpty()) {
                        SwingUtilities.invokeLater(() -> refreshCurrentLocation());
                    }
                }
            }, 60000, 60000); // Refresh every minute
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(mainFrame,
            "Weather Information App\nVersion 1.0\n\nCreated by Your Name",
            "About Weather App",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showWelcomeMessage() {
        weatherPanel.removeAll();
        forecastPanel.removeAll();
        
        JLabel welcomeLabel = new JLabel("Enter a location to get weather information", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Example: 'London', 'New York', 'Tokyo'", JLabel.CENTER);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        weatherPanel.add(Box.createVerticalGlue());
        weatherPanel.add(welcomeLabel);
        weatherPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        weatherPanel.add(instructionLabel);
        weatherPanel.add(Box.createVerticalGlue());
        
        weatherPanel.revalidate();
        weatherPanel.repaint();
    }
    
    private void performSearch() {
        String location = locationField.getText().trim();
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Please enter a location", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        loadWeatherData(location, true);
    }
    
    private void refreshCurrentLocation() {
        if (currentLocation != null && !currentLocation.isEmpty()) {
            loadWeatherData(currentLocation, false);
        }
    }
    
    private void loadWeatherData(String location, boolean addToHistory) {
        try {
            setCursor(Cursor.WAIT_CURSOR);
            
            // Set as current location
            currentLocation = location;
            
            // Fetch current weather
            String weatherData = apiClient.fetchWeatherData(location);
            displayWeatherData(location, weatherData);
            
            // Fetch forecast
            String forecastData = apiClient.fetchForecastData(location);
            displayForecastData(forecastData);
            
            // Add to history if needed
            if (addToHistory) {
                history.addEntry(location, weatherData);
                updateHistoryList();
            }
            
            // Switch to the current weather tab
            tabbedPane.setSelectedIndex(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error fetching weather data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
    
    private void displayWeatherData(String location, String weatherData) {
        weatherPanel.removeAll();
        
        // Parse and display weather data
        WeatherData data = WeatherData.fromJson(weatherData);
        
        // Location and current conditions
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel locationLabel = new JLabel(location + ", " + data.getCountry());
        locationLabel.setFont(new Font("Arial", Font.BOLD, 24));
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel conditionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel iconLabel = new JLabel(iconManager.getIcon(data.getCondition()));
        
        // Use temperature with the user's preferred unit
        double displayTemp = settings.convertTemperature(data.getTemperature());
        JLabel tempLabel = new JLabel(String.format("%.1f%s", displayTemp, settings.getTemperatureSymbol()));
        tempLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        conditionsPanel.add(iconLabel);
        conditionsPanel.add(tempLabel);
        
        JLabel conditionLabel = new JLabel(data.getCondition());
        conditionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lastUpdatedLabel = new JLabel("Last updated: " + data.getLastUpdated());
        lastUpdatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lastUpdatedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        headerPanel.add(locationLabel);
        headerPanel.add(conditionsPanel);
        headerPanel.add(conditionLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lastUpdatedLabel);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
        
        // Convert min/max temps to user's preferred units
        double minTemp = settings.convertTemperature(data.getMinTemp());
        double maxTemp = settings.convertTemperature(data.getMaxTemp());
        
        detailsPanel.add(new JLabel("Min/Max Temperature:"));
        detailsPanel.add(new JLabel(String.format("%.1f%s / %.1f%s", 
                                    minTemp, settings.getTemperatureSymbol(),
                                    maxTemp, settings.getTemperatureSymbol())));
        
        detailsPanel.add(new JLabel("Humidity:"));
        detailsPanel.add(new JLabel(data.getHumidity() + "%"));
        
        detailsPanel.add(new JLabel("Wind:"));
        detailsPanel.add(new JLabel(data.getWindSpeed() + " km/h"));
        
        // Additional details panel
        JPanel additionalPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        additionalPanel.setBorder(BorderFactory.createTitledBorder("Additional Information"));
        
        additionalPanel.add(new JLabel("Pressure:"));
        additionalPanel.add(new JLabel(data.getPressure() + " hPa"));
        
        additionalPanel.add(new JLabel("Visibility:"));
        additionalPanel.add(new JLabel(data.getVisibility() + " meters"));
        
        // Add all components to the weather panel
        weatherPanel.add(headerPanel);
        weatherPanel.add(detailsPanel);
        weatherPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        weatherPanel.add(additionalPanel);
        
        // Add some spacing at the bottom
        weatherPanel.add(Box.createVerticalGlue());
        
        weatherPanel.revalidate();
        weatherPanel.repaint();
    }
    
    private void displayForecastData(String forecastData) {
        forecastPanel.removeAll();
        
        WeatherForecast forecast = WeatherForecast.fromJson(forecastData);
        List<WeatherForecast.DailyForecast> forecasts = forecast.getForecasts();
        
        if (forecasts.isEmpty()) {
            JLabel noDataLabel = new JLabel("No forecast data available", JLabel.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.BOLD, 16));
            forecastPanel.add(noDataLabel);
        } else {
            // Add a title
            JLabel titleLabel = new JLabel("5-Day Weather Forecast for " + currentLocation, JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            forecastPanel.add(titleLabel);
            
            // Add each day's forecast
            for (WeatherForecast.DailyForecast daily : forecasts) {
                JPanel dayPanel = createDailyForecastPanel(daily);
                forecastPanel.add(dayPanel);
            }
        }
        
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }
    
    private JPanel createDailyForecastPanel(WeatherForecast.DailyForecast daily) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Date
        JLabel dateLabel = new JLabel(daily.getDate());
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(dateLabel, BorderLayout.NORTH);
        
        // Weather icon
        JLabel iconLabel = new JLabel(iconManager.getIcon(daily.getCondition()));
        panel.add(iconLabel, BorderLayout.WEST);
        
        // Temperature and condition
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        
        // Convert temperatures to user's preferred unit
        double minTemp = settings.convertTemperature(daily.getMinTemp());
        double maxTemp = settings.convertTemperature(daily.getMaxTemp());
        
        JLabel tempLabel = new JLabel(String.format("Min: %.1f%s / Max: %.1f%s", 
                                      minTemp, settings.getTemperatureSymbol(),
                                      maxTemp, settings.getTemperatureSymbol()));
        
        JLabel conditionLabel = new JLabel(daily.getCondition());
        
        infoPanel.add(tempLabel);
        infoPanel.add(conditionLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateHistoryList() {
        historyModel.clear();
        List<WeatherHistory.HistoryEntry> entries = history.getHistory();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        
        for (WeatherHistory.HistoryEntry entry : entries) {
            String formattedTime = sdf.format(entry.getTimestamp());
            historyModel.addElement(entry.getLocation() + " (" + formattedTime + ")");
        }
    }
    
    private void clearHistory() {
        history.clearHistory();
        historyModel.clear();
    }
    
    private void setCursor(int cursorType) {
        mainFrame.setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new WeatherInfoApp();
        });
    }
}
