package com.unesc.pesquisa.view;

import javax.swing.*;
import java.awt.*;

public class IndexingWindow extends JFrame {
    private JPanel panel;
    private JLabel labelGif;

    public IndexingWindow(Component parent) {
        super("Indexando arquivos");
        setContentPane(panel);
        setLocationRelativeTo(parent);
        setSize(400, 200);
        setVisible(true);
    }

    private void createUIComponents() {
        Icon imgIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/duck_running.gif"));
        labelGif = new JLabel(imgIcon);
    }
}
