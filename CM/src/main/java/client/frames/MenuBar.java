package client.frames;

import javax.swing.JMenuBar;
import client.menus.ColorMenu;
import client.menus.EditMenu;
import client.menus.FileMenu;
import client.menus.StrokeMenu;
import client.menus.ZoomMenu;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private DrawingPanel drawingPanel;
	private FileMenu fileMenu;
	private EditMenu editMenu;
	private ColorMenu colorMenu;
	private StrokeMenu strokeMenu;
	//private ZoomMenu zoomMenu;

	public MenuBar() {
		this.fileMenu = new FileMenu();
		this.add(this.fileMenu);

		this.editMenu = new EditMenu();
		this.add(this.editMenu);
		
		this.colorMenu = new ColorMenu();
		this.add(this.colorMenu);
		
		this.strokeMenu = new StrokeMenu();
		this.add(this.strokeMenu);

//		this.zoomMenu = new ZoomMenu();
//		this.add(this.zoomMenu);
	}

	public void associate(DrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
		this.fileMenu.associate(this.drawingPanel);
		this.editMenu.associate(this.drawingPanel);
		this.colorMenu.associate(this.drawingPanel);
		this.strokeMenu.associate(this.drawingPanel);
		//this.zoomMenu.associate(this.drawingPanel);
	}

	public void checkWindowSave() {
		this.fileMenu.quit();
	}
	
}
