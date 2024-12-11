
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
import java.io.IOException;

public class EmptyTimelineScreen extends JPanel implements ActionListener{
    private final int screenWidth = 1000;
    private final int screenHeight = 600;
    
    private JTextField titleField, yearField;
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
            
        }
    }
}