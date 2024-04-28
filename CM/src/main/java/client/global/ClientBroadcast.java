package client.global;

import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import server.Tools;

public class ClientBroadcast {
    public static void broadcastShape(GShape gShape) {
        String shapeDetails = Tools.serializeShape(gShape);

        broadcacstMessage("ADD", shapeDetails);
    }

    public static void broadcastUpdate(GShape gShape) {
        String shapeDetails = Tools.serializeShape(gShape);

        broadcacstMessage("UPD", shapeDetails);
    }

    public static void broadcastDelete(GShape gShape) {
        String shapeDetails = Tools.serializeShape(gShape);

        broadcacstMessage("DEL", shapeDetails);
    }

    private static void broadcacstMessage(String code, String message) {
        CMInteractionInfo interInfo = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();

        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());
        due.setDummyInfo(code + message);

        Main.cmClientApp.getCmClientStub().broadcast(due);
    }
}
