package client.shapes;

import client.frames.DrawingPanel;
import client.global.Main;
import java.util.Vector;

public class GSelection extends GRectangle {
	private static final long serialVersionUID = 1L;
	private Vector<GShape> containedShapes;
	
	public GSelection() {
		this.containedShapes = new Vector<GShape>();
	}

	@Override
	public GShape clone() {
		return new GSelection();
	}
	
	public void contains(Vector<GShape> shapes) {
		DrawingPanel drawingPanel = Main.mainFrame.getDrawingPanel();
		this.containedShapes.clear();
		for(GShape shape : shapes) {
			if(this.getShape().contains(shape.getShape().getBounds()) && !drawingPanel.isShapeLockedByAnotherUser(shape)) {
				this.containedShapes.add(shape);
				shape.setSelected(true);
			}
		}
	}

	public Vector<GShape> getContainedShapes() {
		return containedShapes;
	}
}
