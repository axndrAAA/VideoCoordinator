package Coordinator;

import Form.Grid;
import org.opencv.core.Scalar;

import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;

public class BotOnImage {

    private int xPos, yPos;
    private String name;
    private int number;
    private Scalar HSVmin, HSVmax;
    private Scalar colour;

    public static int objCount = 1;


    public BotOnImage(){
        this.number = 0;
        this.name = "null";
        this.colour = new Scalar(0,0,0);
    }
    public BotOnImage(Scalar hsv_min, Scalar hsv_max){
        this.number = objCount++;
        this.name = "obj";
        HSVmax = hsv_max;
        HSVmin = hsv_min;
        this.colour = new Scalar(0,0,0);
    }
    public BotOnImage(int _num, String _name, Scalar hsv_min, Scalar hsv_max, Scalar _colrRGB){
        this(hsv_min,hsv_max);
        this.number = _num;
        this.name = _name;
        this.colour = _colrRGB;
    }

    public BotOnImage(String str)throws NumberFormatException{
        String[] params = str.split(" ");
        int number = Integer.valueOf(params[0]);
        Scalar hsv_min = new Scalar(Double.parseDouble(params[2]),
                Double.parseDouble(params[3]),
                Double.parseDouble(params[4]));
        Scalar hsv_max = new Scalar(Double.parseDouble(params[5]),
                Double.parseDouble(params[6]),
                Double.parseDouble(params[7]));
        Scalar color = new Scalar(Double.parseDouble(params[8]),
                Double.parseDouble(params[9]),
                Double.parseDouble(params[10]));
        this.number = number;
        this.name = params[1];
        this.HSVmin = hsv_min;
        this.HSVmax = hsv_max;
        this.colour = color;
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
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }


    public String getName() {
        return name + " " + number;
    }
    public void setName(String name) {
        this.name = name;
    }


    public Scalar getHSVmax() {
        return HSVmax;
    }
    public void setHSVmax(Scalar HSVmax) {
        this.HSVmax = HSVmax;
    }


    public Scalar getColour() {
        return colour;
    }

    public int getyPos() {
        return yPos;
    }
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }


    public Scalar getHSVmin() {
        return HSVmin;
    }
    public void setHSVmin(Scalar HSVmin) {
        this.HSVmin = HSVmin;
    }

    public void setColour(Scalar colour) {
        this.colour = colour;
    }
    public Color getRGBColor(){
        return new Color((int)colour.val[0],(int)colour.val[1],(int)colour.val[2]);
    }


    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Integer> getParametersList(){
        ArrayList<Integer> ret = new ArrayList<Integer>(6);
        ret.add(0, (Double.valueOf(HSVmin.val[0]).intValue()));
        ret.add(1, (Double.valueOf(HSVmax.val[0]).intValue()));

        ret.add(2, (Double.valueOf(HSVmin.val[1]).intValue()));
        ret.add(3, (Double.valueOf(HSVmax.val[1]).intValue()));

        ret.add(4, (Double.valueOf(HSVmin.val[2]).intValue()));
        ret.add(5, (Double.valueOf(HSVmax.val[2]).intValue()));

        return ret;
    }
}
