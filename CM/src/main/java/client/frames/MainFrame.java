package client.frames;

import client.shapes.GShape;
import server.CMClientApp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static client.global.Main.cmClientApp;

public class MainFrame extends JFrame {
  private final MenuBar menuBar;
  private final ToolBar toolBar;
  private DrawingPanel drawingPanel;
  private JTabbedPane tabPane;
  private JButton joinLeaveButton;
  private JTextArea logArea;
  private JScrollPane scrollPane;
  private final Vector<JPanel> drawingPanelList;
  private int count = 1;

  // sungwoon 작성

  public Vector<JPanel> getDrawingPanelList() {
    return drawingPanelList;
  }

  public DrawingPanel getDrawingPanel() {
    return this.drawingPanel;
  }

  // sungwoon 작성 여기까지

  @SuppressWarnings("static-access")
  public MainFrame() {
    // attribute
    this.setSize(1100, 800);
    this.setTitle("Drawing Board :: 협동분산시스템 6팀");
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    ImageIcon img = new ImageIcon("images/canvas.png");
    this.setIconImage(img.getImage());

    drawingPanelList = new Vector<JPanel>();

    BorderLayout borderlayout = new BorderLayout();
    this.setLayout(borderlayout);
    ExitHandler exitHandler = new ExitHandler();
    this.addWindowListener(exitHandler);

    // components
    drawingPanel = new DrawingPanel();
    toolBar = new ToolBar();
    menuBar = new MenuBar();

    // 비활성화
    disableComponents();

    add(drawingPanel, BorderLayout.CENTER);
    add(toolBar, BorderLayout.NORTH);
    setJMenuBar(menuBar);


    logArea = new JTextArea();
    logArea.setEditable(false);

    // TODO: log UI 깔끔하게 수정
    scrollPane = new JScrollPane(logArea);
    add(scrollPane, BorderLayout.EAST);
    scrollPane.setPreferredSize(new Dimension(200, this.getHeight()));

    joinLeaveButton = new JButton("Join");

    joinLeaveButton.addActionListener(e -> {
      if (joinLeaveButton.getText().equals("Join")) {
        attemptLogin();
      } else {
        cmClientApp.logoutProcess();
        this.drawingPanel.setShapes(new Vector<GShape>());
        joinLeaveButton.setText("Join");
        disableComponents();
      }
    });

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(scrollPane, BorderLayout.CENTER);
    rightPanel.add(joinLeaveButton, BorderLayout.SOUTH);

    add(rightPanel, BorderLayout.EAST);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        cmClientApp.logoutProcess();
      }
    });

//    this.tabPane = new JTabbedPane();
//    TabbedPaneHandler tabbedPaneHandler = new TabbedPaneHandler();
//    tabPane.addTab("File", this.drawingPanel);
//    this.add(tabPane);

//    JPanel btnpanel = new JPanel();
//    JButton addPanelBtn = new JButton("add DrawingPanel");
//    JButton removePanelBtn = new JButton("remove SelectedPanel");
//    btnpanel.add(addPanelBtn);
//    btnpanel.add(removePanelBtn);

//    addPanelBtn.addActionListener(tabbedPaneHandler);
//    addPanelBtn.setActionCommand("addPanel");
//    removePanelBtn.addActionListener(tabbedPaneHandler);
//    removePanelBtn.setActionCommand("removePanel");
//    this.add(btnpanel, borderlayout.SOUTH);

    // association
    this.toolBar.associate(drawingPanel);
    this.menuBar.associate(drawingPanel);
  }
  private void attemptLogin() {
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    final JComponent[] inputs = new JComponent[] {
            new JLabel("username"),
            usernameField,
            new JLabel("password"),
            passwordField
    };
    int result = JOptionPane.showConfirmDialog(this, inputs, "Login", JOptionPane.DEFAULT_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String username = usernameField.getText();
      String password = new String(passwordField.getPassword());
      boolean loginSuccess = cmClientApp.loginProcess(username, password);
      if (loginSuccess) {
        joinLeaveButton.setText("Leave");
        enableComponents();
      } else {
        JOptionPane.showMessageDialog(this, "로그인 실패", "Login Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  private void enableComponents() {
    SwingUtilities.invokeLater(() -> {
      for (Component comp : toolBar.getComponents()) {
        comp.setEnabled(true);
      }
      for (int i = 0; i < menuBar.getMenuCount(); i++) {
        menuBar.getMenu(i).setEnabled(true);
      }
      drawingPanel.setEnabled(true);
      drawingPanel.addMouseHandling();
    });
  }

  private void disableComponents() {
    SwingUtilities.invokeLater(() -> {
      for (Component comp : toolBar.getComponents()) {
        comp.setEnabled(false);
      }
      for (int i = 0; i < menuBar.getMenuCount(); i++) {
        menuBar.getMenu(i).setEnabled(false);
      }
      drawingPanel.setEnabled(false);
      drawingPanel.removeMouseHandling();
    });
  }


  public void updateLog(String message) {
    SwingUtilities.invokeLater(() -> {
      logArea.append(message + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }
  public static void setTabTitle(JPanel tab, String title) {
    JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class,
        tab);

    for (int tabIndex = 0; tabIndex < tabbedPane.getTabCount(); tabIndex++) {
      if (SwingUtilities.isDescendingFrom(tab, tabbedPane.getComponentAt(tabIndex))) {
        tabbedPane.setTitleAt(tabIndex, title);
        break;
      }
    }
  }

  private class ExitHandler extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
      menuBar.checkWindowSave();
    }
  }

  private class TabbedPaneHandler implements ActionListener, ChangeListener {

    private TabbedPaneHandler() {
      tabPane.addChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("addPanel")) {
        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanelList.add(drawingPanel);
        tabPane.addTab("File" + count, drawingPanel);
        count++;
      } else {
        if (drawingPanelList.size()-1 > 0) {
          drawingPanelList.remove(tabPane.getSelectedIndex());
          tabPane.remove(tabPane.getSelectedIndex());
        }

      }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
      tabPane = (JTabbedPane) e.getSource();
      drawingPanel = (DrawingPanel) drawingPanelList.get(tabPane.getSelectedIndex());
      toolBar.associate(drawingPanel);
      menuBar.associate(drawingPanel);

    }
  }
}
