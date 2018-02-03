package Form;

import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Александр on 22.04.2017.
 */
public class JMapArea extends JPanel{
    private Image image = null;
    public JMapArea(Image i){
        super();
        this.image = i;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
