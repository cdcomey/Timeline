import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.util.TreeSet;
import java.util.Iterator;

/*
TIMELINE STRUCTURE
the first part is the center line, the "line" part of the timeline
it will be long and skinny, and centered on the screen

on either end of the center line will be two triangles, indicating that the timeline continues past where we can see it on screen
their midpoint will have the same y-coordinate as the center of the center line

periodically protruding from the center line are notches with dates above them
they should be tall, skinny, and evenly dispersed over the center line, and should never overlap with the triangles
*/

public class Timeline{
	
	private final int screenWidth, screenHeight;
	private final int lineLength, connectingLineHeight, connectingLineWidth, notchWidth, notchHeight;
	private final int[] T1xCoords, T1yCoords, T2xCoords;
	private Color timelineColor;
	
	private int centerYear, zoomLevel, lineCenter, connectingLineY;
	private int numberOfNotches, newLineWidth, space, notchPosition, notchX, notchY;
	private int currentYear, currentYearLength;
	private Date leftDate, rightDate;
	
	private Iterator<Event> it;
	private DLList<Rectangle> drawnEventCoordinates, drawnPeriodCoordinates, drawnLineCoordinates;
	
	public Timeline(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		centerYear = 1492;							// the initial year at the center of the screen, can (and likely will) be changed by the user
		zoomLevel = 4;									// determines how many years are shown on-screen at one time, can be changed by the user
		lineCenter = screenHeight*1/2;			// y-coordinate of the top-left corner of the center line
		lineLength = 25;								// used for determining dimensions of the triangles on the ends of the line
		notchWidth = 3;								// how wide each notch is
		notchHeight = 10;								// how tall each notch is
		
		//determine the coordinates of the left arrow to be drawn on the timeline
		T1xCoords = new int[3];																// being a triangle, it of course has three sides
		T1xCoords[0] = 0;																		// one point will always be on the very left side of the screen
		T1xCoords[1] = screenWidth / (int)(lineLength * Math.sqrt(2));		// the next is a certain distance away, so that the triangle is equilateral
		T1xCoords[2] = T1xCoords[1];														// the last will be just below the second
		
		// the triangles and lines may be shifted up and down by the user, so the height is not static and so should 
		T1yCoords = new int[3];
		initializeYCoords();
		
		//determine the coordinates of the right arrow to be drawn on the timeline
		T2xCoords = new int[3];
		T2xCoords[0] = screenWidth;
		T2xCoords[1] = screenWidth - (screenWidth / (int)(lineLength * Math.sqrt(2)));
		T2xCoords[2] = screenWidth - (screenWidth / (int)(lineLength * Math.sqrt(2)));
		
		// find the coordinates of the line connecting the two arrows
		connectingLineHeight = screenHeight / (5*lineLength);
		connectingLineWidth = T2xCoords[2]-T1xCoords[2];
			
		numberOfNotches = zoomLevel+1;
		newLineWidth = connectingLineWidth - notchWidth * numberOfNotches;
		space = newLineWidth / (numberOfNotches-1);
		initializeNotchData();
	}
	
	private void drawBasicLine(Graphics g){
		// draw the arrows on the edges of the timeline
		g.setColor(timelineColor);
		g.fillPolygon(T1xCoords, T1yCoords, 3);
		g.fillPolygon(T2xCoords, T1yCoords, 3);
		
		// draw the line connecting the arrows, or the actual timeline
		g.fillRect(T1xCoords[2], connectingLineY, T2xCoords[2]-T1xCoords[2], connectingLineHeight);
	}
	
	private void drawNotchAndYear(Graphics g, int i, boolean modernDating){
			//draw the notches
			notchX = i*(space+notchWidth) + T1xCoords[2];
			g.setColor(timelineColor);
			g.fillRect(notchX, notchY, notchWidth, notchHeight);
			
			//draw the years above the notches
			currentYear = centerYear + notchPosition;
			// System.out.println("currentYear (" + currentYear + ") = centerYear (" + centerYear + ") + notchPosition (" + notchPosition + ")");
			// System.out.println("i: " + i);
			currentYearLength = Date.isLeapYear(currentYear) ? 366 : 365;	// we will need this to position events on the timeline later
			String yearString = Date.yearString(currentYear, modernDating);
			g.setFont(Event.boldFont());
			g.drawString(yearString, notchX - g.getFontMetrics().stringWidth(yearString) / 2, notchY - g.getFontMetrics().getHeight());
	}
	
