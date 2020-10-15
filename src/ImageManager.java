package me.RestrictedPower.ImageEncryptor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageManager {
	private BufferedImage img;
	private boolean successfulOperation;
	public ImageManager(String dir) {
		try {
			this.img = ImageIO.read(new File(dir));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* Modifies the given image to contain the given text */
	public BufferedImage modify(String text) {
		int n = text.length();
	    int space = img.getWidth()*img.getHeight();
	    if(space-1<n) {
	    	successfulOperation = false; // Text can not fit on image 
	    	return null;
	    }
		
		//Create a copy of the given image
		BufferedImage ret = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	    Graphics g = ret.getGraphics();
	    g.drawImage(img, 0, 0, null);
	    
	    //Convert the |text| we want to encrypt to a unique rgb value and update the first pixel to it
	    int[] first = Util.intToRgb(n);
	    ret.setRGB(0, 0, new Color(first[0],first[1],first[2]).getRGB());
	    
	    //Calculate the maximum equal space each character can have from each other but also fit in the image
	    int spacing = (space-1)/n;
	    
	    //Finally fill the correct cells with these special pixels that represent each character
	    for(int i = 0; i<n; i++) {
	    	int[] co = getCords(i*spacing+1);
	    	Color last = new Color(img.getRGB(co[0], co[1]));
	    	int[] rgb = modifyRgbToMachChar(last.getRed(), last.getGreen(), last.getBlue(), text.charAt(i));
	    	ret.setRGB(co[0], co[1], new Color(rgb[0],rgb[1],rgb[2]).getRGB());
	    }
	    g.dispose();
	    successfulOperation = true; //Image successfully created.
	    return ret;
	}
	
	
	/* Decrypts the text of the given image */
	public String getText() {
		int space = img.getHeight()*img.getWidth();
		
		/* Find the size of the encrypted message by the first character */
		Color f = new Color(img.getRGB(0, 0));
		int n = Util.rgbToInt(f.getRed(), f.getGreen(), f.getBlue());
		if(n==0) return "";
		
		// If the n value is bigger than the image can contain, then the image is corrupted or does not contain encrypted text.
	    if(space-1<n) {
	    	successfulOperation = false;; 
	    	return null;
	    }
	    
		//Using similar methods used to encrypt, use reverse engineering to find each character of the text
		int spacing = (space-1)/n;
		String res = "";
	    for(int i = 0; i<n; i++) {
	    	int[] co = getCords(i*spacing+1);
	    	Color last = new Color(img.getRGB(co[0], co[1]));
	    	Character c = getCharByRgb(last.getRed(), last.getGreen(), last.getBlue());
	    	if(c==null) {
	    		successfulOperation = false; //If at least one rgb module can not be translated to a valid character the image is corrupted or does not contain encrypted text.
	    		return null;
	    	}
	    	res += c;
	    }
	    successfulOperation = true; //Successfully decrypted the hidden text
	    return res;
	}
	
	/* Translate an integer position to a cordinate system */
	private int[] getCords(int loc) {
		int[] ret = new int[2];
		ret[0] = loc%img.getWidth();
		ret[1] = loc/img.getWidth();
		return ret;
	}
	
	/* Find an rgb value that both looks like the given pixel and contains a hidden character represented as the last character of each rgb value */
	public int[] modifyRgbToMachChar(int r, int g, int b, char c) {
		int key = Util.charToKeyMap.get(c);
		int[] res = {r,g,b};
		res[0] -= r%10;
		res[1] -= g%10;
		res[2] -= b%10;
		int[] lastVals = Util.base10toBase5(key);
		for(int i = 0; i<3; i++) res[i] += lastVals[i];
		return res;
	}
	
	/* Retrieve a character from an rgb pixel, returns null if the rgb doesn't match to any characters */
	public Character getCharByRgb(int r, int g, int b) {
		int key = Util.base5toBase10(r%10, g%10, b%10);
		if(!Util.keyToCharMap.containsKey(key)) return (Character) null;
		return Util.keyToCharMap.get(key);
	}
	
	public boolean successfulOperation() {
		return successfulOperation;
	}
}
