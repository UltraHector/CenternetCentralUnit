package com.TroyEmpire.CenternetCentralUnit.GUI;

/**
 * this is the main window of the GUI if CCU
 */
public class GUIMain {

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LoginFrame loginFrame = new LoginFrame("登陆CCU");
				loginFrame.setVisible(true);
			}
		});
	}
}
