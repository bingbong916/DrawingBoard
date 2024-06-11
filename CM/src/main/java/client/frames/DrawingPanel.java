package client.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.global.ClientBroadcast;
import client.global.Main;
import client.menus.PopupMenu;
import client.shapes.GAnchor;
import client.shapes.GAnchor.EAnchors;
import client.shapes.GSelection;
import client.shapes.GShape;
import client.shapes.GShape.EDrawingStyle;
import client.shapes.GShape.EOnState;
import client.shapes.GTextBox;
import client.tool.Clipboard;
import client.tool.CursorManager;
import client.transformer.GDrawer;
import client.transformer.GMover;
import client.transformer.GResizer;
import client.transformer.GRotator;
import client.transformer.GTransformer;

public class DrawingPanel extends JPanel implements java.awt.print.Printable {

  private boolean isUpdated;

  private PreviewPanel previewPanel;

  private Color lineColor;
  private Color fillColor;
  private Color backgroundColor;
  private Color objectbackgroundColor;
  private int stroke;
  private Image openimage;
  private Image pixelimage;
  private int index;
  private float[] dash;
  private double scale;

  private Vector<GShape> shapes;
  private GShape selectedShape;
  private GShape shapeTool;
  private final Clipboard clip;
  private GTransformer transformer;

  private final Vector<GShape> forFront;
  private final Map<String, String> lockedShapes = new HashMap<>();
  private EDrawingState eDrawingState;
  private ECurrentState eCurrentState;
  private EAnchors eAnchor;

  private enum EDrawingState {
    eIdle, // no draw state
    eTransforming
  }

  public enum ECurrentState {
    eDrawing, // only draw shape(draw)
    eSelecting // only transform shape(move,resize,rotate)
  }

  public DrawingPanel() {
    // attributes
    this.lineColor = Color.BLACK;
    this.fillColor = null;
    this.stroke = 1;
    this.backgroundColor = null;
    this.objectbackgroundColor = null;
    this.isUpdated = false;

    BorderLayout borderlayout = new BorderLayout();
    this.setLayout(borderlayout);
    this.setForeground(Color.BLACK);
    this.setBackground(Color.WHITE);
    this.eDrawingState = EDrawingState.eIdle;
    this.eCurrentState = ECurrentState.eSelecting;

    // components
    this.forFront = new Vector<GShape>();
    this.shapes = new Vector<GShape>();
    this.clip = new Clipboard();
    this.transformer = null;
    this.openimage = null;
    this.pixelimage = null;
    this.index = 0;
    this.scale = 1.0;

    MouseHandler mouseHandler = new MouseHandler();
    // button
    this.addMouseListener(mouseHandler);
    // position
    this.addMouseMotionListener(mouseHandler);
    // wheel
    this.addMouseWheelListener(mouseHandler);
  }

  public void initiatePanel() {
    this.shapes.clear();
    this.clip.tempshapes.clear();
    this.clip.clipshapes.clear();
    this.forFront.clear();
    this.isUpdated = false;
    this.fillColor = null;
    this.objectbackgroundColor = null;
    this.lineColor = Color.BLACK;
    this.backgroundColor = Color.WHITE;
    this.stroke = 1;
    this.selectedShape = null;
    this.openimage = null;
    this.pixelimage = null;
    previewPanel.setFillColor(Color.WHITE);
    previewPanel.setLineColor(Color.BLACK);
    if (Main.cmClientApp.getCmClientStub() == null) {
      System.err.println("CMClientStub is not initialized!");
      return;
    }
    this.setBackgroundColor(backgroundColor);
    this.repaint();
  }

  // get/set methods

  public void associatePreviewPanel(PreviewPanel previewPanel) {
    this.previewPanel = previewPanel;
    this.previewPanel.setFillColor(fillColor);
    this.previewPanel.setLineColor(lineColor);
  }

  public boolean isUpdated() {
    return isUpdated;
  }

  public Vector<GShape> getShapes() {
    return this.shapes;
  }

  public ECurrentState geteCurrentState() {
    return eCurrentState;
  }

