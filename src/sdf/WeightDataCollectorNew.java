package sdf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Main weighing application with new categorized interface
 * Implements all requirements for the restructured weighing application
 */
public class WeightDataCollectorNew extends JFrame {
    private static final Color BACKGROUND_COLOR = Color.YELLOW;
    private static final Color MAIN_BUTTON_COLOR = new Color(0, 128, 0); // Green
    private static final Color WEIGH_BUTTON_COLOR = Color.RED;
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    // Components
    private JComboBox<String> portComboBox;
    private JLabel weightDisplay;
    private JPanel mainCategoryPanel;
    private JPanel subCategoryPanel;
    private JButton weighButton;
    private JTextArea statusArea;
    
    // Application logic
    private FlintabScale scale;
    private AppSettings settings;
    private CsvWriter csvWriter;
    private ProductCategory selectedMainCategory;
    private ProductCategory selectedSubCategory;
    
    public WeightDataCollectorNew() {
        // Initialize backend components first
        scale = new FlintabScale();
        settings = new AppSettings();
        csvWriter = new CsvWriter(settings.getDefaultSaveLocation());
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Load saved port selection
        String savedPort = settings.getSelectedPort();
        if (savedPort != null && !savedPort.isEmpty()) {
            portComboBox.setSelectedItem(savedPort);
        }
        
        updateWeighButtonState();
        startWeightDisplayUpdater();
    }
    
    /**
     * Initializes all GUI components
     */
    private void initializeComponents() {
        setTitle("Aplicația de Cântărire - Interfață Nouă");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Set background color
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Port selection
        portComboBox = new JComboBox<>();
        for (String port : settings.getAvailablePorts()) {
            portComboBox.addItem(port);
        }
        portComboBox.setPreferredSize(new Dimension(150, 30));
        
        // Weight display
        weightDisplay = new JLabel("0.000 kg", SwingConstants.CENTER);
        weightDisplay.setFont(new Font("Arial", Font.BOLD, 24));
        weightDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        weightDisplay.setOpaque(true);
        weightDisplay.setBackground(Color.WHITE);
        weightDisplay.setPreferredSize(new Dimension(200, 60));
        
        // Main category panel
        mainCategoryPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        mainCategoryPanel.setBackground(BACKGROUND_COLOR);
        createMainCategoryButtons();
        
        // Sub category panel
        subCategoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        subCategoryPanel.setBackground(BACKGROUND_COLOR);
        subCategoryPanel.setVisible(false);
        
        // Weigh button
        weighButton = new JButton("CANTARESTE");
        weighButton.setFont(new Font("Arial", Font.BOLD, 18));
        weighButton.setBackground(WEIGH_BUTTON_COLOR);
        weighButton.setForeground(BUTTON_TEXT_COLOR);
        weighButton.setPreferredSize(new Dimension(200, 50));
        weighButton.setEnabled(false);
        
        // Status area
        statusArea = new JTextArea(5, 40);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusArea.setBackground(new Color(240, 240, 240));
        
        appendStatus("Aplicația a fost inițializată.");
        appendStatus("Selectați portul serial și categoria produsului.");
    }
    
    /**
     * Creates main category buttons
     */
    private void createMainCategoryButtons() {
        for (ProductCategory category : ProductCategory.getMainCategories()) {
            JButton button = new JButton(category.getDisplayName());
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(MAIN_BUTTON_COLOR);
            button.setForeground(BUTTON_TEXT_COLOR);
            button.setPreferredSize(new Dimension(180, 40));
            button.setFocusPainted(false);
            
            button.addActionListener(e -> selectMainCategory(category));
            mainCategoryPanel.add(button);
        }
    }
    
    /**
     * Sets up the layout of the application
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - port selection and weight display
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel portLabel = new JLabel("Port Serial:");
        portLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(portLabel);
        topPanel.add(portComboBox);
        
        JLabel weightLabel = new JLabel("Greutate:");
        weightLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(weightLabel);
        topPanel.add(weightDisplay);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - category buttons
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel mainCatLabel = new JLabel("Categorii Principale:", SwingConstants.CENTER);
        mainCatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        centerPanel.add(mainCatLabel, BorderLayout.NORTH);
        centerPanel.add(mainCategoryPanel, BorderLayout.CENTER);
        centerPanel.add(subCategoryPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - weigh button and status
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(weighButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        
        JScrollPane statusScrollPane = new JScrollPane(statusArea);
        statusScrollPane.setPreferredSize(new Dimension(600, 120));
        bottomPanel.add(statusScrollPane, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Padding around the whole frame
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Port selection change
        portComboBox.addActionListener(e -> {
            String selectedPort = (String) portComboBox.getSelectedItem();
            scale.setPort(selectedPort);
            settings.setSelectedPort(selectedPort);
            settings.saveSettings();
            appendStatus("Port selectat: " + selectedPort);
            updateWeighButtonState();
        });
        
        // Weigh button
        weighButton.addActionListener(e -> performWeighing());
        
        // Window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cleanup();
                System.exit(0);
            }
        });
    }
    
    /**
     * Selects a main category and shows subcategories
     */
    private void selectMainCategory(ProductCategory category) {
        selectedMainCategory = category;
        selectedSubCategory = null;
        
        // Clear and populate subcategory panel
        subCategoryPanel.removeAll();
        
        JLabel subCatLabel = new JLabel("Selectați subcategoria pentru " + category.getDisplayName() + ":");
        subCatLabel.setFont(new Font("Arial", Font.BOLD, 12));
        subCategoryPanel.add(subCatLabel);
        
        ProductCategory[] subCategories = ProductCategory.getSubCategories(category);
        for (ProductCategory subCategory : subCategories) {
            JButton subButton = new JButton(subCategory.getDisplayName());
            subButton.setFont(new Font("Arial", Font.PLAIN, 10));
            subButton.setBackground(Color.LIGHT_GRAY);
            subButton.setPreferredSize(new Dimension(250, 30));
            subButton.setToolTipText("Cod: " + subCategory.getProductCode());
            
            subButton.addActionListener(e -> selectSubCategory(subCategory));
            subCategoryPanel.add(subButton);
        }
        
        subCategoryPanel.setVisible(true);
        subCategoryPanel.revalidate();
        subCategoryPanel.repaint();
        
        appendStatus("Categorie principală selectată: " + category.getDisplayName());
        updateWeighButtonState();
    }
    
