package client.frames;
import client.global.Constants;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ColorPanel extends JPanel{

    public ColorPanel() {
        setLayout(new GridLayout(2,9));
        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener actionListener = new ColorHandler();

        for (Constants.EColors ecolors : Constants.EColors.values()) {
            JRadioButton button = new JRadioButton();
            button.setPreferredSize(new Dimension(30, 30));
            button.setBackground(Color.white);
            button.setIcon(new ImageIcon(ecolors.getImage()));
            button.setSelectedIcon(new ImageIcon(ecolors.getSelectedImage()));
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
