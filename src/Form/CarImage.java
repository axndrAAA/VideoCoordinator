package Form;

import Car.Car;

import java.awt.*;

/**
 * Created by Александр on 22.04.2017.
 */
public class CarImage {
    int diam;
    int ort;
     int area_heigh;
     int area_with;


    private Car car;
    public CarImage(Car car,int h,int w)
    {
        super();
        this.area_heigh = h;
        this.area_with = w;
        this.car = car;
        this.diam = 12;
        this.ort = 10;
    }
    public void setMapSize(int w,int h){
        this.area_with = w;
        this.area_heigh = h;
    }
    public int map(int value, int fromLow,int fromHigh,int toLow,int  toHigh){
        int ret = value;
        ret = (value - fromLow)*(toHigh - toLow) / (fromHigh - fromLow) + toLow;
        return ret;
    }
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        //круг
        g2d.setStroke(new BasicStroke(2.0f));

        int x2L = 0;
        int y2L = 0;
        int xCoor = 0;
        int yCoor = 0;

        synchronized (car){
            if (car != null){
                xCoor = map(car.getX(),0,150,0,area_with);
                yCoor = map(car.getY(),0,200,0,area_heigh);
                x2L = (int)(ort*Math.sin(car.getAzimut()*Math.PI/180.0)) + xCoor;
                y2L = -(int)(ort*Math.cos(car.getAzimut()*Math.PI/180.0)) + yCoor;


            }else {
                return;
            }
            g2d.drawOval(xCoor - diam /2, yCoor - diam /2, diam, diam);
            g2d.drawLine(xCoor,yCoor,x2L,y2L);
        }

    }
}
