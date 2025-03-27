
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*  this allows the user to create their own timeline
    the user inputs the title, start year, default years displayed on screen, categories, and tags
    the class creates the necessary directory and files for the Screen class to use
    the JPanel then transitions from this to a Screen instance
*/ 
public class EmptyTimelineScreen extends JPanel implements ActionListener{
    private final int screenWidth = 1000;
    private final int screenHeight = 600;
    
    private JTextField titleField, yearField, zoomField;
    private JTextArea categoriesTextArea, tagsTextArea;
    private JButton createButton;

    public EmptyTimelineScreen(){
        setLayout(null);

        titleField = new JTextField("TITLE");
        titleField.setBounds(10, screenHeight*1/20, screenWidth*1/4, 30);
        add(titleField);

        yearField = new JTextField("Starting Year");
        yearField.setBounds(titleField.getX(), titleField.getY() + titleField.getHeight() + 10, titleField.getWidth(), titleField.getHeight());
        add(yearField);

        zoomField = new JTextField("Number of years displayed on screen");
        zoomField.setBounds(titleField.getX(), yearField.getY() + titleField.getHeight() + 10, titleField.getWidth(), titleField.getHeight());
        add(zoomField);

        categoriesTextArea = new JTextArea("CATEGORIES (delete these lines)\ncategory name;R;G;B;color name");
        categoriesTextArea.setBounds(screenWidth*1/3, titleField.getY(), titleField.getWidth(), screenHeight*2/3);
        add(categoriesTextArea);

        tagsTextArea = new JTextArea("TAGS (delete these lines)\n// comments will be ignored");
        tagsTextArea.setBounds(screenWidth*2/3, titleField.getY(), titleField.getWidth(), categoriesTextArea.getHeight());
        add(tagsTextArea);

        createButton = new JButton("Create Timeline");
        createButton.setBounds(screenWidth/2 - titleField.getWidth()/2, screenHeight*5/6, titleField.getWidth(), titleField.getHeight());
        add(createButton);
        createButton.addActionListener(this);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
	
	public Dimension getPreferredSize(){
		return new Dimension(screenWidth, screenHeight);
	}

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == createButton){
            String title = titleField.getText();
            String timeline_dir_str = "Timelines/" + title;
            File timeline_dir = new File(timeline_dir_str);
            timeline_dir.mkdirs();
            timeline_dir_str += "/";

            File timeline_file = new File(timeline_dir_str + title + ".rtf");
            try (FileWriter writer = new FileWriter(timeline_file)) {
                writer.write("\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            File categories_file = new File(timeline_dir_str + "categories.txt");
            try (FileWriter writer = new FileWriter(categories_file)) {
                writer.write(categoriesTextArea.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            File tags_file = new File(timeline_dir_str + "tags.txt");
            try (FileWriter writer = new FileWriter(tags_file)) {
                writer.write(tagsTextArea.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            File config_file = new File(timeline_dir_str + "config.txt");
            try (FileWriter writer = new FileWriter(config_file)) {
                writer.write("centerYear:" + yearField.getText() + "\n");
                writer.write("zoomLevel:" + zoomField.getText() + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            File images_dir = new File(timeline_dir_str + "Images");
            images_dir.mkdirs();

            Runner.switchToMainScreen(title);
        }
    }
}