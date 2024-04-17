package client.global;
import client.frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import server.CMClientApp;
import server.CMClientEventHandler;

import static java.lang.System.exit;

public class Main {
	public static void main(String[] args) {
		CMClientApp cmClientApp = new CMClientApp();
		boolean isInit = cmClientApp.init();
		if (isInit) {
			if (cmClientApp.loginProcess(cmClientApp.getCmClientApp())) {
				startPaint();
				cmClientApp.startChat();
			} else {
				exit(1);
			}
		} else {
			exit(1);
		}
	}

	private static void startPaint() {
		MainFrame mainframe = new MainFrame();
		mainframe.setVisible(true);
		mainframe.setResizable(true);
		mainframe.setLocationRelativeTo(null);
	}
}
