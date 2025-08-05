package sdf;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles CSV data writing organized by shifts and dates
 * Creates organized output with categories on vertical, independent structure with total columns
 */
public class CsvWriter {
    private static final String[] MONTHS_ROMANIAN = {
        "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie",
        "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"
    };
    
    private static final String[] SHIFTS = {"Schimbul I", "Schimbul II", "Schimbul III"};
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private String baseDirectory;
    
    public CsvWriter(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        ensureDirectoryExists();
    }
    
    /**
     * Ensures the base directory exists
     */
    private void ensureDirectoryExists() {
        File dir = new File(baseDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Records a weighing entry
     * @param category The selected product category
     * @param weight The measured weight in kg
     * @param timestamp The timestamp of the measurement
     */
    public void recordWeighing(ProductCategory category, double weight, LocalDateTime timestamp) {
        if (category == null || category.getProductCode() == null) {
            System.err.println("Cannot record weighing for category without product code");
            return;
        }
        
        String shift = determineShift(timestamp);
        String fileName = generateFileName(timestamp);
        File csvFile = new File(baseDirectory, fileName);
        
        // Read existing data
        Map<String, Map<String, Double>> existingData = readExistingData(csvFile);
        
        // Add new entry
        String categoryKey = category.getDisplayName() + " (" + category.getProductCode() + ")";
        existingData.computeIfAbsent(categoryKey, k -> new HashMap<>())
                   .merge(shift, weight, Double::sum);
        
        // Write updated data
        writeData(csvFile, existingData, timestamp);
        
        System.out.println("Recorded weighing: " + weight + " kg for " + categoryKey + " in " + shift);
    }
    
    /**
     * Determines the shift based on time
     * @param timestamp The timestamp
     * @return The shift name
     */
    private String determineShift(LocalDateTime timestamp) {
        int hour = timestamp.getHour();
        
        if (hour >= 6 && hour < 14) {
            return SHIFTS[0]; // Schimbul I (6:00-14:00)
        } else if (hour >= 14 && hour < 22) {
            return SHIFTS[1]; // Schimbul II (14:00-22:00)
        } else {
            return SHIFTS[2]; // Schimbul III (22:00-6:00)
        }
    }
    
    /**
     * Generates the CSV file name based on date
     * @param timestamp The timestamp
     * @return The file name
     */
    private String generateFileName(LocalDateTime timestamp) {
        String month = MONTHS_ROMANIAN[timestamp.getMonthValue() - 1];
        int year = timestamp.getYear();
        return String.format("Cantariri_%s_%d.csv", month, year);
    }
    
    /**
     * Reads existing data from CSV file
     * @param csvFile The CSV file
     * @return Map of category to shift weights
     */
    private Map<String, Map<String, Double>> readExistingData(File csvFile) {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();
        
        if (!csvFile.exists()) {
            return data;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            String[] headers = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] values = line.split(",");
                
                if (headers == null) {
                    headers = values;
                    continue; // Skip header row
                }
                
                if (values.length >= 2 && !values[0].trim().isEmpty()) {
                    String category = values[0].trim();
                    Map<String, Double> shifts = new HashMap<>();
                    
                    // Read shift values (skip first column which is category name)
                    for (int i = 1; i < Math.min(values.length - 1, headers.length - 1); i++) { // -1 to exclude total column
                        if (i < SHIFTS.length + 1) { // +1 because first column is category
                            try {
                                double weight = Double.parseDouble(values[i].trim());
                                if (weight > 0) {
                                    shifts.put(SHIFTS[i - 1], weight);
                                }
                            } catch (NumberFormatException e) {
                                // Ignore invalid numbers
                            }
                        }
                    }
                    
                    if (!shifts.isEmpty()) {
                        data.put(category, shifts);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading existing CSV data: " + e.getMessage());
        }
        
        return data;
    }
    
    /**
     * Writes data to CSV file
     * @param csvFile The CSV file
     * @param data The data to write
     * @param timestamp The current timestamp
     */
    private void writeData(File csvFile, Map<String, Map<String, Double>> data, LocalDateTime timestamp) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            // Write header comment
            writer.println("# Cantariri - " + MONTHS_ROMANIAN[timestamp.getMonthValue() - 1] + " " + timestamp.getYear());
            writer.println("# Generat: " + timestamp.format(TIMESTAMP_FORMATTER) + " UTC");
            writer.println("# User: Vlad2321334");
            writer.println();
            
            // Write CSV header
            writer.print("Categorie Produs");
            for (String shift : SHIFTS) {
                writer.print("," + shift);
            }
            writer.println(",Total");
            
            // Group by main categories
            writeMainCategoryData(writer, data, ProductCategory.FRIGIDERE);
            writeMainCategoryData(writer, data, ProductCategory.VITRINE_FRIG);
            writeMainCategoryData(writer, data, ProductCategory.DULAPI_SPUMATI);
            writeMainCategoryData(writer, data, ProductCategory.AC);
            writeMainCategoryData(writer, data, ProductCategory.DOZATOARE);
            writeMainCategoryData(writer, data, ProductCategory.CALORIFERE_ELECTRICE);
            
            // Write any remaining categories that don't fit the main categories
            Set<String> writtenCategories = new HashSet<>();
            for (ProductCategory mainCat : ProductCategory.getMainCategories()) {
                for (ProductCategory subCat : ProductCategory.getSubCategories(mainCat)) {
                    String key = subCat.getDisplayName() + " (" + subCat.getProductCode() + ")";
                    writtenCategories.add(key);
                }
            }
            
            for (Map.Entry<String, Map<String, Double>> entry : data.entrySet()) {
                if (!writtenCategories.contains(entry.getKey())) {
                    writeCategoryRow(writer, entry.getKey(), entry.getValue());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error writing CSV data: " + e.getMessage());
        }
    }
    
    /**
     * Writes data for a main category and its subcategories
     * @param writer The PrintWriter
     * @param data The data map
     * @param mainCategory The main category
     */
    private void writeMainCategoryData(PrintWriter writer, Map<String, Map<String, Double>> data, ProductCategory mainCategory) {
        // Write main category header
        writer.println();
        writer.println("# " + mainCategory.getDisplayName());
        
        double categoryTotal = 0.0;
        Map<String, Double> categoryTotalsByShift = new HashMap<>();
        
        // Write subcategories
        for (ProductCategory subCategory : ProductCategory.getSubCategories(mainCategory)) {
            String key = subCategory.getDisplayName() + " (" + subCategory.getProductCode() + ")";
            Map<String, Double> shifts = data.get(key);
            
            if (shifts != null && !shifts.isEmpty()) {
                writeCategoryRow(writer, key, shifts);
                
                // Add to category totals
                for (Map.Entry<String, Double> shiftEntry : shifts.entrySet()) {
                    categoryTotalsByShift.merge(shiftEntry.getKey(), shiftEntry.getValue(), Double::sum);
                    categoryTotal += shiftEntry.getValue();
                }
            }
        }
        
        // Write category total row if there's data
        if (categoryTotal > 0) {
            writer.print("TOTAL " + mainCategory.getDisplayName());
            for (String shift : SHIFTS) {
                Double shiftTotal = categoryTotalsByShift.get(shift);
                writer.print("," + (shiftTotal != null ? String.format("%.3f", shiftTotal) : "0.000"));
            }
            writer.println("," + String.format("%.3f", categoryTotal));
        }
    }
    
    /**
     * Writes a single category row
     * @param writer The PrintWriter
     * @param category The category name
     * @param shifts The shift data
     */
    private void writeCategoryRow(PrintWriter writer, String category, Map<String, Double> shifts) {
        writer.print(category);
        
        double total = 0.0;
        for (String shift : SHIFTS) {
            Double weight = shifts.get(shift);
            if (weight != null && weight > 0) {
                writer.print("," + String.format("%.3f", weight));
                total += weight;
            } else {
                writer.print(",0.000");
            }
        }
        
        writer.println("," + String.format("%.3f", total));
    }
    
    /**
     * Gets the current CSV file path for today
     * @return The file path
     */
    public String getCurrentCsvFilePath() {
        LocalDateTime now = LocalDateTime.now();
        String fileName = generateFileName(now);
        return new File(baseDirectory, fileName).getAbsolutePath();
    }
    
    /**
     * Gets a summary of recorded data for the current month
     * @return Summary string
     */
    public String getMonthSummary() {
        LocalDateTime now = LocalDateTime.now();
        File csvFile = new File(baseDirectory, generateFileName(now));
        
        if (!csvFile.exists()) {
            return "Nu există date pentru " + MONTHS_ROMANIAN[now.getMonthValue() - 1] + " " + now.getYear();
        }
        
        Map<String, Map<String, Double>> data = readExistingData(csvFile);
        int categoriesCount = data.size();
        double totalWeight = data.values().stream()
                .flatMap(shifts -> shifts.values().stream())
                .mapToDouble(Double::doubleValue)
                .sum();
        
        return String.format("Luna %s %d: %d categorii, %.3f kg total",
                MONTHS_ROMANIAN[now.getMonthValue() - 1], now.getYear(), categoriesCount, totalWeight);
    }
}