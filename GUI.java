package ORFfinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {

    String sequence_string = "";

    static JButton save_button = new JButton(), analyse_button = new JButton(), browse_button = new JButton();
    static JTextField name_file, search_word;
    public static JPanel visualisation;
    static JLabel file_name_entered, orf_found, header_name, sequence_entered;
    static JTextArea sequence;

    boolean pressed = false;

    public static void frame(){
        GUI frame_gui = new GUI();
        frame_gui.setTitle("Open Reading Frame Predictor");
        frame_gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_gui.setSize(800, 800);
        frame_gui.setLayout(null);
        frame_gui.gui();
        frame_gui.setVisible(true);
    }

    public void gui(){
        Container window = getContentPane();

        // label for the file textfield
        file_name_entered = new JLabel();
        file_name_entered.setBounds(20, 10, 100, 20);
        file_name_entered.setText("Enter a filename:");
        window.add(file_name_entered);

        // textfield to enter a file
        name_file = new JTextField();
        name_file.setBounds(20, 40, 500, 20);
        window.add(name_file);

        // button to browse files with
        browse_button.setBounds(550, 40, 150, 20);
        browse_button.setText("Browse files");
        window.add(browse_button);
        browse_button.addActionListener(this);

        // label for the header textfield
        header_name = new JLabel();
        header_name.setBounds(20, 70, 100, 20);
        header_name.setText("Enter a header:");
        window.add(header_name);

        // button to browse files with
        save_button.setBounds(550, 100, 150, 20);
        save_button.setText("Save to database");
        window.add(save_button);
        save_button.addActionListener(this);

        // label for the text area
        sequence_entered = new JLabel();
        sequence_entered.setBounds(20, 120, 250, 20);
        sequence_entered.setText("Enter a sequence:");
        window.add(sequence_entered);

        // create textfield in which to add a word to search for
        search_word = new JTextField();
        search_word.setBounds(20, 100, 500, 20);
        window.add(search_word);

        // create a jbutton to analyse the chosen file with
        analyse_button.setBounds(610, 420, 150, 20);
        analyse_button.setText("Analyse sequence");
        window.add(analyse_button);
        analyse_button.addActionListener(this);

        // create textarea in which to display sequence about the selected file
        sequence = new JTextArea();
        sequence.setBounds(20, 150, 740, 260);
        window.add(sequence);

        // create a jlabel which says to enter a file
        orf_found = new JLabel();
        orf_found.setBounds(20, 420, 300, 20);
        orf_found.setText("Open reading frames found in sequence:");
        window.add(orf_found);

        // create jpanel in which to display the orf's of the sequence which was given
        visualisation = new JPanel();
        visualisation.setBounds(20, 450, 740, 260);
        visualisation.setBackground(Color.WHITE);
        window.add(visualisation);

    }

    static void visualiseORF (ArrayList<ORFfinder.OpenReadingFrame.ORF> orfs) {
        visualisation.removeAll();
        for (ORFfinder.OpenReadingFrame.ORF orf : orfs) {
            String orfstring = "";
            if (orf.open_reading_frame_sequence.length() > 50) {
                orfstring = "Reading frame: " + orf.frame + " | postition: " + orf.start + ":" + orf.stop + " | sequence: " + orf.open_reading_frame_sequence.substring(0, 47) + "...";
            } else {
                orfstring = "Reading frame: " + orf.frame + " | postition: " + orf.start + ":" + orf.stop + " | sequence: " + orf.open_reading_frame_sequence;
            }
            JLabel orftoadd = new JLabel(orfstring);
            visualisation.add(orftoadd);
            visualisation.revalidate();
            visualisation.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser select_file;

        File selectedFile;
        int reply;

        if (e.getSource() == browse_button) {
            select_file = new JFileChooser();
            reply = select_file.showOpenDialog(this);
            if (reply == JFileChooser.APPROVE_OPTION) {
                selectedFile = select_file.getSelectedFile();
                name_file.setText(selectedFile.getAbsolutePath());
            }
        }
        if (e.getSource() == analyse_button){
            try {
                pressed = true;
                sequence_string = ORFfinder.OpenReadingFrame.readFile(name_file.getText());
                sequence.setText(sequence_string);
            } catch (ORFfinder.NotDNA notDNA) {
                notDNA.printStackTrace();
            }
        }

        if (e.getSource() == save_button){
            if (pressed){
                String henk = search_word.getText()
;                ORFfinder.OpenReadingFrame.saveDatabase(OpenReadingFrame.data, henk, sequence_string);
            }else try {
                throw new PressedBefore();
            } catch (PressedBefore pressedBefore) {
                pressedBefore.printStackTrace();
            }

        }
    }
}

class PressedBefore extends Exception{
    public PressedBefore() { super ("The save button cannot be pressed before the analyse button");
    }
}