    /**
     * Selects a subcategory
     */
    private void selectSubCategory(ProductCategory category) {
        selectedSubCategory = category;
        
        // Highlight selected subcategory button
        for (Component comp : subCategoryPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals(category.getDisplayName())) {
                    btn.setBackground(new Color(100, 149, 237)); // Cornflower blue
                    btn.setForeground(Color.WHITE);
                } else if (!btn.getText().contains("Selectați")) {
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setForeground(Color.BLACK);
                }
            }
        }
        
        appendStatus("Subcategorie selectată: " + category.getDisplayName() + " (" + category.getProductCode() + ")");
        updateWeighButtonState();
    }
    
    /**
     * Updates the state of the weigh button
     */
    private void updateWeighButtonState() {
        String selectedPort = (String) portComboBox.getSelectedItem();
        boolean enabled = selectedPort != null && !selectedPort.trim().isEmpty() && 
                         selectedSubCategory != null;
        weighButton.setEnabled(enabled);
    }
    
    /**
     * Performs the weighing operation
     */
    private void performWeighing() {
        if (selectedSubCategory == null) {
            appendStatus("EROARE: Nu este selectată o subcategorie.");
            return;
        }
        
        String selectedPort = (String) portComboBox.getSelectedItem();
        if (selectedPort == null || selectedPort.trim().isEmpty()) {
            appendStatus("EROARE: Nu este selectat un port serial.");
            return;
        }
        
        weighButton.setEnabled(false);
        weighButton.setText("Se cântărește...");
        
        appendStatus("Începe cântărirea pentru: " + selectedSubCategory.getDisplayName());
        
        // Connect and perform weighing
        CompletableFuture<Void> weighingTask = scale.connect()
            .thenCompose(connected -> {
                if (!connected) {
                    appendStatus("EROARE: Nu s-a putut conecta la portul " + selectedPort);
                    return CompletableFuture.completedFuture(0.0);
                }
                
                appendStatus("Conectat la " + selectedPort + ". Se efectuează cântărirea...");
                return scale.performWeighing();
            })
            .thenAccept(weight -> {
                SwingUtilities.invokeLater(() -> {
                    scale.disconnect(); // Always disconnect after weighing
                    
                    if (weight > 0) {
                        // Record the weighing
                        LocalDateTime timestamp = LocalDateTime.now();
                        csvWriter.recordWeighing(selectedSubCategory, weight, timestamp);
                        
                        appendStatus(String.format("Cântărire completă: %.3f kg", weight));
                        appendStatus("Datele au fost salvate în CSV.");
                        
                        // Update weight display
                        weightDisplay.setText(String.format("%.3f kg", weight));
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            String.format("Cântărire reușită!\n\nProdus: %s\nGreutate: %.3f kg\nTimestamp: %s",
                                selectedSubCategory.getDisplayName(), weight, timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            "Cântărire Completă", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        appendStatus("EROARE: Nu s-a putut obține o greutate stabilă.");
                        JOptionPane.showMessageDialog(this, 
                            "Nu s-a putut obține o greutate stabilă.\nVerificați conexiunea cu cântarul.",
                            "Eroare Cântărire", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                    weighButton.setText("CANTARESTE");
                    updateWeighButtonState();
                });
            })
            .exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    scale.disconnect();
                    appendStatus("EROARE în timpul cântăririi: " + throwable.getMessage());
                    
                    weighButton.setText("CANTARESTE");
                    updateWeighButtonState();
                });
                return null;
            });
    }
    
    /**
     * Starts the weight display updater
     */
    private void startWeightDisplayUpdater() {
        Timer timer = new Timer(500, e -> {
            if (scale.isConnected()) {
                double currentWeight = scale.getCurrentWeight();
                if (currentWeight > 0) {
                    weightDisplay.setText(String.format("%.3f kg", currentWeight));
                    if (scale.isWeightStable()) {
                        weightDisplay.setBackground(Color.GREEN);
                    } else {
                        weightDisplay.setBackground(Color.YELLOW);
                    }
                } else {
                    weightDisplay.setBackground(Color.WHITE);
                }
            } else {
                weightDisplay.setBackground(Color.WHITE);
            }
        });
        timer.start();
    }
    
    /**
     * Appends a status message
     */
    private void appendStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            statusArea.append("[" + timestamp + "] " + message + "\n");
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        });
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        if (scale != null) {
            scale.disconnect();
        }
        if (settings != null) {
            settings.saveSettings();
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            // Using default look and feel
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            WeightDataCollectorNew app = new WeightDataCollectorNew();
            app.setLocationRelativeTo(null); // Center on screen
            app.pack();
            app.setVisible(true);
            
            System.out.println("Weighing Application Started");
            System.out.println("CSV save location: " + app.csvWriter.getCurrentCsvFilePath());
        });
    }
}