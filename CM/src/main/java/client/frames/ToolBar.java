package client.frames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import client.global.Constants.EShapes;

@SuppressWarnings("serial")
public class ToolBar extends JToolBar {

	private ActionListener actionListener;
	private DrawingPanel drawingPanel;
	private PreviewPanel previewPanel;
	private ColorPanel colorPanel;
	

	public ToolBar() {
		ButtonGroup buttonGroup = new ButtonGroup();
		this.actionListener = new ToolbarHandler();
		
		for (EShapes eButton : EShapes.values()) {
			JRadioButton button = new JRadioButton();
			button.setPreferredSize(new Dimension(60,60));
			button.setBackground(Color.white); // toString() = name()
			button.setIcon(new ImageIcon(eButton.getImage()));
			button.setSelectedIcon(new ImageIcon(eButton.getSelectedImage()));
			button.setActionCommand(eButton.toString()); // toString is bring enum names. ex)eOval,eLine...
			button.setToolTipText(eButton.getTooltipName());
			button.addActionListener(actionListener);
			this.add(button);
			buttonGroup.add(button);
		}

		this.addSeparator();
		this.colorPanel = new ColorPanel();
		this.add(colorPanel);

		JButton colorBtn = new JButton();
		colorBtn.setIcon(new ImageIcon("images/colorwheel.png"));
		colorBtn.addActionListener(new ColorBtnHandler());

		JButton colorBucketBtn = new JButton();
		colorBucketBtn.setIcon(new ImageIcon("images/colorbucket.png"));
		colorBucketBtn.addActionListener(new ColorBucketBtnHandler());

		this.add(colorBtn);
		this.add(colorBucketBtn);

		this.previewPanel = new PreviewPanel();
		this.addSeparator();
		this.add(previewPanel);
	}

	public void associate(DrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
		JRadioButton defaultBtn = ((JRadioButton) this.getComponent(EShapes.eSelection.ordinal()));
		defaultBtn.doClick();
		drawingPanel.associatePreviewPanel(previewPanel);
	}


	private class ToolbarHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			EShapes eShapeTool = EShapes.valueOf(event.getActionCommand());
			drawingPanel.seteCurrentState(eShapeTool.getCurrentState());
			drawingPanel.setSelection(eShapeTool.newInstance());
		}
	}

	private class ColorBtnHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Color selectedColor = JColorChooser.showDialog(null, "Color", Color.yellow);
			drawingPanel.setLineColor(selectedColor);
			drawingPanel.setSelectedLineColor();
		}
	}

	private class ColorBucketBtnHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Color selectedColor = JColorChooser.showDialog(null, "Color", Color.yellow);
			drawingPanel.setFillColor(selectedColor);
			drawingPanel.setSelectedFillColor();
		}
	}

}
