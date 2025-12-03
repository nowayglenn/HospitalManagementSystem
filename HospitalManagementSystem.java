import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Main application class
public class HospitalManagementSystem extends JFrame {

// PLACEHOLDER NING DAPITA
final String NAME_PLACEHOLDER = "Last Name, First Name";
final Color PLACEHOLDER_COLOR = Color.GRAY;
final Color DEFAULT_TEXT_COLOR = Color.BLACK;

// Data Structures
private HashMap<String, Patient> patients;
private HashMap<String, Doctor> doctors;
private ArrayList<Appointment> appointments;
private Stack<Action> actionHistory;

// Queues for Patient Priority System
public Queue<String> emergencyQueue;
public Queue<String> regularQueue;

// Mga UI Components
private JPanel sidebarPanel;
private JPanel contentPanel;
private DefaultTableModel patientTableModel;
private DefaultTableModel doctorTableModel;
private DefaultTableModel appointmentTableModel;
private DefaultTableModel queueTableModel;

private String loggedInUser = "Admin User";
private String userRole = "Administrator";

// PARA CSV EXPORTER INSTANCE
private CSVExporter csvExporter;

public HospitalManagementSystem() {
    // Initialize data structures
    patients = new HashMap<>();
    doctors = new HashMap<>();
    appointments = new ArrayList<>();
    actionHistory = new Stack<>();

    // Naggamit og LinkedList para sa Queue implementation
    emergencyQueue = new LinkedList<>();
    regularQueue = new LinkedList<>();
    csvExporter = new CSVExporter(this);

    // Setup Frame
    setTitle("Hospital Management System");
    setSize(1450, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create sidebar
    sidebarPanel = createSidebar();
    mainPanel.add(sidebarPanel, BorderLayout.WEST);

    // Create content area
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBackground(Color.WHITE);
    
    // Show Patient Management by default
    showPatientManagement();
    
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    add(mainPanel);
    setVisible(true);
}
// Helper method para icon
private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL, description);
            // Resize the icon for the sidebar buttons (20x20)
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newimg, description);
        } else {
            System.err.println("Couldn't find image file: " + path);
            return null;
        }
    }

private JPanel createSidebar() {
    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(new Color(248, 249, 250));
    sidebar.setPreferredSize(new Dimension(250, 800));
    sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

    // Header with logo and user info
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
    headerPanel.setBackground(new Color(248, 249, 250));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15)); 
    headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Header Tittle (Hospital Management System)
    JLabel titleLabel = new JLabel("Hospital Management System");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);// Gi center ag Hospital Management System nga tittle
    headerPanel.add(titleLabel);
    
    headerPanel.add(Box.createVerticalStrut(30));

    // User profile with icon (kadtong green)
    JPanel profilePanel = new JPanel();
    profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
    profilePanel.setBackground(new Color(248, 249, 250));
    profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT); 

    // User icon (circle with initials) ag green nga circle naay AU sa sud
    JPanel iconPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(76, 175, 80));
            g2d.fillOval(0, 0, 60, 60);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            String initials = "AU";
            int x = (60 - fm.stringWidth(initials)) / 2;
            int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(initials, x, y);
        }
    };
    
    iconPanel.setPreferredSize(new Dimension(60, 60));
    iconPanel.setMaximumSize(new Dimension(60, 60));
    iconPanel.setBackground(new Color(248, 249, 250));
    iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    profilePanel.add(iconPanel);
    profilePanel.add(Box.createVerticalStrut(10));

    JLabel userNameLabel = new JLabel(loggedInUser);
    userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    profilePanel.add(userNameLabel);

    JLabel userRoleLabel = new JLabel(userRole);
    userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    userRoleLabel.setForeground(new Color(128, 128, 128));
    userRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    profilePanel.add(userRoleLabel);

    headerPanel.add(profilePanel);
    sidebar.add(headerPanel);

    sidebar.add(Box.createVerticalStrut(20));

    // Mga Menu bar (Patient Management, Doctor Management, Appointment Operations, Patient Queue Management)
    JButton patientBtn = createSidebarButton("Patient Management","/icons/Patient-management.png", true);// naay icon
    JButton doctorBtn = createSidebarButton("Doctor Management","/icons/Doctor-management.png", false); // naay icon
    JButton appointmentBtn = createSidebarButton("Appointment Operations","/icons/Appointment-operations.png", false);// naay icon
    JButton queueBtn = createSidebarButton("Patient Queue","/icons/Patient-queue.png", false);// naay icon

    // Aron ma arrange ang navigation buttons vertically, and mo control sa spacing gamit ang Box layout manager.
    sidebar.add(patientBtn);
    sidebar.add(Box.createVerticalStrut(2)); 
    sidebar.add(doctorBtn);
    sidebar.add(Box.createVerticalStrut(2)); 
    sidebar.add(appointmentBtn);
    sidebar.add(Box.createVerticalStrut(2)); 
    sidebar.add(queueBtn);

    // Event listeners
    patientBtn.addActionListener(e -> {
        resetSidebarButtons(sidebar);
        patientBtn.setBackground(new Color(66, 103, 178));
        patientBtn.setForeground(Color.WHITE);
        showPatientManagement();
    });

    doctorBtn.addActionListener(e -> {
        resetSidebarButtons(sidebar);
        doctorBtn.setBackground(new Color(66, 103, 178));
        doctorBtn.setForeground(Color.WHITE);
        showDoctorManagement();
    });

    appointmentBtn.addActionListener(e -> {
        resetSidebarButtons(sidebar);
        appointmentBtn.setBackground(new Color(66, 103, 178));
        appointmentBtn.setForeground(Color.WHITE);
        showAppointmentOperations();
    });

    queueBtn.addActionListener(e -> {
        resetSidebarButtons(sidebar);
        queueBtn.setBackground(new Color(66, 103, 178));
        queueBtn.setForeground(Color.WHITE);
        showQueueManagement();
    });

    // Add glue to push logout button to bottom
    sidebar.add(Box.createVerticalGlue());

    // Logout  button sa bottom sulod sa vertical navigation bar menu.
    JButton logoutBtn = new JButton("Logout");
    
    
    ImageIcon logoutIcon = createImageIcon("/icons/logout.png", "Logout");
    if (logoutIcon != null) {
        logoutBtn.setIcon(logoutIcon);
        // Ga add og spacing for consistency with other buttons
        logoutBtn.setIconTextGap(10); 
    }
    
    logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT); 
    logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    logoutBtn.setBackground(new Color(248, 249, 250));
    logoutBtn.setForeground(new Color(220, 53, 69));
    logoutBtn.setFocusPainted(false);
    logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
    logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    logoutBtn.setHorizontalAlignment(SwingConstants.CENTER);

    // Function sa logout, if yes mo balik sa login page
    logoutBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage());
        }
    });

    sidebar.add(Box.createVerticalStrut(10));
    sidebar.add(logoutBtn);
    sidebar.add(Box.createVerticalStrut(20));

    return sidebar;
}

