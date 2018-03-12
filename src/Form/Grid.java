package Form;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.*;

/**
 * Created by Александр on 22.04.2017.
 */
public class Grid {
    private Point upLeftCorner; /// = new org.opencv.core.Point(20,30);
    private Point downRightCorner;
    private int rowsNumber;
    private int colsNumber;
    private Scalar color;
    private int thickness = 1;

    public Grid(Point upLeftCorner, Point downRightCorner, int rowsNumber, int colsNumber) {
        this.upLeftCorner = upLeftCorner;
        this.downRightCorner = downRightCorner;
        this.rowsNumber = rowsNumber;
        this.colsNumber = colsNumber;
        this.color = new Scalar(Color.RED.getBlue(),Color.RED.getGreen(),Color.RED.getRed());
        this.thickness = 1;
    }

    public Grid(Point upLeftCorner, Point downRightCorner) {
        this(upLeftCorner,downRightCorner,5,5);
    }

    public void getGridedCameraFeed(Mat ret){

        //rows
        for(int i = 0; i < this.getRowsNumber() + 1;i++){
            int y = ((int)(this.getDownRightCorner().y - this.getUpLeftCorner().y)/this.getRowsNumber())*i + (int)this.getUpLeftCorner().y;
            Imgproc.line(ret,new Point(this.getUpLeftCorner().x,y),new Point(this.getDownRightCorner().x,y),this.getColor(),this.getThickness());
        }
        //cols
        for(int j = 0; j < this.getColsNumber() + 1;j++){
            int x = ((int)(this.getDownRightCorner().x - this.getUpLeftCorner().x)/this.getColsNumber())*j + (int)this.getUpLeftCorner().x;
            Imgproc.line(ret,new Point(x,this.getUpLeftCorner().y),new Point(x,this.getDownRightCorner().y),this.getColor(),this.getThickness());
        }

    }

    public Point getUpLeftCorner() {
        return upLeftCorner;
    }

    public Point getDownRightCorner() {
        return downRightCorner;
    }

    public int getRowsNumber() {
        return rowsNumber;
    }

    public int getColsNumber() {
        return colsNumber;
    }

    public Scalar getColor() {
        return color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setUpLeftCorner(Point upLeftCorner) {        this.upLeftCorner = upLeftCorner;    }

    public void setDownRightCorner(Point downRightCorner) {
        //check x
        if(downRightCorner.x < this.upLeftCorner.x )
            downRightCorner.x = this.upLeftCorner.x;
        //check y
        if(downRightCorner.y < this.upLeftCorner.y)
            downRightCorner.y = this.upLeftCorner.y;
        this.downRightCorner = downRightCorner;
    }

    public void setRowsNumber(int rowsNumber) {
        this.rowsNumber = rowsNumber;
    }

    public void setColsNumber(int colsNumber) {
        this.colsNumber = colsNumber;
    }

    public void setColor(Scalar color) {
        this.color = color;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}
