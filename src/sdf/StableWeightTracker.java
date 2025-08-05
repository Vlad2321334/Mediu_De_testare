package sdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks weight stability for accurate measurements
 * Preserves logic from old application for detecting stable weight readings
 */
public class StableWeightTracker {
    private List<Double> recentWeights;
    private final int bufferSize;
    private final double stabilityThreshold;
    private final int minStableReadings;
    
    public StableWeightTracker() {
        this(10, 0.01, 5); // Default values
    }
    
    public StableWeightTracker(int bufferSize, double stabilityThreshold, int minStableReadings) {
        this.bufferSize = bufferSize;
        this.stabilityThreshold = stabilityThreshold;
        this.minStableReadings = minStableReadings;
        this.recentWeights = new ArrayList<>();
    }
    
    /**
     * Adds a new weight reading to the tracker
     * @param weight The weight reading in kg
     */
    public void addReading(double weight) {
        recentWeights.add(weight);
        
        // Keep only the most recent readings
        if (recentWeights.size() > bufferSize) {
            recentWeights.remove(0);
        }
    }
    
    /**
     * Checks if the weight is stable based on recent readings
     * @return true if weight is stable, false otherwise
     */
    public boolean isStable() {
        if (recentWeights.size() < minStableReadings) {
            return false;
        }
        
        // Check last minStableReadings for stability
        List<Double> stableCheckReadings = recentWeights.subList(
            Math.max(0, recentWeights.size() - minStableReadings), 
            recentWeights.size()
        );
        
        double min = stableCheckReadings.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = stableCheckReadings.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        
        return (max - min) <= stabilityThreshold;
    }
    
    /**
     * Gets the current stable weight
     * @return The average of recent stable readings
     */
    public double getStableWeight() {
        if (!isStable() || recentWeights.isEmpty()) {
            return 0.0;
        }
        
        // Return average of last stable readings
        List<Double> stableReadings = recentWeights.subList(
            Math.max(0, recentWeights.size() - minStableReadings), 
            recentWeights.size()
        );
        
        return stableReadings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    /**
     * Clears all weight readings
     */
    public void clear() {
        recentWeights.clear();
    }
    
    /**
     * Gets the most recent weight reading
     * @return The last weight reading or 0.0 if no readings
     */
    public double getCurrentReading() {
        return recentWeights.isEmpty() ? 0.0 : recentWeights.get(recentWeights.size() - 1);
    }
}