// The createSidebarButton method now accepts 3 arguments (text, iconPath, isSelected)
private JButton createSidebarButton(String text, String iconPath, boolean isSelected) {
    ImageIcon icon = null;
    if (iconPath != null && !iconPath.isEmpty()) {
        icon = createImageIcon(iconPath, text);
    }

    // Use plain text since the icon handles the visual part
    JButton button = new JButton(text); 
    button.setAlignmentX(Component.LEFT_ALIGNMENT); 
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    
    // Set the icon and space it out
    if (icon != null) {
        button.setIcon(icon); 
        button.setIconTextGap(10); 
    }

    if (isSelected) {
        button.setBackground(new Color(66, 103, 178));
        button.setForeground(Color.WHITE);
    } else {
        button.setBackground(new Color(248, 249, 250));
        button.setForeground(Color.BLACK);
    }

    // Hover effect
    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (!button.getBackground().equals(new Color(66, 103, 178))) {
                button.setBackground(new Color(233, 236, 239));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (!button.getBackground().equals(new Color(66, 103, 178))) {
                button.setBackground(new Color(248, 249, 250));
            }
        }
    });

    return button;
}

private void resetSidebarButtons(JPanel sidebar) {
    for (Component comp : sidebar.getComponents()) {
        if (comp instanceof JButton && !((JButton) comp).getText().contains("Logout")) {
            comp.setBackground(new Color(248, 249, 250));
            comp.setForeground(Color.BLACK);
        }
    }
}

private void showPatientManagement() {
    contentPanel.removeAll();
    contentPanel.add(createPatientPanel());
    contentPanel.revalidate();
    contentPanel.repaint();
}

private void showDoctorManagement() {
    contentPanel.removeAll();
    contentPanel.add(createDoctorPanel());
    contentPanel.revalidate();
    contentPanel.repaint();
}

private void showAppointmentOperations() {
    contentPanel.removeAll();
    contentPanel.add(createAppointmentPanel());
    contentPanel.revalidate();
    contentPanel.repaint();
}

private void showQueueManagement() {
    contentPanel.removeAll();
    contentPanel.add(createQueuePanel());
    contentPanel.revalidate();
    contentPanel.repaint();
}

private JButton createStyledButton(String text, Color background, Color foreground) {
    JButton button = new JButton(text);
    button.setBackground(background);
    button.setForeground(foreground);
    button.setFocusPainted(false);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
}

/*To check if a given ID string adheres to a specific format requirement
before it's processed or saved in your Hospital Management System*/
private boolean isValidId(String id) {
    if (id == null || id.isEmpty()) {
        return false;
    }
    return id.matches("^[a-zA-Z0-9]+$");
}

// ===========================================
//          PATIENT MANAGEMENT PANEL
// ===========================================

