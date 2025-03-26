import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import java.util.TreeSet;
import java.util.ArrayList;

public class Screen extends JPanel implements ActionListener, KeyEventDispatcher, MouseListener{
	
	private final int screenWidth = 1700;
	private final int screenHeight = 950;
	
	// JComponents
	private JButton saveChangesButton, addNewEventButton, addNewPeriodButton, removeEventButton;
	private JButton showTaggedEventsButton, hideTaggedEventsButton, addTagButton, removeTagButton;
	private JButton prevImageButton, nextImageButton, findImageButton, saveImageButton, deleteImageButton;
	
	private JTextArea tagTextArea, captionTextArea;
	private JTextPane titleTextPane, descriptionTextPane;
	private JScrollPane descriptionScrollPane, tagPane;
	private JTextField monthField, dayField, yearField, month2Field, day2Field, year2Field;
	private JTextField redField, greenField, blueField, hexField;
	private JComboBox<String> eventAlignmentComboBox, eventShapeComboBox, tagComboBox;
	private JComboBox<Tag> categoryComboBox;
	private JCheckBox eventPositionCheckBox, BCCheckBox, BCCheckBox2, presentCheckBox, isImageEventCheckBox;
	
	private JFileChooser chooser;	// used to find images to add to events
	private FileNameExtensionFilter filter;	// used to only display image files in the file explorer
	
	private Timeline timeline;
	private GenericEvent selectedEvent;
	private TreeSet<GenericEvent> eventTree;
	private boolean editMode, modernDating, controlKeyDown, shiftKeyDown, showTagHider, darkMode;
	private byte taggedEventsVisibility; // -1 is hide tagged events, 0 is show all, 1 is show only tagged events
	private String[] tagList;
	private byte imageIndex;
	private Color backgroundColor;
	private final Color lightModeColor, darkModeColor;
	private final String imagePathPrefix;
	private String imagePath = "";
	private final String timelineTitle;
	
	// both the descriptionScrollPane and captionTextArea will change bounds depending on whether editMode is on
	private int descriptionScrollPaneX1, descriptionScrollPaneY1, descriptionScrollPaneW1, descriptionScrollPaneH1;
	private int descriptionScrollPaneX2, descriptionScrollPaneY2, descriptionScrollPaneW2, descriptionScrollPaneH2;
	private int captionX1, captionY1, captionW1, captionH1;
	private int captionX2, captionY2, captionW2, captionH2;
	
	// prevImageButton and nextImageButton will change location for the same reason, but not size
	private int prevImageButtonX1, prevImageButtonY1, prevImageButtonX2, prevImageButtonY2;
	private int nextImageButtonX1, nextImageButtonY1, nextImageButtonX2, nextImageButtonY2;

	
	
	public Screen(String timelineTitle){
		setLayout(null);
		setFocusable(true);
		addMouseListener(this);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		// timelineTitle is the name of the timeline
		// loads folder with that name into the Timeline class
		eventTree = new TreeSet<GenericEvent>();
		this.timelineTitle = timelineTitle;
		GenericEvent.setCapListPath("Timelines/" + timelineTitle);
		readFromFile(timelineTitle);

		initializeJComponents();
		
		timeline = new Timeline(screenWidth, screenHeight, timelineTitle);
		editMode = false;
		modernDating = false;
		controlKeyDown = false;
		shiftKeyDown = false;
		showTagHider = false;
		darkMode = true;
		taggedEventsVisibility = 0;
		imageIndex = 0;
		
		imagePathPrefix = "Timelines/" + timelineTitle + "/Images";
		chooser = new JFileChooser(imagePathPrefix);
		filter = new FileNameExtensionFilter("image types", "jpg", "jpeg", "png", "svg", "gif");
		chooser.setFileFilter(filter);
		
		lightModeColor = new Color(230, 230, 230);
		darkModeColor = new Color(40, 40, 40);
		backgroundColor = lightModeColor;
		
		updateComponentVisibility(true);
	}

