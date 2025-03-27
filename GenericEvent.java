import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;

// most of the information about an Event or Period
public class GenericEvent implements Comparable<GenericEvent>{
	
	protected String title, description;
	protected boolean present;
	protected Date date;
	protected static String dir;
	protected Color color;
	protected String hex;
	protected String category;
	protected ArrayList<String> tags;
	protected ArrayList<MyImage> images;
	
	protected static final Font normalFont = new Font("Helvetica", Font.PLAIN, 14);
	protected static final Font boldFont = new Font("Helvetica", Font.BOLD, 14);
	protected static final Font italicFont = new Font("Helvetica", Font.ITALIC, 14);
	protected static final Font boldItalicFont = new Font("Helvetica", Font.BOLD + Font.ITALIC, 14);
		
	private static Calendar calendar = Calendar.getInstance();
    private static Date today = new Date(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
	
	//blank event/period
	public GenericEvent(){
		title = "GenericEvent";
		description = "";
		date = new Date(1, 1, 1);
		color = new Color(128, 128, 128);
		hex = RGBtoHex(color.getRed(), color.getGreen(), color.getBlue());
		category = "<category>";
		tags = new ArrayList<String>();
		images = new ArrayList<MyImage>();
		
	}
	
	public String getTitle(){ return title; }
	public Date getDate(){ return date; }
	public String getDescription(){ return description; }
	public int getMonth(){ return date.getMonth(); }
	public int getDay(){ return date.getDay(); }
	public int getYear(){ return date.getYear(); }
	public boolean isPresent(){ return present; }
	public Color getColor(){ return color; }
	public String getHex(){ return hex; }
	public String getCategory(){ return category; }
	public static Date today(){ return today; }
	public ArrayList<String> getTags(){ return tags; }
	public ArrayList<MyImage> getImages(){ return images; }
	public static Font boldFont(){ return boldFont; }
	
	public String getTagString(){
		String s = "";
		if (tags.size() == 0)
			return "none";
		
		for (int i = 0; i < tags.size(); i++)
			s += tags.get(i) + ", ";
		
		return s.substring(0, s.length()-2);
	}
	
	public String toString(int currentYear, boolean modernDating){
        return "GenericEvent";
    }
	
	public String toStringVerbose(){
        return "GenericEvent";
    }
	
	@Override
	public boolean equals(Object o){
		GenericEvent ge = (GenericEvent)o;
		return title.equals(ge.getTitle()) && date.equals(ge.getDate());
	}
	
	@Override
	public int compareTo(GenericEvent ge){
		if (date.compareTo(ge.getDate()) == 0)
			return title.compareTo(ge.getTitle());
		return date.compareTo(ge.getDate());
	}
	
	public static String RGBtoHex(int red, int green, int blue){
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		String hex = "#";
		hex += digits[red / 16];
		hex += digits[red % 16];
		hex += digits[green / 16];
		hex += digits[green % 16];
		hex += digits[blue / 16];
		hex += digits[blue % 16];
		
		return hex;
	}
	
	public static int[] HextoRGB(String hex){
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int[] rgbColors = new int[3];
		if (hex.charAt(0) == '#')
			hex = hex.substring(1);
		for (int i = 0; i < hex.length(); i++){
			for (int j = 0; j < digits.length; j++){
				char hexChar = ("abcdef".contains(""+hex.charAt(i))) ? Character.toUpperCase(hex.charAt(i)) : hex.charAt(i);
				if (hexChar == digits[j]){
					rgbColors[i / 2] += (i % 2 == 0) ? 16*j : j;
					break;
				}
			}
		}
		
		return rgbColors;
	}
	
	private ArrayList<String> setTags(ArrayList<String> localTags){
		ArrayList<String> newTags = new ArrayList<String>();
		if (localTags == null)
			return newTags;
		
		for (int i = 0; i < localTags.size(); i++){
			newTags.add(localTags.get(i));
		}
		
		return newTags;
	}
	
	public boolean addTag(String newTag){
		if (tags.size() == 0){
			tags.add(newTag);
			return true;
		}
		
		for (int i = 0; i < tags.size(); i++){
			if (tags.get(i).equals(newTag))
				return false;
			if (tags.get(i).compareTo(newTag) < 0){
				tags.add(i, newTag);
				return true;
			}
		}
		
		tags.add(newTag);
		return true;
	}
	
	// custom method that accounts for RTF
	// essentially, we cut the string into pieces based on whether they are italicized or not
	// we then stitch the pieces together so that they appear to be a single string
	public void drawString(Graphics g, int x, int y){
		Font defaultFont, altFont;
		String editedTitle = title;

		// sometimes the RTF markers will have spaces after them, and sometimes they don't
		// this makes it easier to process
		editedTitle = editedTitle.replace("\\i ", "\\i");
		editedTitle = editedTitle.replace("\\i0 ", "\\i0");
		editedTitle = editedTitle.replace("\\b ", "\\b");
		editedTitle = editedTitle.replace("\\b0 ", "\\b0");

		// this is only built to handle the entire text being bold, since this is what the bold text was intended for in this project
		// this sets the fonts to be used for the rest of the method, then removes the bold markers
		if (title.contains("\\b") && title.contains("\\b0")){
			// drawString actually draws several pixels to the right for bolded/italicized text, so this needs to be accounted for
			x -= 2;
			defaultFont = boldFont;
			altFont = boldItalicFont;
			editedTitle = editedTitle.replace("\\b0", "").replace("\\b", "");
			
		} else {
			defaultFont = normalFont;
			altFont = italicFont;
		}
		
		// breaks string into italicized and standard pieces, then draws them separately
		if (title.contains("\\i") && title.contains("\\i0")){
			// the ending of the previous piece
			int prevStringX = x;
			do {
				if (editedTitle.indexOf("\\i") == 0){
					g.setFont(altFont);
					String frag = editedTitle.substring(editedTitle.indexOf("\\i")+2, editedTitle.indexOf("\\i0"));
					g.drawString(frag, prevStringX, y);
					prevStringX += g.getFontMetrics().stringWidth(frag);
					// drawString actually draws several pixels to the right for bolded/italicized text, so this needs to be accounted for
					prevStringX += 2;
					editedTitle = editedTitle.substring(editedTitle.indexOf("\\i0")+3);
				} else {
					g.setFont(defaultFont);
					String frag = editedTitle.substring(0, editedTitle.indexOf("\\i"));
					g.drawString(frag, prevStringX, y);
					prevStringX += g.getFontMetrics().stringWidth(frag);
					prevStringX -= 2;
					editedTitle = editedTitle.substring(editedTitle.indexOf("\\i"));
				}
			} while (editedTitle.contains("\\i") && editedTitle.contains("\\i0"));
			
			// if the last part of the string is non-italicized, draw that as well
			if (editedTitle.length() > 0){
				g.setFont(defaultFont);
				g.drawString(editedTitle, prevStringX, y);
			}
		} else {
			g.setFont(defaultFont);
			g.drawString(editedTitle, x, y);
		}
	}
	
	// determine the length of the title, accounting for the varying width of bold and italic segments
	// this also removes the RTF markers that are not displayed as text, so should not contribute to the length
	// this method is similar to drawString
	public int formattedLength(Graphics g){
		Font tempFont = g.getFont();
		int formattedStringWidth = 0;
		
		Font defaultFont, altFont;
		String editedTitle = title;

		// similar to drawString, standardize the RTF formatting and set up the fonts
		editedTitle = editedTitle.replace("\\i ", "\\i");
		editedTitle = editedTitle.replace("\\i0 ", "\\i0");
		editedTitle = editedTitle.replace("\\b ", "\\b");
		editedTitle = editedTitle.replace("\\b0 ", "\\b0");
		if (title.contains("\\b") && title.contains("\\b0")){
			defaultFont = boldFont;
			altFont = boldItalicFont;
			editedTitle = editedTitle.replace("\\b0", "").replace("\\b", "");
			// editedTitle = title.substring(title.indexOf("\\b")+2, title.indexOf("\\b0"));
		} else {
			defaultFont = normalFont;
			altFont = italicFont;
			editedTitle = title;
		}
		
		if (title.contains("\\i") && title.contains("\\i0")){
			do {
				if (editedTitle.indexOf("\\i") == 0){
					g.setFont(altFont);
					formattedStringWidth += g.getFontMetrics().stringWidth(editedTitle.substring(editedTitle.indexOf("\\i")+2, editedTitle.indexOf("\\i0")));
					editedTitle = editedTitle.substring(editedTitle.indexOf("\\i0")+3);
				} else {
					g.setFont(defaultFont);
					// System.out.println(editedTitle);
					formattedStringWidth += g.getFontMetrics().stringWidth(editedTitle.substring(0, editedTitle.indexOf("\\i")));
					editedTitle = editedTitle.substring(editedTitle.indexOf("\\i"));
				}
			} while (editedTitle.contains("\\i") && editedTitle.contains("\\i0"));
			
			if (editedTitle.length() > 0){
				g.setFont(defaultFont);
				formattedStringWidth += g.getFontMetrics().stringWidth(editedTitle);
			}
		} else {
			g.setFont(defaultFont);
			formattedStringWidth = g.getFontMetrics().stringWidth(editedTitle);
		}
		
		g.setFont(tempFont);
		return formattedStringWidth;
	}
	
	protected String capitalize(String s){
		String path = "";
		try{
			path = dir + "/proper_nouns.txt";
			File dictionary = new File(path);
			Scanner sc = new Scanner(dictionary);
			while (sc.hasNextLine()){
				String properNoun = sc.nextLine();
				String stringToReplace = properNoun.toLowerCase();
				stringToReplace = stringToReplace.replaceAll("á", "a");
				stringToReplace = stringToReplace.replaceAll("é", "e");
				stringToReplace = stringToReplace.replaceAll("í", "i");
				stringToReplace = stringToReplace.replaceAll("ñ", "n");
				stringToReplace = stringToReplace.replaceAll("ó", "o");
				stringToReplace = stringToReplace.replaceAll("ú", "u");
				s = s.replaceAll(stringToReplace, properNoun);
			}
		} catch (FileNotFoundException ex){
			// System.err.println("FileNotFoundException in GenericEvent.capitalize(): could not find " + path);
		}
			
		return s;
	}
	
	public void addImage(String imageName, String caption){
		imageName = truncatePath(imageName);
		images.add(new MyImage(imageName, caption));
	}
	
	public void deleteImage(int index){
		images.remove(index);
	}
	
	public void setImage(int index, String imageName, String caption){
		imageName = truncatePath(imageName);
		images.set(index, new MyImage(imageName, caption));
	}

	// sets the path for the directory
	// this should only be run once
	public static void setCapListPath(String path){
		if (path == null || dir != null){
			return;
		}

		dir = path;
	}

	private String truncatePath(String imageName){
		String delim = "Timeline/";
		int loc = Math.max(0, imageName.indexOf(delim) + delim.length());
		return imageName.substring(loc);
	}
}