package sdf;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Screenshot utility for the weighing application
 */
public class ScreenshotTaker {
    
    public static void takeScreenshot(WeightDataCollectorNew app, String filename) {
        try {
            // Wait for the GUI to be fully rendered
            SwingUtilities.invokeAndWait(() -> {
                app.repaint();
            });
            
            Thread.sleep(1000); // Give time for rendering
            
            // Get the component to screenshot
            Component component = app.getContentPane();
            BufferedImage image = new BufferedImage(
                component.getWidth(), 
                component.getHeight(), 
                BufferedImage.TYPE_INT_RGB
            );
            
            Graphics2D g2d = image.createGraphics();
            component.paint(g2d);
            g2d.dispose();
            
            // Save the image
            File outputFile = new File("/tmp/" + filename);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Screenshot saved: " + outputFile.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("Error taking screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Run with headless mode disabled
        System.setProperty("java.awt.headless", "false");
        
        SwingUtilities.invokeLater(() -> {
            try {
                WeightDataCollectorNew app = new WeightDataCollectorNew();
                app.setLocationRelativeTo(null);
                app.setVisible(true);
                
                // Take initial screenshot
                SwingUtilities.invokeLater(() -> {
                    takeScreenshot(app, "weighing_app_initial.png");
                    
                    // Simulate selecting a category
                    try {
                        Thread.sleep(500);
                        
                        // Find and click the FRIGIDERE button
                        Container mainCategoryPanel = findComponent(app, "mainCategoryPanel");
                        if (mainCategoryPanel != null) {
                            for (Component comp : mainCategoryPanel.getComponents()) {
                                if (comp instanceof JButton && ((JButton) comp).getText().equals("FRIGIDERE")) {
                                    ((JButton) comp).doClick();
                                    break;
                                }
                            }
                        }
                        
                        Thread.sleep(500);
                        takeScreenshot(app, "weighing_app_with_subcategories.png");
                        
                        System.out.println("Screenshots completed!");
                        System.exit(0);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
    
    private static Container findComponent(Container parent, String name) {
        for (Component comp : parent.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(name)) {
                return (Container) comp;
            }
            if (comp instanceof Container) {
                Container found = findComponent((Container) comp, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}