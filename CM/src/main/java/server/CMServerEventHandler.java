package server;

import client.shapes.GShape;
import com.google.gson.Gson;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CMServerEventHandler implements CMAppEventHandler {
    private CMServerStub m_serverStub;

    public CMServerEventHandler(CMServerStub serverStub) {
        m_serverStub = serverStub;
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
        String message = due.getDummyInfo();
        String type = message.substring(0, 3);
        String content = message.substring(3);
        GShape requestShape = Tools.deserializeString(content);
        if (requestShape == null) {
            System.out.println("서버로 들어오는 요청 잘못됨.");
            return;
        }
        switch (type) {
            case "ADD" -> {
                CMServerApp.shapeList.add(requestShape);
                System.out.println("추가됨");
                System.out.println(CMServerApp.shapeList);
            }
            case "UPD" -> {
                for (int i = 0; i < CMServerApp.shapeList.size(); i++) {
                    if (CMServerApp.shapeList.get(i).equals(requestShape)) {
                        CMServerApp.shapeList.set(i, requestShape);
                        System.out.println("업뎃됨");
                        System.out.println(CMServerApp.shapeList);
                        break;
                    }
                }
            }
            case "DEL" -> {
                CMServerApp.shapeList.removeIf(gShape -> gShape.equals(requestShape));
                System.out.println("삭제됨");
                System.out.println(CMServerApp.shapeList);
            }
            case "LOC" -> { // 도형 수정 락
                // 락이 기존에 없거나 본인 경우
                String lockValue = CMServerApp.lockMap.getOrDefault(requestShape.getShapeId(), null);
                if (lockValue == null || lockValue.equals(cmEvent.getSender())) {
                    sendTrueOrFalse(true, cmEvent.getSender());
                    CMServerApp.lockMap.put(requestShape.getShapeId(), cmEvent.getSender());
                    System.out.println("LOCKLOCKLOCK111" + requestShape.getShapeId().substring(0, 10));
                } else { // 락을 다른 사람이 갖고 있는 경우
                    sendTrueOrFalse(false, cmEvent.getSender());
                    System.out.println("LOCKLOCKLOCK222" + requestShape.getShapeId().substring(0, 10));
                }
            }
            case "UNL" -> { // 도형 수정 언락
                if (CMServerApp.lockMap.get(requestShape.getShapeId()).equals(cmEvent.getSender())) {
                    CMServerApp.lockMap.remove(requestShape.getShapeId());
                }
            }
        }
    }

    private void sendTrueOrFalse(boolean isOk, String receiver) {
        CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();
        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());

        if (isOk) {
            due.setDummyInfo("TRU"); // 락을 잡았다.
        } else {
            due.setDummyInfo("FAL"); // 락 못 잡는다.
        }
        CMServerApp.m_serverStub.send(due, receiver);
    }

    private void processSessionEvent(CMEvent cmEvent) {
        CMSessionEvent se = (CMSessionEvent) cmEvent;
        switch (se.getID()) {
            case CMSessionEvent.LOGIN -> {
                System.out.println("[" + se.getUserName() + "] requests login.");

                sendShapesList(se.getUserName());
            }
        }
    }

    public void sendShapesList(String receiver) {
        // 로그인 한 유저에게 저장된 도형 목록 전송
        CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();
        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());

        for (GShape gShape : CMServerApp.shapeList) {
            String message = "ADD" + Tools.serializeShape(gShape);
            due.setDummyInfo(message);
            CMServerApp.m_serverStub.send(due, receiver);
        }
//        // 도형이 있는 경우
//        if (!CMServerApp.shapeList.isEmpty()) {
//            Gson gson = new Gson();
//            String jsonString = gson.toJson(CMServerApp.shapeList);
//            due.setDummyInfo(jsonString);
//            CMServerApp.m_serverStub.send(due, receiver);
//        } else { // 도형이 없는 경우
//            due.setDummyInfo("");
//            CMServerApp.m_serverStub.send(due, receiver);
//        }
    }
}
