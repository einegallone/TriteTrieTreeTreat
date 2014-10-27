package frugalinstant;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import trietree.TrieTree;

/**
 * Frugal Instant - The Poor Man's Google Instant
 * 
 * This GUI allows a user to import String Keys and Integer Values to produce
 * autocomplete entries for words or phrases with similar prefixes. The entries
 * are ranked in decreasing order by the Integer Value.
 * 
 * The GUI can accept such data in the form of CSVs where each row consists of
 * "Key\,Value" with "\," as the delimiter. It can also export its data to a CSV
 * with the mentioned format.
 */
public class MainAppFrame extends JFrame
{
    /**
     * GUI stuffs
     */
    private static final long serialVersionUID = 8711387955280095124L;
    private final JTextField textFieldFilepathInput = new JTextField();
    private final JLabel lblFilepathInputInstructions = new JLabel(
                                                                   "<html>Input filepath of CSV that contains keys and values to be populated into program. Each row in the file must contain two entries: a string for retrieval and an integer that indicates a rank for the key to appear in the autocomplete box. The two entries must be delimited by \"\\,\" rather than just the comma (do not end lines with the delimiter). CSVs with any invalid row will fail to import without affecting existing data. Existing keys will be ignored by default; other keys will be added.</html>");
    private final JPanel panelGenerateTree = new JPanel();
    private final JButton btnImportCSV = new JButton("Import");
    private final JButton btnClearTree = new JButton("Clear Data");
    private final JButton btnExportCSV = new JButton("Export");
    private final JButton btnFilepath = new JButton("...");
    private final JLabel lblCsv = new JLabel("CSV Path");
    private final JCheckBox chckbxOverwriteValues = new JCheckBox(
                                                                  "<html>For conflicting keys, overwrite existing values with imported data.</html>");
    private final JTextField textFieldSearch = new JTextField();
    private final JPanel panelAutocomplete = new JPanel();
    private final JLabel lblSearch = new JLabel("Search");
    private final JButton btnClearSearch = new JButton("Clear");
    private final JTable tableAutocomplete = new JTable();
    private final JPanel panelStatus = new JPanel();
    private final JLabel lblStatus = new JLabel("");

    /**
     * Private members
     */
    private TrieTree<Integer> tt = new TrieTree<Integer>();
    private DefaultTableModel tableEntries = new DefaultTableModel(new Object[][]
    {}, new String[]
    { "Key", "Value" })
    {
        private static final long serialVersionUID = -5625250449981871387L;
        Class[] columnTypes = new Class[]
        { String.class, Integer.class };

        public Class getColumnClass(int columnIndex)
        {
            return columnTypes[columnIndex];
        }

        boolean[] columnEditables = new boolean[]
        { false, true };

        public boolean isCellEditable(int row, int column)
        {
            return columnEditables[column];
        }
    };
    private final JLabel lblAutocompleteInstructions = new JLabel(
                                                                  "<html>Import data first. Then type in any phrase into the search. Autocomplete terms will appear in the table below in the order of the value ranking. You can update the rank in the table.</html>");

    // Column names in the autocomplete table.
    private static enum Columns
    {
        KEY, VALUE
    }

