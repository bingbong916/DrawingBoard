package client.global;

import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import server.CMServerApp;
import server.Tools;

public class ClientSend {
    public static void requestLock(GShape gShape) {
        GShape clonedGShape = gShape.cloneShapes();
        clonedGShape.setSelected(false);
        String shapeDetails = Tools.serializeShape(clonedGShape);

        sendMessage("SERVER", "LOC", shapeDetails);
    }

    public static void requestUnlock(GShape gShape) {
        GShape clonedGShape = gShape.cloneShapes();
        clonedGShape.setSelected(false);
        String shapeDetails = Tools.serializeShape(clonedGShape);

        sendMessage("SERVER", "UNL", shapeDetails);
    }

    private static void sendMessage(String receiver, String code, String message) {
        CMInteractionInfo interInfo = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();
        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());

        due.setDummyInfo(code + message);
        Main.cmClientApp.getCmClientStub().send(due, receiver);
    }
}
