package server;

import client.frames.DrawingPanel;
import client.frames.MainFrame;
import client.global.Main;
import client.shapes.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class CMClientEventHandler implements CMAppEventHandler {
    private CMClientStub m_ClientStub;

    private MainFrame mainFrame;

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }


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
                System.out.println("◎● Log: Message From \"" + cmEvent.getSender() + "\"");
                // TODO: user name - SERVER로 로그인 못하게 해야 됨.
                if (cmEvent.getSender().equals("SERVER")) {
                    processDummyEventFromServer(cmEvent);
                } else {
                    processDummyEvent(cmEvent);
                }
            }
        }
    }

    private void processDummyEventFromServer(CMEvent cmEvent) {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        // 빈 리스트 이면 바로 끝
        if (due.getDummyInfo().isEmpty()) {
            System.out.println("◎● Log: no initial shape");
            return;
        }
        // json을 List<String>으로 변환
        List<String> shapeStringList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            shapeStringList = gson.fromJson(due.getDummyInfo(), listType);
        } catch (JsonSyntaxException e) {
            System.out.println("◎● Log: JsonSyntaxException occured");
            return;
        }
        System.out.println("◎● Log: initial shape count is " + shapeStringList.size());
        for (String shapeString : shapeStringList) {
            GShape newGShape = Tools.deserializeString(shapeString);
            if (newGShape == null) { // shapeString이 GShape 객체가 아닌 경우
                System.out.println("◎● Log: inner text is not a GShape Obejct");
                continue;
            }
            System.out.println(newGShape);
            // TODO: 이거 drawingPanel 생성이 되기 전임...
            DrawingPanel drawingPanel = mainFrame.getDrawingPanel();
            Vector<GShape> gShapes = (Vector<GShape>) drawingPanel.getShapes();
            gShapes.add(newGShape);
        }
        mainFrame.getDrawingPanel().repaint();
    }

    private void processDummyEvent(CMEvent cmEvent) {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        String user = due.getSender();
        // TODO: 이름 겹치게 로그인 안 되도록 해야 함.
        if (user.equals(m_ClientStub.getMyself().getName())) {
            System.out.println("◎● Log: 내 메시지라 추가 안됨.");
            return;
        }

        System.out.println("◎● Log: " + due.getDummyInfo());
        GShape result = Tools.deserializeString(due.getDummyInfo());
        if (result instanceof GLine) {
            System.out.println(" - GLine 도착 ●◎");
        } else if (result instanceof GOval) {
            System.out.println(" - GOval 도착 ●◎");
        } else if (result instanceof GPencil) {
            System.out.println(" - GPencil 도착 ●◎");
        } else if (result instanceof GPolygon) {
            System.out.println(" - GPolygon 도착 ●◎");
        } else if (result instanceof GTextBox) {
            System.out.println(" - GTextBox 도착 ●◎");
        } else if (result instanceof GRectangle) {
            System.out.println(" - GRectangle 도착 ●◎");
        } else if (result instanceof GTriangle) {
            System.out.println(" - GTriangle 도착 ●◎");
        } else {
            System.out.println("◎● Log: GShape 객체가 아닌 것이 도착함.\n" + result);
            return;
        }
        DrawingPanel drawingPanel = mainFrame.getDrawingPanel();
        Vector<GShape> gShapes = (Vector<GShape>) drawingPanel.getShapes();
        gShapes.add(result);
        mainFrame.getDrawingPanel().repaint();
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
