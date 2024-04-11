package global;

import com.google.gson.Gson;
import frames.MainFrame;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import shapes.GShape;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class CMClientEventHandler implements CMAppEventHandler {
    private CMClientStub m_ClientStub;

    public CMClientEventHandler(CMClientStub clientStub) {
        this.m_ClientStub = clientStub;
    }

    @Override
    public void processEvent(CMEvent cmEvent) {
        switch (cmEvent.getType()) {
            case CMInfo.CM_SESSION_EVENT -> {
                processSessionEvent(cmEvent);
            }
            case CMInfo.CM_DUMMY_EVENT -> {
                processDummyEvent(cmEvent);
            }
        }
    }

    private void processDummyEvent(CMEvent cmEvent) {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        System.out.println("1111111111111");
        // 기존 그림이 없는 경우
        if (due.getDummyInfo().isEmpty()) {
            System.out.println("222222222222");
            CMPaintApp.startMainFrame();
            return;
        }
        // 기존 그림이 있는 경우
        Gson gson = new Gson();
        String[] shapes = gson.fromJson(due.getDummyInfo(), String[].class);
        for (String shape : shapes) {
            CMPaintApp.init_shapes.add(deserializeFromString(shape));
        }
        MainFrame mainFrame = CMPaintApp.startMainFrame();
        mainFrame.initDrawingPanelPaint();
    }
    // 문자열을 객체로 역직렬화하는 메서드
    private static GShape deserializeFromString(String str) {
        try {
            byte[] data = Base64.getDecoder().decode(str);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            return (GShape) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processSessionEvent(CMEvent cmEvent) {
        CMSessionEvent se = (CMSessionEvent) cmEvent;
        switch (se.getID()) {
            case CMSessionEvent.SESSION_TALK -> {
                System.out.println("[" + se.getUserName() + "] : " + se.getTalk());
            }
        }
    }
}
