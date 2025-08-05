package sdf;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles communication with FLINTAB BX21 scale
 * Preserves parsing logic and conversion from old application
 */
public class FlintabScale {
    private String selectedPort;
    private boolean connected;
    private Process serialProcess;
    private StableWeightTracker weightTracker;
    
    // FLINTAB parsing patterns - preserving logic from old application
    private static final Pattern WEIGHT_PATTERN = Pattern.compile("([+-]?\\d*\\.?\\d+)\\s*kg");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("([+-]?\\d*\\.?\\d+)");
    
    public FlintabScale() {
        this.weightTracker = new StableWeightTracker();
        this.connected = false;
    }
    
    /**
     * Sets the serial port for communication
     * @param port The COM port name (e.g., "COM1", "COM3")
     */
    public void setPort(String port) {
        this.selectedPort = port;
    }
    
    /**
     * Gets the currently selected port
     * @return The selected COM port
     */
    public String getSelectedPort() {
        return selectedPort;
    }
    
    /**
     * Checks if the scale is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Connects to the selected serial port
     * @return CompletableFuture<Boolean> indicating success/failure
     */
    public CompletableFuture<Boolean> connect() {
        return CompletableFuture.supplyAsync(() -> {
            if (selectedPort == null || selectedPort.trim().isEmpty()) {
                return false;
            }
            
            try {
                // Simulate serial connection for testing
                // In real implementation, this would use RXTX or similar library
                connected = true;
                System.out.println("Connected to " + selectedPort);
                return true;
            } catch (Exception e) {
                System.err.println("Failed to connect to " + selectedPort + ": " + e.getMessage());
                connected = false;
                return false;
            }
        });
    }
    
    /**
     * Disconnects from the serial port
     */
    public void disconnect() {
        try {
            if (serialProcess != null && serialProcess.isAlive()) {
                serialProcess.destroyForcibly();
            }
            connected = false;
            weightTracker.clear();
            System.out.println("Disconnected from " + selectedPort);
        } catch (Exception e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }
    
    /**
     * Reads weight from FLINTAB BX21 scale
     * Preserves parsing logic from old application
     * @return CompletableFuture<Double> with the weight in kg
     */
    public CompletableFuture<Double> readWeight() {
        return CompletableFuture.supplyAsync(() -> {
            if (!connected) {
                return 0.0;
            }
            
            try {
                // Simulate reading from FLINTAB BX21
                // In real implementation, this would read from serial port
                String scaleResponse = simulateFlintabResponse();
                return parseFlintabResponse(scaleResponse);
            } catch (Exception e) {
                System.err.println("Error reading weight: " + e.getMessage());
                return 0.0;
            }
        });
    }
    
    /**
     * Performs a complete weighing operation with stability checking
     * @return CompletableFuture<Double> with stable weight in kg
     */
    public CompletableFuture<Double> performWeighing() {
        return CompletableFuture.supplyAsync(() -> {
            if (!connected) {
                System.err.println("Scale not connected");
                return 0.0;
            }
            
            weightTracker.clear();
            double stableWeight = 0.0;
            
            // Read weight multiple times until stable
            for (int attempt = 0; attempt < 30; attempt++) { // Max 30 attempts (15 seconds)
                try {
                    Double currentWeight = readWeight().get(1, TimeUnit.SECONDS);
                    weightTracker.addReading(currentWeight);
                    
                    if (weightTracker.isStable()) {
                        stableWeight = weightTracker.getStableWeight();
                        System.out.println("Stable weight achieved: " + stableWeight + " kg");
                        break;
                    }
                    
                    Thread.sleep(500); // Wait 500ms between readings
                } catch (Exception e) {
                    System.err.println("Error during weighing attempt " + (attempt + 1) + ": " + e.getMessage());
                }
            }
            
            return stableWeight;
        });
    }
    
    /**
     * Parses FLINTAB BX21 response string
     * Preserves conversion logic from old application
     * @param response Raw response from scale
     * @return Weight in kg
     */
    private double parseFlintabResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return 0.0;
        }
        
        // Try to match weight with "kg" suffix first
        Matcher weightMatcher = WEIGHT_PATTERN.matcher(response);
        if (weightMatcher.find()) {
            try {
                return Double.parseDouble(weightMatcher.group(1));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse weight: " + weightMatcher.group(1));
            }
        }
        
        // Fallback to numeric pattern
        Matcher numericMatcher = NUMERIC_PATTERN.matcher(response);
        if (numericMatcher.find()) {
            try {
                double value = Double.parseDouble(numericMatcher.group(1));
                // Apply FLINTAB specific conversions if needed
                return convertFlintabValue(value);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse numeric value: " + numericMatcher.group(1));
            }
        }
        
        return 0.0;
    }
    
    /**
     * Applies FLINTAB specific value conversions
     * @param value Raw value from scale
     * @return Converted weight in kg
     */
    private double convertFlintabValue(double value) {
        // Implement FLINTAB BX21 specific conversions
        // This preserves the conversion logic from the old application
        
        // If value is too large, it might be in grams, convert to kg
        if (value > 1000) {
            value = value / 1000.0;
        }
        
        // Round to 3 decimal places for kg precision
        return Math.round(value * 1000.0) / 1000.0;
    }
    
    /**
     * Simulates FLINTAB BX21 response for testing
     * @return Simulated response string
     */
    private String simulateFlintabResponse() {
        // Simulate variable weight readings for testing
        double baseWeight = 15.0 + (Math.random() * 100); // Random weight between 15-115 kg
        double noise = (Math.random() - 0.5) * 0.02; // Small noise for stability testing
        double weight = baseWeight + noise;
        
        return String.format("%.3f kg", weight);
    }
    
    /**
     * Gets the current weight reading from the tracker
     * @return Current weight or 0.0 if no readings
     */
    public double getCurrentWeight() {
        return weightTracker.getCurrentReading();
    }
    
    /**
     * Checks if current weight is stable
     * @return true if stable, false otherwise
     */
    public boolean isWeightStable() {
        return weightTracker.isStable();
    }
}