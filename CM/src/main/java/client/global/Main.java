package client.global;
import client.frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import server.CMClientApp;
import server.CMClientEventHandler;

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
			exit(1);
		}
	}

	private static void startPaint(CMClientApp cmClientApp) {
		MainFrame mainframe = new MainFrame(cmClientApp);
		mainframe.setVisible(true);
		mainframe.setResizable(true);
		mainframe.setLocationRelativeTo(null);
	}
}
