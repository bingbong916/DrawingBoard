package client.global;

import client.shapes.GShape;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import server.Tools;

public class ClientBroadcast {
    public static void broadcastShape(GShape gShape) {
        GShape clonedGShape = gShape.cloneShapes();
        clonedGShape.setSelected(false);
        String shapeDetails = Tools.serializeShape(clonedGShape);

        broadcastMessage("ADD", shapeDetails);
    }

    public static void broadcastUpdate(GShape gShape) {
        GShape clonedGShape = gShape.cloneShapes();
        clonedGShape.setSelected(false);
        String shapeDetails = Tools.serializeShape(clonedGShape);

        broadcastMessage("UPD", shapeDetails);
    }

    public static void broadcastDelete(GShape gShape) {
        GShape clonedGShape = gShape.cloneShapes();
        clonedGShape.setSelected(false);
        String shapeDetails = Tools.serializeShape(clonedGShape);

        broadcastMessage("DEL", shapeDetails);
    }
    public static void broadcastLock(GShape gShape) {
        if (!Main.mainFrame.getDrawingPanel().isShapeLocked(gShape)) {
            String shapeId = gShape.getShapeId();
            broadcastMessage("LOC", shapeId);
        }
    }
    public static void broadcastUnlock(GShape gShape) {
        String shapeId = gShape.getShapeId();
        if (!Main.mainFrame.getDrawingPanel().isShapeLocked(gShape)) {
            broadcastMessage("UNL", shapeId);
        }
    }
    private static void broadcastMessage(String code, String message) {
        CMInteractionInfo interInfo = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();

        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());
        due.setDummyInfo(code + message);

        Main.cmClientApp.getCmClientStub().broadcast(due);
    }
}
