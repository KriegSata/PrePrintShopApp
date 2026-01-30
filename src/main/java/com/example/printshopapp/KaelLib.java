package com.example.printshopapp;

import javax.swing.*;
import java.awt.*;

public class KaelLib {

    public static int sum(int a, int b) {
        return a + b;
    }

    public static double average(int sum, int count) {
        return count > 0 ? (double) sum / count : 0;
    }

    public static boolean isEven(int num) {
        return num % 2 == 0;
    }

    public static boolean isOdd(int num) {
        return !isEven(num);
    }

    public static int findMax(int current, int newNum) {
        return Math.max(current, newNum);
    }

    public static int findMin(int current, int newNum) {
        return Math.min(current, newNum);
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(50));
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static String formatCurrency(double amount) {
        return String.format("₱%.2f", amount);
    }

    public static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private float opacity = 0.4f;

        public BackgroundPanel(String imagePath) {
            setOpaque(false);
            try {
                java.net.URL imageUrl = BackgroundPanel.class.getResource(imagePath);
                if (imageUrl == null) {
                    System.err.println("Could not find image: " + imagePath);
                    return;
                }
                backgroundImage = new ImageIcon(imageUrl).getImage();

                // Print debug information
                System.out.println("Background image loaded successfully");
                System.out.println("Image dimensions: " + backgroundImage.getWidth(this) + "x" + backgroundImage.getHeight(this));
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Enable anti-aliasing for better quality
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Set opacity
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

                // Calculate scaling to fill the panel while maintaining aspect ratio
                double scaleX = (double) getWidth() / backgroundImage.getWidth(this);
                double scaleY = (double) getHeight() / backgroundImage.getHeight(this);
                double scale = Math.max(scaleX, scaleY);

                int newWidth = (int) (backgroundImage.getWidth(this) * scale);
                int newHeight = (int) (backgroundImage.getHeight(this) * scale);

                // Center the image
                int x = (getWidth() - newWidth) / 2;
                int y = (getHeight() - newHeight) / 2;

                g2d.drawImage(backgroundImage, x, y, newWidth, newHeight, this);
                g2d.dispose();
            }
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
            repaint();
        }
    }

    public static void setBackgroundImage(JFrame frame, String imagePath) {
        BackgroundPanel backgroundPanel = new BackgroundPanel(imagePath);
        backgroundPanel.setLayout(new BorderLayout());

        // Get the existing content pane components
        Container contentPane = frame.getContentPane();
        Component[] components = contentPane.getComponents();

        // Make sure the background panel is non-opaque
        backgroundPanel.setOpaque(false);

        // Make the frame's content pane non-opaque
        if (frame.getContentPane() instanceof JComponent) {
            ((JComponent) frame.getContentPane()).setOpaque(false);
        }

        // Move all components to the background panel
        for (Component comp : components) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).setOpaque(false);
            }
            backgroundPanel.add(comp);
        }

        // Set the background panel as the content pane
        frame.setContentPane(backgroundPanel);
        frame.revalidate();
        frame.repaint();

        // Print debug information
        System.out.println("Background panel set for frame: " + frame.getTitle());
        System.out.println("Panel dimensions: " + backgroundPanel.getWidth() + "x" + backgroundPanel.getHeight());
    }
}
