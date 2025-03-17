import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class Runner{
    private static JFrame frame;

    public static void switchToMainScreen(String title){
        Screen screen = new Screen(title);
        frame = new JFrame(title);
        frame.add(screen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]){
        if (args.length > 0){
            Screen sc = new Screen(args[0]);
            frame = new JFrame(args[0]);
            
            frame.add(sc);
    
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } else{
            EmptyTimelineScreen emptyScreen = new EmptyTimelineScreen();
            frame = new JFrame("Timeline");
            
            frame.add(emptyScreen);
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
    }
}