  public void setUpdated(boolean isUpdated) {
    this.isUpdated = isUpdated;
  }

  @SuppressWarnings("unchecked")
  public void setShapes(Object shapes) {
    this.shapes = (Vector<GShape>) shapes;
  }

  public void openImage(Image image) {
    openimage = image;
    isUpdated = true;
  }

  public void setImagePixel(Object imageobject) {
    int[] imagePixel = (int[]) imageobject;
    if (imagePixel != null) {
      Toolkit tk = Toolkit.getDefaultToolkit();
      ColorModel cm = ColorModel.getRGBdefault();
      pixelimage = tk.createImage(new MemoryImageSource(100, 100, cm, imagePixel, 0, 100));
    }
    isUpdated = false;
  }

  public void setUserFont(Font font) {
    if (this.selectedShape instanceof GTextBox) {
      ((GTextBox) this.selectedShape).setFont(font);
    }
    this.repaint();
  }

  public void seteCurrentState(int currentstate) {
    if (currentstate == 0) {
      this.eCurrentState = ECurrentState.eDrawing;
      this.clearSelected();
    } else {
      this.eCurrentState = ECurrentState.eSelecting;
    }
  }

  public void addMouseHandling() {
    MouseHandler mouseHandler = new MouseHandler();
    this.addMouseListener(mouseHandler);
    this.addMouseMotionListener(mouseHandler);
  }

  public void removeMouseHandling() {
    for (MouseListener listener : this.getMouseListeners()) {
      this.removeMouseListener(listener);
    }
    for (MouseMotionListener listener : this.getMouseMotionListeners()) {
      this.removeMouseMotionListener(listener);
    }
  }

  public void setSelection(GShape shapeTool) {
    this.shapeTool = shapeTool;
  }

  public void setStroke(int index, float[] dash) {
    for (GShape shape : this.shapes) {
      if (shape.isSelected()) {
        shape.setStroke(index);
        shape.setStrokeDash(dash);
        ClientBroadcast.broadcastUpdate(shape.cloneShapes());
      }
    }
    this.stroke = index;
    this.dash = dash;
    previewPanel.setStroke(index);
    previewPanel.setDash(dash);
    this.repaint();
  }

  public int getStroke() {
    return this.stroke;
  }

  public float[] getDash() {
    return this.dash;
  }

  public void setSelectedLineColor() {
    for (GShape shape : this.shapes) {
      if (shape.isSelected()) {
        shape.setLineColor(lineColor);
        ClientBroadcast.broadcastUpdate(shape.cloneShapes());
      }
    }
    this.repaint();
  }

  public void setSelectedFillColor() {
    for (GShape shape : this.shapes) {
      if (shape.isSelected()) {
        shape.setFillColor(fillColor);
        ClientBroadcast.broadcastUpdate(shape.cloneShapes());
      }
    }
    this.repaint();
  }

  public void setLineColor(Color linecolor) {
    this.lineColor = linecolor;
    previewPanel.setLineColor(linecolor);
  }

  public void setFillColor(Color fillcolor) {
    this.fillColor = fillcolor;
    previewPanel.setFillColor(fillcolor);
  }

  public void setBackgroundColor(Color backgroundcolor) {
    this.backgroundColor = backgroundcolor;
    this.setBackground(backgroundColor);
    this.isUpdated = true;
  }

  public void setObjectBackColor(Object backcolorobject) {
    this.objectbackgroundColor = (Color) backcolorobject;
  }

  public void setColorInfo() {
    this.selectedShape.setFillColor(this.fillColor);
    this.selectedShape.setLineColor(this.lineColor);
    this.selectedShape.setStroke1(stroke);
    this.selectedShape.setDash1(dash);
  }

  public void setShapeFrontBack(boolean front) {
    this.forFront.clear();

    if (front) {
      this.shapes.remove(this.selectedShape);
      this.forFront.addAll(this.shapes);
      this.forFront.add(this.selectedShape);
    } else {
      this.forFront.add(this.selectedShape);
      this.shapes.remove(this.selectedShape);
      this.forFront.addAll(this.shapes);
    }
    this.shapes.clear();
    this.shapes.addAll(this.forFront);
    this.repaint();
  }

