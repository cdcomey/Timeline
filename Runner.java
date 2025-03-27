import javax.swing.JFrame;
import javax.swing.JPanel;

public class Runner{
    private static JFrame frame;

    // this will be run in EmptyTimelineScreen once the setup is finished
    public static void switchToMainScreen(String title){
        Screen screen = new Screen(title);
        frame = new JFrame(title);
        frame.add(screen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]){
        // load in timeline from files
        if (args.length > 0){
            switchToMainScreen(args[0]);
        } 
        
        // load timeline creator
        else {
            EmptyTimelineScreen emptyScreen = new EmptyTimelineScreen();
            frame = new JFrame("Timeline");
            
            frame.add(emptyScreen);
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
    }
}