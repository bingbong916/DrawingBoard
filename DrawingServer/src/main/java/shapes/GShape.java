package shapes;

import shapes.GAnchor.EAnchors;
import transformer.GResizerDto;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;

@SuppressWarnings("serial")
abstract public class GShape implements Serializable {
	// attributes
	protected int tMoveX, tMoveY;

	// components
	protected boolean selected;
	private Color lineColor;
	private Color fillColor;
	private int stroke;
	private float[] dash;

	protected Shape shape;
	protected GAnchor gAnchors;
	protected EAnchors eAnchors;
	private EDrawingStyle eDrawingStyle;

	private AffineTransform af = null;

	public enum EOnState {
		eOnShape, eOnResize, eOnRotate;
	}

	public enum EDrawingStyle {
		e2PointDrawing, eNPointDrawing;
	}

	public GShape() {
		af = new AffineTransform();
		this.lineColor = Color.BLACK;
		this.fillColor = null;
		this.selected = false;
		this.stroke = 0;
		this.dash = null;
		this.gAnchors = new GAnchor();
	}

	public void seteDrawingStyle(EDrawingStyle eDrawingStyle) {
		this.eDrawingStyle = eDrawingStyle;
	}

	public EDrawingStyle geteDrawingStyle() {
		return this.eDrawingStyle;
	}

	public EAnchors getAnchor() {
		return this.eAnchors;
	}

	public GAnchor getGAnchor() {
		return this.gAnchors;
	}

	public void verticalPaste() {
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.translate(10, 10);
		this.shape = affineTransform.createTransformedShape(this.shape);
	}
	
	public EOnState onShape(int x, int y) {
		if (this.selected) {
			EAnchors eAnchor = this.gAnchors.onShape(x, y);
			if (eAnchor == EAnchors.RR) {
				return EOnState.eOnRotate;
			}
			if (eAnchor == null) {
				if (this.shape.contains(x, y)) {
					return EOnState.eOnShape;
				} else if (this.linecontains(x, y)) {
					return EOnState.eOnShape;
				}
			} else {
				this.eAnchors = eAnchor;
				return EOnState.eOnResize;
			}
		} else {
			if (this.shape.contains(x, y)) {
				return EOnState.eOnShape;
			}
		}
		return null;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setStroke(int index) {
		this.stroke = index;
	}
	
	public void setStrokeDash(float[] dash) {
		this.dash = dash;
	}

	public Shape getShape() {
		return this.shape;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setLineColor(Color color) {
		this.lineColor = color;
	}

	public void setFillColor(Color color) {
		this.fillColor = color;
	}

	public void setNullFillColor() {
		fillColor = null;
	}

	public void draw(Graphics2D graphics2d) {
		if (this.stroke != 0) {
			graphics2d.setStroke(new BasicStroke(this.stroke));
		}

		if(this.dash != null) {
			graphics2d.setStroke(new BasicStroke(this.stroke, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 1.0f, this.dash, 0.0f));
		}
		
		if (this.fillColor != null) {
			graphics2d.setColor(fillColor);
			graphics2d.fill(this.shape);
		}

		graphics2d.setColor(this.lineColor);
		graphics2d.draw(this.shape);
		graphics2d.setStroke(new BasicStroke(1));
		
		if (this.selected) {
			this.gAnchors.setBoundingRect(this.shape.getBounds());
			this.gAnchors.draw(graphics2d);
		}
	}

	public void move(int dw, int dh) {
		af.setToTranslation(dw, dh);
		this.shape = af.createTransformedShape(this.shape);
	}

	public void resize(GResizerDto dto) {
		if (dto != null) {
			af.setToTranslation(dto.getTx(), dto.getTy());
			af.scale(dto.getSx(), dto.getSy());
			af.translate(-(dto.getTx()), -(dto.getTy()));
			this.shape = af.createTransformedShape(this.shape);
		}
	}

	public void rotate(double angle, Point rotatePoint) {
		// setToRotation = radian, rotate = degree
		af.setToRotation(Math.toRadians(angle), rotatePoint.getX(), rotatePoint.getY());
		this.shape = af.createTransformedShape(this.shape);
	}

	public GShape cloneShapes() {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return (GShape) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Rectangle getBounds() {
		return this.shape.getBounds();
	}
	
	public abstract void finishMoving(Graphics2D graphics2d, int x, int y);

	public abstract void setInitPoint(int x1, int y1);

	public abstract void addPoint(int x, int y);

	public abstract void setFinalPoint(int x2, int y2);

	public abstract void movePoint(int x, int y);

	public abstract GShape clone();

	public boolean linecontains(int x, int y) {
			if (this.shape.getBounds().contains(new Point(x, y))) {return true;	}
			else {	return false;}
	}

}