  public void clearAllShapes() {
    this.shapes.clear();
    this.selectedShape = null;
    this.transformer = null;
    this.isUpdated = false;
    this.repaint();
  }

  // 서버로부터 lockMap을 업데이트하는 메서드
  public void updateLockMap(Map<String, String> newLockMap) {
    lockedShapes.clear();
    lockedShapes.putAll(newLockMap);
  }

  // 도형 잠금 요청 메서드
  public void lockShape(String shapeId) {
    GShape shape = findShapeById(shapeId);
    if (shape != null) {
      ClientBroadcast.broadcastLock(shape);
    }
  }

  // 도형 잠금 해제 요청 메서드
  public void unlockShape(String shapeId) {
    GShape shape = findShapeById(shapeId);
    if (shape != null) {
      ClientBroadcast.broadcastUnlock(shape);
    }
  }

  public GShape findShapeById(String shapeId) {
    for (GShape shape : shapes) {
      if (shape.getShapeId().equals(shapeId)) {
        return shape;
      }
    }
    return null;
  }

  public boolean isShapeLockedByAnotherUser(GShape shape) {
    String currentUser = Main.cmClientApp.getCmClientStub().getCMInfo().getInteractionInfo().getMyself().getName();
    return lockedShapes.containsKey(shape.getShapeId()) && !lockedShapes.get(shape.getShapeId()).equals(currentUser);
  }

  public void lockShapes(Vector<GShape> shapes) {
    for (GShape shape : shapes) {
      lockShape(shape.getShapeId());
    }
  }

  public void unlockShapes(Vector<GShape> shapes) {
    for (GShape shape : shapes) {
      unlockShape(shape.getShapeId());
    }
  }

  public void paint(Graphics g) {
    Graphics2D graphics2d = (Graphics2D) g;
    super.paint(g);

    if (this.scale != 1.0) {
      AffineTransform transform = new AffineTransform();
      transform.scale(this.scale, this.scale);
      graphics2d.setTransform(transform);
    }

    if (this.openimage != null) {
      graphics2d.drawImage(openimage, 0, 0, this);
    }

    if (this.pixelimage != null) {
      graphics2d.drawImage(pixelimage, 0, 0, this);
    }

    if (this.selectedShape != null) {
      this.selectedShape.draw(graphics2d);
    }

    if (this.selectedShape instanceof GTextBox) {
      (selectedShape).draw(graphics2d);
    }

    if (this.objectbackgroundColor != null) {
      this.setBackground(objectbackgroundColor);
      this.objectbackgroundColor = null;
    }

    for (GShape shape : shapes) {
      shape.draw(graphics2d);
    }

    repaint();
  }

  private GShape transformInitGShape;

  private void initTransforming(int x1, int y1) {
    if (this.selectedShape != null) {
      transformInitGShape = this.selectedShape.cloneShapes();
    } else {
      transformInitGShape = null;
    }
    if (this.transformer == null) {
      return; // transformer 가 null 인 경우 처리
    }

    if (this.transformer instanceof GDrawer) {
      this.clearSelected();
      this.selectedShape = this.shapeTool.clone();
      transformInitGShape = this.selectedShape.cloneShapes();
      this.setColorInfo();
      if (!(this.selectedShape instanceof GSelection)) {
        this.shapes.add(this.selectedShape);
        UUID uuid = UUID.randomUUID();
        String uniqueKey = uuid.toString();
        this.selectedShape.setShapeId(uniqueKey);
        ClientBroadcast.broadcastShape(this.selectedShape);
      }
    }
    if (this.selectedShape instanceof GSelection) {
      this.selectedShape.setFillColor(Color.LIGHT_GRAY);
      this.selectedShape.setLineColor(Color.BLACK);
    }

    this.transformer.setgShape(this.selectedShape);
    this.transformer.initTransforming(x1, y1);
  }

