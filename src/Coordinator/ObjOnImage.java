package Coordinator;

import Form.Grid;
import org.opencv.core.Scalar;

import java.awt.*;

public class ObjOnImage {

    private int xPos, yPos;
    private int xRealPos, yRealPos;
    private String name;
    private int number;
    private Scalar HSVmin, HSVmax;
    private Scalar colour;

    public static int objCount = 1;


    public ObjOnImage(){
        this.number = 0;
        this.name = "null";
        this.colour = new Scalar(0,0,0);
    }
    public ObjOnImage(Scalar hsv_min, Scalar hsv_max){
        this.number = objCount++;
        this.name = "obj";
        HSVmax = hsv_max;
        HSVmin = hsv_min;
        this.colour = new Scalar(0,0,0);
    }
    public ObjOnImage(int _num, String _name, Scalar hsv_min, Scalar hsv_max, Scalar _colrRGB){
        this(hsv_min,hsv_max);
        this.number = _num;
        this.name = _name;
        this.colour = _colrRGB;
    }

    @Override
    public String toString(){
        String ret = "";
        ret = ret +
                number + " " + name + " " +
                HSVmin.val[0] + " " + HSVmin.val[1] + " " + HSVmin.val[2] + " " +
                HSVmax.val[0] + " " + HSVmax.val[1] + " " + HSVmax.val[2] + " " +
                colour.val[0] + " " + colour.val[1] + " " + colour.val[3];
        return ret;

    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public String getName() {
        return name + " " + number;
    }

    public Scalar getHSVmin() {
        return HSVmin;
    }

    public Scalar getHSVmax() {
        return HSVmax;
    }

    public Scalar getColour() {
        return colour;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHSVmin(Scalar HSVmin) {
        this.HSVmin = HSVmin;
    }

    public void setHSVmax(Scalar HSVmax) {
        this.HSVmax = HSVmax;
    }

    public void setColour(Scalar colour) {
        this.colour = colour;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Color getRGBColor(){
        return new Color((int)colour.val[0],(int)colour.val[1],(int)colour.val[2]);
    }

    public Point getRealCoordinates(double cameraHeigh, double cameraFocus, Grid grid) {
//        this.xRealPos = (int)((cameraHeigh*(xPos - grid.getUpLeftCorner().x)*0.02645833333333)/cameraFocus);
//        this.yRealPos = (int)((cameraHeigh*(yPos - grid.getUpLeftCorner().y)*0.02645833333333) / cameraFocus);
        this.xRealPos = xPos - (int)grid.getUpLeftCorner().x;
        this.yRealPos = yPos - (int)grid.getUpLeftCorner().y;
        //Debug
        //System.out.println(this.number + "   " + this.xRealPos + "   " + this.yRealPos);

        return new Point(this.xPos,this.yPos);
    }
}
