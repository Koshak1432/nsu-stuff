package kosh.display;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageWindow {
    public ImageWindow(BufferedImage bufImage, String name) {
        this.image = new ImageIcon(bufImage);
        this.title = name;
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void displayImage() {
        JScrollPane pane = new JScrollPane(new JLabel(image), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(pane);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle(title);
    }

    private final ImageIcon image;
    private final String title;
    private final JFrame frame = new JFrame();
}
