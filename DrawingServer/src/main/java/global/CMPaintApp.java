package global;
import frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import shapes.GShape;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class CMPaintApp {
	public static CMClientStub m_clientStub;
	private CMClientEventHandler m_eventHandler;
	public static Vector<GShape> init_shapes = null;

	public CMPaintApp() {
		m_clientStub = new CMClientStub();
		m_eventHandler = new CMClientEventHandler(m_clientStub);
	}

	public CMClientStub getClientStub() {
		return m_clientStub;
	}

	public CMClientEventHandler getClientEventHandler() {
		return m_eventHandler;
	}

	public static void main(String[] args) {

		CMPaintApp client = new CMPaintApp();
		CMClientStub cmStub = client.getClientStub();
		CMClientEventHandler eventHandler = client.getClientEventHandler();
		boolean ret = false;

		// initialize CM
		cmStub.setAppEventHandler(eventHandler);
		ret = cmStub.startCM();

		if (ret) {
			System.out.println("init success");
		} else {
			System.out.println("init error!");
			return;
		}

		loginProcess(client);
	}

	public static MainFrame startMainFrame() {
		// 그림판 시작
		MainFrame mainframe = new MainFrame();
		mainframe.setVisible(true);
		mainframe.setResizable(true);
		mainframe.setLocationRelativeTo(null);

		return mainframe;
	}

	private static void loginProcess(CMPaintApp client) {
		String strUserName = null;
		String strPassword = null;
		boolean bRequestResult = false;
		Console console = System.console();
		System.out.print("user name: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			strUserName = br.readLine();
			if(console == null)
			{
				System.out.print("password: ");
				strPassword = br.readLine();
			}
			else
				strPassword = new String(console.readPassword("password: "));

		} catch (IOException e) {
			e.printStackTrace();
		}
		bRequestResult = client.getClientStub().loginCM(strUserName, strPassword);
		if(bRequestResult)
			System.out.println("successfully sent the login request.");
		else
			System.err.println("failed the login request!");
	}
}
