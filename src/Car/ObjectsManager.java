package Car;

import java.io.IOException;
import java.util.ArrayList;

public class ObjectsManager {
    //класс содержит список платформ,и способен осуществлять управление каждой из них

    private ArrayList<BotTransmitter> bots;

    private byte globalSpeedLimit;


    public ObjectsManager(int numberOfObj){
        bots = new ArrayList<BotTransmitter>(numberOfObj);
        int ref_port = 778;
        int tryCount = 0;
        for(int i = 0; i < numberOfObj; i++){
            BotModel model;
            BotTransmitter transmitter;
            tryCount = 0;
            try {
                model = new BotModel(ref_port);
                transmitter = new BotTransmitter(model);
                transmitter.start();
                bots.add(transmitter);
                System.out.println("синхронизация через IP " + model.getIP() + ":" + model.getPort() + " запущена.");
                ref_port++;
            }catch (IOException ex){
                System.out.println(ex.getMessage());
                System.out.println("Проблеммы с получением доступа к порту " + ref_port + " пробуем еще раз");
                if(tryCount > 10){
                    System.out.println("Порт " + ref_port + " не доступен.");
                    i+=2;
                }
                try {
                    Thread.currentThread().sleep(200);
                    tryCount++;
                }catch (InterruptedException e){}
                model = null;
                transmitter = null;
                i--;
            }
        }

    }
    public ArrayList<BotTransmitter> getBotsList() {
        return bots;
    }

    public byte getGlobalSpeedLimit() {return globalSpeedLimit;}

    public void setGlobalSpeedLimit(byte globalSpeedLimit) {
        this.globalSpeedLimit = globalSpeedLimit;
        for (BotTransmitter botTransmitter: bots) {
            botTransmitter.getBotModel().setSpeedLimit(globalSpeedLimit);
        }
    }
    public void closeConnection(int conNumber){
        if(conNumber < 0){
            for (BotTransmitter bt:bots) {
                bt.destruct();
                bt.interrupt();
            }
            return;
        }
        try {
            bots.get(conNumber).restore();
        }catch (IndexOutOfBoundsException ex){
            System.out.println(ex.getMessage());
        }
    }
}