	private void findPeriodCoords(Graphics g, TreeSet<Event> eventSet, String[] tags, byte taggedEventsVisibility){
		it = eventSet.iterator();
		
		leftDate = new Date(1, 1, centerYear - (numberOfNotches / 2));		// the leftmost visible date on the timeline
		rightDate = new Date(1, 1, centerYear + ((numberOfNotches-1) / 2));	// the rightmost visible date on the timeline
		int periodX1 = -1;	// default value
		int periodX2 = -1;	// default value
		int periodY1 = -1;	// default value
		int periodHeight = 30;	
		
		while (it.hasNext()){	// the condition doesn't really matter, we will be exiting with break statements
			Event currentPeriod = it.next();
			if (!currentPeriod.getIsPeriod()) // we are only concerned with periods right now, so we ignore non-periods
				continue;
			
			Date periodDate = currentPeriod.getDate();	// the date on which the period starts
			Date periodDate2 = currentPeriod.isPresent() ? Event.today() : currentPeriod.getDate2();	// the date on which the period ends
			
			// if the start date is after the rightmost visible date, then it should not be visible on the timeline, so we skip it
			if (periodDate.compareTo(rightDate) > 0)
				break;	//since the periods are sorted, no periods after this will ever appear on the timeline, so the rest are skipped
			
			// if the end date is left of the leftmost visible date, then it should not be visible on the timeline, so we skip it
			if (periodDate2.compareTo(leftDate) < 0)
				continue;
			
			// if (currentPeriod.getTags().size() > 0)
				// System.out.println(currentPeriod.getTitle());
			
			boolean skipPeriod = false;
			
			// this block of code hides any event with a tag the user has said they wanted to hide
			if (taggedEventsVisibility == -1){
				System.out.println("attempting to hide tagged events");
				for (String each : tags){
					for (int j = 0; j < currentPeriod.getTags().size(); j++){
						System.out.println("\tcomparing '" + each + "' against '" + currentPeriod.getTags().get(j) + "'");
						if (each.equals(currentPeriod.getTags().get(j))){
							System.out.println("\t\tmatch found");
							skipPeriod = true;
							break;
						}
					}
					
					if (skipPeriod)
						break;
				}
			}
			
			// similarly, this block of code hides any event that does not have a tag the user has said they want to see
			else if (taggedEventsVisibility == 1){
				skipPeriod = true;
				// System.out.println("running tag checks for " + currentPeriod.getTitle());
				for (String each : tags){
					for (int j = 0; j < currentPeriod.getTags().size(); j++){
						// System.out.println("\tcomparing '" + each + "' against '" + currentPeriod.getTags().get(j) + "'");
						if (each.equals(currentPeriod.getTags().get(j))){
							// System.out.println("\tmatch found");
							skipPeriod = false;
							break;
						}
					}
					
					if (!skipPeriod)
						break;
				}
			}
			
			if (skipPeriod)
				continue;
			
			// at this point, we have gone through (mostly) every reason we have to not draw a period, so will will now begin actually drawing it`
			// the bulk of the work from here will be figuring out where the period will be drawn
			// the periods are drawn as rectangles, so we will need to know two x values and two y values
			g.setColor(currentPeriod.getColor());
			periodY1 = connectingLineY + connectingLineHeight + notchWidth;	// coordinate of the top edge
			
			if (periodDate.compareTo(leftDate) < 0)	// if the period started before the leftmost visible date, we will snap its left side to a set point, otherwise it may overlap with the triangle at the edge of the timeline
				periodX1 = T1xCoords[1];
			else																// otherwise, use this formula to determine its left edge
				periodX1 = (int)((periodDate.getYear() - leftDate.getYear())*(space+notchWidth) + T1xCoords[2] + currentPeriod.getDate().getDayOfYear() * ((space+notchWidth) / (double)currentYearLength));
			
			if (periodDate2.compareTo(rightDate) > 0)	// similarly, if the period ended after the rightmost visible date, it may overlap with the other triangle, so we'll stop it before it flows over
				periodX2 = T2xCoords[1];
			else																	// otherwise, use a similar formula to the above to find its right edge
				periodX2 = (int)((periodDate2.getYear() - leftDate.getYear())*(space+notchWidth) + T1xCoords[2] + periodDate2.getDayOfYear() * ((space+notchWidth) / (double)currentYearLength));
			
			// the x-coords are now finalized
			// next is to shift the period down until it does not overlap with any other drawn periods
			for (int j = 0; j < drawnPeriodCoordinates.size(); j++){
				Rectangle rect = drawnPeriodCoordinates.get(j);
				/* System.out.println("comparing '" + currentPeriod.getTitle() + "' against '" + rect.getEvent().getTitle() + "'");
				System.out.println("\t" + periodX2							 + " >= " + rect.getX() 	+ " : " + (periodX2 >= rect.getX()));
				System.out.println("\t" + periodX1							 + " <= " + rect.getX2() 	+ " : " + (periodX1 <= rect.getX2()));
				System.out.println("\t" + (periodY1 + periodHeight) + " >= " + rect.getY() 	+ " : " + (periodY1 + periodHeight >= rect.getY()));
				System.out.println("\t" + periodY1							 + " <= " + rect.getY2() 	+ " : " + (periodY1 <= rect.getY2())); */
				if (periodX2 > rect.getX() && periodX1 < rect.getX2() && (periodY1 + periodHeight) >= rect.getY() && periodY1 <= rect.getY2()){
					periodY1 += periodHeight + 5;
					j=0;
					/* System.out.println("\tmoving '" + currentPeriod.getTitle() + "' down");
					System.out.println("\tnew y: " + periodY1); */
				}
			}
		
			//if the only visible part of the period is just a sliver on the edge of the timeline, it's too small to be worth showing, so it's skipped
			// System.out.println(currentPeriod.getTitle());
			// System.out.println("condition 1: " + periodX2 + " - " + periodX1 + " == " + (periodX2-periodX1) + " < 10 : " + (periodX2 - periodX1 < 10));
			// System.out.println("condition 2: " + periodDate2.shortForm() + " " + (periodDate2.compareTo(leftDate) == 0));
			// System.out.println("condition 3: " + (periodDate.getYear() != periodDate2.getYear()));
			if (periodX2 - periodX1 < 10 && (periodDate2.compareTo(leftDate) == 0 || periodDate.compareTo(rightDate) == 0) && periodDate.getYear() != periodDate2.getYear())
				continue;

			//now that the coords of the period have been found, add it to the list of periods to draw
			drawnPeriodCoordinates.add(new Rectangle(periodX1, periodY1, periodX2-periodX1, periodHeight, currentPeriod, false, currentPeriod.getColor()));
			
			//if the period started before or ended after the border of the timeline on screen, add a dashed line to the end to indicate it went on longer
			if (periodDate.compareTo(leftDate) < 0)
				drawnLineCoordinates.add(new Rectangle(0, periodY1+(periodHeight+notchWidth)/2, periodX1, notchWidth, currentPeriod.getColor()));
			if (periodDate2.compareTo(rightDate) > 0)
				drawnLineCoordinates.add(new Rectangle(periodX2, periodY1+(periodHeight+notchWidth)/2, screenWidth-periodX2, notchWidth, currentPeriod.getColor()));
				
			int titleLength = currentPeriod.formattedLength(g);
			if (titleLength >= (periodX2-periodX1-5)){
				//find coordinates and dimensions of the new rectangle
				periodX2 = periodX1 + titleLength + 10;
				int oldPeriodY1 = periodY1;
				periodY1 += 2*periodHeight + 10;
				for (int j = 0; j < drawnPeriodCoordinates.size(); j++){
					Rectangle rect = drawnPeriodCoordinates.get(j);
					/* System.out.println("comparing '" + currentPeriod.getTitle() + "' against '" + rect.getEvent().getTitle() + "'");
					System.out.println("\t" + periodX2								+ " >= " + rect.getX()		+ " : " + (periodX2 >= rect.getX()));
					System.out.println("\t" + periodX1								+ " <= " + rect.getX2()	+ " : " + (periodX1 <= rect.getX2()));
					System.out.println("\t" + (periodY1 + periodHeight)	+ " >= " + rect.getY()		+ " : " + (periodY1 + periodHeight >= rect.getY()));
					System.out.println("\t" + periodY1								+ " <= " + rect.getY2()	+ " : " + (periodY1 <= rect.getY2())); */
					if (periodX2 > rect.getX() && periodX1 < rect.getX2() && periodY1 + periodHeight >= rect.getY() && periodY1 <= rect.getY2()){
						periodY1 += periodHeight + 5;
						// System.out.println("\tall checks passed - moving event down");
					}
				}
				
				// System.out.println((oldPeriodY1+periodHeight+5) + " >= " + periodY1);
				if (oldPeriodY1 + periodHeight + 5 >= periodY1)
					periodY1 += periodHeight + 5;
				// System.out.println("original '"		+ currentPeriod.getTitle() + "' : (" + periodX1 + ", " + oldPeriodY1	+ ", " + periodX2 + ", " + (periodY1+periodHeight) + ")");
				// System.out.println("detached '"	+ currentPeriod.getTitle() + "' : (" + periodX1 + ", " + periodY1			+ ", " + periodX2 + ", " + (periodY1+periodHeight) + ")");
				drawnPeriodCoordinates.add(new Rectangle(periodX1, periodY1, periodX2-periodX1, periodHeight, currentPeriod, true, currentPeriod.getColor()));
				drawnLineCoordinates.add(new Rectangle(periodX1, oldPeriodY1+periodHeight, notchWidth, periodY1 - (oldPeriodY1+periodHeight), currentPeriod.getColor()));
			}
		}
	}
	
