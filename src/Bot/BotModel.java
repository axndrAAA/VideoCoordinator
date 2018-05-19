package Bot;

import Coordinator.BotOnImage;
import Coordinator.MedianFilter;
import Form.Grid;
import org.opencv.core.Point;


/**
 * Created by Александр on 26.10.2016.
 */
public class BotModel {//класс-модель платформы

    private byte fsb;//1 - back; 2 - stop; 3 - forward
    private byte lfr;//1 - left; 2 - forward; 3 - right
    private byte speed;//0 - 9 - скорость вращения
    private boolean isArrived;//признак прибытия на точку

    private byte speedLimit;
    private  int x;//координаты
    private int y;
    private int azimut;//угол между осью 0у и осью платформы

    private Point destPoint;

    private String IP;
    private  int port;
    private String name = "bot";
    private BotOnImage botOnImage;

    private byte mode;//0 - просто управление, 1 - следование в точку

    private MedianFilter xFilter;
    private MedianFilter yFilter;

    public BotModel(){
        this.fsb = 2;
        this.lfr = 2;
        this.speed = 0;
        this.isArrived = false;
        this.x = 0;
        this.y = 0;
        this.azimut = 0;
        this.IP = "";
        this.port = 777;
        this.mode = 0;
        this.speedLimit = 9;
        this.xFilter = new MedianFilter(x);
        this.yFilter = new MedianFilter(y);
    }
    public BotModel(int port){
        this();
        this.port = port;
    }
    public BotModel(int port, BotOnImage boi){
        this(port);
        botOnImage = boi;
    }

    public String getName() {return name + port;}
    public void setName(String name) {this.name = name;}

    public String getIP() {
        return IP;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {    return port;  }
    public void setPort(int port) {  this.port = port;  }

    public void setSpeed(byte spd) {
        if(spd >speedLimit ){
            this.speed = speedLimit;
            return;
        }
        if(spd <=0 ){
            this.speed = 0;
            return;
        }
        this.speed = spd;
    }
    public byte getSpeed() {
        return speed;
    }

    public byte getMode() {   return mode; }
    private void setMode(byte mode) {
        if(destPoint != null)
            this.mode = mode;
    }

    public synchronized  byte getspeedLimit(){
        return speedLimit;
    }
    public synchronized void setSpeedLimit(byte lim ){
        if(lim > 9)
            lim = 9;
        if(lim < 0)
            lim =0;
        speedLimit = lim;
    }

    public synchronized int getAzimut() { return azimut;}
    public synchronized void setAzimut(int az) {this.azimut = az;}

    public synchronized int getX() { return x;}
    public synchronized int getY() { return y;}

    public synchronized void setX(int x) {
        //this.x = xFilter.getFilteredValue(x);
        this.x = x;
        botOnImage.setxPos(this.x);
    }
    public synchronized void setY(int y) {
        //this.y = yFilter.getFilteredValue(y);
        this.y = y;
        botOnImage.setyPos(this.y);
    }

    public Point getMetricCoordinates(double cameraHeigh, double cameraFocus, Grid grid) {
        //метод должен возвращать координаты метки в сантимтрах
//        this.xRealPos = (int)((cameraHeigh*(xPos - grid.getUpLeftCorner().x)*0.02645833333333)/cameraFocus);
//        this.yRealPos = (int)((cameraHeigh*(yPos - grid.getUpLeftCorner().y)*0.02645833333333) / cameraFocus);
        //Debug
        //System.out.println(this.number + "   " + this.xRealPos + "   " + this.yRealPos);

        return new Point(this.x,this.y);
    }

    public boolean isArrived() {return isArrived;}
    public void setArrived(boolean arrived) {isArrived = arrived;}

    public BotOnImage getBotOnImage() {return botOnImage;}
    public void setBotOnImage(BotOnImage botOnImage) {this.botOnImage = botOnImage;}


    public synchronized byte goForward(){    fsb = 3; return fsb;    }
    public synchronized byte goBack(){   fsb = 1; return fsb;    }
    public synchronized byte stop(){ fsb = 2; return fsb;    }
    public synchronized byte turnLeft(){ lfr = 1; return lfr;    }
    public synchronized byte turnRight(){    lfr = 3;return lfr;}
    public synchronized byte goStraight(){   lfr = 2; return lfr; }

    public synchronized String getMessage(){
        String message = "";
        if(mode == 0){
            message = "b1/" + String.valueOf(fsb) + String.valueOf(lfr) + String.valueOf(speed)+ "/e";
        }
        if(mode == 1){
            message = "b2/" + String.valueOf(x) + "/" + String.valueOf(y) + "/"
                    + String.valueOf(destPoint.x) + "/" + String.valueOf(destPoint.y) + "/e";
        }
        return message;
    }

    public synchronized  void parseStatus(String[] sArr){

        synchronized (this){
            try {
                if(sArr[1].equals("0")){
                    this.setArrived(false);
                }
                if (sArr[1].equals("1")){
                    this.setArrived(true);
                }

                int azimut = new Integer(sArr[2]);
                this.setAzimut(azimut);
            }catch (ArrayIndexOutOfBoundsException ex){
                System.out.println("Ошибка парсинга статуса sArr.size= " + sArr.length);
            }
        }

    }

    public double goToPoint(Point destPoint, double eps){
        //алгоритм контроля прихода в точку

        this.destPoint = destPoint;
        double distance = Math.sqrt((this.getX()-destPoint.x)*(this.getY()-destPoint.y));
        synchronized (this){
            //переключение в режим следования в точку
            setMode((byte) 1);

            while (distance > eps){
                distance = Math.sqrt((this.getX()-destPoint.x)*(this.getY()-destPoint.y));
                //ожидаем пока доедет.

                //TODO
                //debug
                break;

            }
            setMode((byte) 0);
            stop();
        }

        //вернем фактический промах
        return distance;
    }


}