  private void keepTransforming(int x2, int y2) {
    Graphics2D g2 = (Graphics2D) this.getGraphics();
    g2.setXORMode(this.getBackground());
    try {
      if (!isShapeLockedByAnotherUser(this.selectedShape)) {
        this.transformer.keepTransforming(g2, x2, y2);
        if (this.selectedShape != null) {
          ClientBroadcast.broadcastUpdate(this.selectedShape);
        }
      }
    } catch (NullPointerException ignored) {
    }
  }

  private void finishTransforming(int x2, int y2) {
    if (this.selectedShape != null && !isShapeLockedByAnotherUser(this.selectedShape)) {
      this.transformer.finishTransforming((Graphics2D) this.getGraphics(), x2, y2);
      if (this.transformer instanceof GDrawer) {
        if (this.selectedShape instanceof GSelection) {
          ((GSelection) this.selectedShape).contains(this.shapes);
          Vector<GShape> containedShapes = ((GSelection) this.selectedShape).getContainedShapes();
          containedShapes.removeIf(this::isShapeLockedByAnotherUser);
          lockShapes(containedShapes);
          this.selectedShape = null;
          this.isUpdated = this.shapes.size() > 0;
        } else {
          if (!this.shapes.contains(this.selectedShape)) {
            this.shapes.add(this.selectedShape);
            this.clip.tempshapes.clear();
            this.isUpdated = true;

            UUID uuid = UUID.randomUUID();
            String uniqueKey = uuid.toString();
            this.selectedShape.setShapeId(uniqueKey);
            ClientBroadcast.broadcastShape(this.selectedShape);
          } else {
            ClientBroadcast.broadcastUpdate(this.selectedShape);
          }
        }
      } else if (this.transformInitGShape != null) {
        ClientBroadcast.broadcastUpdate(this.selectedShape.cloneShapes());
      }
      this.repaint();
    } else {
      System.err.println("선택된 도형이 없거나, 다른 유저가 사용 중");
    }
  }

  private void continueTransforming(int x2, int y2) {
    this.transformer.setgShape(this.selectedShape);
    Graphics2D g2 = (Graphics2D) this.getGraphics();
    this.transformer.continueTransforming(g2, x2, y2);
  }

  // methods
  private void defineActionState(int x, int y) {
    EOnState eOnState = onShape(x, y);
    if (eOnState == null) {
      this.clearSelected(); // 도형 외 다른 부분을 누름
      this.transformer = new GDrawer(); // 그림 그리기 모드
    } else if (geteCurrentState() == ECurrentState.eSelecting) {
      GShape shapeUnderCursor = getShapeAt(x, y); // 위치에서 도형 검색
      if (shapeUnderCursor != null && !isShapeLockedByAnotherUser(shapeUnderCursor)) {
        this.selectedShape = shapeUnderCursor;
        if (this.selectedShape != null && this.selectedShape != shapeUnderCursor) {
          this.clearSelected(); // 다른 도형을 선택했을 때 현재 선택된 도형 해제
        }
        this.selectedShape = shapeUnderCursor;
        if (!this.selectedShape.isSelected()) {
          this.selectedShape.setSelected(true);
          lockShape(this.selectedShape.getShapeId()); // 내가 누른 도형 lock
        }
        switch (eOnState) {
          case eOnShape:
            this.transformer = new GMover();
            break;
          case eOnResize:
            this.transformer = new GResizer();
            break;
          case eOnRotate:
            this.transformer = new GRotator();
            break;
          default:
            this.eDrawingState = null;
            break;
        }
      } else {
        clearSelected(); // 다른 사람이 선택한 도형을 클릭 -> clearSelected
      }
    }
  }
  private GShape getShapeAt(int x, int y) {
    for (GShape shape : this.shapes) {
      if (shape.onShape(x, y) != null) {
        return shape;
      }
    }
    return null;
  }

