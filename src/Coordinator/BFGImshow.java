package Coordinator;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class BFGImshow extends Imshow {

    private int deaultWidth = 600;
    private int deaultHeigh = 500;

    public BFGImshow(String title){
        super(title);
        this.Window.setResizable(true);
        this.Window.setSize(new Dimension(deaultWidth,deaultHeigh));

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(final KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {//нажатие
                if(e.getKeyCode() == 16){
                    //разворачиваем на весь экран
                    makeShit();
                }

            }
            if(e.getID() == KeyEvent.KEY_RELEASED){//отпускане
                if(e.getKeyCode() == 27){
                    //возвращаем нормальный вид
                    makeShit0();
                }
            }
            return false;
        }
    };

    @Override
    public synchronized void showImage(Mat img) {
        Imgproc.resize(img, img, new Size((double)this.Window.getSize().width, (double)this.Window.getSize().height));
        BufferedImage bufImage = null;
        try {
            bufImage = this.toBufferedImage(img);
            this.image.setImage(bufImage);
            //this.Window.pack();
            this.label.updateUI();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    //разворот на весь экран без рамок
    private synchronized void makeShit(){

        this.Window.dispose();
        this.Window.setUndecorated(true);
        this.Window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.Window.setVisible(true);

    }

    private synchronized void makeShit0(){
        this.Window.dispose();
        this.Window.setUndecorated(false);
        this.Window.setSize(new Dimension(deaultWidth,deaultHeigh));
        this.Window.setVisible(true);
    }
}
