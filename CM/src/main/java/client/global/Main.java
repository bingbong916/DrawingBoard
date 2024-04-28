package client.global;
import client.frames.MainFrame;
import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import server.CMClientApp;
import server.Tools;

import java.util.List;

import static java.lang.System.exit;

public class Main {
	public static MainFrame mainFrame = new MainFrame();
	public static CMClientApp cmClientApp = new CMClientApp(mainFrame);

	public static void main(String[] args) {
		mainFrame = new MainFrame();
		cmClientApp = new CMClientApp(mainFrame);
		if (cmClientApp.init()) {
			startPaint(cmClientApp);
			cmClientApp.startChat();
		} else {
			System.exit(1);
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