	private void findEventCoords(Graphics g, TreeSet<Event> eventSet, String[] tags, byte taggedEventsVisibility){
		it = eventSet.iterator();
		int eventX1 = -1;
		int eventX2 = -1;
		int eventY1 = -1;
		int eventHeight = 30;
		int lineX = -1;
		
		if (Event.today().getYear() == currentYear){
			eventX1 = (int)((currentYear - leftDate.getYear())*(space+notchWidth) + T1xCoords[2] + Event.today().getDayOfYear() * ((space+notchWidth) / (double)currentYearLength));
			drawnLineCoordinates.add(new Rectangle(eventX1, 0, notchWidth, screenHeight, timelineColor));
		}
		
		while (it.hasNext()){
			Event currentEvent = it.next();
			if (currentEvent == null || currentEvent.getIsPeriod())
				continue;
			
			boolean skipEvent = false;
				
				if (taggedEventsVisibility == -1){
					// System.out.println("attempting to hide tagged '" + currentEvent.getTitle() + "'");
					for (String each : tags){
						for (int j = 0; j < currentEvent.getTags().size(); j++){
							// System.out.println("\tcomparing '" + each.getText() + "' against '" + currentEvent.getTags().get(j) + "'");
							if (each.equals(currentEvent.getTags().get(j))){
								// System.out.println("\t\tmatch found");
								skipEvent = true;
								break;
							}
						}
						
						if (skipEvent)
							break;
					}
				}
				
				else if (taggedEventsVisibility == 1){
					skipEvent = true;
					for (String each : tags){
						for (int j = 0; j < currentEvent.getTags().size(); j++){
							// System.out.println("\tcomparing '" + each.getText() + "' against '" + currentEvent.getTags().get(j) + "'");
							if (each.equals(currentEvent.getTags().get(j))){
								// System.out.println("\tmatch found");
								skipEvent = false;
								break;
							}
						}
						
						if (!skipEvent)
							break;
					}
				}
				
				if (skipEvent)
					continue;
			
			if (currentEvent.getDate().compareTo(rightDate) > 0)
				break;
			
			if (currentEvent.getYear() == currentYear){
				g.setColor(currentEvent.getColor());
				// System.out.println(currentYear + " is " + currentYearLength + " days");
				// System.out.println(currentYear + ": " + currentEvent.getTitle());
				// System.out.print("(" + currentYear + " - " + leftDate.getYear() + ") * (" + space + " + " + notchWidth + ")");
				// System.out.println(" + " + T1xCoords[2] + " + " + currentEvent.getDate().getDayOfYear() + " * (" + space + " + " + notchWidth + ") / " + currentYearLength);
				lineX = (int)((currentYear - leftDate.getYear())*(space+notchWidth) + T1xCoords[2] + currentEvent.getDate().getDayOfYear() * ((space+notchWidth) / (double)currentYearLength));
				// System.out.println("lineX : " + lineX);
				
				int titleLength = currentEvent.formattedLength(g);
				
				int leftAlignX = lineX;
				int centerAlignX = lineX - (titleLength + notchWidth)/2;
				int rightAlignX = lineX - titleLength;
				int width = titleLength + 10;
				String alignment = currentEvent.getAlignment();
				
				if (rightAlignX + width >= screenWidth || leftAlignX <= 0)
					continue;
				
				// System.out.println((alignment.equals("Left-aligned") + " && " + (leftAlignX + width > screenWidth)) + " || " + (centerAlignX < 0));
				
				if ((alignment.equals("Left-aligned") && (leftAlignX + width <= screenWidth)) || centerAlignX < 0)
					eventX1 = leftAlignX;
				else if ((alignment.equals("Left-aligned") && centerAlignX <= screenWidth && leftAlignX > screenWidth) || 
				(alignment.equals("Centered") && centerAlignX >= 0 && centerAlignX+width <= screenWidth) || 
				(alignment.equals("Right-aligned") && centerAlignX >= 0 && rightAlignX < 0))
					eventX1 = centerAlignX;
				else if (centerAlignX + width > screenWidth || (alignment.equals("Right-aligned") && leftAlignX >= 0))
					eventX1 = rightAlignX;
				
				// System.out.println("eventX1: " + eventX1);
				eventX2 = eventX1 + width;
				eventY1 = connectingLineY - eventHeight - 40;
				
				//find starting y
				for (int j = 0; j < drawnEventCoordinates.size(); j++){
					if (drawnEventCoordinates.get(j) != null){
						Rectangle rect = drawnEventCoordinates.get(j);
						/* System.out.println("checking '" + currentEvent.getTitle() + "' against '" + rect.getEvent().getTitle() + "'");
						System.out.println("\t" + eventX2 + " >= " + rect.getX() + " : " + (eventX2 >= rect.getX()));
						System.out.println("\t" + eventX1 + " <= " + (rect.getX()+rect.getWidth()) + " : " + (eventX1 <= rect.getX() + rect.getWidth()));
						System.out.println("\t" + (eventY1+eventHeight) + " >= " + rect.getY() + " : " + (eventY1 + eventHeight >= rect.getY()));
						System.out.println("\t" + eventY1 + " <= " + (rect.getY()+rect.getHeight()) + " : " + (eventY1 <= rect.getY() + rect.getHeight())); */
						if (eventX2 >= rect.getX() && eventX1 <= rect.getX() + rect.getWidth() && eventY1 + eventHeight >= rect.getY() && eventY1 <= rect.getY() + rect.getHeight()){
							eventY1 = rect.getY() - rect.getHeight() - 5;
							j = 0;
							// System.out.println("\tmoving event '" + currentEvent.getTitle() + "' up : new coords (" + eventX1 + ", " + eventY1 + ", " + eventX2 + ", " + (eventY1+eventHeight) + ")");
						}
					}
				}
				
				drawnEventCoordinates.add(new Rectangle(eventX1, eventY1, eventX2-eventX1, eventHeight, currentEvent, false, currentEvent.getColor()));
				drawnLineCoordinates.add(new Rectangle(lineX, eventY1+eventHeight, notchWidth, connectingLineY - (eventY1+eventHeight), currentEvent.getColor()));
				
				// System.out.println(notchX  + " + " + currentEvent.getDayOfYear() + " * " + ((space+notchWidth) / currentYearLength));
				// System.out.println("(" + eventX1 + ", " + eventY1 + ", " + (eventX2-eventX1) + ", " + eventHeight + ")");
				// g.fillRect(eventX1, eventY1, g.getFontMetrics().stringWidth(currentEvent.getTitle()), eventHeight);
				// System.out.println(currentEvent.getTitle().substring(0, currentEvent.getTitle().length()-1));
				// System.out.println("\t\t(" + eventX1 + ", " + eventY1 + ", " + eventX2 + ", " + (eventY1 + eventHeight) + ")");
				// System.out.println(currentEvent.toStringVerbose());
			}
		}	
	}
	
