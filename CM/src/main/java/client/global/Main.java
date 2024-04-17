package client.global;
import client.frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import server.CMClientApp;
import server.CMClientEventHandler;

import javax.swing.*;

import java.util.Vector;

import static java.lang.System.exit;

public class Main {
	public static void main(String[] args) {
		CMClientApp cmClientApp = new CMClientApp();
		boolean isInit = cmClientApp.init();
		if (isInit) {
			if (cmClientApp.loginProcess(cmClientApp.getCmClientApp())) {
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
		MainFrame mainframe = new MainFrame();
		mainframe.setVisible(true);
		mainframe.setResizable(true);
		mainframe.setLocationRelativeTo(null);

		// init drawingPanelList
		Vector<JPanel> drawingPanelList = mainframe.getDrawingPanelList();
		cmClientApp.getCmClientEventHandler().setDrawingPanelList(drawingPanelList);
	}
}