	// helper function for constructor
	// only run once, used to keep the constructor readable
	private void initializeJComponents(){
		titleTextPane = new JTextPane();
		titleTextPane.setEditorKit(new RTFEditorKit());
		titleTextPane.setBounds(30, 30, screenWidth/2, 30);
		add(titleTextPane);

		descriptionTextPane = new JTextPane();
		descriptionTextPane.setEditorKit(new RTFEditorKit());
		add(descriptionTextPane);
		
		// set 1 is when editMode is false
		descriptionScrollPaneX1 = screenWidth*1/20;
		descriptionScrollPaneY1 = screenHeight*1/20;
		descriptionScrollPaneW1 = screenWidth*12/20;
		descriptionScrollPaneH1 = screenHeight*18/20;
		
		// set 2 is when editMode is true
		descriptionScrollPaneX2 = titleTextPane.getX();
		descriptionScrollPaneY2 = titleTextPane.getY() + titleTextPane.getHeight() + 30;
		descriptionScrollPaneW2 = titleTextPane.getWidth();
		descriptionScrollPaneH2 = screenHeight - 30 - (titleTextPane.getY() + titleTextPane.getHeight() + 30);
		
		descriptionScrollPane = new JScrollPane(descriptionTextPane); 
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		descriptionScrollPane.setBounds(descriptionScrollPaneX1, descriptionScrollPaneY1, descriptionScrollPaneW1, descriptionScrollPaneH1);
		add(descriptionScrollPane);
		
		final int fieldHeight = 30;
		
		monthField = new JTextField("Month");
		monthField.setBounds(titleTextPane.getX() + titleTextPane.getWidth() + 30, titleTextPane.getY(), 80, fieldHeight);
		add(monthField);		
		
		dayField = new JTextField("Day");
		dayField.setBounds(monthField.getX() + monthField.getWidth() + 15, monthField.getY(), 30, fieldHeight);
		add(dayField);

		yearField = new JTextField("Year");
		yearField.setBounds(dayField.getX() + dayField.getWidth() + 15, dayField.getY(), 60, fieldHeight);
		add(yearField);
		
		BCCheckBox = new JCheckBox("BC");
		BCCheckBox.setBounds(yearField.getX() + yearField.getWidth() + 10, yearField.getY(), 60, fieldHeight);
		BCCheckBox.setOpaque(false);
		add(BCCheckBox);
		
		month2Field = new JTextField("Month");
		month2Field.setBounds(BCCheckBox.getX() + BCCheckBox.getWidth() + 60, BCCheckBox.getY(), monthField.getWidth(), fieldHeight);
		add(month2Field);
		
		day2Field = new JTextField("Day");
		day2Field.setBounds(month2Field.getX() + month2Field.getWidth() + 15, month2Field.getY(), 30, fieldHeight);
		add(day2Field);
		
		year2Field = new JTextField("Year");
		year2Field.setBounds(day2Field.getX() + day2Field.getWidth() + 15, day2Field.getY(), 60, fieldHeight);
		add(year2Field);
		
		BCCheckBox2 = new JCheckBox("BC");
		BCCheckBox2.setBounds(year2Field.getX() + year2Field.getWidth() + 10, year2Field.getY(), BCCheckBox.getWidth(), fieldHeight);
		BCCheckBox2.setOpaque(false);
		add(BCCheckBox2);
		
		presentCheckBox = new JCheckBox("present");
		presentCheckBox.setOpaque(false);
		add(presentCheckBox);
		presentCheckBox.addActionListener(this);

		isImageEventCheckBox = new JCheckBox("Image Event");
		isImageEventCheckBox.setBounds(BCCheckBox.getX() + BCCheckBox.getWidth() + 40 + 90 + 40, BCCheckBox.getY(), 110, fieldHeight);
		isImageEventCheckBox.setOpaque(false);
		add(isImageEventCheckBox);
		isImageEventCheckBox.addActionListener(this);
		
		redField = new JTextField("RED");
		redField.setBounds(monthField.getX(), descriptionScrollPaneY2 + 10, 30, fieldHeight);
		add(redField);
		
		greenField = new JTextField("GRN");
		greenField.setBounds(redField.getX() + redField.getWidth() + 20, redField.getY(), redField.getWidth(), fieldHeight);
		add(greenField);
		
		blueField = new JTextField("BLU");
		blueField.setBounds(greenField.getX() + greenField.getWidth() + 20, greenField.getY(), greenField.getWidth(), fieldHeight);
		add(blueField);
		
		hexField = new JTextField("#HEX");
		hexField.setBounds((redField.getX() + blueField.getX() + blueField.getWidth() - 70)/2, redField.getY() + redField.getHeight() + 20, 70, fieldHeight);
		add(hexField);
		
		ArrayList<Tag> categoriesList = new ArrayList<Tag>();
		try{
			File file = new File("Timelines/" + timelineTitle + "/categories.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[1500];
			reader.read(text);
			String s = new String(text);
			String[] categoriesArr = s.split("\n");
			for (String each : categoriesArr){
				String[] categoryArr = each.split(";");
				if (categoryArr.length == 5)
					categoriesList.add(new Tag(categoryArr[0], new Color(Integer.parseInt(categoryArr[1]), Integer.parseInt(categoryArr[2]), Integer.parseInt(categoryArr[3])), categoryArr[4]));
				else if (categoryArr.length == 4)
					categoriesList.add(new Tag(categoryArr[0], new Color(Integer.parseInt(categoryArr[1]), Integer.parseInt(categoryArr[2]), Integer.parseInt(categoryArr[3]))));
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in Screen() attempting to set up categories");
			System.err.println(ex);
			categoriesList.add(new Tag("<category>", new Color(128, 128, 128)));	//gray
		}
		
		Tag[] categories = new Tag[categoriesList.size()];
		for (int i = 0; i < categories.length; i++)
			categories[i] = categoriesList.get(i);
		
		final int buttonHeight = 40;
		
		categoryComboBox = new JComboBox<Tag>(categories);
		categoryComboBox.setBounds(blueField.getX() + blueField.getWidth() + 150, blueField.getY(), 400, buttonHeight);
		add(categoryComboBox);
		
		eventPositionCheckBox = new JCheckBox();
		eventShapeComboBox = new JComboBox<String>();
		
		String[] orientations = {"Left-aligned", "Centered", "Right-aligned"};
		eventAlignmentComboBox = new JComboBox<String>(orientations);
		eventAlignmentComboBox.setBounds((screenWidth + categoryComboBox.getX() + categoryComboBox.getWidth() - 140)/2, categoryComboBox.getY(), 140, buttonHeight);
		add(eventAlignmentComboBox);
		eventAlignmentComboBox.setSelectedIndex(1);
		
		tagTextArea = new JTextArea();
		tagTextArea.setLineWrap(true);
		tagTextArea.setWrapStyleWord(true);
		tagTextArea.setEditable(false);
		
		tagPane = new JScrollPane(tagTextArea); 
        tagPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tagPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tagPane.setBounds(redField.getX() - 10, eventAlignmentComboBox.getY() + eventAlignmentComboBox.getHeight() + 100, 500, 100);
		add(tagPane);
		
		ArrayList<String> tagStrings = new ArrayList<String>();
		try{
			File file = new File("Timelines/" + timelineTitle + "/tags.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[10000];
			reader.read(text);
			String s = new String(text);
			String[] tagsArr = s.split("\n");
			for (String each : tagsArr){
				if (each.contains("//") || each.length() == 0 || (int)each.charAt(0) == 0)
					continue;
				tagStrings.add(new String(each));
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in Screen() attempting to set up tags");
			System.err.println(ex);
			tagStrings.add(new String("temp"));
		}
		
		String[] tagsArr = new String[tagStrings.size()];
		for (int i = 0; i < tagsArr.length; i++)
			tagsArr[i] = tagStrings.get(i);
		tagComboBox = new JComboBox<String>(tagsArr);
		tagComboBox.setBounds(tagPane.getX() + tagPane.getWidth() + 30, tagPane.getY(), 300, buttonHeight);
		add(tagComboBox);
		
		saveChangesButton = new JButton("Save changes");
		saveChangesButton.setBounds(screenWidth*3/4 - 160 - 30, screenHeight - 2*40 - 30, 160, buttonHeight);
		add(saveChangesButton);
		saveChangesButton.addActionListener(this);
		
		removeEventButton = new JButton("Delete Event");
		removeEventButton.setBounds(saveChangesButton.getX() + saveChangesButton.getWidth() + 60, saveChangesButton.getY(), saveChangesButton.getWidth(), buttonHeight);
		add(removeEventButton);
		removeEventButton.addActionListener(this);
		
		addNewEventButton = new JButton("Add event");
		addNewEventButton.setBounds(screenWidth/2 - saveChangesButton.getWidth() - 30, 10, saveChangesButton.getWidth(), buttonHeight);
		add(addNewEventButton);
		addNewEventButton.addActionListener(this);
		
		addNewPeriodButton = new JButton("Add period");
		addNewPeriodButton.setBounds(screenWidth/2 + 30, 10, saveChangesButton.getWidth(), buttonHeight);
		add(addNewPeriodButton);
		addNewPeriodButton.addActionListener(this);
		
		addTagButton = new JButton("Add tag");
		addTagButton.setBounds(tagComboBox.getX(), tagPane.getY() + tagPane.getHeight() - buttonHeight, tagComboBox.getWidth()/2 - 5, buttonHeight);
		add(addTagButton);
		addTagButton.addActionListener(this);
		
		removeTagButton = new JButton("Remove tag");
		removeTagButton.setBounds(tagComboBox.getX() + tagComboBox.getWidth() - addTagButton.getWidth(), addTagButton.getY(), addTagButton.getWidth(), buttonHeight);
		add(removeTagButton);
		removeTagButton.addActionListener(this);
		
		showTaggedEventsButton = new JButton("Show tagged events");
		showTaggedEventsButton.setBounds(tagPane.getX(), addTagButton.getY() + addTagButton.getHeight() + 15, 200, buttonHeight);
		add(showTaggedEventsButton);
		showTaggedEventsButton.addActionListener(this);
		
		hideTaggedEventsButton = new JButton("Hide tagged events");
		hideTaggedEventsButton.setBounds(tagPane.getX() + tagPane.getWidth() - showTaggedEventsButton.getWidth(), removeTagButton.getY() + removeTagButton.getHeight() + 15, showTaggedEventsButton.getWidth(), buttonHeight);
		add(hideTaggedEventsButton);
		hideTaggedEventsButton.addActionListener(this);
		
		prevImageButtonX1 = descriptionScrollPaneX1 + descriptionScrollPaneW1 + (screenWidth - descriptionScrollPaneX1 - descriptionScrollPaneW1 - 80)/2;
		prevImageButtonY1 = descriptionScrollPaneY1;
		prevImageButtonX2 = removeTagButton.getX() + removeTagButton.getWidth() - 80;
		prevImageButtonY2 = removeTagButton.getY() + removeTagButton.getHeight() + buttonHeight;
		prevImageButton = new JButton("^ ^ ^");
		prevImageButton.setBounds(prevImageButtonX1, prevImageButtonY1, 80, buttonHeight);
		add(prevImageButton);
		prevImageButton.addActionListener(this);
		
		nextImageButtonX1 = prevImageButtonX1;
		nextImageButtonY1 = descriptionScrollPaneY1+ descriptionScrollPaneH1 - buttonHeight;
		nextImageButtonX2 = prevImageButtonX2;
		nextImageButtonY2 = prevImageButtonY2 + buttonHeight + 15;
		nextImageButton = new JButton("v v v");
		nextImageButton.setBounds(nextImageButtonX1, nextImageButtonY1, prevImageButton.getWidth(), buttonHeight);
		add(nextImageButton);
		nextImageButton.addActionListener(this);
		
		deleteImageButton = new JButton("Delete Image");
		deleteImageButton.setBounds(removeTagButton.getX(), saveChangesButton.getY() - 2*prevImageButton.getHeight(), removeTagButton.getWidth(), buttonHeight);
		add(deleteImageButton);
		deleteImageButton.addActionListener(this);
		
		saveImageButton = new JButton("Save Image");
		saveImageButton.setBounds(deleteImageButton.getX(), deleteImageButton.getY() - buttonHeight - 10, deleteImageButton.getWidth(), buttonHeight);
		add(saveImageButton);
		saveImageButton.addActionListener(this);
		
		findImageButton = new JButton("Find Image");
		findImageButton.setBounds(saveImageButton.getX(), saveImageButton.getY() - buttonHeight - 10, saveImageButton.getWidth(), buttonHeight);
		add(findImageButton);
		findImageButton.addActionListener(this);
		
		captionX1 = descriptionScrollPaneX1 + descriptionScrollPaneW1 + 5;
		captionY1 = prevImageButtonY1 + screenHeight*2/3 + 5;
		captionW1 = screenWidth - captionX1 - 5;
		captionH1 = nextImageButtonY1 - captionY1 - 5;
		
		captionX2 = tagComboBox.getX();
		captionY2 = nextImageButtonY2 + buttonHeight + 10;
		captionW2 = tagComboBox.getWidth();
		captionH2 = findImageButton.getY() - (nextImageButtonY2 + buttonHeight + 10) - 20;
		
		captionTextArea = new JTextArea("Caption");
		captionTextArea.setBounds(captionX2, captionY2, captionW2, captionH2);
		captionTextArea.setLineWrap(true);
		captionTextArea.setWrapStyleWord(true);
		add(captionTextArea);
	}
	
	//methods
	public Dimension getPreferredSize(){
		return new Dimension(screenWidth, screenHeight);
	}
	
	// draws all objects on screen
	// is called many times, unpredictably, so should be lightweight
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		// draw the background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, screenWidth, screenHeight);
		
		// draw the timeline
		if (selectedEvent == null && !showTagHider)
			timeline.drawTimeline(g, eventTree, tagList, taggedEventsVisibility, modernDating, darkMode);
		
		// draw the fields for the selected event
		if (selectedEvent != null){
			if (editMode){
				// dimensions for drawn image in edit mode
				int xCenter = tagPane.getX() + tagPane.getWidth()/2;
				int yCenter = prevImageButton.getY() + (saveChangesButton.getY() - prevImageButton.getY() - prevImageButton.getHeight())/2;
				int width = tagPane.getWidth();
				int height = saveChangesButton.getY() - prevImageButton.getY() - prevImageButton.getHeight();
				
				if (selectedEvent.getImages().size() > imageIndex){
					selectedEvent.getImages().get(imageIndex).drawFromFile(g, xCenter, yCenter, width, height, true, true);
				} else if (!imagePath.isEmpty()){
					MyImage img = new MyImage(imagePath);
					img.drawFromFile(g, xCenter, yCenter, width, height, true, true);
				}
			}
			if (selectedEvent.getImages().size() >= imageIndex+1){
				if (editMode){
				} else {
					selectedEvent.getImages().get(imageIndex).drawFromFile(g, 
						prevImageButtonX1 + prevImageButton.getWidth()/2, 
						prevImageButtonY1 + prevImageButton.getHeight() + 5 + (screenHeight*2/3 * 1/2), 
						screenWidth - descriptionScrollPaneX1 - descriptionScrollPaneW1 - 2*5, 
						screenHeight*2/3, true, true);
					// g.setColor(Color.white);
					// g.drawRect(
					// 	descriptionScrollPaneX1 + descriptionScrollPaneW1 + 5,
					// 	prevImageButtonY1 + prevImageButton.getHeight() + 5, 
					// 	screenWidth - descriptionScrollPaneX1 - descriptionScrollPaneW1 - 2*5, 
					// 	screenHeight*2/3);
				}
			}
			
			// draw colored squares around the RGB fields as a kind of label
			if (editMode){
				g.setColor(Color.red);
				g.fillRect(redField.getX() - 10, redField.getY() - 10, redField.getWidth() + 20, redField.getHeight() + 20);
				g.setColor(Color.green);
				g.fillRect(greenField.getX() - 10, greenField.getY() - 10, greenField.getWidth() + 20, greenField.getHeight() + 20);
				g.setColor(Color.blue);
				g.fillRect(blueField.getX() - 10, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
				g.setColor(selectedEvent.getColor());
				g.fillRect(blueField.getX() + blueField.getWidth() + 50, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
				g.setColor(Color.black);
				g.drawRect(blueField.getX() + blueField.getWidth() + 50, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == saveChangesButton){
			saveChanges();
		} else if (e.getSource() == addNewEventButton){
			Event event = new Event();
			eventTree.add(event);
			selectedEvent = event;
			updateComponentVisibility(false);
			initializeFieldText();
		} else if (e.getSource() == addNewPeriodButton){
			Period period = new Period();
			eventTree.add(period);
			selectedEvent = period;
			updateComponentVisibility(true);
			initializeFieldText();
		} else if (e.getSource() == removeEventButton){
			eventTree.remove(selectedEvent);			
			selectedEvent = null;
			updateComponentVisibility(true);
			writeToFile();
		} else if (e.getSource() == presentCheckBox){
			presentFieldHandler();
		} else if (e.getSource() == showTaggedEventsButton){
			taggedEventsVisibility = 1;
			showTagHider = false;
			
			if (!tagTextArea.getText().equals("none"))
				tagList = tagTextArea.getText().split(", ");
			
			updateComponentVisibility(true);
		} else if (e.getSource() == hideTaggedEventsButton){
			showTagHider = false;
			if (tagTextArea.getText().equals("none"))
				taggedEventsVisibility = 0;
			else {
				taggedEventsVisibility = -1;
				tagList = tagTextArea.getText().split(", ");
			}

			updateComponentVisibility(true);
		} else if (e.getSource() == addTagButton){
			String tag = (String)tagComboBox.getSelectedItem();
			if (showTagHider){
				if (!tagTextArea.getText().contains(tag))
					tagTextArea.setText(tagTextArea.getText().equals("none") ? tag : (tagTextArea.getText() + ", " + tag));
			} else {
				if (selectedEvent.addTag(tag))
					tagTextArea.setText(selectedEvent.getTagString());
			}
		} else if (e.getSource() == removeTagButton){
			String tag = (String)tagComboBox.getSelectedItem();
			if (showTagHider){
				if (!tagTextArea.getText().contains(tag)){}
				else if (tagTextArea.getText().contains(", ")){
					int tagSpot = tagTextArea.getText().indexOf(tag);
					String s = tagTextArea.getText().substring(0, tagSpot);
					if (tagSpot + tag.length() == tagTextArea.getText().length())
						s = tagTextArea.getText().substring(0, tagSpot - 2);
					else
						s += tagTextArea.getText().substring(tagSpot + tag.length() + 2, tagTextArea.getText().length());
					tagTextArea.setText(s);
				} else
					tagTextArea.setText("none");
			} else {
				selectedEvent.getTags().remove(tag);
				tagTextArea.setText(selectedEvent.getTagString());
			}
		} else if (e.getSource() == prevImageButton){
			imageIndex = (byte)Math.max(0, imageIndex-1);
			updateComponentVisibility(selectedEvent instanceof Period);
		} else if (e.getSource() == nextImageButton){
			imageIndex = (byte)Math.min(imageIndex+1, selectedEvent.getImages().size() - 1);
			updateComponentVisibility(selectedEvent instanceof Period);
		} else if (e.getSource() == findImageButton){
			int result = chooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION){
				imagePath = chooser.getSelectedFile().getPath();
			}
			imageIndex++;
		} else if (e.getSource() == saveImageButton){
			// System.out.println(imageIndex + " >= " + selectedEvent.getImages().size());
			// print(imagePath);
			if (imageIndex >= selectedEvent.getImages().size())
				selectedEvent.addImage(imagePathPrefix+imagePath, captionTextArea.getText());
			else
				selectedEvent.setImage(imageIndex, imagePathPrefix+imagePath, captionTextArea.getText());
			imagePath = "";
			// System.out.println(selectedEvent.getImages().size());
		} else if (e.getSource() == deleteImageButton){
			selectedEvent.deleteImage(imageIndex);
			imageIndex--;
		}
		
		repaint();
	}
	
	private void saveChanges(){
		GenericEvent event = new GenericEvent();
		boolean allChecksPassed = false;
		try{
			// title and description
			String title = saveTextPane(titleTextPane);
			String description = saveTextPane(descriptionTextPane);
							
			// date
			String monthString = monthField.getText();
			int month = 0;
			if ("1234567890".contains("" + monthString.charAt(0)))
				month = Integer.parseInt(monthString);
			else{
				for (int i = 0; i < Date.getMonthNames().length; i++){
					if (monthString.substring(0, 3).toUpperCase().equals(Date.getMonthNames()[i].substring(0, 3).toUpperCase()))
						month = i+1;
				}
			}
			int day = Integer.parseInt(dayField.getText());
			int year = Integer.parseInt(yearField.getText());
			if (BCCheckBox.isSelected())
				year = (year - 1) * -1;
			
			if (month <= 0 || day <= 0 || month > 12)
				throw new IndexOutOfBoundsException("month or day input too low, or month input too high");
			if (Date.getMonthLengths()[month-1] < day && !(month == 2 && day == 29 && Date.isLeapYear(year)))
				throw new IndexOutOfBoundsException("day input too high");
			
			// color and category
			int red = 128;
			int green = 128;
			int blue = 128;
			
			Tag category = new Tag("null", new Color(red, green, blue));
			if (categoryComboBox.getSelectedItem() instanceof Tag && !category.equals("<category>")){
				category = (Tag)categoryComboBox.getSelectedItem();
				Color color = category.getColor();
				red = color.getRed();
				green = color.getGreen();
				blue = color.getBlue();
			} else {
			
				try{
					red = Integer.parseInt(redField.getText());
					green = Integer.parseInt(greenField.getText());
					blue = Integer.parseInt(blueField.getText());
				} catch (NumberFormatException ex){}
				
				if (red == selectedEvent.getColor().getRed() && green == selectedEvent.getColor().getGreen() && blue == selectedEvent.getColor().getBlue() 
					|| red < 0 || green < 0 || blue < 0 || red > 255 || green > 255 || blue > 255){
					int[] rgbColors = GenericEvent.HextoRGB(hexField.getText());
					red = rgbColors[0];
					green = rgbColors[1];
					blue = rgbColors[2];
				}
			}
			
			// tags
			String[] tagArr = tagTextArea.getText().split(", ");
			ArrayList<String> tags = new ArrayList<String>();
			if (tagArr.length > 0 && !tagArr[0].equals("none") && tagArr[0].length() > 1){
				for (int i = 0; i < tagArr.length; i++)
					tags.add(tagArr[i]);
			}
			
			if (selectedEvent instanceof Period && presentCheckBox.isSelected()){
				event = new Period(title, description, month, day, year, true, red, green, blue, category.toString(), tags, selectedEvent.getImages());
				allChecksPassed = true;
			} else if (selectedEvent instanceof Period && !presentCheckBox.isSelected()){
				String month2String = month2Field.getText();
				int month2 = 0;
				if ("1234567890".contains("" + month2String.charAt(0)))
					month2 = Integer.parseInt(month2String);
				else{
					for (int i = 0; i < Date.getMonthNames().length; i++){
						if (month2String.substring(0, 3).toUpperCase().equals(Date.getMonthNames()[i].substring(0, 3).toUpperCase())){
							month2 = i+1;
							break;
						}
					}
				}
				int day2 = Integer.parseInt(day2Field.getText());
				int year2 = Integer.parseInt(year2Field.getText());
				if (BCCheckBox2.isSelected())
					year2 = (year2 - 1) * -1;
				
				if (month2 <= 0 || day2 <= 0 || month2 > 12)
					throw new IndexOutOfBoundsException("month2 or year2 input too low, or month2 input too high");
				if (Date.getMonthLengths()[month2-1] < day2 && !(month2 == 2 && day2 == 29 && Date.isLeapYear(year2)))
					throw new IndexOutOfBoundsException("day2 input too high");
				if (year2 < year || (year2 == year && month2 < month) || (year2 == year && month2 == month && day2 < day))
					throw new IndexOutOfBoundsException("date2 occurs before date1");
				if (year == year2 && month == month2 && day == day2)
					throw new IndexOutOfBoundsException("date1 is the same as date2");
				
				event = new Period(title, description, month, day, year, month2, day2, year2, red, green, blue, category.getTitle(), tags, selectedEvent.getImages());
				allChecksPassed = true;
			} else {
				String alignment = eventAlignmentComboBox.getSelectedItem().toString();
				boolean isImageEvent = isImageEventCheckBox.isSelected();
				event = new Event(title, description, month, day, year, red, green, blue, category.getTitle(), alignment, tags, selectedEvent.getImages(), isImageEvent);
				allChecksPassed = true;
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in actionPerformed() attempting to save changes");
			// System.err.println(ex);
			ex.printStackTrace();
		}
		
		//saving the changed event, then removing the old and adding the new allows the new event to be sorted if the date is changed
		if (allChecksPassed){
			eventTree.remove(selectedEvent);
			eventTree.add(event);
			print("full event before saving: " + event.toStringVerbose());
			selectedEvent = null;
			updateComponentVisibility(event instanceof Period);
			writeToFile();
		}
	}

	private String saveTextPane(JTextPane textPane){
		String text = "";
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			RTFEditorKit kit = (RTFEditorKit)textPane.getEditorKit();
			Document doc = textPane.getDocument();
			kit.write(out, doc, 0, doc.getLength());
			text = out.toString();
			print("in saveTextPane: " + text);
			text = text.substring(text.indexOf("\\cf0 ")+5, text.lastIndexOf("\\ul0\\par"));
			text = text.substring(0, text.indexOf("\\par"));
			text = reAddRTFDelimiters(text, 'i');
			text = reAddRTFDelimiters(text, 'b');
		} catch (Exception e){
			e.printStackTrace();
		}

		return text;
	}
	
	private void readFromFile(String timelineTitle){
		File file = new File("Timelines/" + timelineTitle + "/" + timelineTitle + ".rtf");
		// print(file.getName());
		String eventString = "";
         
        try (FileInputStream fis = new FileInputStream(file)) {
			
            RTFEditorKit rtfKit = new RTFEditorKit();
            StyledDocument doc = new DefaultStyledDocument();
			String s = new String(Files.readAllBytes(Paths.get("Timelines/" + timelineTitle + "/" + timelineTitle + ".rtf")));
			s = s.substring(s.indexOf("Title: "), s.length());

			String[] eventArr = {};
			String regex = "\\nend\\\\\\n\\\\\\n";
			eventArr = s.split(regex);
			// for (int i = 0; i < eventArr.length; i++){
			// 	print(i + " " + eventArr[i]);
			// }
			eventTree = new TreeSet<GenericEvent>();
			
			// we skip the last element because it's just a closing bracket
			for (int i = 0; i < eventArr.length-1; i++){
				eventString = eventArr[i];
				String title = eventString.substring(eventString.indexOf("Title: ") + 7, eventString.indexOf("\\\nDescription: "));
				String description = eventString.substring(eventString.indexOf("Description: ") + 13, eventString.indexOf("\\\nType: "));
				String type = eventString.substring(eventString.indexOf("Type: ") + 6, eventString.indexOf("\\\nDate: "));				
				eventString = eventString.substring(eventString.indexOf("\\\nDate: ") + 8);
				
				int month = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
				eventString = eventString.substring(eventString.indexOf("/") + 1);
				int day = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
				eventString = eventString.substring(eventString.indexOf("/") + 1);
				
				GenericEvent event;
				if (type.equals("Period")){
					int year = Integer.parseInt(eventString.substring(0, eventString.indexOf(" - ")));
					eventString = eventString.substring(eventString.indexOf(" - ") + 3);
					
					String date2String = eventString.substring(0, eventString.indexOf("\nColor: "));
					eventString = eventString.substring(eventString.indexOf("Color: ") + 7);
					
					int red = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int green = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int blue = Integer.parseInt(eventString.substring(0, eventString.indexOf("\\\nCategory: ")));
					eventString = eventString.substring(eventString.indexOf("Category: ") + 10);
					String category = eventString.substring(0, eventString.indexOf("\\\nAlignment: "));
					eventString = eventString.substring(eventString.indexOf("Alignment: ") + 11);
					String alignment = eventString.substring(0, eventString.indexOf("\\\nTags: "));
					eventString = eventString.substring(eventString.indexOf("Tags: ") + 6);
					String[] tagArr = eventString.substring(0, eventString.indexOf("\\\nImages: ")).split(", ");
					ArrayList<String> tagList = new ArrayList<String>();
					for (int j = 0; j < tagArr.length; j++){
						if (tagArr[j].equals("none"))
							break;
						tagList.add(tagArr[j]);
					}
					
					eventString = eventString.substring(eventString.indexOf("Images: ") + 8);
					ArrayList<MyImage> imgList = new ArrayList<MyImage>();
					if (!eventString.equals("none")){
						// System.out.println("string: " + eventString);
						String[] imgArr = eventString.split(" \\| ");
						// System.out.println("length: " + imgArr.length);
						
						for (int j = 0; j < imgArr.length; j++){
							// System.out.println(imgArr[j]);
							imgList.add(new MyImage(imgArr[j].substring(0, imgArr[j].indexOf(";")), imgArr[j].substring(imgArr[j].indexOf(";")+1)));
						}
					}
					
					if (date2String.equals("present")){
						event = new Period(title, description, month, day, year, true, red, green, blue, category, tagList, imgList);
					} else {
						int month2 = Integer.parseInt(date2String.substring(0, date2String.indexOf("/")));
						date2String = date2String.substring(date2String.indexOf("/") + 1);
						int day2 = Integer.parseInt(date2String.substring(0, date2String.indexOf("/")));
						date2String = date2String.substring(date2String.indexOf("/") + 1);
						int year2 = Integer.parseInt(date2String);
						
						event = new Period(title, description, month, day, year, month2, day2, year2, red, green, blue, category, tagList, imgList);
					}
				} else {
					int year = Integer.parseInt(eventString.substring(0, eventString.indexOf("\\\nColor: ")));
					eventString = eventString.substring(eventString.indexOf("Color: ") + 7);
					int red = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int green = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int blue = Integer.parseInt(eventString.substring(0, eventString.indexOf("\\\nCategory: ")));
					eventString = eventString.substring(eventString.indexOf("Category: ") + 10);
					String category = eventString.substring(0, eventString.indexOf("\\\nAlignment: "));
					eventString = eventString.substring(eventString.indexOf("Alignment: ") + 11);
					String alignment = eventString.substring(0, eventString.indexOf("\\\nTags: "));
					eventString = eventString.substring(eventString.indexOf("Tags: ") + 6);
					String[] tagArr = eventString.substring(0, eventString.indexOf("\\\nImages: ")).split(", ");
					ArrayList<String> tagList = new ArrayList<String>();
					for (int j = 0; j < tagArr.length; j++){
						if (tagArr[j].equals("none"))
							break;
						tagList.add(tagArr[j]);
					}
					
					eventString = eventString.substring(eventString.indexOf("Images: ") + 8);
					String[] imgArr = {eventString};
					if (eventString.indexOf(" | ") >= 0)
						imgArr = eventString.split(" | ");
					ArrayList<MyImage> imgList = new ArrayList<MyImage>();
					for (int j = 0; j < imgArr.length; j++){
						if (imgArr[j].equals("none\\"))
							break;
						// print("imgArr for " + title + ": " + imgArr[j]);
						imgList.add(new MyImage(imgArr[j].substring(0, imgArr[j].indexOf(";")), imgArr[j].substring(imgArr[j].indexOf(";")+1)));
					}
					
					event = new Event(title, description, month, day, year, red, green, blue, category, alignment, tagList, imgList, type.equals("ImageEvent"));
				}
				
				eventTree.add(event);
			}
			
            fis.close();
        } catch (IOException e) {
			System.err.println("IOException occurred in readFromFile()");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
			System.err.println("IndexOutOfBoundsException occurred in Screen.readFromFile()");
			System.err.println(eventString);
			e.printStackTrace();
		}
	}
	
	private void readFromTimelineFile(){
		try{
			eventTree = new TreeSet<GenericEvent>();
			File file = new File("US History Timeline.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[473453];
			reader.read(text);
			String s = new String(text);
			
			String s1 = s.substring(s.indexOf("Type: Event"), s.indexOf("Type: Period"));
			String s2 = s.substring(s.indexOf("Type: Period"), s.indexOf("Type: Group"));
			
			String[] events = s1.split("Type: Event\n");
			for (int i = 1; i < events.length; i++){
				
				String title, description;
				if (events[i].indexOf("Description:\n") == -1){
					title = events[i].substring(events[i].indexOf("Name:\n")+"Name:\n".length(), events[i].indexOf("Time:"));
					description = "";
				} else {
					title = events[i].substring(events[i].indexOf("Name:\n")+"Name:\n".length(), events[i].indexOf("Description:\n"));
					description = events[i].substring(events[i].indexOf("Description:\n")+"Description:\n".length(), events[i].indexOf("Time:"));
				}
				
				
				String timeStr = events[i].substring(events[i].indexOf("Time: ")+"Time: ".length(), events[i].indexOf("\n", events[i].indexOf("Time")));
				int year = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int month = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int day;
				
				if (timeStr.indexOf(' ') == -1)
					day = Integer.parseInt(timeStr.substring(0));
				else
					day = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				
				eventTree.add(new Event(title, description, month, day, year, 128, 128, 128, "<category>", "Centered", null, null, false));
			}
			
			String[] periods = s2.split("Type: Period\n");
			for (int i = 1; i < periods.length; i++){
				String title, description;
				if (periods[i].indexOf("Description:\n") == -1){
					title = periods[i].substring(periods[i].indexOf("Name:\n")+"Name:\n".length(), periods[i].indexOf("Time from:"));
					description = "";
				} else {
					title = periods[i].substring(periods[i].indexOf("Name:\n")+"Name:\n".length(), periods[i].indexOf("Description:\n"));
					description = periods[i].substring(periods[i].indexOf("Description:\n")+"Description:\n".length(), periods[i].indexOf("Time from:"));
				}
				
				
				String timeStr = periods[i].substring(periods[i].indexOf("Time from: ")+11, periods[i].indexOf("\n", periods[i].indexOf("Time from: ")));
				int year = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int month = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int day;
				
				if (timeStr.indexOf(' ') == -1)
					day = Integer.parseInt(timeStr.substring(0));
				else
					day = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				
				String timeStr2 = periods[i].substring(periods[i].indexOf("Time to: ")+9, periods[i].indexOf("\n", periods[i].indexOf("Time to: ")));
				int year2, month2, day2;
				
				if (timeStr2.equals("@")){
					year2 = GenericEvent.today().getYear();
					month2 = GenericEvent.today().getMonth();
					day2 = GenericEvent.today().getDay();
				} else {
				
					year2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
					timeStr2 = timeStr2.substring(timeStr2.indexOf(' ')+1);
					
					if (timeStr2.indexOf(' ') == -1)
						month2 = Integer.parseInt(timeStr2.substring(0));
					else
						month2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
					
					timeStr2 = timeStr2.substring(timeStr2.indexOf(' ')+1);
					
					if (timeStr2.length() == 0)
						day2 = 1;
					else if (timeStr2.indexOf(' ') == -1)
						day2 = Integer.parseInt(timeStr2.substring(0));
					else
						day2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
				}
				
				eventTree.add(new Period(title, description, month, day, year, month2, day2, year2, 128, 128, 128, "null", null, null));
			}
			
			reader.close();
			
		} catch (Exception ex){
			ex.printStackTrace();	
		}
	}
	
	private void writeToFile(){
		File file = new File("Timelines/" + timelineTitle + "/" + timelineTitle + ".rtf");
		String s = "{\\rtf1\\ansi\\deff0" + 
			"{\\fonttbl\\f0\\fswiss\\fcharset0 Helvetica;\\f1\\fswiss\\fcharset0 Helvetica-Oblique;}\n";
		for (GenericEvent e : eventTree){
			s += e.toStringVerbose() + "\nend\\\n\\\n";;
		}

		s += "}";
		
		try(FileOutputStream fos = new FileOutputStream(file)) {
            //convert string to byte array
            byte[] bytes = s.getBytes();
            //write byte array to file
            fos.write(bytes);
            fos.close();
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
			System.err.println("IOException occurred in writeToFile()");
            e.printStackTrace();
        }
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e){
		if (e.getID() == KeyEvent.KEY_PRESSED){
			keyPressed(e);
			// print(descriptionTextPane.getText());
			// printRaw(descriptionTextPane.getText());
		} else if (e.getID() == KeyEvent.KEY_RELEASED){
			keyReleased(e);
		}

		return false;
	}

	public void keyPressed(KeyEvent e){
		// System.out.println(e.getKeyCode());
		
		if (e.getKeyCode() == 16){ //shift
			shiftKeyDown = true;
		} else if (e.getKeyCode() == 17){ //control
			controlKeyDown = true;
			print("controlKeyDown");
		} else if (e.getKeyCode() == 27){ //escape
			selectedEvent = null;
			showTagHider = false;
			updateComponentVisibility(true);
		}else if (e.getKeyCode() == 37){ //left arrow
			scrollTimeline(-1);
		} else if (e.getKeyCode() == 38){ //up arrow
			timeline.shiftTimelineUp();
		} else if (e.getKeyCode() == 39){ //right arrow
			scrollTimeline(1);
		} else if (e.getKeyCode() == 40){ //down arrow
			timeline.shiftTimelineDown();
		} else if (e.getKeyCode() == 45){ //minus/underscore
			if (controlKeyDown)
				timeline.zoomOut();
		} else if (e.getKeyCode() == 61){ //equals/plus
			if (controlKeyDown)
				timeline.zoomIn();
		} else if (e.getKeyCode() == 66){ //B
			if (controlKeyDown && descriptionTextPane.hasFocus()){
				toggleStyle(descriptionTextPane, StyleConstants.Bold);
			} else if (controlKeyDown && titleTextPane.hasFocus()){
				toggleStyle(titleTextPane, StyleConstants.Bold);
			}
		} else if (e.getKeyCode() == 73){ //I
			if (controlKeyDown && descriptionTextPane.hasFocus()){
				toggleStyle(descriptionTextPane, StyleConstants.Italic);
			} else if (controlKeyDown && titleTextPane.hasFocus()){
				toggleStyle(titleTextPane, StyleConstants.Italic);
			}
		} else if (e.getKeyCode() == 112){ //F1
			editMode = !editMode;
			showTagHider = false;
			updateComponentVisibility(selectedEvent == null ? true : selectedEvent instanceof Period);
		} else if (e.getKeyCode() == 113){ //F2
			modernDating = !modernDating;
			BCCheckBox.setText(modernDating ? "BCE" : "BC");
			BCCheckBox2.setText(modernDating ? "BCE" : "BC");
		} else if (e.getKeyCode() == 114){ //F3
			showTagHider = !showTagHider;
			editMode = false;
			selectedEvent = null;
			updateComponentVisibility(true);
			
			if (showTagHider){
				if (tagList == null || tagList.length == 0)
					tagTextArea.setText("none");
				else{
					String s = "";
					for (int i = 0; i < tagList.length; i++)
						s += tagList[i] + ", ";
					
					tagTextArea.setText(s.substring(0, s.length() - 2));
				}
			}
		} else if (e.getKeyCode() == 115){ //F4
			darkMode = !darkMode;
			backgroundColor = darkMode ? darkModeColor : lightModeColor;
		}
		
		repaint();
	}
	
	public void keyReleased(KeyEvent e){
		if (e.getKeyCode() == 16){ //shift
			shiftKeyDown = false;
		} else if (e.getKeyCode() == 17){ //control
			controlKeyDown = false;
			print("controlKeyUp");
		}
	}
	
	public void keyTyped(KeyEvent e){}
	
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();
		System.out.println("(" + mouseX + ", " + mouseY + ")");
		// print(timeline.getDrawnEventCoordinates().size() + " + " + timeline.getDrawnPeriodCoordinates().size());
		
		for (int i = 0; i < timeline.getDrawnEventCoordinates().size() + timeline.getDrawnPeriodCoordinates().size(); i++){
			Rectangle rect = null;
			if (i < timeline.getDrawnEventCoordinates().size())
				rect = timeline.getDrawnEventCoordinates().get(i);
			else
				rect = timeline.getDrawnPeriodCoordinates().get(i - timeline.getDrawnEventCoordinates().size());
			
			// System.out.println(rect.getEvent().getTitle() + " : " + rect.getCoords());
			// print(mouseX >= rect.getX());
			// print(mouseX <= rect.getX2());
			// print(mouseY >= rect.getY());
			// print(mouseY <= rect.getY2());
			if (mouseX >= rect.getX() && mouseX <= rect.getX2() && mouseY >= rect.getY() && mouseY <= rect.getY2() && selectedEvent == null){
				// System.out.println("clicked on " + rect.getEvent().getTitle());
				selectedEvent = rect.getEvent();
				initializeFieldText();
			}
		}
		
		updateComponentVisibility(selectedEvent == null ? true : selectedEvent instanceof Period);
		repaint();
	}

	//this shifts the timeline forward or back 1, 10, 100, or 1000 years depending on whether control or shift is held
	private void scrollTimeline(int direction){
		int scrollAmount = direction;
		if (selectedEvent != null)
			scrollAmount = 0;
		else if (controlKeyDown && shiftKeyDown)
			scrollAmount *= 1000;
		else if (shiftKeyDown)
			scrollAmount *= 100;
		else if (controlKeyDown)
			scrollAmount *= 10;

		timeline.setCenterYear(timeline.getCenterYear()+scrollAmount);
		//timeline.setCenterYear(timeline.getCenterYear() - (int)Math.pow(10, (shiftKeyDown ? 2 : 0) + (controlKeyDown ? 1 : 0)));
		//the above line does the same thing and is more concise and nice-looking, but i believe it is slower because of the pow method
	}

	private void print(String s){ System.out.println(s); }
	private void print(int i){ System.out.println(""+i); }
	private void print(boolean b){ System.out.println(""+b); }

	private void printRaw(String s){
		if (s == null){
			print("s is empty");
			return;
		}

		print("string is " + s.length());			
		for (int i = 0; i < s.length(); i++){
			char chr = s.charAt(i);
			if (chr == '\r')
				System.out.print("\\r");
			else if (chr == '\t')
				System.out.print("\\t");
			else if (chr == '\n')
				System.out.print("\\n");
			else
				System.out.print(chr);
		}

		System.out.println();
	}
	
	private void updateComponentVisibility(boolean isPeriod){
		boolean showEditTools1 = editMode && selectedEvent == null;
		boolean showEditTools2 = editMode && selectedEvent != null;
		
		if (selectedEvent == null){
			imageIndex = 0;
			prevImageButton.setVisible(false);
			nextImageButton.setVisible(false);
		}
		
		addNewEventButton.setVisible(showEditTools1);
		addNewPeriodButton.setVisible(showEditTools1);
		titleTextPane.setVisible(showEditTools2);
		descriptionScrollPane.setVisible(selectedEvent != null);
		monthField.setVisible(showEditTools2);
		dayField.setVisible(showEditTools2);
		yearField.setVisible(showEditTools2);
		redField.setVisible(showEditTools2);
		greenField.setVisible(showEditTools2);
		blueField.setVisible(showEditTools2);
		hexField.setVisible(showEditTools2);
		BCCheckBox.setVisible(showEditTools2);
		presentCheckBox.setVisible(showEditTools2);
		eventPositionCheckBox.setVisible(showEditTools2);
		eventShapeComboBox.setVisible(showEditTools2);
		saveChangesButton.setVisible(showEditTools2);
		removeEventButton.setVisible(showEditTools2);
		categoryComboBox.setVisible(showEditTools2);
		captionTextArea.setVisible(showEditTools2);
		findImageButton.setVisible(showEditTools2);
		saveImageButton.setVisible(showEditTools2);
		prevImageButton.setVisible(showEditTools2 && imageIndex > 0);
		nextImageButton.setVisible(showEditTools2 && imageIndex < selectedEvent.getImages().size() - 1);
		deleteImageButton.setVisible(showEditTools2 && selectedEvent.getImages().size() > 0);
		
		if (showEditTools2){
			descriptionTextPane.setEditable(true);
			descriptionScrollPane.setBounds(descriptionScrollPaneX2, descriptionScrollPaneY2, descriptionScrollPaneW2, descriptionScrollPaneH2);
			
			prevImageButton.setBounds(prevImageButtonX2, prevImageButtonY2, prevImageButton.getWidth(), prevImageButton.getHeight());
			nextImageButton.setBounds(nextImageButtonX2, nextImageButtonY2, nextImageButton.getWidth(), nextImageButton.getHeight());
			captionTextArea.setBounds(captionX2, captionY2, captionW2, captionH2);
			captionTextArea.setEditable(true);
		} else if (!editMode && selectedEvent != null){
			descriptionTextPane.setEditable(false);
			descriptionScrollPane.setBounds(descriptionScrollPaneX1, descriptionScrollPaneY1, descriptionScrollPaneW1, descriptionScrollPaneH1);
			descriptionTextPane.setCaretPosition(0);
			prevImageButton.setVisible(imageIndex > 0);
			nextImageButton.setVisible(imageIndex < selectedEvent.getImages().size() - 1);
			prevImageButton.setBounds(prevImageButtonX1, prevImageButtonY1, prevImageButton.getWidth(), prevImageButton.getHeight());
			nextImageButton.setBounds(nextImageButtonX1, nextImageButtonY1, nextImageButton.getWidth(), nextImageButton.getHeight());
			captionTextArea.setVisible(imageIndex > 0);
			captionTextArea.setBounds(captionX1, captionY1, captionW1, captionH1);
			captionTextArea.setEditable(false);
		}
		
		if (isPeriod){
			month2Field.setVisible(showEditTools2);
			day2Field.setVisible(showEditTools2);
			year2Field.setVisible(showEditTools2);
			BCCheckBox2.setVisible(showEditTools2);
			eventAlignmentComboBox.setVisible(false);
			presentCheckBox.setBounds(BCCheckBox2.getX() + BCCheckBox2.getWidth() + 40, BCCheckBox2.getY(), 90, BCCheckBox2.getHeight());
			isImageEventCheckBox.setVisible(false);
		} else {
			month2Field.setVisible(false);
			day2Field.setVisible(false);
			year2Field.setVisible(false);
			BCCheckBox2.setVisible(false);
			eventAlignmentComboBox.setVisible(showEditTools2);
			presentCheckBox.setBounds(BCCheckBox.getX() + BCCheckBox.getWidth() + 40, BCCheckBox.getY(), 90, BCCheckBox.getHeight());
			isImageEventCheckBox.setVisible(showEditTools2);
		}
		
		tagComboBox.setVisible(showEditTools2 || showTagHider);
		tagPane.setVisible(showEditTools2 || showTagHider);
		addTagButton.setVisible(showEditTools2 || showTagHider);
		removeTagButton.setVisible(showEditTools2 || showTagHider);
		showTaggedEventsButton.setVisible(showTagHider);
		hideTaggedEventsButton.setVisible(showTagHider);
	}
	
	private void initializeFieldText(){
		updateRTFPane(titleTextPane, selectedEvent.getTitle(), true);		
		updateRTFPane(descriptionTextPane, selectedEvent.getDescription(), editMode);

		descriptionTextPane.setCaretPosition(0);
		tagComboBox.setSelectedIndex(0);
		
		redField.setText(Date.numFormat(selectedEvent.getColor().getRed(), 3));
		greenField.setText(Date.numFormat(selectedEvent.getColor().getGreen(), 3));
		blueField.setText(Date.numFormat(selectedEvent.getColor().getBlue(), 3));
		hexField.setText(GenericEvent.RGBtoHex(selectedEvent.getColor().getRed(), selectedEvent.getColor().getGreen(), selectedEvent.getColor().getBlue()));
		
		Tag temp1 = new Tag(selectedEvent.getCategory(), selectedEvent.getColor());
		boolean categoryBoxSet = false;
		for (int j = 0; j < categoryComboBox.getItemCount(); j++){
			Tag temp2 = (Tag)categoryComboBox.getItemAt(j);
			if (temp1.getTitle().equals(temp2.getTitle())){
				categoryComboBox.setSelectedItem(temp2);
				categoryBoxSet = true;
				break;
			}
		}
		
		if (!categoryBoxSet)
			categoryComboBox.setSelectedIndex(0);
		
		tagTextArea.setText(selectedEvent.getTagString());
		
		if (selectedEvent.getYear() <= 0)
			yearField.setText(Integer.toString(-selectedEvent.getYear()+1));
		else
			yearField.setText(Integer.toString(selectedEvent.getYear()));
		
		if (selectedEvent.isPresent()){
			presentCheckBox.setSelected(true);
		} else {
			presentCheckBox.setSelected(false);
			monthField.setText(Integer.toString(selectedEvent.getMonth()));
			dayField.setText(Integer.toString(selectedEvent.getDay()));
			BCCheckBox.setSelected(selectedEvent.getYear() < 0);
			
			if (selectedEvent instanceof Period){
				Period p = (Period)selectedEvent;
				month2Field.setText(Integer.toString(p.getMonth2()));
				day2Field.setText(Integer.toString(p.getDay2()));
				BCCheckBox2.setSelected(p.getYear2() < 0);
				
				if (p.getYear2() <= 0)
					year2Field.setText(Integer.toString(-p.getYear2()+1));
				else
					year2Field.setText(Integer.toString(p.getYear2()));
			} else if (selectedEvent instanceof Event){
				Event e = (Event)selectedEvent;
				isImageEventCheckBox.setSelected(e.getIsImageEvent());
			}
		}
		
		presentFieldHandler();
		
		if (!(selectedEvent instanceof Period)){
			Event e = (Event)selectedEvent;
			eventAlignmentComboBox.setSelectedItem(e.getAlignment());
		}
	}

	private void updateRTFPane(JTextPane textPane, String text, boolean editMode){
		int fontSize = editMode ? 15 : 30;
		print("updating font size to " + fontSize);
		try {
			String rtfHeader = "{\\rtf1\\ansi\\deff0" + 
			"{\\fonttbl\\f0\\fswiss\\fcharset0 Helvetica;\\f1\\fswiss\\fcharset0 Helvetica-Oblique;}" + 
			"\\fs" + (2*fontSize) + "\\pard ";
			String rtfFooter = "}";
			print("in updateRTFPane: " + text);
			String rtfString = rtfHeader + text + rtfFooter;
			print("rtfString: " + rtfString);
			ByteArrayInputStream rtfStream = new ByteArrayInputStream(rtfString.getBytes(StandardCharsets.UTF_8));
			
			RTFEditorKit rtfKit = (RTFEditorKit)textPane.getEditorKit();
			Document doc = textPane.getDocument();
			doc.remove(0, doc.getLength());
			rtfKit.read(rtfStream, doc, 0);

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void toggleStyle(JTextPane textPane, Object style){
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		if (start == end){
			// No text is selected, toggle the current typing style
			MutableAttributeSet inputAttributes = textPane.getInputAttributes();

			if (style == StyleConstants.Bold){
				try {
					int caretPos = textPane.getCaretPosition();
					if (caretPos < doc.getLength() && doc.getText(caretPos+1, 1) != "\n")
						textPane.setCaretPosition(caretPos+1);
				} catch (BadLocationException e){
					e.printStackTrace();
				}

				if (StyleConstants.isBold(inputAttributes))
					StyleConstants.setBold(inputAttributes, false);
				else
					StyleConstants.setBold(inputAttributes, true);
			} else if (style == StyleConstants.Italic){
				if (StyleConstants.isItalic(inputAttributes))
					StyleConstants.setItalic(inputAttributes, false);
				else
					StyleConstants.setItalic(inputAttributes, true);
			}
			

		} else {
			print(descriptionTextPane.getSelectionStart() + " " + descriptionTextPane.getSelectionEnd());
			Element element = doc.getCharacterElement(start);
			AttributeSet attrs = element.getAttributes();
			boolean isCurrentlySet = StyleConstants.isBold(attrs) && style.equals(StyleConstants.Bold) ||
									StyleConstants.isItalic(attrs) && style.equals(StyleConstants.Italic);

			SimpleAttributeSet newAttrs = new SimpleAttributeSet(attrs);
			StyleConstants.setBold(newAttrs, style.equals(StyleConstants.Bold) ? !isCurrentlySet : StyleConstants.isBold(attrs));
			StyleConstants.setItalic(newAttrs, style.equals(StyleConstants.Italic) ? !isCurrentlySet : StyleConstants.isItalic(attrs));

			doc.setCharacterAttributes(start, end - start, newAttrs, true);
			print(descriptionTextPane.getSelectionStart() + " " + descriptionTextPane.getSelectionEnd());
			textPane.setSelectionStart(start);
			textPane.setSelectionEnd(end);
			print(descriptionTextPane.getSelectionStart() + " " + descriptionTextPane.getSelectionEnd());
		}
	}

	private String reAddRTFDelimiters(String text, char delim){
		String delimStartText = "\\" + delim;
		String delimEndText = "\\" + delim + "0";
		int delimStart = text.indexOf(delimStartText);
		int delimEnd = text.indexOf(delimEndText);
		if (delimStart == delimEnd && delimStart != -1){
			text = delimStartText + " " + text;
		}

		return text;
	}
	
	private void presentFieldHandler(){
		if (presentCheckBox.isSelected()){
			if (selectedEvent instanceof Period){
				month2Field.setEditable(false);
				month2Field.setText(GenericEvent.today().getMonth() + "");
				day2Field.setEditable(false);
				day2Field.setText(GenericEvent.today().getDay() + "");
				year2Field.setEditable(false);
				year2Field.setText(GenericEvent.today().getYear() + "");
				BCCheckBox2.setEnabled(false);
				BCCheckBox2.setSelected(false);
			} else {
				monthField.setEditable(false);
				monthField.setText(GenericEvent.today().getMonth() + "");
				dayField.setEditable(false);
				dayField.setText(GenericEvent.today().getDay() + "");
				yearField.setEditable(false);
				yearField.setText(GenericEvent.today().getYear() + "");
				BCCheckBox.setEnabled(false);
				BCCheckBox.setSelected(false);
			}
		} else {
			monthField.setEditable(true);
			dayField.setEditable(true);
			yearField.setEditable(true);
			BCCheckBox.setEnabled(true);
			if (selectedEvent instanceof Period){
				month2Field.setEditable(true);
				day2Field.setEditable(true);
				year2Field.setEditable(true);
				BCCheckBox2.setEnabled(true);
			}
		}
	}
}