  private void clearSelected() {
    Vector<GShape> shapesToUnlock = new Vector<>();
    // 현재 선택된 도형을 shapesToUnlock 벡터에 추가
    if (this.selectedShape != null) {
      shapesToUnlock.add(this.selectedShape);
    }
    // 선택된 도형을 shapesToUnlock 벡터에 추가, 선택 해제
    for (GShape shape : this.shapes) {
      if (shape.isSelected()) {
        shapesToUnlock.add(shape);
        shape.setSelected(false);
      }
    }
    // unlockShapes 메서드 호출, 도형 잠금을 해제
    unlockShapes(shapesToUnlock);
    // 선택된 도형을 null 로 설정
    this.selectedShape = null;
    this.repaint();
  }

  public EOnState onShape(int x, int y) {
    for (GShape shape : this.shapes) {
      if (isShapeLockedByAnotherUser(shape)) continue;
      EOnState eOnState = shape.onShape(x, y);
      if (eOnState != null) {
        return eOnState;
      }
    }
    return null;
  }

  private EAnchors confirmAnchorSelected(int x, int y) {
    if (selectedShape != null) {
      GAnchor gAnchor = selectedShape.getGAnchor();
      eAnchor = gAnchor.getSelectedAnchor(x, y);
    } else {
      eAnchor = null;
    }
    return eAnchor;
  }

  private Cursor getResizeCursor(EAnchors eAnchor) {
    Cursor resizeCursor = null;
    switch (eAnchor) {
      case NW:
        resizeCursor = CursorManager.NW_CURSOR;
        break;
      case NN:
        resizeCursor = CursorManager.NN_CURSOR;
        break;
      case NE:
        resizeCursor = CursorManager.NE_CURSOR;
        break;
      case EE:
        resizeCursor = CursorManager.EE_CURSOR;
        break;
      case SE:
        resizeCursor = CursorManager.SE_CURSOR;
        break;
      case SS:
        resizeCursor = CursorManager.SS_CURSOR;
        break;
      case SW:
        resizeCursor = CursorManager.SW_CURSOR;
        break;
      case WW:
        resizeCursor = CursorManager.WW_CURSOR;
        break;
      case RR:
        resizeCursor = CursorManager.RR_CURSOR;
        break;
      default:
        this.setCursor(CursorManager.DEFAULT_CURSOR);
        break;
    }

    return resizeCursor;
  }

  public void changeCursor(int x, int y) {
    if (geteCurrentState() == ECurrentState.eDrawing) {
      this.setCursor(CursorManager.CROSSHAIR_CURSOR);
    } else {
      this.eAnchor = confirmAnchorSelected(x, y);
      this.setCursor(onShape(x, y) == EOnState.eOnShape ? CursorManager.MOVE_CURSOR
              : (this.eAnchor != null && selectedShape.isSelected()) ? getResizeCursor(eAnchor)
              : CursorManager.DEFAULT_CURSOR);
    }
  }

  public void undo() {
    if (this.clip.tempshapes.size() > 0) {
      this.clearSelected();
      index = clip.getTempShape().size() - 1;
      this.shapes.add(clip.getTempShape().get(index));
      this.shapes.lastElement().setSelected(true);
      clip.tempshapes.remove(index);
    }
    this.repaint();
  }

  public void redo() {
    if (this.shapes.size() > 0) {
      this.clip.setTempShape(shapes.lastElement());
      this.shapes.remove(this.shapes.lastElement());

      if (!this.shapes.isEmpty()) {
        this.shapes.lastElement().setSelected(true);
      }
      this.selectedShape = null;
    }
    this.repaint();
  }

  public void cut() {
    Vector<GShape> selectedShapes = new Vector<GShape>();
    for (int i = this.shapes.size() - 1; i >= 0; i--) {
      if (this.shapes.get(i).isSelected()) {
        selectedShapes.add(this.shapes.get(i));
        this.shapes.remove(i);
      }
    }
    this.clip.setContents(selectedShapes);
    this.selectedShape = null;
    this.repaint();
  }

  public void copy() {
    Vector<GShape> selectedShapes = new Vector<GShape>();
    for (GShape shape : this.shapes) {
      if (shape.isSelected()) {
        selectedShapes.add(shape);
      }
    }
    this.clip.setContents(selectedShapes);
    this.repaint();
  }

