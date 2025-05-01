import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

enum FontType {
	DEFAULT,
	ITALIC,
	BOLD,
	BOLDITALIC
}

class FormattedString{
	public FontType fontType;
	public String text;

	public FormattedString(FontType fontType, String text){
		this.fontType = fontType;
		this.text = text;
	}
}

// most of the information about an Event or Period
public class GenericEvent implements Comparable<GenericEvent>{
	
	protected String title, description;
	protected FormattedString[] splitTitle;
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
		splitTitle = splitRTFString(title);
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

	private static void saveFrag(boolean isBold, boolean isItalic, String frag, ArrayList<FormattedString> fragList){
		if (frag.isEmpty())
			return;
		FontType font = FontType.values()[(isBold ? 2 : 0) + (isItalic ? 1 : 0)];
		fragList.add(new FormattedString(font, frag));
	}

	protected static FormattedString[] splitRTFString(String title){
		String editedTitle = title.replaceAll("\\\\([bi]0?)\\s", "\\\\$1");

		boolean isBold = false;
		boolean isItalic = false;
		int lastPos = 0;
		ArrayList<FormattedString> result = new ArrayList<FormattedString>();

		Pattern pattern = Pattern.compile("\\\\(b0?|i0?)");
		Matcher matcher = pattern.matcher(editedTitle);

		while (matcher.find()){
			String control = matcher.group(1);
			int matchStart = matcher.start();

			String frag = editedTitle.substring(lastPos, matchStart);
			saveFrag(isBold, isItalic, frag, result);

			switch (control) {
				case "b" -> isBold = true;
				case "b0" -> isBold = false;
				case "i" -> isItalic = true;
				case "i0" -> isItalic = false;
			}

			lastPos = matcher.end();
		}

		if (lastPos < editedTitle.length()){
			saveFrag(isBold, isItalic, editedTitle.substring(lastPos), result);
		}

		return result.toArray(new FormattedString[0]);
	}

	public void drawString(Graphics g, int x, int y){
		for (FormattedString s : splitTitle){
			// System.out.println(s.fontType + " | " + s.text);
			switch (s.fontType){
				case DEFAULT:
					g.setFont(normalFont);
					g.drawString(s.text, x, y);
					x += g.getFontMetrics().stringWidth(s.text);
					// x += 2;
					break;
				case ITALIC:
					g.setFont(italicFont);
					g.drawString(s.text, x, y);
					x += g.getFontMetrics().stringWidth(s.text);
					// x -= 2;
					break;
				case BOLD:
					g.setFont(boldFont);
					g.drawString(s.text, x, y);
					x += g.getFontMetrics().stringWidth(s.text);
					break;
				case BOLDITALIC:
					g.setFont(boldItalicFont);
					g.drawString(s.text, x, y);
					x += g.getFontMetrics().stringWidth(s.text);
					break;
			}
		}
	}
	
	// determine the length of the title, accounting for the varying width of bold and italic segments
	public int formattedLength(Graphics g){
		Font storageFont = g.getFont();
		int length = 0;
		for (FormattedString s : splitTitle){
			switch (s.fontType){
				case DEFAULT -> g.setFont(normalFont);
				case ITALIC -> g.setFont(italicFont);
				case BOLD -> g.setFont(boldFont);
				case BOLDITALIC -> g.setFont(boldItalicFont);
			}

			length += g.getFontMetrics().stringWidth(s.text);
		}

		g.setFont(storageFont);
		return length;
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