private JPanel createPatientPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Header with title and export button 
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(245, 245, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setBackground(new Color(245, 245, 245));

    JLabel titleLabel = new JLabel("Patient Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titlePanel.add(titleLabel);

    JLabel subtitleLabel = new JLabel("Manage patients, doctors, and appointments efficiently");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(128, 128, 128));
    titlePanel.add(subtitleLabel);

    headerPanel.add(titlePanel, BorderLayout.WEST);

    JButton exportButton = createStyledButton("Export to CSV", new Color(108, 117, 125), Color.WHITE);
    exportButton.addActionListener(e -> csvExporter.exportToCSV(patientTableModel, "Patient_Data"));
    headerPanel.add(exportButton, BorderLayout.EAST);

    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Content Panel
    JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    contentPanel.setBackground(Color.WHITE);

    // Form Panel
    JPanel formContainer = new JPanel(new BorderLayout(10, 10));
    formContainer.setBackground(Color.WHITE);
    formContainer.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
    BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    JLabel formTitle = new JLabel("Patient Information");
    formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    formContainer.add(formTitle, BorderLayout.NORTH);

    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBackground(Color.WHITE);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;

    
    JTextField idField = new JTextField(15);
    JTextField nameField = new JTextField(15);
    JTextField ageField = new JTextField(15);
    JTextField genderField = new JTextField(15);
    JTextField contactField = new JTextField(15);
    JTextField addressField = new JTextField(15);
    JTextField bloodtypeField = new JTextField(15);
    
    nameField.setText(NAME_PLACEHOLDER);
    nameField.setForeground(PLACEHOLDER_COLOR);
    nameField.addFocusListener(new FocusAdapter(){
       
        @Override
        public void focusGained(FocusEvent e){
            if(nameField.getText().equals(NAME_PLACEHOLDER)){
                nameField.setText("");
                nameField.setForeground(DEFAULT_TEXT_COLOR);
            }
        }
        
        @Override
        public void focusLost(FocusEvent e){
            if (nameField.getText().isEmpty()){
                nameField.setText(NAME_PLACEHOLDER);
                nameField.setForeground(PLACEHOLDER_COLOR);
            }
        }
    });

    // Style text fields
    JTextField[] fields = {idField, ageField, nameField, genderField, contactField, addressField, bloodtypeField};
    for (JTextField field : fields) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
    // Label nis input box kadtong mga Patient ID, Full name, basta mga ingana.
    // Row 1
    gbc.gridx = 0; gbc.gridy = 0;
    inputPanel.add(new JLabel("Patient ID:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(idField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Age:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(ageField, gbc);

    // Row 2
    gbc.gridx = 0; gbc.gridy = 1;
    inputPanel.add(new JLabel("Full Name:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(nameField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Contact:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(contactField, gbc);

    // Row 3
    gbc.gridx = 0; gbc.gridy = 2;
    inputPanel.add(new JLabel("Gender:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(genderField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Address:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(addressField, gbc);

    // Row 4
    gbc.gridx = 0; gbc.gridy = 3;
    inputPanel.add(new JLabel("Blood Type:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(bloodtypeField, gbc);

    formContainer.add(inputPanel, BorderLayout.CENTER);

    // Button Panel 
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
    buttonPanel.setBackground(Color.WHITE);
    
    //Mga buttons sa Patient Management
    // Register button
    JButton addButton = createStyledButton("Register Patient", new Color(46, 204, 113), Color.WHITE);// green
    // Remove button
    JButton removeButton = createStyledButton("Remove Patient", new Color(231, 76, 60), Color.WHITE);// red
    // Search by ID button
    JButton searchIdButton = createStyledButton("Search by ID", new Color(52, 152, 219), Color.WHITE);// blue
    // Search by Name button
    JButton searchNameButton = createStyledButton("Search by Name", new Color(52, 152, 219), Color.WHITE);// light blue
    // Search by Bloodtype button
    JButton searchBloodButton = createStyledButton("Search by Blood Type", new Color(155, 89, 182), Color.WHITE);// violet
    // Update bytton
    JButton updateButton = createStyledButton("Update", new Color(241, 196, 15), Color.BLACK);// yellow 
    // Display all button
    JButton displayAllButton = createStyledButton("Display All", new Color(189, 195, 199), Color.BLACK);// light gray
    // Sort patients button
    JButton sortButton = createStyledButton("Sort Patients", new Color(26, 188, 156), Color.WHITE);// dark green
    // Clear fields button
    JButton refreshButton = createStyledButton("Clear Fields", new Color(149, 165, 166), Color.WHITE);// gray

    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);
    buttonPanel.add(searchIdButton);
    buttonPanel.add(searchNameButton);
    buttonPanel.add(searchBloodButton);
    buttonPanel.add(updateButton);
    buttonPanel.add(displayAllButton);
    buttonPanel.add(sortButton);
    buttonPanel.add(refreshButton);

    formContainer.add(buttonPanel, BorderLayout.SOUTH);
    contentPanel.add(formContainer, BorderLayout.NORTH);

    // Table 
    String[] columns = {"ID", "Name", "Age", "Gender", "Contact", "Address", "Blood Type"};
    patientTableModel = new DefaultTableModel(columns, 0);
    JTable table = new JTable(patientTableModel);
    table.setFillsViewportHeight(true);
    table.setRowHeight(25);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    table.getTableHeader().setBackground(new Color(240, 240, 240));
    
    // Para mo Load existing patient data sa table model when the panel is created
    refreshPatientTable(); 
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Footer with Undo button
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
    footerPanel.setBackground(Color.WHITE);
    JButton undoButton = createStyledButton("Undo Last Action", new Color(243, 156, 18), Color.WHITE);
    undoButton.addActionListener(e -> undoLastAction());
    footerPanel.add(undoButton);
    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    // Button Actions
    // ADD BUTTON ACTION (Register Patient)
    addButton.addActionListener(e -> {
        try {
            String nameText = nameField.getText().trim();
            if (nameText.equals(NAME_PLACEHOLDER)){
                nameText = "";
            }
            String id = idField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String name = nameText;
            String gender = genderField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String bloodtype = bloodtypeField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name are required!");
                return;
            }

            if (!isValidId(id)) {
                JOptionPane.showMessageDialog(this, "Patient ID must contain only letters and numbers!");
                return;
            }

            if (patients.containsKey(id)) {
                JOptionPane.showMessageDialog(this, "Patient ID already exists!");
                return;
            }

            // This registers the patient and adds them to the map
            Patient patient = new Patient(id, name, age, gender, contact, address, bloodtype);
            patients.put(id, patient);

            actionHistory.push(new Action() {
                @Override
                public void undo() {
                    patients.remove(id);
                    refreshPatientTable();
                }
            });

            // This refreshes the JTable to show the new patient data
            refreshPatientTable();
            clearPatientFields(idField, nameField, ageField, genderField, contactField, addressField, bloodtypeField);
            JOptionPane.showMessageDialog(this, "Patient registered successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please fill required fields!");
        }
    });

    removeButton.addActionListener(e -> {
        String id = idField.getText().trim();
        if (patients.containsKey(id)) {
            Patient removedPatient = patients.get(id);
            patients.remove(id);

            actionHistory.push(new Action() {
                @Override
                public void undo() {
                    patients.put(id, removedPatient);
                    refreshPatientTable();
                }
            });

            refreshPatientTable();
            clearPatientFields(idField, nameField, ageField, genderField, contactField, addressField, bloodtypeField);
            JOptionPane.showMessageDialog(this, "Patient removed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found!");
        }
    });

    searchIdButton.addActionListener(e -> {
        String id = idField.getText().trim();
        Patient patient = patients.get(id);
        if (patient != null) {
            patientTableModel.setRowCount(0);
            patientTableModel.addRow(new Object[]{
                patient.getId(), patient.getName(), patient.getAge(),
                patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType()
            });
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found!");
        }
    });

    searchNameButton.addActionListener(e -> {
        String name = nameField.getText().trim().toLowerCase();
        patientTableModel.setRowCount(0);
        boolean found = false;
        for (Patient patient : patients.values()) {
            if (patient.getName().toLowerCase().contains(name)) {
                patientTableModel.addRow(new Object[]{
                    patient.getId(), patient.getName(), patient.getAge(),
                    patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType()
                });
                found = true;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "No patients found with that name!");
        }
    });

    searchBloodButton.addActionListener(e -> {
        String bloodType = bloodtypeField.getText().trim().toUpperCase();
        if (bloodType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a blood type to search!");
            return;
        }
        patientTableModel.setRowCount(0);
        boolean found = false;
        for (Patient patient : patients.values()) {
            if (patient.getBloodType().toUpperCase().equals(bloodType)) {
                patientTableModel.addRow(new Object[]{
                    patient.getId(), patient.getName(), patient.getAge(),
                    patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType()
                });
                found = true;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "No patients found with blood type: " + bloodType);
        }
    });

    updateButton.addActionListener(e -> {
        String id = idField.getText().trim();
        if (patients.containsKey(id)) {
            Patient patient = patients.get(id);
            Patient oldPatient = new Patient(patient.getId(), patient.getName(),
                patient.getAge(), patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType());

            try {
                patient.setName(nameField.getText().trim());
                patient.setAge(Integer.parseInt(ageField.getText().trim()));
                patient.setGender(genderField.getText().trim());
                patient.setContact(contactField.getText().trim());
                patient.setAddress(addressField.getText().trim());
                patient.setBloodType(bloodtypeField.getText().trim());

                actionHistory.push(new Action() {
                    @Override
                    public void undo() {
                        patient.setName(oldPatient.getName());
                        patient.setAge(oldPatient.getAge());
                        patient.setGender(oldPatient.getGender());
                        patient.setContact(oldPatient.getContact());
                        patient.setAddress(oldPatient.getAddress());
                        patient.setBloodType(oldPatient.getBloodType());
                        refreshPatientTable();
                    }
                });

                refreshPatientTable();
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found!");
        }
    });

    displayAllButton.addActionListener(e -> refreshPatientTable());

    sortButton.addActionListener(e -> {
        String[] sortOptions = {"Patient ID (A-Z/Low to High)", "Patient ID (Z-A/High to Low)","Last Name (A-Z)", "Last Name (Z-A)", "Age (Low to High)", "Age (High to Low)", "Blood Type"};
        String choice = (String) JOptionPane.showInputDialog(
            this, "Choose sorting criteria:", "Sort Patients",
            JOptionPane.QUESTION_MESSAGE, null, sortOptions, sortOptions[0]
        );
        if (choice != null) {
            sortPatients(choice);
        }
    });

    refreshButton.addActionListener(e -> {
        clearPatientFields(idField, nameField, ageField, genderField, contactField, addressField, bloodtypeField);
    });

    return mainPanel;
}
// Para sa sorting
private void sortPatients(String criteria) {
    List<Patient> patientList = new ArrayList<>(patients.values());

    switch (criteria) {
        case "Patient ID (A-Z/Low to High)":
            patientList.sort(Comparator.comparing(Patient::getId));
            break;
        case "Patient ID (Z-A/High to Low)":
            patientList.sort(Comparator.comparing(Patient::getId).reversed());
            break;
        case "Last Name (A-Z)":
            patientList.sort(Comparator.comparing(Patient::getName));
            break;
        case "Last Name (Z-A)":
            patientList.sort(Comparator.comparing(Patient::getName).reversed());
            break;
        case "Age (Low to High)":
            patientList.sort(Comparator.comparingInt(Patient::getAge));
            break;
        case "Age (High to Low)":
            patientList.sort(Comparator.comparingInt(Patient::getAge).reversed());
            break;
        case "Blood Type":
            patientList.sort(Comparator.comparing(Patient::getBloodType));
            break;
    }

    patientTableModel.setRowCount(0);
    for (Patient patient : patientList) {
        patientTableModel.addRow(new Object[]{
            patient.getId(), patient.getName(), patient.getAge(),
            patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType()
        });
    }
    JOptionPane.showMessageDialog(this, "Patients sorted by: " + criteria);
}

// ==========================================
//           DOCTOR MANAGEMENT PANEL
// ==========================================

private JPanel createDoctorPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(245, 245, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setBackground(new Color(245, 245, 245));

    JLabel titleLabel = new JLabel("Doctor Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titlePanel.add(titleLabel);

    JLabel subtitleLabel = new JLabel("Manage patients, doctors, and appointments efficiently");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(128, 128, 128));
    titlePanel.add(subtitleLabel);

    headerPanel.add(titlePanel, BorderLayout.WEST);

    JButton exportButton = createStyledButton("Export to CSV", new Color(108, 117, 125), Color.WHITE);
    exportButton.addActionListener(e -> csvExporter.exportToCSV(doctorTableModel, "Doctor_Data"));
    headerPanel.add(exportButton, BorderLayout.EAST);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Content
    JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    contentPanel.setBackground(Color.WHITE);

    // Form
    JPanel formContainer = new JPanel(new BorderLayout(10, 10));
    formContainer.setBackground(Color.WHITE);
    formContainer.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    JLabel formTitle = new JLabel("Doctor Registration");
    formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    formContainer.add(formTitle, BorderLayout.NORTH);

    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBackground(Color.WHITE);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;

    JTextField idField = new JTextField(15);
    JTextField nameField = new JTextField(15);
    JTextField specializationField = new JTextField(15);
    JTextField contactField = new JTextField(15);

    JTextField[] fields = {idField, nameField, specializationField, contactField};
    for (JTextField field : fields) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
    
    // Label mis input box kadtong mga Doctor ID, Specialization, basta mga ingana.
    // Row 1
    gbc.gridx = 0; gbc.gridy = 0;
    inputPanel.add(new JLabel("Doctor ID:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(idField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Specialization:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(specializationField, gbc);

    // Row 2
    gbc.gridx = 0; gbc.gridy = 1;
    inputPanel.add(new JLabel("Name:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(nameField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Contact:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(contactField, gbc);

    formContainer.add(inputPanel, BorderLayout.CENTER);

    // Mga buttons sa doctor management
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
    buttonPanel.setBackground(Color.WHITE);
    
    // Add doctor button
    JButton addButton = createStyledButton("Add Doctor", new Color(46, 204, 113), Color.WHITE);// green
    // Remove doctor button
    JButton removeButton = createStyledButton("Remove Doctor", new Color(231, 76, 60), Color.WHITE);// red
    // Saecrh by ID button
    JButton searchButton = createStyledButton("Search by ID", new Color(52, 152, 219), Color.WHITE);// blue
    // Display all button
    JButton displayAllButton = createStyledButton("Display All", new Color(189, 195, 199), Color.BLACK);// light gray
    // Clear fields button
    JButton refreshButton = createStyledButton("Clear Fields", new Color(149, 165, 166), Color.WHITE);// gray

    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);
    buttonPanel.add(searchButton);
    buttonPanel.add(displayAllButton);
    buttonPanel.add(refreshButton);

    formContainer.add(buttonPanel, BorderLayout.SOUTH);
    contentPanel.add(formContainer, BorderLayout.NORTH);

    // Table
    String[] columns = {"ID", "Name", "Specialization", "Contact"};
    doctorTableModel = new DefaultTableModel(columns, 0);
    JTable table = new JTable(doctorTableModel);
  
    table.setFillsViewportHeight(true); 
    
    table.setRowHeight(25);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    table.getTableHeader().setBackground(new Color(240, 240, 240));
   
    refreshDoctorTable(); 
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Footer
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
    footerPanel.setBackground(Color.WHITE);
    JButton undoButton = createStyledButton("Undo Last Action", new Color(243, 156, 18), Color.WHITE);
    undoButton.addActionListener(e -> undoLastAction());
    footerPanel.add(undoButton);
    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    // Button Actions
    addButton.addActionListener(e -> {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String contact = contactField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and Name are required!");
            return;
        }

        if (!isValidId(id)) {
            JOptionPane.showMessageDialog(this, "Doctor ID must contain only letters and numbers!");
            return;
        }

        if (doctors.containsKey(id)) {
            JOptionPane.showMessageDialog(this, "Doctor ID already exists!");
            return;
        }

        Doctor doctor = new Doctor(id, name, specialization, contact);
        doctors.put(id, doctor);

        actionHistory.push(new Action() {
            @Override
            public void undo() {
                doctors.remove(id);
                refreshDoctorTable();
            }
        });

        refreshDoctorTable();
        clearDoctorFields(idField, nameField, specializationField, contactField);
        JOptionPane.showMessageDialog(this, "Doctor added successfully!");
    });

    removeButton.addActionListener(e -> {
        String id = idField.getText().trim();
        if (doctors.containsKey(id)) {
            Doctor removedDoctor = doctors.get(id);
            doctors.remove(id);

            actionHistory.push(new Action() {
                @Override
                public void undo() {
                    doctors.put(id, removedDoctor);
                    refreshDoctorTable();
                }
            });

            refreshDoctorTable();
            JOptionPane.showMessageDialog(this, "Doctor removed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Doctor not found!");
        }
    });

    searchButton.addActionListener(e -> {
        String id = idField.getText().trim();
        Doctor doctor = doctors.get(id);
        if (doctor != null) {
            doctorTableModel.setRowCount(0);
            doctorTableModel.addRow(new Object[]{
                doctor.getId(), doctor.getName(),
                doctor.getSpecialization(), doctor.getContact()
            });
        } else {
            JOptionPane.showMessageDialog(this, "Doctor not found!");
        }
    });

    displayAllButton.addActionListener(e -> refreshDoctorTable());

    refreshButton.addActionListener(e -> {
        clearDoctorFields(idField, nameField, specializationField, contactField);
    });

    return mainPanel;
}

// =================================================
//           APPOINTMENT OPERATIONS PANEL
// =================================================

private JPanel createAppointmentPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(245, 245, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setBackground(new Color(245, 245, 245));

    JLabel titleLabel = new JLabel("Appointment Operations");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titlePanel.add(titleLabel);

    JLabel subtitleLabel = new JLabel("Manage patients, doctors, and appointments efficiently");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(128, 128, 128));
    titlePanel.add(subtitleLabel);

    headerPanel.add(titlePanel, BorderLayout.WEST);

    JButton exportButton = createStyledButton("Export to CSV", new Color(108, 117, 125), Color.WHITE);
    exportButton.addActionListener(e -> csvExporter.exportToCSV(appointmentTableModel, "Appointment_Data"));
    headerPanel.add(exportButton, BorderLayout.EAST);

    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Content
    JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    contentPanel.setBackground(Color.WHITE);

    // Form
    JPanel formContainer = new JPanel(new BorderLayout(10, 10));
    formContainer.setBackground(Color.WHITE);
    formContainer.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    JLabel formTitle = new JLabel("Book & Manage Appointments");
    formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    formContainer.add(formTitle, BorderLayout.NORTH);

    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBackground(Color.WHITE);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.33;

    JTextField appointmentIdField = new JTextField(12);
    JTextField patientIdField = new JTextField(12);
    JTextField doctorIdField = new JTextField(12);
    JTextField dateField = new JTextField(15);
    JTextField timeField = new JTextField(15);

    JTextField[] fields = {appointmentIdField, patientIdField, doctorIdField, dateField, timeField};
    for (JTextField field : fields) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
    
    // Label nis input box kadtong mga Appointment ID, Patient ID, basta mga ingana.
    // Row 1
    gbc.gridx = 0; gbc.gridy = 0;
    inputPanel.add(new JLabel("Appointment ID:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(appointmentIdField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("Patient ID:"), gbc);
    gbc.gridx = 3;
    inputPanel.add(patientIdField, gbc);
    gbc.gridx = 4;
    inputPanel.add(new JLabel("Doctor ID:"), gbc);
    gbc.gridx = 5;
    inputPanel.add(doctorIdField, gbc);

    // Row 2
    gbc.gridx = 0; gbc.gridy = 1;
    inputPanel.add(new JLabel("Date (YYY-MM-DD):"), gbc);
    gbc.gridx = 1;
    inputPanel.add(dateField, gbc);
    gbc.gridx = 2;
    inputPanel.add(new JLabel("TIME (HH:MM):"), gbc);
    gbc.gridx = 3;
    inputPanel.add(timeField, gbc);

    formContainer.add(inputPanel, BorderLayout.CENTER);

    // Mga buttons sa Appoinment Operations
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
    buttonPanel.setBackground(Color.WHITE);
    
    // Book appointment button
    JButton bookButton = createStyledButton("Book Appointment", new Color(46, 204, 113), Color.WHITE);// green
    // Cancel appoinment button
    JButton cancelButton = createStyledButton("Cancel Appointment", new Color(231, 76, 60), Color.WHITE);// red
    // View schedule button
    JButton viewScheduleButton = createStyledButton("View Schedule", new Color(52, 152, 219), Color.WHITE);// blue
    // View patient history button
    JButton viewHistoryButton = createStyledButton("View Patient History", new Color(52, 152, 219), Color.WHITE);// light blue
    // Clear fields button
    JButton refreshButton = createStyledButton("Clear Fields", new Color(149, 165, 166), Color.WHITE);// light gray

    buttonPanel.add(bookButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(viewScheduleButton);
    buttonPanel.add(viewHistoryButton);
    buttonPanel.add(refreshButton);

    formContainer.add(buttonPanel, BorderLayout.SOUTH);
    contentPanel.add(formContainer, BorderLayout.NORTH);

    // Table
    String[] columns = {"Appointment ID", "Patient Name", "Doctor Name", "Patient ID", "Doctor ID", "Date", "Time", "Status"};
    appointmentTableModel = new DefaultTableModel(columns, 0);
    JTable table = new JTable(appointmentTableModel);
   
    table.setFillsViewportHeight(true); 
    
    table.setRowHeight(25);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    table.getTableHeader().setBackground(new Color(240, 240, 240));
    
    refreshAppointmentTable(); 
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Footer
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
    footerPanel.setBackground(Color.WHITE);
    JButton undoButton = createStyledButton("Undo Last Action", new Color(243, 156, 18), Color.WHITE);
    undoButton.addActionListener(e -> undoLastAction());
    footerPanel.add(undoButton);
    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    // Button Actions
    bookButton.addActionListener(e -> {
        String appointmentId = appointmentIdField.getText().trim();
        String patientId = patientIdField.getText().trim();
        String doctorId = doctorIdField.getText().trim();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();

        if (appointmentId.isEmpty() || patientId.isEmpty() || doctorId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        if (!isValidId(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment ID must contain only letters and numbers!");
            return;
        }
        if (!isValidId(patientId)) {
            JOptionPane.showMessageDialog(this, "Patient ID must contain only letters and numbers!");
            return;
        }
        if (!isValidId(doctorId)) {
            JOptionPane.showMessageDialog(this, "Doctor ID must contain only letters and numbers!");
            return;
        }

        if (!patients.containsKey(patientId)) {
            JOptionPane.showMessageDialog(this, "Patient not found!");
            return;
        }

        if (!doctors.containsKey(doctorId)) {
            JOptionPane.showMessageDialog(this, "Doctor not found!");
            return;
        }

        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, date, time);
        appointments.add(appointment);

        actionHistory.push(new Action() {
            @Override
            public void undo() {
                appointments.remove(appointment);
                refreshAppointmentTable();
            }
        });

        refreshAppointmentTable();
        appointmentIdField.setText("");
        patientIdField.setText("");
        doctorIdField.setText("");
        dateField.setText("");
        timeField.setText("");
        JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
    });

    cancelButton.addActionListener(e -> {
        String appointmentId = appointmentIdField.getText().trim();
        Appointment toCancel = null;
        for (Appointment apt : appointments) {
            if (apt.getAppointmentId().equals(appointmentId)) {
                toCancel = apt;
                break;
            }
        }

        if (toCancel != null) {
            String oldStatus = toCancel.getStatus();
            toCancel.setStatus("Cancelled");

            Appointment finalToCancel = toCancel;
            actionHistory.push(new Action() {
                @Override
                public void undo() {
                    finalToCancel.setStatus(oldStatus);
                    refreshAppointmentTable();
                }
            });

            refreshAppointmentTable();
            clearAppointmentFields(appointmentIdField, patientIdField, doctorIdField, dateField, timeField);
            JOptionPane.showMessageDialog(this, "Appointment cancelled!");
        } else {
            JOptionPane.showMessageDialog(this, "Appointment not found!");
        }
    });

    viewScheduleButton.addActionListener(e -> refreshAppointmentTable());

    viewHistoryButton.addActionListener(e -> {
        String patientId = patientIdField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Patient ID!");
            return;
        }

        appointmentTableModel.setRowCount(0);
        boolean found = false;
        for (Appointment apt : appointments) {
            if (apt.getPatientId().equals(patientId)) {
                String patientName = patients.containsKey(apt.getPatientId()) ? patients.get(apt.getPatientId()).getName() : "N/A";
                String doctorName = doctors.containsKey(apt.getDoctorId()) ? doctors.get(apt.getDoctorId()).getName() : "N/A";

                appointmentTableModel.addRow(new Object[]{
                    apt.getAppointmentId(), patientName, doctorName,
                    apt.getPatientId(), apt.getDoctorId(),
                    apt.getDate(), apt.getTime(), apt.getStatus()
                });
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No appointment history found for this patient!");
        }
    });

    refreshButton.addActionListener(e -> {
        clearAppointmentFields(appointmentIdField, patientIdField, doctorIdField, dateField, timeField);
    });

    return mainPanel;
}

// =================================================
//           PATIENT QUEUE MANAGEMENT PANEL
// =================================================

private JPanel createQueuePanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(245, 245, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setBackground(new Color(245, 245, 245));

    JLabel titleLabel = new JLabel("Patient Queue Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titlePanel.add(titleLabel);

    JLabel subtitleLabel = new JLabel("Manage patients, doctors, and appointments efficiently");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(128, 128, 128));
    titlePanel.add(subtitleLabel);

    headerPanel.add(titlePanel, BorderLayout.WEST);

    JButton exportButton = createStyledButton("Export to CSV", new Color(108, 117, 125), Color.WHITE);
    exportButton.addActionListener(e -> csvExporter.exportToCSV(queueTableModel, "Queue_Data"));
    headerPanel.add(exportButton, BorderLayout.EAST);

    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Content
    JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    contentPanel.setBackground(Color.WHITE);

    // Form
    JPanel formContainer = new JPanel(new BorderLayout(10, 10));
    formContainer.setBackground(Color.WHITE);
    formContainer.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    JLabel formTitle = new JLabel("Patient Queue & Triage");
    formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    formContainer.add(formTitle, BorderLayout.NORTH);

    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
    inputPanel.setBackground(Color.WHITE);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    JTextField patientIdField = new JTextField(20);
    patientIdField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)
    ));
    patientIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    inputPanel.add(new JLabel("Patient ID to Enqueue:"));
    inputPanel.add(patientIdField);

    formContainer.add(inputPanel, BorderLayout.CENTER);

    // Mga buttons sa Patient Queue Management
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
    buttonPanel.setBackground(Color.WHITE);

    // Add to emergency queue button
    JButton addEmergencyButton = createStyledButton("Add to Emergency Queue", new Color(231, 76, 60), Color.WHITE);// red
    // Add to regular queue button
    JButton addRegularButton = createStyledButton("Add to Regular Queue", new Color(52, 152, 219), Color.WHITE);// blue
    // Procees next patient button
    JButton processNextButton = createStyledButton("Process Next Patient", new Color(46, 204, 113), Color.WHITE);// green
    // View current queue button
    JButton viewQueueButton = createStyledButton("View Current Queue", new Color(189, 195, 199), Color.BLACK);// light gray

    buttonPanel.add(addEmergencyButton);
    buttonPanel.add(addRegularButton);
    buttonPanel.add(processNextButton);
    buttonPanel.add(viewQueueButton);

    formContainer.add(buttonPanel, BorderLayout.SOUTH);
    contentPanel.add(formContainer, BorderLayout.NORTH);

    // Table
    String[] columns = {"Queue Type", "Patient ID", "Patient Name"};
    queueTableModel = new DefaultTableModel(columns, 0);
    JTable table = new JTable(queueTableModel);
   
    table.setFillsViewportHeight(true); 
    
    table.setRowHeight(25);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    table.getTableHeader().setBackground(new Color(240, 240, 240));
    
    refreshQueueTable(); 
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Footer
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
    footerPanel.setBackground(Color.WHITE);
    JButton undoButton = createStyledButton("Undo Last Action", new Color(243, 156, 18), Color.WHITE);
    undoButton.addActionListener(e -> undoLastAction());
    footerPanel.add(undoButton);
    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    // Button Actions
    addEmergencyButton.addActionListener(e -> {
        String id = patientIdField.getText().trim();
        if (patients.containsKey(id)) {
            if (emergencyQueue.contains(id) || regularQueue.contains(id)) {
                JOptionPane.showMessageDialog(this, "Patient already added to the queue!");
                return;
            }
            emergencyQueue.offer(id);
            refreshQueueTable();
            JOptionPane.showMessageDialog(this, "Patient added to Emergency Queue!");
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found!");
        }
    });

    addRegularButton.addActionListener(e -> {
        String id = patientIdField.getText().trim();
        if (patients.containsKey(id)) {
            if (emergencyQueue.contains(id) || regularQueue.contains(id)) {
                JOptionPane.showMessageDialog(this, "Patient already added to the queue!");
                return;
            }
            regularQueue.offer(id);
            refreshQueueTable();
            JOptionPane.showMessageDialog(this, "Patient added to Regular Queue!");
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found!");
        }
    });

    processNextButton.addActionListener(e -> {
        String nextPatientId = null;
        boolean wasEmergency = false;

        if (!emergencyQueue.isEmpty()) {
            nextPatientId = emergencyQueue.poll();
            wasEmergency = true;
        } else if (!regularQueue.isEmpty()) {
            nextPatientId = regularQueue.poll();
        }

        if (nextPatientId != null) {
            String queueType = wasEmergency ? "Emergency" : "Regular";
            Patient patient = patients.get(nextPatientId);
            refreshQueueTable();
            JOptionPane.showMessageDialog(this, "Processing next patient: " + patient.getName() + " from " + queueType + " queue.");
        } else {
            JOptionPane.showMessageDialog(this, "The patient queue is empty.");
        }
    });

    viewQueueButton.addActionListener(e -> refreshQueueTable());

    return mainPanel;
}

// HELPER METHODS
public void refreshQueueTable() {
    queueTableModel.setRowCount(0);

    for (String id : emergencyQueue) {
        String name = patients.containsKey(id) ? patients.get(id).getName() : "N/A";
        queueTableModel.addRow(new Object[]{"Emergency 🚨", id, name});
    }

    for (String id : regularQueue) {
        String name = patients.containsKey(id) ? patients.get(id).getName() : "N/A";
        queueTableModel.addRow(new Object[]{"Regular", id, name});
    }
}

private void refreshPatientTable() {
    patientTableModel.setRowCount(0);
    for (Patient patient : patients.values()) {
        patientTableModel.addRow(new Object[]{
            patient.getId(), patient.getName(), patient.getAge(),
            patient.getGender(), patient.getContact(), patient.getAddress(), patient.getBloodType()
        });
    }
}

private void refreshDoctorTable() {
    doctorTableModel.setRowCount(0);
    for (Doctor doctor : doctors.values()) {
        doctorTableModel.addRow(new Object[]{
            doctor.getId(), doctor.getName(), doctor.getSpecialization(), doctor.getContact()
        });
    }
}

private void refreshAppointmentTable() {
    appointmentTableModel.setRowCount(0);
    for (Appointment apt : appointments) {
        String patientName = patients.containsKey(apt.getPatientId()) ? patients.get(apt.getPatientId()).getName() : "N/A";
        String doctorName = doctors.containsKey(apt.getDoctorId()) ? doctors.get(apt.getDoctorId()).getName() : "N/A";

        appointmentTableModel.addRow(new Object[]{
            apt.getAppointmentId(), patientName, doctorName,
            apt.getPatientId(), apt.getDoctorId(),
            apt.getDate(), apt.getTime(), apt.getStatus()
        });
    }
}

private void clearAppointmentFields(JTextField appointmentId, JTextField patientId, JTextField doctorId, JTextField date, JTextField time) {
    appointmentId.setText("");
    patientId.setText("");
    doctorId.setText("");
    date.setText("");
    time.setText("");
}

private void clearDoctorFields(JTextField id, JTextField name, JTextField specialization, JTextField contact) {
    id.setText("");
    name.setText("");
    specialization.setText("");
    contact.setText("");
}

private void clearPatientFields(JTextField id, JTextField name, JTextField age, JTextField gender, JTextField contact, JTextField address, JTextField bloodtype) {
    
    name.setText(NAME_PLACEHOLDER);
    name.setForeground(PLACEHOLDER_COLOR);
    
    id.setText("");
    age.setText("");
    gender.setText("");
    contact.setText("");
    address.setText("");
    bloodtype.setText("");
}

private void undoLastAction() {
    if (!actionHistory.isEmpty()) {
        actionHistory.pop().undo();
        JOptionPane.showMessageDialog(this, "Last action successfully undone!");
    } else {
        JOptionPane.showMessageDialog(this, "No actions to undo!");
    }
}
// Mao ni ag function aron mo proceed sa main after mag login
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new LoginPage());
}
}   