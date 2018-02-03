package Form;

import Car.Car;

import java.awt.*;

/**
 * Created by Александр on 22.04.2017.
 */
public class GridImage {

    private int area_heigh;
    private int area_with;
    private int row_count;
    private int col_count;

    private JMapArea jma;

    public GridImage(int rows, int cols,int h,int w,JMapArea _jma)
    {
        super();
//        this.area_heigh = h;
//        this.area_with = w;
        this.jma = _jma;
        this.area_heigh = jma.getHeight();
        this.area_with = jma.getWidth();
        this.row_count = rows;
        this.col_count = cols;
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

    public void setArea_heigh(int area_heigh) {
        this.area_heigh = area_heigh;
    }

    public void setArea_with(int area_with) {
        this.area_with = area_with;
    }

    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2.0f));
        this.area_with = jma.getHeight();
        this.area_heigh = jma.getWidth();
        //horizontal lines
        for(int i = 0; i < row_count + 1; i++){
            int x = (area_heigh/row_count)*i;
            g2d.drawLine(x,0,x,area_with);
        }
        //vertical lines
        for(int j = 0; j < col_count + 1; j++){
            int y = (area_with / col_count)*j;
            g2d.drawLine(0,y,area_heigh,y);
        }

    }
}
