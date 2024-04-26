package client.frames;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ColorPanel extends JPanel{

    public ColorPanel() {
        setLayout(new GridLayout(2,9));
        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener actionListener = new ColorHandler();

        String[] colorList = new String[] {
                "images/color01.png", "images/color02.png", "images/color03.png", "images/color04.png", "images/color05.png",
                "images/color06.png", "images/color07.png", "images/color08.png", "images/color09.png", "images/color10.png",
                "images/color11.png", "images/color12.png", "images/color13.png", "images/color14.png", "images/color15.png",
                "images/color16.png", "images/color17.png", "images/color18.png", "images/color19.png", "images/color20.png",
        };

        String[] colorSelectedList = new String[] {
                "images/color01_selected.png", "images/color02_selected.png", "images/color03_selected.png", "images/color04_selected.png", "images/color05_selected.png",
                "images/color06_selected.png", "images/color07_selected.png", "images/color08_selected.png", "images/color09_selected.png", "images/color10_selected.png",
                "images/color11_selected.png", "images/color12_selected.png", "images/color13_selected.png", "images/color14_selected.png", "images/color15_selected.png",
                "images/color16_selected.png", "images/color17_selected.png", "images/color18_selected.png", "images/color19_selected.png", "images/color20_selected.png",
        };

        for (int i=0; i<20; i++) {
            JRadioButton button = new JRadioButton();
            button.setPreferredSize(new Dimension(30, 30));
            button.setBackground(Color.white);
            button.setIcon(new ImageIcon(colorList[i]));
            button.setSelectedIcon(new ImageIcon(colorSelectedList[i]));
            button.addActionListener(actionListener);
            this.add(button);
            buttonGroup.add(button);
        }

        setPreferredSize(new Dimension(300, 60));
    }

    private class ColorHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

        }
    }

}
