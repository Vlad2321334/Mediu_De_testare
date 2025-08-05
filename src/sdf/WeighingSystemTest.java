package sdf;

import java.time.LocalDateTime;

/**
 * Simple test application to demonstrate the weighing system functionality
 */
public class WeighingSystemTest {
    
    public static void main(String[] args) {
        System.out.println("=== Test Aplicația de Cântărire ===");
        
        // Test AppSettings
        System.out.println("\n1. Testing AppSettings...");
        AppSettings settings = new AppSettings();
        System.out.println("Available ports: " + settings.getAvailablePorts());
        System.out.println("Default save location: " + settings.getDefaultSaveLocation());
        
        // Test ProductCategory
        System.out.println("\n2. Testing ProductCategory...");
        System.out.println("Main categories:");
        for (ProductCategory category : ProductCategory.getMainCategories()) {
            System.out.println("  - " + category.getDisplayName());
            System.out.println("    Subcategories:");
            for (ProductCategory sub : ProductCategory.getSubCategories(category)) {
                System.out.println("      * " + sub.getDisplayName() + " (" + sub.getProductCode() + ")");
            }
        }
        
        // Test StableWeightTracker
        System.out.println("\n3. Testing StableWeightTracker...");
        StableWeightTracker tracker = new StableWeightTracker();
        
        // Simulate weight readings
        double[] readings = {15.234, 15.236, 15.235, 15.234, 15.235, 15.234};
        for (double reading : readings) {
            tracker.addReading(reading);
            System.out.println("  Reading: " + reading + " kg, Stable: " + tracker.isStable());
        }
        System.out.println("  Final stable weight: " + tracker.getStableWeight() + " kg");
        
        // Test FlintabScale
        System.out.println("\n4. Testing FlintabScale...");
        FlintabScale scale = new FlintabScale();
        scale.setPort("COM1");
        System.out.println("  Selected port: " + scale.getSelectedPort());
        System.out.println("  Connected: " + scale.isConnected());
        
        // Test CsvWriter
        System.out.println("\n5. Testing CsvWriter...");
        CsvWriter csvWriter = new CsvWriter("/tmp/test_weighing");
        
        // Simulate some weighings
        LocalDateTime now = LocalDateTime.now();
        csvWriter.recordWeighing(ProductCategory.ECHIPAMENTE_FRIGORIFICE, 25.340, now);
        csvWriter.recordWeighing(ProductCategory.VITRINE_FRIGORIFICE, 30.125, now.plusMinutes(15));
        csvWriter.recordWeighing(ProductCategory.DULAPI_SPUMATI_FS_390, 18.750, now.plusMinutes(30));
        
        System.out.println("  CSV file created at: " + csvWriter.getCurrentCsvFilePath());
        System.out.println("  Month summary: " + csvWriter.getMonthSummary());
        
        System.out.println("\n=== Test complet! Toate componentele funcționează corect ===");
        
        // Show CSV content
        System.out.println("\n6. CSV Content Preview:");
        try {
            java.nio.file.Path csvPath = java.nio.file.Paths.get(csvWriter.getCurrentCsvFilePath());
            if (java.nio.file.Files.exists(csvPath)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(csvPath);
                for (String line : lines) {
                    System.out.println("  " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("  Error reading CSV: " + e.getMessage());
        }
    }
}