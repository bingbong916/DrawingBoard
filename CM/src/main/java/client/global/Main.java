package client.global;
import client.frames.MainFrame;
import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import server.CMClientApp;
import server.Tools;

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

	public static void broadcastShape(GShape gShape) {
		String shapeDetails = Tools.serializeShape(gShape);

		CMInteractionInfo interInfo = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();

		CMDummyEvent due = new CMDummyEvent();
		due.setHandlerSession(myself.getCurrentSession());
		due.setHandlerGroup(myself.getCurrentGroup());
		due.setDummyInfo(shapeDetails);

		Main.cmClientApp.getCmClientStub().broadcast(due);
	}
}
