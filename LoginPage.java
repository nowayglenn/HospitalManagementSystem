import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private final int CARD_RADIUS = 25;
    private final Color hospitalBlue = new Color(30, 144, 255);

    public LoginPage() {
        setTitle("Hospital Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(new GridBagLayout());

        JPanel loginCard = createLoginCard();
        add(loginCard);
        setVisible(true);
    }

    private JPanel createLoginCard() {
        
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Para sa shadow effect
                g2d.setColor(new Color(0, 0, 0, 17));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, CARD_RADIUS, CARD_RADIUS);

                // Para sa main card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, CARD_RADIUS, CARD_RADIUS);

                g2d.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };

        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(500, 480));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        //Logo label
        JLabel logoLabel = new JLabel("Hospital Management System");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(hospitalBlue);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(logoLabel);

        cardPanel.add(Box.createVerticalStrut(10));

        JLabel subTitleLabel = new JLabel("Manage patients, doctors, and appointments efficiently");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subTitleLabel);

        cardPanel.add(Box.createVerticalStrut(40));

        JLabel instructionLabel = new JLabel("USER LOGIN");
        instructionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(instructionLabel);

        cardPanel.add(Box.createVerticalStrut(30));

        // ==========================================
        //        USERNAME SECTION NING DAPITA
        // ==========================================
        
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setOpaque(false);
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(320, 40));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createVerticalStrut(5));
        usernamePanel.add(usernameField);

        cardPanel.add(usernamePanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // =========================================
        //        PASSWORD SECTION NING DAPITA
        // =========================================
        
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setOpaque(false);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(320, 40));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createVerticalStrut(5));
        passwordPanel.add(passwordField);

        
        cardPanel.add(passwordPanel);
        cardPanel.add(Box.createVerticalStrut(30));

        // Para sa login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setBackground(hospitalBlue);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new LoginButtonListener());
        cardPanel.add(loginButton);

        cardPanel.add(Box.createVerticalStrut(30));
        
         // Demo Credentials text under the Login button
        JLabel lblDemo = new JLabel("Demo Credentials: Admin / admin", SwingConstants.CENTER);
        lblDemo.setForeground(Color.GRAY);
        lblDemo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDemo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(lblDemo); 
        
        return cardPanel;
    }

    //Mao ni ag connection sa login into main
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPage.this,
                        "Please enter both Username and Password.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Ge set nga password paras login page
            if (username.equals("admin") && password.equals("password")) {
                // 1. Para ma close ang Login Window
                dispose();
                
                // 2. Open the Main Application Window on the Event Dispatch Thread (EDT)
                SwingUtilities.invokeLater(() -> {
                    // Mao ni anag line nga mo connect sa duha ka files
                    new HospitalManagementSystem(); 
                });
            } else {
                JOptionPane.showMessageDialog(LoginPage.this,
                        "Invalid Username or Password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Apply native OS look and feel for a better user experience
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ensure the entire application starts on the EDT
        // Para safe ang GUI dili mag crash
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}