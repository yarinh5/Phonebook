import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Map;


public class PhonePannel extends JPanel {
    private JButton addCon, deleteCon, updateCon, importButton, export;
    private JTable table;
    private JLabel searchLabel;
    private JTextField searchTxt;
    private JScrollPane scrollPane;
    private PhoneBook book;
    private DefaultTableModel tbm;

    public PhonePannel() {
        // Buttons
        addCon = new JButton("Add Contact");
        deleteCon = new JButton("Delete Contact");
        updateCon = new JButton("Update Contact");
        importButton = new JButton("Import");
        export = new JButton("Export");

        // Label
        searchLabel = new JLabel("search: ");

        // Text-box
        searchTxt = new JTextField(10);

        // Create table
        table = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Serif", Font.BOLD, 20));

        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);

        book = new PhoneBook();
        buildPannel();
    }

    private void buildPannel() {
        // Add columns to table
        String[] columnNames = {"First Name", "Last Name", "Telephone"};
        tbm = (DefaultTableModel) this.table.getModel();
        for (String col : columnNames) {
            tbm.addColumn(col);
        }
        // Add the buttons to controller
        JPanel controls = new JPanel();
        GridLayout experimentLayout = new GridLayout(3, 0);
        controls.setLayout(experimentLayout);
        add(searchLabel);
        add(searchTxt);
        add(addCon);
        add(deleteCon);
        add(updateCon);
        add(importButton);
        add(export);
        add(scrollPane);

        // Control listener
        searchTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTxtKeyReleased();
            }
        });
        ControlsListener l = new ControlsListener();
        addCon.addActionListener(l);
        deleteCon.addActionListener(l);
        updateCon.addActionListener(l);
        export.addActionListener(l);
        importButton.addActionListener(l);
        this.setBackground(Color.WHITE);
    }

    private void searchTxtKeyReleased() {
        PhoneBook newBook = new PhoneBook(book.Search(searchTxt.getText()));
        UpdateTable(newBook, tbm);
    }

    private void UpdateTable(PhoneBook map, DefaultTableModel dtm) {
        if (dtm.getRowCount() > 0) {
            for (int i = dtm.getRowCount() - 1; i > -1; i--) {
                dtm.removeRow(i);
            }
        }

        String[][] data = new String[map.getBook().size()][3];
        int i = 0;
        for (Map.Entry<String, String> entry : map.getBook().entrySet()) {
            data[i][0] = entry.getKey().split(" ")[0];
            data[i][1] = entry.getKey().split(" ")[1];
            data[i][2] = entry.getValue();
            dtm.insertRow(i, data[i]);
            i++;
        }

        this.table.setModel(dtm);
        dtm.fireTableDataChanged();
    }

    private String getSelectedTelephone() {
        int row = this.table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return (String) this.table.getValueAt(row, 2);
    }

    private String getSelectedFirstName() {
        int row = this.table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return (String) this.table.getValueAt(row, 0);
    }

    private String getSelectedLastName() {
        int row = this.table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return (String) this.table.getValueAt(row, 1);
    }

    private class ControlsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            searchTxt.setText("");
            if (e.getSource() == addCon) {
                addAction();
            } else if (e.getSource() == deleteCon) {
                deleteAction();
            } else if (e.getSource() == updateCon) {
                updateAction();
            } else if (e.getSource() == importButton) {
                importAction();
            } else if (e.getSource() == export) {
                exportAction();
            }
        }

        private void importAction() {
            IOactions io = new IOactions();
            JFileChooser chooser = new JFileChooser(new File(String.valueOf(FileSystems.getDefault().getPath("."))));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filepath = chooser.getSelectedFile().getName();

                try {
                    book.setBook(io.Import(filepath));
                    UpdateTable(book, tbm);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getParent(), "File not found", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Error ex) {
                    JOptionPane.showMessageDialog(getParent(), ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exportAction() {
            IOactions io = new IOactions();
            JFileChooser fileChooser = new JFileChooser(new File(String.valueOf(FileSystems.getDefault().getPath("."))));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(getParent());
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String saveAs = fileToSave.getAbsolutePath();
                try {
                    io.Export(book.getBook(), saveAs);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getParent(), "File not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void updateAction() {
            String oldTel = getSelectedTelephone();
            if (oldTel == null) {
                JOptionPane.showMessageDialog(getParent(), "You should select contact to edit", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String fname = getSelectedFirstName();
                String lname = getSelectedLastName();
                String oldFullName = String.format("%s %s", fname, lname);
                JTextField fnameTxt = new JTextField(fname);
                JTextField lnameTxt = new JTextField(lname);
                JTextField telephoneTxt = new JTextField(oldTel);
                Object[] message = {
                        "First Name: ", fnameTxt,
                        "Last Name: ", lnameTxt,
                        "Telephone :", telephoneTxt,
                };
                int option = JOptionPane.showConfirmDialog(getParent(), message, "Add a new contact", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    fname = fnameTxt.getText();
                    lname = lnameTxt.getText();
                    String fullName = String.format("%s %s", fname, lname);
                    String telephone = telephoneTxt.getText();
                    if (book.isExist(fullName) && !fullName.equals(oldFullName)) {
                        JOptionPane.showMessageDialog(getParent(), String.format("%s already exist in the phonebook", fullName), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!oldFullName.equals(fullName)) {
                            book.DeleteContact(oldFullName);
                        }
                        try {
                            book.AddContact(telephone, fname, lname);
                            UpdateTable(book, tbm);
                        } catch (Error ex) {
                            JOptionPane.showMessageDialog(getParent(), ex, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        private void deleteAction() {
            String fullName = String.format("%s %s", getSelectedFirstName(), getSelectedLastName());
            if (getSelectedTelephone() == null) {
                JOptionPane.showMessageDialog(getParent(), "You should select contact to delete", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                book.DeleteContact(fullName);
                UpdateTable(book, tbm);
            }
        }

        private void addAction() {
            JTextField fnameTxt = new JTextField();
            JTextField lnameTxt = new JTextField();
            JTextField telephoneTxt = new JTextField();
            Object[] message = {
                    "First Name: ", fnameTxt,
                    "Last Name: ", lnameTxt,
                    "Telephone :", telephoneTxt,
            };
            int option = JOptionPane.showConfirmDialog(getParent(), message, "Add a new contact", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String fname = fnameTxt.getText();
                String lname = lnameTxt.getText();
                String telephone = telephoneTxt.getText();
                String fullName = String.format("%s %s", fname, lname);
                if (book.isExist(fullName)) {
                    JOptionPane.showMessageDialog(null, String.format("Contact %s is already exist", fullName), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        book.AddContact(telephone, fname, lname);
                        UpdateTable(book, tbm);
                    } catch (Error ex) {
                        JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        }
    }
}


