import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

/**
 * CSVExporter Class - Handles exporting table data to CSV files.
 * This class ensures modern, standard-compliant CSV output by properly escaping fields.
 */
public class CSVExporter {
    
    private JFrame parentFrame;

    /**
     * Constructor
     * @param parentFrame The main frame for dialog centering/parenting
     */
    public CSVExporter(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * Export a table model to CSV file using a Save File dialog.
     * @param model The table model to export
     * @param tableName The name of the table (used for default filename)
     */
    public void exportToCSV(DefaultTableModel model, String tableName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        
        // Set default filename with ISO-like timestamp for modern filenames
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String defaultFileName = tableName + "_" + timestamp + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            // Add .csv extension if not present
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            // Execute the export process
            if (performExport(model, filePath)) {
                 JOptionPane.showMessageDialog(parentFrame,
                        "Data exported successfully to:\n" + filePath,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                 // Error handled in performExport, just show general message
                 // This block is for redundancy/clarity if performExport was used elsewhere
            }
        }
    }
    
    /**
     * Quick export without file dialog (saves to the specified file path).
     * @param model The table model to export
     * @param filePath The full file path where to save
     * @return true if successful, false otherwise
     */
    public boolean quickExport(DefaultTableModel model, String filePath) {
        return performExport(model, filePath);
    }
    
    /**
     * Core logic for writing the table data to the CSV file.
     * @param model The table model to export
     * @param filePath The full path of the output file
     * @return true if successful, false otherwise
     */
    private boolean performExport(DefaultTableModel model, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            
            // --- 1. Write column headers ---
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.append(escapeCSV(model.getColumnName(i)));
                if (i < model.getColumnCount() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n"); // End of header row
            
            // --- 2. Write data rows ---
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    // Ensure null objects are treated as empty strings for CSV
                    writer.append(escapeCSV(value != null ? value.toString() : ""));
                    if (j < model.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n"); // End of data row
            }
            
            return true;
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error exporting data: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Helper method to escape CSV special characters (Modern CSV Standard).
     * - If a value contains a comma (,), double quote ("), or newline (\n), 
     * the entire value is enclosed in double quotes.
     * - Any double quotes within the value are escaped by doubling them ("").
     * @param value The string value to escape
     * @return Escaped string safe for CSV format
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        // Check for characters that require quoting
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // 1. Escape quotes by doubling them
            value = value.replace("\"", "\"\"");
            // 2. Wrap the whole value in quotes
            return "\"" + value + "\"";
        }
        
        return value;
    }
}