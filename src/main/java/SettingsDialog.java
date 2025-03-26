import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog for application settings
 */
public class SettingsDialog extends JDialog {
    private AppSettings settings;
    private JComboBox<String> temperatureUnitCombo;
    private JComboBox<String> themeCombo;
    private JCheckBox autoRefreshCheck;
    
    public SettingsDialog(JFrame parent, AppSettings settings) {
        super(parent, "Settings", true);
        this.settings = settings;
        
        initComponents();
        loadCurrentSettings();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Temperature unit
        mainPanel.add(new JLabel("Temperature Unit:"));
        temperatureUnitCombo = new JComboBox<>(new String[]{"Celsius", "Fahrenheit"});
        mainPanel.add(temperatureUnitCombo);
        
        // Theme
        mainPanel.add(new JLabel("Theme:"));
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        mainPanel.add(themeCombo);
        
        // Auto-refresh
        mainPanel.add(new JLabel("Auto-refresh:"));
        autoRefreshCheck = new JCheckBox("Enable auto-refresh");
        mainPanel.add(autoRefreshCheck);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCurrentSettings() {
        temperatureUnitCombo.setSelectedItem(settings.getTemperatureUnit());
        themeCombo.setSelectedItem(settings.getTheme());
        autoRefreshCheck.setSelected(settings.isAutoRefresh());
    }
    
    private void saveSettings() {
        settings.setTemperatureUnit((String) temperatureUnitCombo.getSelectedItem());
        settings.setTheme((String) themeCombo.getSelectedItem());
        settings.setAutoRefresh(autoRefreshCheck.isSelected());
        settings.saveSettings();
    }
}
