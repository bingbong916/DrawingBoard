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
import java.util.*;

public class CMClientEventHandler implements CMAppEventHandler {
    private CMClientStub m_ClientStub;

    private MainFrame mainFrame;

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public CMClientEventHandler(CMClientStub clientStub, MainFrame mainFrame) {
        this.m_ClientStub = clientStub;
        this.mainFrame = mainFrame;
    }

    @Override
    public void processEvent(CMEvent cmEvent) {
        switch (cmEvent.getType()) {
            case CMInfo.CM_SESSION_EVENT -> {
                processSessionEvent(cmEvent);
            }
            case CMInfo.CM_DUMMY_EVENT -> {
                // TODO: user name 반환 수정 필요.
                System.out.println("◎● Log: Message From \"" + cmEvent.getSender() + "\"");
                // TODO: user name - SERVER로 로그인 못하게 해야 됨.

                processDummyEvent(cmEvent);
                /*if (cmEvent.getSender().equals("SERVER")) {
                    processDummyEventFromServer(cmEvent);
                } else {
                    processDummyEvent(cmEvent);
                }*/
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
            Vector<GShape> gShapes = drawingPanel.getShapes();
            gShapes.add(newGShape);
        }
        mainFrame.getDrawingPanel().repaint();
    }

    private void processDummyEvent(CMEvent cmEvent) {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        String user = due.getSender();

        String message = due.getDummyInfo();
        String type = message.substring(0, 3);
        String content = message.substring(3);

        if (user.equals(m_ClientStub.getMyself().getName())) {
            System.out.println("◎● Log: 내 메시지라 추가 안됨.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            DrawingPanel drawingPanel = mainFrame.getDrawingPanel();
            Vector<GShape> gShapes = drawingPanel.getShapes();
            synchronized (gShapes) {
                switch (type) {
                    case "ADD" -> {
                        GShape requestShape = Tools.deserializeString(content);
                        if (requestShape == null) {
                            System.out.println("◎● Log: ADD요청 - 형식 잘못됨.");
                            return;
                        }
                        if (!gShapes.contains(requestShape)) {
                            gShapes.add(requestShape);
                            System.out.println("◎● Log: 도형 추가됨");
                        }
                    }
                    case "UPD" -> {
                        GShape requestShape = Tools.deserializeString(content);
                        if (requestShape == null) {
                            System.out.println("◎● Log: UPDATE요청 - 형식 잘못됨.");
                            return;
                        }
                        gShapes.replaceAll(g -> g.equals(requestShape) ? requestShape : g);
                        System.out.println("◎● Log: 도형 변경됨");
                    }
                    case "DEL" -> {
                        GShape requestShape = Tools.deserializeString(content);
                        if (requestShape == null) {
                            System.out.println("◎● Log: DELETE요청 - 형식 잘못됨.");
                            return;
                        }
                        gShapes.removeIf(g -> g.equals(requestShape));
                        System.out.println("◎● Log: 도형 삭제됨");
                    }
                    case "LOC" -> {
                        System.out.println("◎● Log: 도형 잠금됨 - " + content + " by " + user);
                    }
                    case "UNL" -> {
                        System.out.println("◎● Log: 도형 잠금 해제됨 - " + content + " by " + user);
                    }
                    case "LMP" -> {
                        Gson gson = new Gson();
                        Type typeOfHashMap = new TypeToken<Map<String, String>>() {}.getType();
                        Map<String, String> lockMap = gson.fromJson(content, typeOfHashMap);
                        drawingPanel.updateLockMap(lockMap);
                        System.out.println("◎● Log: 잠금 상태 업데이트됨");
                    }
                }
                mainFrame.getDrawingPanel().repaint();
            }
            System.out.println(gShapes);
        });
    }


    private void processSessionEvent(CMEvent cmEvent) {
        CMSessionEvent se = (CMSessionEvent) cmEvent;
        switch (se.getID()) {
            case CMSessionEvent.JOIN_SESSION_ACK:
                handleJoinSessionAck(se);
                break;
            case CMSessionEvent.CHANGE_SESSION:
                handleChangeSession(se);
                break;
            case CMSessionEvent.SESSION_ADD_USER:
                handleSessionAddUser(se);
                break;
            case CMSessionEvent.SESSION_REMOVE_USER:
                handleSessionRemoveUser(se);
                break;
            default:
                System.out.println("◎● Log: 지원하지 않는 session event ID: " + se.getID());
                break;
        }
    }
    private void handleSessionAddUser(CMSessionEvent se) {
        String userName = se.getUserName();
        // 유저의 IP address 필요시 사용
//        String userHost = se.getHostAddress();
        String message = userName + " has joined the board.";
        mainFrame.updateLog(message);
    }

    private void handleSessionRemoveUser(CMSessionEvent se) {
        String userName = se.getUserName();
        String message = userName + " left the board.";
        mainFrame.updateLog(message);
    }

    // TODO: 필요시 추가 구현
    private void handleJoinSessionAck(CMSessionEvent se) {
        System.out.println("Session Join Acknowledged: " + se.getSessionName() + ", Groups: " + se.getGroupNum());
    }
    // TODO: 필요시 추가 구현
    private void handleChangeSession(CMSessionEvent se) {
        System.out.println("User " + se.getUserName() + " changed to session: " + se.getSessionName());
    }

}
