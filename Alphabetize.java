import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class Alphabetize{
	public static void main(String[] args){
		String s = "";
		File file = new File("proper_nouns.txt");
		try (FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)) {
					
			s = new String(bis.readAllBytes());
			String[] arr = s.split("\n");
			
			for (int i = 0; i < arr.length - 1; i++){
				for (int j = i+1; j < arr.length; j++){
					if (arr[i].compareTo(arr[j]) > 0){
						String temp = arr[j];
						arr[j] = arr[i];
						arr[i] = temp;
					}
				}
			}
			
			s = "";
			for (String each : arr){
				s += "" + each + "\n";
			}
		} catch (IOException e) {
			System.err.println("IOException occurred in reading file");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
			System.err.println("IndexOutOfBoundsException occurred in reading file");
			e.printStackTrace();
		}
		
		try(FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            //convert string to byte array
            byte[] bytes = s.getBytes();
            //write byte array to file
            bos.write(bytes);
            bos.close();
            fos.close();
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
			System.err.println("IOException occurred in writing to file");
            e.printStackTrace();
        }
	}
}
