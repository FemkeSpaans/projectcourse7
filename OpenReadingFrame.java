package ORFfinder;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenReadingFrame{

    static ArrayList<ORF> data = new ArrayList<>();

    public static void main(String[] args) {
        try {
            new GUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public static BufferedReader openFile (String filename) throws FileNotFoundException {
            BufferedReader fileContent = new BufferedReader(new FileReader(filename));
            return fileContent;
        }

        public static void readFile () throws NotDNA {

            try {
                BufferedReader file = openFile(name_file.getText());
                String line;

                while ((line = file.readLine()) != null) {
                    sequence.setText(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (sequence.getText().matches("^[ATCG]*$")) {
                ORFfinder(data);
            } else throw new NotDNA();
        }


        public static ArrayList<ORF> ORFfinder (ArrayList < ORF > data) {

            String seq = sequence.getText().toLowerCase();

            Matcher start = Pattern.compile("(atg)").matcher(seq);
            Matcher stop = Pattern.compile("(taa|tga|tag)").matcher(seq);
            Matcher rstart = Pattern.compile("(gta)").matcher(seq);
            Matcher rstop = Pattern.compile("(aat|agt|gat)").matcher(seq);

            ArrayList<Integer> posstart = new ArrayList<>();
            ArrayList<Integer> posstop = new ArrayList<>();
            ArrayList<Integer> rposstart = new ArrayList<>();
            ArrayList<Integer> rposstop = new ArrayList<>();

            while (start.find()) {
                posstart.add(start.start());
            }
            while (stop.find()) {
                posstop.add(stop.end());
            }
            while (rstop.find()) {
                rposstop.add(rstop.start());
            }
            while (rstart.find()) {
                rposstart.add(rstart.end());
            }

            for (int strt : posstart) {
                int i = 0;
                for (int stp : posstop) {
                    if (stp > strt && stp % 3 == strt % 3) {
                        ORF tempORF = new ORF();
                        tempORF.start = strt + 1;
                        tempORF.stop = stp + 1;
                        posstop.remove(i);
                        tempORF.frame = strt % 3 + 1;
                        tempORF.open_reading_frame_sequence = seq.substring(strt, stp);
                        data.add(tempORF);
                        break;
                    }
                    i++;
                }
            }

            for (int rstrt : rposstart) {
                int x = 0;
                for (int rstp : rposstop) {
                    if (rstp < rstrt && rstp % 3 == rstrt % 3) {
                        ORF tempORF = new ORF();
                        tempORF.start = rstrt + 1;
                        tempORF.stop = rstp + 1;
                        rposstop.remove(x);
                        tempORF.frame = (rstrt % 3 * -1) - 1;
                        StringBuilder sb = new StringBuilder(seq.substring(rstp, rstrt));
                        sb.reverse();
                        tempORF.open_reading_frame_sequence = (sb.toString());
                        data.add(tempORF);
                        break;
                    }
                    x++;
                }
            }
            visualiseORF(data);
            return data;
        }


        static void visualiseORF (ArrayList < ORF > orfs) {
            visualisation.removeAll();
            for (ORF orf : orfs) {
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

        static class ORF {
            int start;
            int stop;
            int frame;
            String open_reading_frame_sequence;
        }

        public static void saveDatabase (ArrayList < ORF > data) {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection_database = DriverManager.getConnection("jdbc:mysql://145.74.104.145:3306/bi212",
                        "bi212", "Aa200012!");
                String query_save = "INSERT INTO Query(HeaderID, Sequence) VALUES(?,?)";
                PreparedStatement values_query_save = connection_database.prepareStatement(query_save);
                values_query_save.setString(1, search_word.getText());
                values_query_save.setString(2, sequence.getText());
                values_query_save.execute();

                String open_reading_frame_save = "INSERT INTO Open_Reading_Frame(Query_HeaderID, ORF, Reading_frame, Start, Stop) VALUES(?,?,?,?,?)";
                for (ORF orf : data) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection_database1 = DriverManager.getConnection("jdbc:mysql://145.74.104.145:3306/bi212",
                            "bi212", "Aa200012!");
                    PreparedStatement values_open_reading_frame_save = connection_database1.prepareStatement(open_reading_frame_save);
                    String orf_seq = orf.open_reading_frame_sequence;
                    int frame = orf.frame;
                    int start = orf.start;
                    int stop = orf.stop;

                    values_open_reading_frame_save.setString(1, search_word.getText());
                    values_open_reading_frame_save.setString(2, orf_seq);
                    values_open_reading_frame_save.setInt(3, frame);
                    values_open_reading_frame_save.setInt(4, start);
                    values_open_reading_frame_save.setInt(5, stop);
                    values_open_reading_frame_save.execute();
                }

            } catch (SQLException | ClassNotFoundException e) {
            }
        }

    }



class NotDNA extends Exception{
    public NotDNA(){
        super("This sequence is not DNA");
    }
}


