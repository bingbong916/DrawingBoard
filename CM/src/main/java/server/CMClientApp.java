package server;

import client.frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.exit;

public class CMClientApp {
    private CMClientStub cmClientStub;
    private CMClientEventHandler cmClientEventHandler;

    public CMClientApp(MainFrame mainFrame) {
        this.cmClientStub = new CMClientStub();
        this.cmClientEventHandler = new CMClientEventHandler(cmClientStub, mainFrame);
        cmClientStub.setAppEventHandler(cmClientEventHandler);
    }

    public CMClientStub getCmClientStub() {
        return cmClientStub;
    }

    public CMClientEventHandler getCmClientEventHandler() {
        return cmClientEventHandler;
    }

    public boolean init() {
        return cmClientStub.startCM();
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
            cmClientStub.chat("/s", strMessage);
        }
    }

    public boolean loginProcess(String userName, String password) {
        boolean requestResult = cmClientStub.loginCM(userName, password);
        if(requestResult) {
            System.out.println("◎● Log: Login request sent successfully.");
        } else {
            System.err.println("◎● Log: Failed to send login request.");
        }
        return requestResult;
    }

    public boolean logoutProcess() {
        boolean requestResult = cmClientStub.logoutCM();
        if (requestResult) {
            System.out.println("◎● Log: Logout request sent successfully.");

            //exit(0);
        } else {
            System.out.println("◎● Log: Failed to send logout request!");
        }
        return false;
    }
}