	public void drawTimeline(Graphics g, TreeSet<Event> eventSet, String[] tags, byte taggedEventsVisibility, boolean modernDating, boolean darkMode){
		timelineColor = darkMode ? Color.white : Color.black;
		
		drawBasicLine(g);
		
		drawnEventCoordinates = new DLList<Rectangle>();
		
		// notch position is based on the one at the center of the timeline, out of the ones currently on screen
		notchPosition = numberOfNotches / -2;
		
		for (int i = 0; i < numberOfNotches; i++, notchPosition++){
			drawNotchAndYear(g, i, modernDating);
			
			drawnPeriodCoordinates = new DLList<Rectangle>();
			drawnLineCoordinates = new DLList<Rectangle>();
			
			//periods
			findPeriodCoords(g, eventSet, tags, taggedEventsVisibility);
			
			//events
			findEventCoords(g, eventSet, tags, taggedEventsVisibility);
			// System.out.println(centerYear + " - (" + numberOfNotches + " / 2) = " + deviation);
			
			for (int j = 0; j < drawnLineCoordinates.size(); j++){
				Rectangle rect = drawnLineCoordinates.get(j);
				g.setColor(rect.getColor());
				rect.drawDashedLine(g, notchHeight, notchWidth);
			}
			
			for (int j = 0; j < drawnPeriodCoordinates.size(); j++){
				Rectangle rect = drawnPeriodCoordinates.get(j);
				rect.drawMe(g);
				if (rect.isDetached() || rect.getEvent().formattedLength(g) < rect.getWidth() - 5){
					g.setColor(setTextColor(rect.getEvent().getColor()));
					rect.getEvent().drawString(g, rect.getX() + 5, rect.getY() + g.getFontMetrics().getHeight() + 3);
					// if (rect.isDetached())
						// drawDashedLine(g, periodX1, oldPeriodY1 + periodHeight, notchWidth, periodY1 - (oldPeriodY1 + periodHeight), periodHeight/2, 5, g.getColor());
				}
			}
			
			// System.out.println(currentYear + ": " + drawnEventCoordinates.size());
			
			for (int j = 0; j < drawnEventCoordinates.size(); j++){
				Rectangle rect = drawnEventCoordinates.get(j);
				rect.drawMe(g);
				g.setColor(setTextColor(rect.getColor()));
				rect.getEvent().drawString(g, rect.getX() + 5, rect.getY() + g.getFontMetrics().getHeight() + 3);
			}
		}
		// System.out.println("//////////////////////////////////////////////////////////////////////////////");
	}
	
