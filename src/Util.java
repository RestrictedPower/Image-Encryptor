package me.RestrictedPower.ImageEncryptor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

public class Util {
	public static HashMap<Character, Integer> charToKeyMap = new HashMap<Character, Integer>();
	public static HashMap<Integer, Character> keyToCharMap = new HashMap<Integer, Character>();
	
	/* Maps all the available characters to integers and vice versa */
	public static void initValidChars() {
		HashSet<Character> allValid = new HashSet<Character>();
		char[] special = "/*!@#$%^&*()\"{}_[]|\\?/<>,.:;'-+=`^~".toCharArray();
		for(char c : special) allValid.add(c);
		for(int i = 'a'; i<='z'; i++) allValid.add((char)(i));
		for(int i = 'A'; i<='Z'; i++) allValid.add((char)(i));
		for(int i = '0'; i<='9'; i++) allValid.add((char)(i));
		allValid.add('\n');
		allValid.add(' ');
		int idx = 0;
		for(char v : allValid) {
			charToKeyMap.put(v, idx);
			keyToCharMap.put(idx++, v);
		}
	}
	
	/* Function that returns the path of a selected .png file or null if the selected file is not .png */
	public static String chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new ImageFilter());
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION)  return chooser.getSelectedFile().getAbsolutePath();
        return null;
	}
	
	/* Filter for .png files */
	private static class ImageFilter extends FileFilter {
		   @Override
		   public boolean accept(File f) {
		      if (f.isDirectory()) return true;
		      String extension = getExtension(f);
		      if (extension == null)  return false;
		      if (extension.equals("png")) return true;
		      return false;
		   }

		   @Override
		   public String getDescription() {
		      return "PNG files (.png)";
		   }
	}
	
	/* Returns the extension of a file */
	public static String getExtension(File f) {
	      String ext = null;
	      String s = f.getName();
	      int i = s.lastIndexOf('.');
	      if (i > 0 &&  i < s.length() - 1)  ext = s.substring(i+1).toLowerCase();
	      return ext;
	}
	
	/* Set a better look and feel for the UI */
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            try {
						UIManager.setLookAndFeel(info.getClassName());
		            } catch (Exception e2) { }
		            break;
		        }
		    }
		}
	}
	
	/* Confirms if a given String contains only valid characters */
	public static boolean isValid(String text) {
		for(char c : text.toCharArray()) if(!charToKeyMap.containsKey(c)) return false;
		return true;
	}
	
	/* Matches a unique integer from  in range 0 to (256^3-1) to a unique rgb color model */
	public static int[] intToRgb(int v) {
		int b = v&255;
		int g = (v >> 8) & 255;
		int r = (v >> 16) & 255;
		return new int[]{r,g,b};
	}
	
	/* Matches a unique rgb color model to a unique integer in range 0 to (256^3-1) - reverse function of intToRgb */
	public static int rgbToInt(int r, int g, int b) {
		return  ((r<<16) + (g<<8) + b);
	}
	
	/* Converts an base 10 integer to a base 5 int array (for a short range of numbers, enough to cover all the valid characters)*/
	public static int[] base10toBase5(int v) {
		int[] res = new int[3];
		res[2] = v%5;
		res[1] = (v/5)%5;
		res[0] = (v/25)%5;
		return res;
	}
	
	/* Converts an base 5 number to a base 10 integer (for a short range of numbers, enough to cover all the valid characters)*/
	public static int base5toBase10(int c2, int c1, int c0) {
		int res = c0;
		res+= c1*5;
		res+= c2*25;
		return res;
	}
	
	/* Saves an image at the same directory of the given image */
	public static boolean saveImage(String prevDir, BufferedImage b) {
		try {
			String newLoc = prevDir.substring(0,prevDir.lastIndexOf("\\")) + "\\encrypted.png";
			ImageIO.write(b, "png", new File(newLoc));
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/* Sends a notification dialog */
	public static void sendNotification(String title, String msg) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/* Sends an error dialog */
	public static void sendError(String title, String msg) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
	}
}
