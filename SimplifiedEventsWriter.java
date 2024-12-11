import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class SimplifiedEventsWriter{
	public static void main(String[] args){
			File input = new File("events.txt");
		try (FileInputStream fis = new FileInputStream(input);
                BufferedInputStream bis = new BufferedInputStream(fis)) {
			String s = new String(bis.readAllBytes());
			String[] eventArr = s.split("\nend\n\n");
			s = "";
			for (int i = 0; i < eventArr.length; i++){
				String event = eventArr[i];
				s += event.substring(0, event.indexOf("isPeriod: "));
				s += "\n\n";
			}
			
			File output = new File("simplified_events.txt");
			if (!output.exists())
				output.createNewFile();
			FileWriter fw = new FileWriter(output.getName(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(s);
			bw.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
}