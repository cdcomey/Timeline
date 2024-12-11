import javax.swing.JFrame;

public class Runner{
    public static void main(String args[]){
        if (args.length > 0){
            Screen sc = new Screen(args[0]);
            JFrame frame = new JFrame("Timeline");
            
            frame.add(sc);
    
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } else{
            EmptyTimelineScreen sc = new EmptyTimelineScreen();
            JFrame frame = new JFrame("Timeline");
            
            frame.add(sc);
    
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }

    }
}