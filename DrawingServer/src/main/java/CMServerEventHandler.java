import com.google.gson.Gson;
import kr.ac.konkuk.ccslab.cm.entity.CMServer;
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
        CMServerApp.shapes.add(due.getDummyInfo());
        System.out.println("dummy msg: " + due.getDummyInfo());
    }

    private void processSessionEvent(CMEvent cmEvent) {
        CMSessionEvent se = (CMSessionEvent) cmEvent;
        switch (se.getID()) {
            case CMSessionEvent.LOGIN -> {
                System.out.println("[" + se.getUserName() + "] requests login.");

                // 로그인 한 유저에게 저장된 도형 목록 전송
                CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
                CMUser myself = interInfo.getMyself();
                CMDummyEvent due = new CMDummyEvent();
                due.setHandlerSession(myself.getCurrentSession());
                due.setHandlerGroup(myself.getCurrentGroup());
                // 도형이 있는 경우
                if (!CMServerApp.shapes.isEmpty()) {
                    System.out.println("11111");
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(CMServerApp.shapes);
                    due.setDummyInfo(jsonString);
                    CMServerApp.m_serverStub.send(due, se.getUserName());
                } else { // 도형이 없는 경우
                    System.out.println("22222");
                    due.setDummyInfo("");
                    CMServerApp.m_serverStub.send(due, se.getUserName());
                }
            }
        }
    }
}
