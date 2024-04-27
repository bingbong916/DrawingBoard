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

	public static void broadcastShape(GShape gShape) {
		String shapeDetails = Tools.serializeShape(gShape);

		broadcacstMessage("ADD", shapeDetails);
	}

	public static void broadcastUpdate(GShape gShape) {
		String shapeDetails = Tools.serializeShape(gShape);

		broadcacstMessage("UPD", shapeDetails);
	}

	public static void broadcastDelete(GShape gShape) {
		String shapeDetails = Tools.serializeShape(gShape);

		broadcacstMessage("DEL", shapeDetails);
	}

	private static void broadcacstMessage(String code, String message) {
		CMInteractionInfo interInfo = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();

		CMDummyEvent due = new CMDummyEvent();
		due.setHandlerSession(myself.getCurrentSession());
		due.setHandlerGroup(myself.getCurrentGroup());
		due.setDummyInfo(code + message);

		Main.cmClientApp.getCmClientStub().broadcast(due);
	}
}
