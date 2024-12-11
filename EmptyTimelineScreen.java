
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TextArea;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

public class EmptyTimelineScreen extends JPanel{
    public EmptyTimelineScreen(){
        JTextField titleField = new JTextField("Title");
        JTextArea categoriesTextArea = new JTextArea("CATEGORIES\ncategory name;R;G;B;color name");
        JTextArea tagsTextArea = new JTextArea("TAGS (one per line)\n// comments will be ignored");
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
}