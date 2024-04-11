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

    public static void main(String[] args) {
        CMClientApp client = new CMClientApp();
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
            cmStub.chat("/b", strMessage);
        }
    }

    private static void loginProcess(CMClientApp client) {
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
