package me.RestrictedPower.ImageEncryptor.View;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import me.RestrictedPower.ImageEncryptor.ImageManager;
import me.RestrictedPower.ImageEncryptor.Util;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class MainView {
	private final int Height = 400, Width = 550;
	private JFrame frame;
	private JTabbedPane tabbedPane;
	private JPanel encryptPanel, decryptPanel;
	private JLabel chosenEncryptedImageLabel, chosenDecryptedImageLabel, txtToEncryptLabel, txtToDecryptLabel;
	private JTextField chosenEncryptedImageDir, chosenDecryptedImageDir;
	private JTextArea textToEncrypt, textToDecrypt;
	private JButton encryptButton, chooseEncryptImageButton, chooseDecryptImageButton, decryptButton;
	public MainView() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initialize() {
		Util.setLookAndFeel();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame();
		frame.setTitle("Message to image encryptor/decryptor.");
		frame.setBounds((int) (dim.getWidth()-Width)/2,(int) (dim.getHeight()-Height)/2, Width, Height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 550, 375);
		tabbedPane.setOpaque(false);
		tabbedPane.setFocusable(false);
		frame.getContentPane().add(tabbedPane);
		
		initEncryptPanel();
		initDecryptPanel();
	}
	
	private void initEncryptPanel() {
		encryptPanel = new JPanel();
		encryptPanel.setOpaque(false);
		encryptPanel.setFocusable(false);
		encryptPanel.setLayout(null);
		tabbedPane.addTab("Encrypt", null, encryptPanel, null);
		
		//Choose image label
		chosenEncryptedImageLabel = new JLabel("Chosen Image:");
		chosenEncryptedImageLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chosenEncryptedImageLabel.setBounds(10, 45, 115, 20);
		encryptPanel.add(chosenEncryptedImageLabel);
		
		
		//Choose image button
		chooseEncryptImageButton = new JButton("Choose an image to encrypt");
		chooseEncryptImageButton.setOpaque(false);
		chooseEncryptImageButton.setFocusable(false);
		chooseEncryptImageButton.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chooseEncryptImageButton.setBounds(10, 10, 520, 25);
		chooseEncryptImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = Util.chooseFile();
				if(s!=null) chosenEncryptedImageDir.setText(s);
			}
		});
		encryptPanel.add(chooseEncryptImageButton);
		
		
		//Selected Image Text Field
		chosenEncryptedImageDir = new JTextField("Not selected yet.");
		chosenEncryptedImageDir.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chosenEncryptedImageDir.setBounds(135, 45, 395, 25);
		encryptPanel.add(chosenEncryptedImageDir);
		
		
		//Text to encrypt area
		textToEncrypt = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textToEncrypt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 100, 520, 200);
		encryptPanel.add(scrollPane);
		
		//Text to encrypt label
		txtToEncryptLabel = new JLabel("Text to encrypt:");
		txtToEncryptLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		txtToEncryptLabel.setBounds(10, 75, 127, 23);
		encryptPanel.add(txtToEncryptLabel);
		
		//Encrypt button
		encryptButton = new JButton("Encrypt text to image");
		encryptButton.setOpaque(false);
		encryptButton.setFocusable(false);
		encryptButton.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		encryptButton.setBounds((Width-240)/2, 309, 240, 23);
		encryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String txt = textToEncrypt.getText();
				String location = chosenEncryptedImageDir.getText();
				if(!Util.isValid(txt)) {
					Util.sendError("Invalid text!", "Make sure the text you input contains only alphanumeric and special characters!");
					return;
				}
				File f = new File(location);
				if((!f.isFile()) || (!Util.getExtension(f).equals("png"))) {
					Util.sendError("Invalid file!", "The given directory is not a .png file!");
					return;
				}
				if(txt.equalsIgnoreCase("")) {
					Util.sendError("Text empty!", "Please give at least one character to encrypt!");
					return;
				}
				ImageManager m = new ImageManager(location);
				BufferedImage img = m.modify(txt);
				if(!m.successfulOperation()) {
					Util.sendError("Could not save text into image!", "Probably the text doesn't fit in the image!");
					return;
				}
				if(Util.saveImage(f.getAbsolutePath(),img)) Util.sendNotification("Message encrypted!", "The text was succesfully encrypted into the image!\n You can find the encrypted image at the directory of the chosen image as \"encrypted.png\"");
				else Util.sendError("Error!", "There was an error saving the image!");
			}
		});
		encryptPanel.add(encryptButton);
	}
	
	private void initDecryptPanel() {
		decryptPanel = new JPanel();
		decryptPanel.setOpaque(false);
		decryptPanel.setFocusable(false);
		decryptPanel.setLayout(null);
		tabbedPane.addTab("Decrypt", null, decryptPanel, null);
		
		//Choose image button
		chooseDecryptImageButton = new JButton("Choose an image to decrypt");
		chooseDecryptImageButton.setOpaque(false);
		chooseDecryptImageButton.setFocusable(false);
		chooseDecryptImageButton.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chooseDecryptImageButton.setBounds(10, 10, 520, 25);
		chooseDecryptImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = Util.chooseFile();
				if(s!=null) chosenDecryptedImageDir.setText(s);
			}
		});
		decryptPanel.add(chooseDecryptImageButton);
		
		//Selected Image Text Field
		chosenDecryptedImageDir = new JTextField("Not selected yet.");
		chosenDecryptedImageDir.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chosenDecryptedImageDir.setBounds(135, 45, 395, 25);
		decryptPanel.add(chosenDecryptedImageDir);
		
		//Text to decrypt area
		textToDecrypt = new JTextArea();
		textToDecrypt.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textToDecrypt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 100, 520, 200);
		decryptPanel.add(scrollPane);
		
		//Text to decrypt label
		txtToDecryptLabel = new JLabel("Decrypted text:");
		txtToDecryptLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		txtToDecryptLabel.setBounds(10, 75, 127, 23);
		decryptPanel.add(txtToDecryptLabel);
		
		//Choose image label
		chosenDecryptedImageLabel = new JLabel("Chosen Image:");
		chosenDecryptedImageLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chosenDecryptedImageLabel.setBounds(10, 45, 115, 20);
		decryptPanel.add(chosenDecryptedImageLabel);
		
		//Decrypt button
		decryptButton = new JButton("Decrypt image to text");
		decryptButton.setOpaque(false);
		decryptButton.setFocusable(false);
		decryptButton.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		decryptButton.setBounds((Width-240)/2, 309, 240, 23);
		decryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String location = chosenDecryptedImageDir.getText();
				File f = new File(location);
				if((!f.isFile()) || (!Util.getExtension(f).equals("png"))) {
					Util.sendError("Invalid file!", "The given directory is not a .png file!");
					return;
				}
				ImageManager m = new ImageManager(location);
				String s = m.getText();
				if(!m.successfulOperation()) {
					Util.sendError("Could not decrypt the text!", "Make sure the input image is an encrypted image by this software.");
					return;
				}
				textToDecrypt.setText(s);
				Util.sendNotification("Success!", "Message decrypted.");
			}
		});
		decryptPanel.add(decryptButton);
	}
}
