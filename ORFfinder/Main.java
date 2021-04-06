package ORFfinder;

public class Main {

    /**
     * Main method
     * @param args takes user arguments if Jar file is accessed from command line
     */
    public static void main(String[] args) {
        try{
            ORFfinder.GUI.frame();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
