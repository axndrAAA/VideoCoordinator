package Bot;

import Coordinator.BotOnImage;

import java.io.*;
import java.util.ArrayList;

public class BotsManager {
    //класс содержит список платформ,и способен осуществлять управление каждой из них

    private ArrayList<BotTransmitter> bots;

    private byte globalSpeedLimit;


    public BotsManager(int numberOfBots){

        bots = new ArrayList<BotTransmitter>(numberOfBots);
        int ref_port = 778;
        int tryCount = 0;
        for(int i = 0; i < numberOfBots; i++){
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

    public BotsManager(int numberOfBots,String settingsFileParth){
        this(numberOfBots);

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFileParth)));
            String str;
            reader.readLine();//вычитывание заголовочной строки

            for (int i = 0; i < bots.size();i++){
                str = reader.readLine();
                BotOnImage boi =  new BotOnImage(str);
                bots.get(i).getBotModel().setBotOnImage(boi);
            }
        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
        }catch (IOException ex){
            System.out.println(ex.getMessage());
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

    public BotModel getBot(int i)throws IndexOutOfBoundsException{
        return bots.get(i).getBotModel();
    }
}
