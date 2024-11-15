package client.frames;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PreviewPanel extends JPanel {

	private Color lineColor;
	private Color fillColor;
	private int stroke;
	private float[] dash;

	public PreviewPanel() {
		lineColor = Color.BLACK;
		fillColor = Color.WHITE;
		stroke = 1;
		this.setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));

		JLabel previewlabel = new JLabel("색상 미리보기");
		this.add(previewlabel);
	}

	public void setLineColor(Color linecolor) {
		this.lineColor = linecolor;
	}

	public void setStroke(int index) {
		this.stroke = index;
	}

	public void setDash(float[] dash) {
		this.dash = dash;
	}

	public void setFillColor(Color fillcolor) {
		this.fillColor = fillcolor;
	}

	public void paint(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g;
		super.paint(graphics2d);

		if (this.fillColor != null) {
			graphics2d.setColor(fillColor);
			graphics2d.fillRect(10, 25, 30, 30);
		}
		graphics2d.setColor(lineColor);

		graphics2d.drawRect(10, 25, 30, 30);

		repaint();
	}

}
