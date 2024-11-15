package client.tool;

import java.util.Vector;
import client.shapes.GShape;

public class Clipboard {

	public Vector<GShape> tempshapes;//redo,undo
	public Vector<GShape> clipshapes;//copy,cut,paste

	public Clipboard() {
		this.tempshapes = new Vector<GShape>();
		this.clipshapes = new Vector<GShape>();
	}

	public void setTempShape(GShape shape) {
		this.tempshapes.add(shape);
	}

	public Vector<GShape> getTempShape() {
		return this.tempshapes;
	}

	public void setContents(Vector<GShape> shapes) {
		this.clipshapes.clear();
		this.clipshapes.addAll(shapes);
	}

	public Vector<GShape> getContents() {
		Vector<GShape> clipshapes = new Vector<GShape>();
		for (GShape shape : this.clipshapes) {
			GShape clonedShape = shape.cloneShapes();
			clipshapes.add(clonedShape);
		}
		return clipshapes;
	}

}
