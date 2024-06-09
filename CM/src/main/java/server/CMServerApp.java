package server;

import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMServerApp {
    public static List<GShape> shapeList = new ArrayList<>();
    public static Map<String, String> lockMap = new HashMap<>();
    public static CMServerStub m_serverStub;
    private CMServerEventHandler m_eventHandler;

    public CMServerApp() {
        m_serverStub = new CMServerStub();
        m_eventHandler = new CMServerEventHandler(m_serverStub);
    }

    public CMServerStub getServerStub() {
        return m_serverStub;
    }

    public CMServerEventHandler getServerEventHandler() {
        return m_eventHandler;
    }

    public static void main(String[] args) {
        CMServerApp server = new CMServerApp();
        CMServerStub cmStub = server.getServerStub();
        cmStub.setAppEventHandler(server.getServerEventHandler());
        cmStub.startCM();
    }
}
