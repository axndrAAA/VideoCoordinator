package Coordinator;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Imshow {
    public JFrame Window;
    protected ImageIcon image;
    protected JLabel label;
    protected MatOfByte matOfByte;
    protected Boolean SizeCustom;
    protected int Height;
    protected int Width;

    public Imshow(){
        this("Imshow");
    }
    public Imshow(String title) {
        this.Window = new JFrame();
        this.image = new ImageIcon();
        this.label = new JLabel();
        this.matOfByte = new MatOfByte();
        this.label.setIcon(this.image);
        this.Window.getContentPane().add(this.label);
        this.Window.setResizable(false);
        this.Window.setTitle(title);
        this.SizeCustom = false;
        this.setCloseOption(JFrame.HIDE_ON_CLOSE);
        this.Window.setVisible(true);
    }

    public Imshow(String title, int height, int width) {
        this(title);
        this.SizeCustom = true;
        this.Height = height;
        this.Width = width;
    }

    public void showImage(Mat img) {
        if (this.SizeCustom.booleanValue()) {
            Imgproc.resize(img, img, new Size((double)this.Height, (double)this.Width));
        }
        BufferedImage bufImage = null;

        try {
            bufImage = this.toBufferedImage(img);
            this.image.setImage(bufImage);
            this.Window.pack();
            this.label.updateUI();
            //this.Window.setVisible(true);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }



    public static BufferedImage toBufferedImage(Mat m) {
        int type = 10;
        if (m.channels() > 1) {
            type = 5;
        }

        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public void setCloseOption(int option) {
        switch(option) {
            case 0:
                this.Window.setDefaultCloseOperation(3);
                break;
            case 1:
                this.Window.setDefaultCloseOperation(1);
                break;
            default:
                this.Window.setDefaultCloseOperation(3);
        }

    }

}