	public int getCenterYear(){ return centerYear; }
	public DLList<Rectangle> getDrawnEventCoordinates(){ return drawnEventCoordinates; }
	public DLList<Rectangle> getDrawnPeriodCoordinates(){ return drawnPeriodCoordinates; }
	public void setCenterYear(int c){ centerYear = c; }
	
	public void zoomOut(){
		zoomLevel++;
		initializeNotchData();
	}
	public void zoomIn(){
		zoomLevel = Math.max(1, zoomLevel-1);
		initializeNotchData();
	}
	
	public void shiftTimelineUp(){
		lineCenter -= 10;
		initializeYCoords();
	}
	
	public void shiftTimelineDown(){
		lineCenter += 10;
		initializeYCoords();
	}
	
	private void initializeYCoords(){
		T1yCoords[0] = lineCenter;
		T1yCoords[1] = (int)(lineCenter + screenHeight / (lineLength * Math.sqrt(2)));
		T1yCoords[2] = (int)(lineCenter - screenHeight / (lineLength * Math.sqrt(2)));
		connectingLineY = (T1yCoords[1]+T1yCoords[2]-connectingLineHeight)/2;
		notchY = T1yCoords[0]-notchHeight;
	}
	
	private void initializeNotchData(){
		numberOfNotches = zoomLevel+1;
		newLineWidth = connectingLineWidth - notchWidth * numberOfNotches;
		space = newLineWidth / (numberOfNotches-1);
	}
	
	private Color setTextColor(Color bgColor){
		return (bgColor.getRed() * 0.299f + bgColor.getGreen() * 0.670 + bgColor.getBlue() * 0.114f > 150) ? new Color(0, 0, 0) : new Color(255, 255, 255);
	}
}