    /**
     * ENTRY POINT: Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    MainAppFrame frame = new MainAppFrame();
                    frame.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainAppFrame()
    {
        panelAutocomplete.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                                                     "Frugal Instant", TitledBorder.LEADING,
                                                     TitledBorder.TOP, null, null));
        panelAutocomplete.setLayout(null);
        textFieldSearch.setBounds(53, 61, 378, 20);
        textFieldSearch.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                return;
            }

            public void keyReleased(KeyEvent e)
            {
                // Empty table.
                tableEntries.setRowCount(0);

                // Retrieve Keys and Values from the trie tree and put them into
                // the new table.
                if (!textFieldSearch.getText().isEmpty())
                {
                    HashMap<String, Integer> kvc = tt.keyValueCollectionWithPrefix(textFieldSearch.getText());

                    // Create a Value comparator to sort map by Value in
                    // descending order.
                    class ValueComparator implements Comparator<String>
                    {
                        Map<String, Integer> base;

                        public ValueComparator(Map<String, Integer> base)
                        {
                            this.base = base;
                        }

                        public int compare(String a, String b)
                        {
                            // Returning 0 would merge keys
                            if (base.get(a) >= base.get(b))
                            {
                                return -1;
                            }
                            else
                            {
                                return 1;
                            }
                        }
                    }

                    // Populate the TreeMap.
                    ValueComparator bvc = new ValueComparator(kvc);
                    TreeMap<String, Integer> sorted_kvc = new TreeMap<String, Integer>(bvc);
                    sorted_kvc.putAll(kvc);

                    // Populate the table entries.
                    for (Map.Entry<String, Integer> entry : sorted_kvc.entrySet())
                    {
                        tableEntries.addRow(new Object[]
                        { entry.getKey(), entry.getValue() });
                    }
                }

                // Set the existing table to show the new entries.
                tableAutocomplete.setModel(tableEntries);

                return;
            }

            public void keyTyped(KeyEvent e)
            {
                return;
            }
        });

        panelAutocomplete.add(textFieldSearch);
        textFieldSearch.setToolTipText("Search for..");
        textFieldSearch.setColumns(10);
        lblSearch.setBounds(10, 64, 33, 14);

        panelAutocomplete.add(lblSearch);
        btnClearSearch.setToolTipText("Clear search box");
        btnClearSearch.setBounds(441, 60, 63, 23);
        btnClearSearch.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                // Empty autocomplete fields.
                textFieldSearch.setText("");
                tableEntries.setRowCount(0);
                tableAutocomplete.setModel(tableEntries);

                return;
            }
        });

        panelAutocomplete.add(btnClearSearch);
        tableAutocomplete.setModel(tableEntries);
        tableAutocomplete.setBounds(10, 54, 494, 177);

        // Add listener to table
        Action actionTableCellListener = new AbstractAction()
        {
            private static final long serialVersionUID = -9180513430057463324L;

            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener) e.getSource();

                // If column is not the Value column, then exit. (Shouldn't be
                // possible with default table settings.)
                if (tcl.getColumn() != 1)
                    return;

                // If empty, revert to old Value.
                if (tcl.getNewValue() == null)
                {
                    tcl.getTable().setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
                    return;
                }

                // Check if Value has changed.
                if (tcl.getOldValue() == tcl.getNewValue())
                    return;

                // Otherwise attempt to update the Key to the new Value.
                if (tt.update((String) tcl.getTable().getValueAt(tcl.getRow(), Columns.KEY.ordinal()),
                              (Integer) tcl.getTable().getValueAt(tcl.getRow(), Columns.VALUE.ordinal())))
                {
                    lblStatus.setText("Successfully updated Key ["
                        + (String) tcl.getTable().getValueAt(tcl.getRow(), Columns.KEY.ordinal())
                        + "] from Value [" + tcl.getOldValue() + "] to [" + tcl.getNewValue() + "].");
                }
                else
                {
                    lblStatus.setText("Failed to update Key ["
                        + (String) tcl.getTable().getValueAt(tcl.getRow(), Columns.KEY.ordinal())
                        + "] from Value [" + tcl.getOldValue() + "] to [" + tcl.getNewValue() + "].");
                }
            }
        };
        TableCellListener tcl = new TableCellListener(tableAutocomplete, actionTableCellListener);

        JScrollPane scrollPaneAutocomplete = new JScrollPane(tableAutocomplete);
        scrollPaneAutocomplete.setLocation(10, 92);
        scrollPaneAutocomplete.setSize(494, 139);
        panelAutocomplete.add(scrollPaneAutocomplete);
        lblAutocompleteInstructions.setVerticalAlignment(SwingConstants.TOP);
        lblAutocompleteInstructions.setBounds(10, 21, 494, 28);

        panelAutocomplete.add(lblAutocompleteInstructions);
        panelGenerateTree.setBounds(10, 11, 514, 167);
        getContentPane().add(panelGenerateTree);
        panelGenerateTree.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Data",
                                                     TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelGenerateTree.setLayout(null);
        textFieldFilepathInput.setBounds(66, 105, 248, 20);
        panelGenerateTree.add(textFieldFilepathInput);
        textFieldFilepathInput.setToolTipText("Enter filepath with keys and values for tree population.");
        textFieldFilepathInput.setColumns(10);
        initGUI();
    }

    /**
     * Initialize the GUI components but do not start the frame. This method
     * could be public if desired.
     */
    private void initGUI()
    {
        lblSearch.setLabelFor(textFieldSearch);
        setTitle("Frugal Instant - The Poor Man's Google Instant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 550, 550);
        getContentPane().setLayout(null);
        lblCsv.setLabelFor(textFieldFilepathInput);
        lblFilepathInputInstructions.setBounds(10, 22, 494, 72);
        panelGenerateTree.add(lblFilepathInputInstructions);
        lblFilepathInputInstructions.setVerticalAlignment(SwingConstants.TOP);
        lblFilepathInputInstructions.setLabelFor(textFieldFilepathInput);
        btnImportCSV.setToolTipText("Import CSV");
        btnImportCSV.setBounds(364, 104, 65, 23);
        btnImportCSV.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Check user input
                if (textFieldFilepathInput.getText().length() == 0)
                {
                    lblStatus.setText("Filepath error: I can't let you do that, Star Fox! Your filepath field is empty!");
                    return;
                }

                long startTime = new Date().getTime();
                boolean populateTreeStatus = populateTree(textFieldFilepathInput.getText(), tt, chckbxOverwriteValues.isSelected());
                long endTime = new Date().getTime();
                
                if (populateTreeStatus)
                    lblStatus.setText("Successfully imported data in " + (endTime - startTime) + " ms.");
                else
                    lblStatus.setText("Failed to import data in " + (endTime - startTime) + " ms.");
            }
        });

        panelGenerateTree.add(btnImportCSV);
        btnClearTree.setToolTipText("Delete current dataset");
        btnClearTree.setBounds(364, 133, 140, 23);
        btnClearTree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                                                                 "Are you sure you want to clear your data?",
                                                                 "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION)
                {
                    tt.removeAll();
                    lblStatus.setText("Data all cleared.");
                }
            }
        });

        panelGenerateTree.add(btnClearTree);
        btnExportCSV.setToolTipText("Export CSV");
        btnExportCSV.setBounds(439, 104, 65, 23);
        btnExportCSV.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Check user input
                if (textFieldFilepathInput.getText().length() == 0)
                {
                    lblStatus.setText("Filepath error: I can't let you do that, Star Fox! Your filepath field is empty!");
                    return;
                }

                if (tt.isEmpty())
                {
                    lblStatus.setText("No data to export.");
                    return;
                }

                File file = new File(textFieldFilepathInput.getText());
                if (file.exists())
                {
                    if (file.isDirectory())
                    {
                        lblStatus.setText("Invalid filepath. This is a directory.");
                        return;
                    }

                    int dialogResult = JOptionPane.showConfirmDialog(null, "Overwrite existing file?",
                                                                     "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION)
                    {
                        return;
                    }
                }
                else
                {
                    try
                    {
                        file.createNewFile();
                    }
                    catch (IOException err)
                    {
                        err.printStackTrace();
                        lblStatus.setText("Failed to create file.");
                        return;
                    }
                }

                long startTime = new Date().getTime();
                boolean exportTreeStatus = exportTree(file);
                long endTime = new Date().getTime();

                if (exportTreeStatus)
                    lblStatus.setText("Successfully exported data in " + (endTime - startTime) + " ms.");
                else
                    lblStatus.setText("Failed to export data in " + (endTime - startTime) + " ms.");
            }
        });

        panelGenerateTree.add(btnExportCSV);
        btnFilepath.setToolTipText("Browse...");
        btnFilepath.setBounds(324, 104, 30, 23);
        btnFilepath.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                // Pop up with directory browser dialog
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated Values", "csv",
                                                                             "txt");
                chooser.setFileFilter(filter);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                // Set the filepath text field if the user succeeds in getting
                // a filepath
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    textFieldFilepathInput.setText(chooser.getSelectedFile().getAbsolutePath());
                }
                else if (returnVal == JFileChooser.ERROR_OPTION)
                {
                    lblStatus.setText("There was an error in getting the filepath.");
                }
            }
        });

        panelGenerateTree.add(btnFilepath);
        lblCsv.setBounds(10, 108, 46, 14);

        panelGenerateTree.add(lblCsv);
        chckbxOverwriteValues.setVerticalAlignment(SwingConstants.TOP);
        chckbxOverwriteValues.setBounds(10, 132, 344, 20);

        panelGenerateTree.add(chckbxOverwriteValues);
        panelAutocomplete.setBounds(10, 189, 514, 242);

        getContentPane().add(panelAutocomplete);
        panelStatus.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null,
                                               null));
        panelStatus.setBounds(10, 442, 514, 59);

        getContentPane().add(panelStatus);
        panelStatus.setLayout(null);
        lblStatus.setForeground(Color.BLUE);
        lblStatus.setVerticalAlignment(SwingConstants.TOP);
        lblStatus.setBounds(10, 21, 494, 27);

        panelStatus.add(lblStatus);
    }

    /**
     * Starts the already initialized frame, making it visible and ready to
     * interact with the user.
     */
    private void start()
    {
        setResizable(false);
        setVisible(true);
    }

    /**
     * This function exports the trie tree to a CSV. Each row contains
     * "String\,Integer", where delimiter is "\,".
     * 
     * @param file
     *            Output CSV.
     * @return True if the data was successfully exported. False if the data
     *         failed to be written.
     */
    private boolean exportTree(final File file)
    {
        boolean rc = true; // Return code
        HashMap<String, Integer> kvc = tt.allKeyValues();

        FileWriter fw = null;
        BufferedWriter bw = null;

        try
        {
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            String content = "";

            // Each entry needs to be delimited by "\,".
            for (Map.Entry<String, Integer> entry : kvc.entrySet())
            {
                content = content + entry.getKey() + "\\," + entry.getValue() + "\r\n";
            }

            bw.write(content);
        }
        catch (IOException err)
        {
            err.printStackTrace();
            lblStatus.setText("Failed to write to file.");
            rc = false;
        }
        finally
        {
            try
            {
                bw.close();
            }
            catch (IOException err)
            {
                err.printStackTrace();
                lblStatus.setText("Failed to close file.");
                rc = false;
            }
        }

        return rc;
    }

    /**
     * This function takes a filepath for a CSV and populates a trie tree. Each
     * row in the CSV is expected to take the format "String\,Integer". If there
     * are any misformatted entries, the tree does not get populated. If there
     * are repeated keys, the last Integer will be used.
     * 
     * @param filepath
     *            Input CSV.
     * @param tt
     *            Trie tree to populate.
     * @param update
     *            If true, update any existing values. If false, skip existing
     *            entries.
     * @return True if trie tree was successfully populated. False if there was
     *         an error reading from the CSV or there are misformatted entries.
     */
    private boolean populateTree(final String filepath, TrieTree<Integer> tt, final boolean update)
    {
        boolean rc = true; // Return code
        BufferedReader buffer = null;

        HashMap<String, Integer> kvc = new HashMap<String, Integer>();

        try
        {
            FileReader input = new FileReader(filepath);
            buffer = new BufferedReader(input);
            String line = null;
            line = buffer.readLine();

            // Extract contents from CSV and put them in a map first.
            // Once all rows have been validated, insert the entries into the
            // tree.
            while (line != null)
            {
                // We expect each line to contain two entries, delimited by
                // "\,".
                String[] keyValue = line.split("\\\\,");
                if (keyValue.length == 2)
                {
                    if (keyValue[0] != null && keyValue[1] != null)
                    {
                        // Remove any whitespaces from the integer string.
                        keyValue[1] = keyValue[1].replaceAll("\\s+", "");
                        Integer value = Integer.parseInt(keyValue[1]);

                        kvc.put(keyValue[0], value);
                    }
                    else
                    {
                        rc = false;
                        break;
                    }
                }
                else
                {
                    rc = false;
                    break;
                }

                line = buffer.readLine();
            }

            // rc is true if there wasn't an issue with the CSV format, so
            // populate the tree from the HashMap.
            if (rc)
            {
                for (Map.Entry<String, Integer> entry : kvc.entrySet())
                {
                    // If it fails to put, then attempt to update if user
                    // wants to update data.
                    if (!tt.put(entry.getKey(), entry.getValue()) && update)
                        // If we fail here, just move on.
                        tt.update(entry.getKey(), entry.getValue());
                }
            }
        }
        catch (FileNotFoundException e)
        {
            // Error in opening the file.
            e.printStackTrace();
            rc = false;
        }
        catch (IOException e)
        {
            // Error in reading from the file.
            e.printStackTrace();
            rc = false;
        }
        catch (NumberFormatException e)
        {
            // Error in the number format of the file.
            e.printStackTrace();
            rc = false;
        }
        finally
        {
            try
            {
                if (buffer != null)
                    buffer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                rc = false;
            }
        }

        return rc;
    }
}
