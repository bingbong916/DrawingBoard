package client.global;
import client.frames.MainFrame;
import server.CMClientApp;

import static java.lang.System.exit;

public class Main {
	public static CMClientApp cmClientApp = new CMClientApp();
	public static MainFrame mainFrame = new MainFrame();

	public static void main(String[] args) {
		boolean isInit = cmClientApp.init();
		if (isInit) {
			if (cmClientApp.loginProcess(cmClientApp)) {
				startPaint(cmClientApp);
				cmClientApp.startChat();
			} else {
				exit(1);
			}
		} else {
			exit(2);
		}
	}

	private static void startPaint(CMClientApp cmClientApp) {
		mainFrame.setVisible(true);
		mainFrame.setResizable(true);
		mainFrame.setLocationRelativeTo(null);

		// set mainFrame in ClientEventHandler
		cmClientApp.getCmClientEventHandler().setMainFrame(mainFrame);
	}
}
