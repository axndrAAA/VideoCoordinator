package Bot;

import Coordinator.BotOnImage;

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

    private int xdest;
    private int ydest;

    private String IP;
    private  int port;
    private String name = "bot";
    private BotOnImage botOnImage;

    private byte mode;//0 - просто управление, 1 - следование в точку

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
    public void setMode(byte mode) {this.mode = mode;}

    public int getXdest() {  return xdest; }
    public int getYdest() {  return ydest;  }

    public void setXdest(int xdest) {this.xdest = xdest;}
    public void setYdest(int ydest) {this.ydest = ydest;}

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

    public synchronized void setX(int x) {this.x = x;}
    public synchronized void setY(int y) {this.y = y;}

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
            message = "b2/" + String.valueOf(x) + "/" + String.valueOf(y) + "/" + String.valueOf(xdest) + "/" + String.valueOf(ydest) + "/e";
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


}
