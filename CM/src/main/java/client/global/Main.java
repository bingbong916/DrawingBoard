package client.global;
import client.frames.MainFrame;
import server.CMClientApp;

import static java.lang.System.exit;

public class Main {
	public static CMClientApp cmClientApp = new CMClientApp();

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
		MainFrame mainframe = new MainFrame();
		mainframe.setVisible(true);
		mainframe.setResizable(true);
		mainframe.setLocationRelativeTo(null);

		// set mainFrame in ClientEventHandler
		cmClientApp.getCmClientEventHandler().setMainFrame(mainframe);
	}
}
