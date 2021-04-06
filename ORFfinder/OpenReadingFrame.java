package ORFfinder;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenReadingFrame{

    static ArrayList<ORF> data = new ArrayList<>();

        public static BufferedReader openFile (String filename) throws FileNotFoundException {
            return new BufferedReader(new FileReader(filename));
        }

        public static String readFile (String input_file) throws NotDNA {

        StringBuilder text_sequence = new StringBuilder();

            try {
                BufferedReader file = openFile(input_file);
                String line;

                while ((line = file.readLine()) != null) {
                    text_sequence.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (text_sequence.toString().matches("^([ATCG]*$|[atgc]*$)")) {
                ORF_finder(data, text_sequence.toString());
            } else throw new NotDNA();
            return text_sequence.toString();
        }


        public static void ORF_finder(ArrayList < ORF > data, String text_sequence) {

            Matcher start_codon = Pattern.compile("(atg)").matcher(text_sequence);
            Matcher stop_codons = Pattern.compile("(taa|tga|tag)").matcher(text_sequence);
            Matcher reverse_start_codon = Pattern.compile("(gta)").matcher(text_sequence);
            Matcher reverse_stop_codons = Pattern.compile("(aat|agt|gat)").matcher(text_sequence);

            ArrayList<Integer> position_start = new ArrayList<>();
            ArrayList<Integer> position_stop = new ArrayList<>();
            ArrayList<Integer> reverse_position_start = new ArrayList<>();
            ArrayList<Integer> reverse_position_stop = new ArrayList<>();

            while (start_codon.find()) {
                position_start.add(start_codon.start());
            }
            while (stop_codons.find()) {
                position_stop.add(stop_codons.end());
            }
            while (reverse_stop_codons.find()) {
                reverse_position_stop.add(reverse_stop_codons.start());
            }
            while (reverse_start_codon.find()) {
                reverse_position_start.add(reverse_start_codon.end());
            }

            for (int start : position_start) {
                int i = 0;
                for (int stop : position_stop) {
                    if (stop > start && stop % 3 == start % 3) {
                        ORF tempORF = new ORF();
                        tempORF.start = start + 1;
                        tempORF.stop = stop + 1;
                        position_stop.remove(i);
                        tempORF.frame = start % 3 + 1;
                        tempORF.open_reading_frame_sequence = text_sequence.substring(start, stop);
                        data.add(tempORF);
                        break;
                    }
                    i++;
                }
            }

            for (int reverse_start : reverse_position_start) {
                int x = 0;
                for (int reverse_stop : reverse_position_stop) {
                    if (reverse_stop < reverse_start && reverse_stop % 3 == reverse_start % 3) {
                        ORF tempORF = new ORF();
                        tempORF.start = reverse_start + 1;
                        tempORF.stop = reverse_stop + 1;
                        reverse_position_stop.remove(x);
                        tempORF.frame = (reverse_start % 3 * -1) - 1;
                        StringBuilder sb = new StringBuilder(text_sequence.substring(reverse_stop, reverse_start));
                        sb.reverse();
                        tempORF.open_reading_frame_sequence = (sb.toString());
                        data.add(tempORF);
                        break;
                    }
                    x++;
                }
            }
            ORFfinder.GUI.visualiseORF(data);
        }


    static class ORF {
            int start;
            int stop;
            int frame;
            String open_reading_frame_sequence;
        }

        public static void saveDatabase (ArrayList < ORF > data, String search_word, String text_sequence) {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection_database = DriverManager.getConnection("jdbc:mysql://145.74.104.145:3306/bi212",
                        "bi212", "Aa200012!");
                String query_save = "INSERT INTO Query(HeaderID, Sequence) VALUES(?,?)";
                PreparedStatement values_query_save = connection_database.prepareStatement(query_save);
                values_query_save.setString(1, search_word);
                values_query_save.setString(2, text_sequence);
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

                    values_open_reading_frame_save.setString(1, search_word);
                    values_open_reading_frame_save.setString(2, orf_seq);
                    values_open_reading_frame_save.setInt(3, frame);
                    values_open_reading_frame_save.setInt(4, start);
                    values_open_reading_frame_save.setInt(5, stop);
                    values_open_reading_frame_save.execute();
                }

            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("SQL error");
            }
        }

    }



class NotDNA extends Exception{
    public NotDNA(){
        super("This sequence is not DNA");
    }
}


