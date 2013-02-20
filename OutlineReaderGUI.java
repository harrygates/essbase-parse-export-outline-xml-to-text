package outlineReader;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.awt.Desktop;

public class OutlineReaderGUI extends JDialog {
    private JPanel contentPane;
    private JTextField inputFile;
    private JTextField outputFile;
    private JButton butInput;
    private JButton butOutput;
    private JTextField delimiter;
    private JButton butCancel;
    private JButton butRun;
    private JButton butBlog;

    public OutlineReaderGUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(butInput);

        butInput.addActionListener(new OpenClass());
        butOutput.addActionListener(new SaveClass());

        butRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        butCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        butBlog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url ="http://harrygates-essbase-blog.blogspot.com";
                try {
                    Desktop dt = Desktop.getDesktop();
                    URI uri = new URI(url);
                    dt.browse(uri.resolve(uri));
                } catch (IOException IOex) {
                    infoBox(IOex.getMessage(), "Error accessing " + url, true);
                } catch (java.net.URISyntaxException URIex) {
                    infoBox(URIex.getMessage(), "Error accessing " + url, true);
                }
            }
        });
    }

    class OpenClass implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            int option = chooser.showOpenDialog(OutlineReaderGUI.this);
            if(option == JFileChooser.APPROVE_OPTION) {
                inputFile.setText((chooser.getSelectedFile()!=null ? chooser.getSelectedFile().getPath()
                        : "nothing"));
            }

            if(option == JFileChooser.CANCEL_OPTION) {
                inputFile.setText("You canceled.");
            }
        }
    }

    class SaveClass implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            int option = chooser.showSaveDialog(OutlineReaderGUI.this);
            if(option == JFileChooser.APPROVE_OPTION) {
                outputFile.setText((chooser.getSelectedFile() != null ? chooser.getSelectedFile().getPath() : "nothing"));
            }

            if(option == JFileChooser.CANCEL_OPTION) {
                outputFile.setText("You canceled.");
            }
        }
    }

    private void onOK() {
        String input = inputFile.getText();
        String output = outputFile.getText();
        String del = delimiter.getText();
        String missing = "";

        if ("".equals(input)) {
           missing = "select a MaxL-generated xml file";
        } else if ("".equals(output)) {
           missing = "select input an output text file";
        } else if ("".equals(del)) {
           missing = "input a delimiter";
        }

        if ("".equals(missing)) {
            MaxLExportOutlineParseXML parser = new MaxLExportOutlineParseXML(input, output, del);
            infoBox("time to run: " + parser.getElapsedTime(), "Elapsed Time", false);
            //dispose();
        } else {
            infoBox("Please " + missing, "Required Field(s) not populated", true);
        }
    }

    private void onCancel() {
        dispose();
    }

    private void infoBox(String message, String location, boolean bError) {
        if (bError) {
            JOptionPane.showMessageDialog(null, message, location, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, message, location, JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public static void main(String[] args) {
        OutlineReaderGUI dialog = new OutlineReaderGUI();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
