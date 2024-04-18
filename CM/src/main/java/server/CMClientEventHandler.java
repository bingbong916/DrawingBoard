package server;

import client.frames.DrawingPanel;
import client.frames.MainFrame;
import client.global.Main;
import client.shapes.*;
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
import java.util.Base64;
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
                processDummyEvent(cmEvent);
            }
        }
    }

    private void processDummyEvent(CMEvent cmEvent) {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        String user = due.getSender();
        if (user.equals(m_ClientStub.getMyself().getName())) {
            System.out.println("◎● Log: 내 메시지라 추가 안됨.");
            return;
        }

        GShape result = deserializeString(due.getDummyInfo());
        if (result instanceof GLine) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GLine 도착 ●◎");
        } else if (result instanceof GOval) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GOval 도착 ●◎");
        } else if (result instanceof GPencil) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GPencil 도착 ●◎");
        } else if (result instanceof GPolygon) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GPolygon 도착 ●◎");
        } else if (result instanceof GTextBox) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GTextBox 도착 ●◎");
        } else if (result instanceof GRectangle) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GRectangle 도착 ●◎");
        } else if (result instanceof GTriangle) {
            System.out.println("◎● Log: " + due.getDummyInfo());
            System.out.println(" - GTriangle 도착 ●◎");
        } else {
            System.out.println("◎● Log: GShape 객체가 아닌 것이 도착함.\n" + result);
            return;
        }
        DrawingPanel drawingPanel = mainFrame.getDrawingPanel();
        Vector<GShape> gShapes = (Vector<GShape>) drawingPanel.getShapes();
        gShapes.add(result);
        System.out.println(gShapes);
    }
    private GShape deserializeString(String encodedShape) {
        try {
            byte[] data = Base64.getDecoder().decode(encodedShape);
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(byteArrayIn);
            return (GShape) in.readObject();
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