  public void paste() {
    Vector<GShape> clipshapes = this.clip.getContents();
    for (GShape shape : clipshapes) {
      this.clearSelected();
      shape.verticalPaste();
      shape.setSelected(true);
    }
    this.shapes.addAll(clipshapes);
    this.clip.setContents(clipshapes);

    repaint();
  }

  public void delete() {
    List<GShape> toDelete = new ArrayList<>();
    for (GShape shape : shapes) {
      if (shape.isSelected()) {
        toDelete.add(shape);
      }
    }
    shapes.removeAll(toDelete);
    toDelete.forEach(ClientBroadcast::broadcastDelete);
    this.selectedShape = null;
    this.clearAllShapes();
    this.repaint();
  }

  // EventHandler
  private class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        defineActionState(e.getX(), e.getY()); // 도형과 상태를 확인하고 설정
        if (eDrawingState == EDrawingState.eIdle) {
          if (shapeTool != null && shapeTool.geteDrawingStyle() == EDrawingStyle.e2PointDrawing) {
            initTransforming(e.getX(), e.getY());
            eDrawingState = EDrawingState.eTransforming;
          }
        }
      } else {
        clearSelected();
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (eDrawingState == EDrawingState.eTransforming) {
        if (shapeTool != null && shapeTool.geteDrawingStyle() == EDrawingStyle.e2PointDrawing) {
          keepTransforming(e.getX(), e.getY());
        }
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (eDrawingState == EDrawingState.eTransforming) {
        if (shapeTool != null && shapeTool.geteDrawingStyle() == EDrawingStyle.e2PointDrawing) {
          finishTransforming(e.getX(), e.getY());
          eDrawingState = EDrawingState.eIdle;
        }
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {// left click
        if (onShape(e.getX(), e.getY()) == null) {
          clearSelected();
        }
        if (e.getClickCount() == 1) {
          mouse1Cliked(e);
        } else if (e.getClickCount() == 2) {
          mouse2Cliked(e);
        }
      } else if (e.getButton() == MouseEvent.BUTTON3) {// right click
        clearSelected();
        PopupMenu popup = new PopupMenu(DrawingPanel.this);
        popup.show(DrawingPanel.this, e.getX(), e.getY());
      }
    }

    private void mouse1Cliked(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        if (shapeTool != null && shapeTool.geteDrawingStyle() == EDrawingStyle.eNPointDrawing) {
          if (eDrawingState == EDrawingState.eIdle) {
            initTransforming(e.getX(), e.getY());
            eDrawingState = EDrawingState.eTransforming;
          } else if (eDrawingState == EDrawingState.eTransforming) {
            continueTransforming(e.getX(), e.getY());
          }
        }
      }
    }

    private void mouse2Cliked(MouseEvent e) {
      if (selectedShape instanceof GTextBox && eCurrentState == ECurrentState.eSelecting) {
        String text = JOptionPane.showInputDialog("삽입하실 글자를 입력해주세요", "글자");
        if (text != null) {
          ((GTextBox) selectedShape).setText(text);
        }
      }
      if (shapeTool != null && shapeTool.geteDrawingStyle() == EDrawingStyle.eNPointDrawing
              && eDrawingState == EDrawingState.eTransforming) {
        finishTransforming(e.getX(), e.getY());
        eDrawingState = EDrawingState.eIdle;
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      if (shapeTool != null && shapeTool.geteDrawingStyle()
              == EDrawingStyle.eNPointDrawing && eDrawingState == EDrawingState.eTransforming) {
        keepTransforming(e.getX(), e.getY());
      } else if (eDrawingState == EDrawingState.eIdle) {
        changeCursor(e.getX(), e.getY());
      }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
      // TODO Auto-generated method stub

    }
  }

  @Override
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
          throws PrinterException {
    Graphics2D grapchis2D;

    if (pageIndex == 0) {
      grapchis2D = (Graphics2D) graphics;
      this.paint(grapchis2D);

      return (PAGE_EXISTS);
    }

    return (NO_SUCH_PAGE);
  }
}
