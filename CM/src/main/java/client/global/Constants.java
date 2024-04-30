package client.global;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import client.menus.ColorMenu;
import client.shapes.GLine;
import client.shapes.GOval;
import client.shapes.GPencil;
import client.shapes.GPolygon;
import client.shapes.GRectangle;
import client.shapes.GSelection;
import client.shapes.GShape;
import client.shapes.GTextBox;
import client.shapes.GTriangle;

public class Constants {

	public enum EColors {
		eColor01("images/color01.png", "images/color01_selected.png", "0x0B0B0B"),
		eColor02("images/color02.png", "images/color02_selected.png", "0x828881"),
		eColor03("images/color03.png", "images/color03_selected.png", "0x870619"),
		eColor04("images/color04.png", "images/color04_selected.png", "0xF11B26"),
		eColor05("images/color05.png", "images/color05_selected.png", "0xFC7C25"),
		eColor06("images/color06.png", "images/color06_selected.png", "0xF7EB01"),
		eColor07("images/color07.png", "images/color07_selected.png", "0x22AB46"),
		eColor08("images/color08.png", "images/color08_selected.png", "0x188DBB"),
		eColor09("images/color09.png", "images/color09_selected.png", "0x444BD7"),
		eColor10("images/color10.png", "images/color10_selected.png", "0xAC50A9"),
		eColor11("images/color11.png", "images/color11_selected.png", "0xFCFCFC"),
		eColor12("images/color12.png", "images/color12_selected.png", "0xC1C2C1"),
		eColor13("images/color13.png", "images/color13_selected.png", "0xB5785B"),
		eColor14("images/color14.png", "images/color14_selected.png", "0xF7ACC1"),
		eColor15("images/color15.png", "images/color15_selected.png", "0xFAC602"),
		eColor16("images/color16.png", "images/color16_selected.png", "0xE4D8A0"),
		eColor17("images/color17.png", "images/color17_selected.png", "0xA9DD16"),
		eColor18("images/color18.png", "images/color18_selected.png", "0x96D9EB"),
		eColor19("images/color19.png", "images/color19_selected.png", "0x779AC8"),
		eColor20("images/color20.png", "images/color20_selected.png", "0xC5BFEA");

		private String image;
		private String selectedimage;
		private String colorvalue;

		EColors(String image, String selectedimage, String colorvalue) {
			this.image = image;
			this.selectedimage = selectedimage;
			this.colorvalue = colorvalue;
		}

		public String getImage() { return image; }

		public String getSelectedImage() {
			return selectedimage;
		}

		public String getColorValue() { return colorvalue; }
	}

	public enum EShapes {
		eSelection(new GSelection(), "images/drag.png", "images/drag-selected.png", "selection", 1),
		eRectangle(new GRectangle(), "images/rectangle.png", "images/rectangle_selected.png", "rectangle", 0),
		eOval(new GOval(), "images/oval.png", "images/oval_selected.png", "oval", 0),
		eTriangle(new GTriangle(), "images/triangle.png", "images/triangle_selected.png", "triangle", 0),
		eLine(new GLine(), "images/line.png", "images/line_selected.png", "line", 0),
		//ePolygon(new GPolygon(), "images/polygon.png", "images/polygon-selected.png", "polygon", 0),
		eTextBox(new GTextBox(), "images/text.png", "images/text-selected.png", "text", 0),
		ePencil(new GPencil(), "images/pencil.png", "images/pencil-selected.png", "pencil", 0);
		//eColor(new ColorMenu(), "image/")

		private GShape shapeTool;
		private String image;
		private String selectedimage;
		private String tooltipname;
		private int currentstate;

		private EShapes(GShape shapeTool, String image, String selectedimage, String tooltipname, int currentstate) {
			this.shapeTool = shapeTool;
			this.image = image;
			this.selectedimage = selectedimage;
			this.tooltipname = tooltipname;
			this.currentstate = currentstate;
		}



		public GShape newInstance() {
			return shapeTool;
		}

		public String getImage() { return image; }

		public String getSelectedImage() {
			return selectedimage;
		}

		public String getTooltipName() {
			return tooltipname;
		}

		public int getCurrentState() {
			return currentstate;
		}
	}

	public enum ECursor {
		eCursorImage("images/default-cursor.png", "images/rotate-cursor.png");

		private String cursorimage;
		private String rotateimage;

		private ECursor(String cursorimage, String rotateimage) {
			this.cursorimage = cursorimage;
			this.rotateimage = rotateimage;
		}

		public String getCursorImage() {
			return cursorimage;
		}

		public String getRotateImage() {
			return rotateimage;
		}

	}

	public enum EMenu {
		eFile("file"),
		eEdit("edit"),
		eColor("color");

		private String text;

		private EMenu(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

	}

	public enum EFileMenuItem {
		eNew("new", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)),
		eOpen("open", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)),
		eOpenImage("openImage", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK)),
		eSave("save", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)),
		eSaveAs("saveAs", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)),
		ePrint("print", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK)),
		eExit("exit", KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK));

		private String text;
		private KeyStroke keyStroke;

		private EFileMenuItem(String text, KeyStroke keyStroke) {
			this.text = text;
			this.keyStroke = keyStroke;
		}

		public String getText() {
			return this.text;
		}

		public KeyStroke getKeyStroke() {
			return this.keyStroke;
		}
	}

	public enum EEditMenuItem {
//		eUndo("undo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK)),
//		eRedo("redo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)),
//		eCut("cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)),
//		eCopy("copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)),
//		ePaste("paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)),
		eDelete("delete", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));

		private String text;
		private KeyStroke keyStroke;

		private EEditMenuItem(String text, KeyStroke keyStroke) {
			this.text = text;
			this.keyStroke = keyStroke;
		}

		public String getText() {
			return this.text;
		}

		public KeyStroke getKeyStroke() {
			return this.keyStroke;
		}

	}

	public enum EColorMenuItem {
		eLineColor("linecolor"), 
		eFillColor("fillcolor");

		private String text;

		private EColorMenuItem(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

	}
	
	public enum EStrokeMenuItem {
		eStroke("stroke");
		private String text;

		private EStrokeMenuItem(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

	}

//	public enum EZoomMenuItem {
//		eZoom("zoom");
//
//		private String text;
//		private EZoomMenuItem(String text) {
//			this.text = text;
//		}
//		public String getText() {
//			return this.text;
//		}
//	}

	public enum EPopupMenu{
		eCopy("Copy","copy"),
		eCut("Cut","cut"),
		ePaste("Paste","paste"),
		eFront("setFront","shapeGoFront"),
		eBack("setBack","shapeGoBack"),
		eBackgroundColor("setBackgroundColor","setBackgroundColor");
		
		private String title;	
		private String actionCommand;
		
		private EPopupMenu(String title,String actionCommand) {
			this.title = title;
			this.actionCommand = actionCommand;
		}		
		public String getTitle() {
			return this.title;
		}
		public String getActionCommand() {
			return this.actionCommand;
		}
	}
	

}
