package Car;

/**
 * Created by Александр on 26.10.2016.
 */
public class Car {//класс-модель платформы
    static {
        tr_alt = 20;
    };

    private byte fsb;//1 - back; 2 - stop; 3 - forward
    private byte lfr;//1 - left; 2 - forward; 3 - right
    private byte speed;//0 - 9 - скорость вращения
    private boolean isArrived;//признак прибытия на точку

    private byte speedLimit;
    private  int x;//координаты
    private int y;
    private int azimut;//угол между осью 0у и осью платформы

    private int xdest;
    private int ydest;

    private String IP;
    private int port;

    public static int tr_alt;

    private int numberInList = 0;

    private byte mode;//0 - просто управление, 1 - следование в точку
    private boolean isNavigate; // работает ли навигация

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
    public void setMode(byte mode) {this.mode = mode;}

    public int getXdest() {  return xdest; }
    public int getYdest() {  return ydest;  }

    public void setXdest(int xdest) {this.xdest = xdest;}
    public void setYdest(int ydest) {this.ydest = ydest;}


    public int getAzimut() { return azimut;}
    public void setAzimut(int az) {this.azimut = az;}

    public int getX() { return x;}
    public int getY() { return y;}

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    public boolean isArrived() {return isArrived;}
    public void setArrived(boolean arrived) {isArrived = arrived;}

    public synchronized boolean isNavigate() {return isNavigate;}
    public void setNavigate(boolean navigate) {isNavigate = navigate;}

    public Car(){
        this.fsb = 2;
        this.lfr = 2;
        this.speed = 0;
        this.isArrived = false;
        this.x = 6;
        this.y = 1;
        this.azimut = 0;
        this.IP = "";
        this.port = 777;
        this.mode = 0;
        this.speedLimit = 9;
    }
    public synchronized byte goForward(){    fsb = 3; return fsb;    }
    public synchronized byte goBack(){   fsb = 1; return fsb;    }
    public synchronized byte stop(){ fsb = 2; return fsb;    }
    public synchronized byte turnLeft(){ lfr = 1; return lfr;    }
    public synchronized byte turnRight(){    lfr = 3;return lfr;}
    public synchronized byte goStraight(){   lfr = 2; return lfr; }
    public synchronized int getNumberInList() {
        return numberInList;
    }

    public String getMessage(){
        String message = "";
        if(mode == 0){
            message = "b1/" + String.valueOf(fsb) + String.valueOf(lfr) + String.valueOf(speed)+ "/e";
        }
        if(mode == 1){
            message = "b2/" + String.valueOf(x) + "/" + String.valueOf(y) + "/" + String.valueOf(xdest) + "/" + String.valueOf(ydest) + "/e";
        }
        return message;
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

    public void parseStatus(String[] sArr){

        synchronized (this){
            if(sArr[1].equals("0")){
                this.setArrived(false);
            }
            if (sArr[1].equals("1")){
                this.setArrived(true);
            }

            int azimut = new Integer(sArr[2]);
            this.setAzimut(azimut);
        }

    }


}
