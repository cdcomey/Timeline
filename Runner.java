import javax.swing.JFrame;

public class Runner{
    public static void main(String args[]){
        Screen sc;
        if (args.length > 0)
            sc = new Screen(args[0]);
        else
            sc = new Screen();
            
        JFrame frame = new JFrame("Timeline");
         
        frame.add(sc);
 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}