package sdf;

import java.io.*;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * Application settings management
 * Handles configuration for the weighing application
 */
public class AppSettings {
    private static final String SETTINGS_FILE = "app_settings.properties";
    private Properties properties;
    private String selectedPort;
    private String defaultSaveLocation;
    
    public AppSettings() {
        this.properties = new Properties();
        loadSettings();
    }
    
    /**
     * Loads settings from file
     */
    private void loadSettings() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
            selectedPort = properties.getProperty("serial.port", "");
            defaultSaveLocation = properties.getProperty("save.location", System.getProperty("user.home"));
        } catch (IOException e) {
            // File doesn't exist or can't be read, use defaults
            System.out.println("Settings file not found, using defaults");
            selectedPort = "";
            defaultSaveLocation = System.getProperty("user.home");
        }
    }
    
    /**
     * Saves settings to file
     */
    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.setProperty("serial.port", selectedPort != null ? selectedPort : "");
            properties.setProperty("save.location", defaultSaveLocation != null ? defaultSaveLocation : System.getProperty("user.home"));
            
            properties.store(output, "Weighing Application Settings");
            System.out.println("Settings saved successfully");
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }
    
    /**
     * Gets the selected serial port
     * @return Selected COM port
     */
    public String getSelectedPort() {
        return selectedPort;
    }
    
    /**
     * Sets the selected serial port
     * @param port COM port name
     */
    public void setSelectedPort(String port) {
        this.selectedPort = port;
    }
    
    /**
     * Gets the default save location for CSV files
     * @return File path for saving
     */
    public String getDefaultSaveLocation() {
        return defaultSaveLocation;
    }
    
    /**
     * Sets the default save location for CSV files
     * @param location File path for saving
     */
    public void setDefaultSaveLocation(String location) {
        this.defaultSaveLocation = location;
    }
    
    /**
     * Gets available COM ports on the system
     * @return List of available COM port names
     */
    public List<String> getAvailablePorts() {
        List<String> ports = new ArrayList<>();
        
        // For Windows systems
        for (int i = 1; i <= 20; i++) {
            ports.add("COM" + i);
        }
        
        // For Unix-like systems (uncomment if needed)
        /*
        try {
            File devDir = new File("/dev");
            if (devDir.exists()) {
                File[] serialPorts = devDir.listFiles((dir, name) -> 
                    name.startsWith("ttyUSB") || name.startsWith("ttyACM") || name.startsWith("ttyS"));
                if (serialPorts != null) {
                    for (File port : serialPorts) {
                        ports.add("/dev/" + port.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning for Unix serial ports: " + e.getMessage());
        }
        */
        
        return ports;
    }
    
    /**
     * Gets a setting value by key
     * @param key Setting key
     * @param defaultValue Default value if key not found
     * @return Setting value
     */
    public String getSetting(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Sets a setting value
     * @param key Setting key
     * @param value Setting value
     */
    public void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Gets the weighing timeout in seconds
     * @return Timeout value
     */
    public int getWeighingTimeout() {
        return Integer.parseInt(properties.getProperty("weighing.timeout", "15"));
    }
    
    /**
     * Sets the weighing timeout
     * @param seconds Timeout in seconds
     */
    public void setWeighingTimeout(int seconds) {
        properties.setProperty("weighing.timeout", String.valueOf(seconds));
    }
    
    /**
     * Gets the stability threshold for weight readings
     * @return Stability threshold
     */
    public double getStabilityThreshold() {
        return Double.parseDouble(properties.getProperty("stability.threshold", "0.01"));
    }
    
    /**
     * Sets the stability threshold
     * @param threshold Stability threshold in kg
     */
    public void setStabilityThreshold(double threshold) {
        properties.setProperty("stability.threshold", String.valueOf(threshold));
    }
    
    /**
     * Gets the minimum number of stable readings required
     * @return Number of readings
     */
    public int getMinStableReadings() {
        return Integer.parseInt(properties.getProperty("min.stable.readings", "5"));
    }
    
    /**
     * Sets the minimum number of stable readings required
     * @param count Number of readings
     */
    public void setMinStableReadings(int count) {
        properties.setProperty("min.stable.readings", String.valueOf(count));
    }
}