package server;

import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class CMClientApp {
    private CMClientStub m_clientStub;
    private CMClientEventHandler m_eventHandler;

    public CMClientApp() {
        m_clientStub = new CMClientStub();
        m_eventHandler = new CMClientEventHandler(m_clientStub);
    }

    public CMClientStub getClientStub() {
        return m_clientStub;
    }

    public CMClientEventHandler getClientEventHandler() {
        return m_eventHandler;
    }

    // ------------------------------------------------------------

    private CMClientApp cmClientApp;
    private CMClientStub cmClientStub;
    private CMClientEventHandler cmClientEventHandler;
    public CMClientApp getCmClientApp() {
        return cmClientApp;
    }

    public CMClientStub getCmClientStub() {
        return cmClientStub;
    }

    public CMClientEventHandler getCmClientEventHandler() {
        return cmClientEventHandler;
    }

    public boolean init() {
        cmClientApp = new CMClientApp();
        cmClientStub = cmClientApp.getClientStub();
        cmClientEventHandler = cmClientApp.getClientEventHandler();

        boolean ret = false;
        // initialize CM
        cmClientStub.setAppEventHandler(cmClientEventHandler);
        ret = cmClientStub.startCM();

        if (ret) {
            System.out.println("init success");
            return true;
        } else {
            System.out.println("init error!");
            return false;
        }
    }

    public void startChat() {
        // Chatting
        BufferedReader br;
        String strMessage = null;
        while (true) {
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                strMessage = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (strMessage.equals("exit")) {
                break;
            }
            cmClientStub.chat("/b", strMessage);
        }
    }

    public boolean loginProcess(CMClientApp client) {
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
            return false;
        }
        bRequestResult = client.getClientStub().loginCM(strUserName, strPassword);
        if(bRequestResult)
            System.out.println("successfully sent the login request.");
        else {
            System.err.println("failed the login request!");
            return false;
        }
        return true;
    }
}
