package Form;

import Bot.BotModel;

import java.awt.*;

/**
 * Created by Александр on 22.04.2017.
 */
public class CarImage {
    int diam;
    int ort;
     int area_heigh;
     int area_with;


    private BotModel botModel;
    public CarImage(BotModel botModel, int h, int w)
    {
        super();
        this.area_heigh = h;
        this.area_with = w;
        this.botModel = botModel;
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

        synchronized (botModel){
            if (botModel != null){
                xCoor = map(botModel.getX(),0,150,0,area_with);
                yCoor = map(botModel.getY(),0,200,0,area_heigh);
                x2L = (int)(ort*Math.sin(botModel.getAzimut()*Math.PI/180.0)) + xCoor;
                y2L = -(int)(ort*Math.cos(botModel.getAzimut()*Math.PI/180.0)) + yCoor;


            }else {
                return;
            }
            g2d.drawOval(xCoor - diam /2, yCoor - diam /2, diam, diam);
            g2d.drawLine(xCoor,yCoor,x2L,y2L);
        }

    }
}
