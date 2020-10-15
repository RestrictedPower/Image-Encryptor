package me.RestrictedPower.ImageEncryptor;

import me.RestrictedPower.ImageEncryptor.View.MainView;

public class Main {
	static MainView view;
	public static void main(String[] args) {
		Util.initValidChars();
		view = new MainView();
	}
}
