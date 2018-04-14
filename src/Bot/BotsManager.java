package Bot;

import Coordinator.BotOnImage;
import org.opencv.core.Point;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class BotsManager {
    //класс содержит список платформ,и способен осуществлять управление каждой из них

    private ArrayList<BotTransmitter> bots;
    private ArrayList<BotDriver> botDrivers;

    private byte globalSpeedLimit;


    public BotsManager(int numberOfBots){

        bots = new ArrayList<BotTransmitter>(numberOfBots);
        botDrivers = new ArrayList<>(numberOfBots);
        int ref_port = 779;
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
        globalSpeedLimit = getBot(0).getspeedLimit();


    }

    public BotsManager(int numberOfBots,String settingsFileParth){
        this(numberOfBots);
        //settingsFileParth - файл с настройками параметров для трекинга объектов.\
        BufferedReader reader = null;
        try{
             reader = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFileParth)));
            String str;
            reader.readLine();//вычитывание заголовочной строки

            for (int i = 0; i < bots.size();i++){
                str = reader.readLine();
                BotOnImage boi =  new BotOnImage(str);
                bots.get(i).getBotModel().setBotOnImage(boi);
            }
        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
        }catch (NumberFormatException ex){
            System.out.println("Файл настроек трекинга поврежден, или не заполнен");
            System.out.println(ex.getMessage());
            try {
                reader.close();
            }catch (IOException exep){}
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }catch (NullPointerException ex){}
    }

    public BotsManager(String settingsFileParth){
        this(countBotsInFile(settingsFileParth),settingsFileParth);
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

    public void saveTrackingSettingsToFile(){
        String fileName = "settings.txt";
        try {
            FileWriter writer = new FileWriter(fileName,false);
            writer.write("//Последние параметры трекига(num name Hmin Smin Vmin Hmax Smax Vmax colorR colorG colorB)\n");
            String curStr = " ";
            for(int i = 0; i < bots.size();i++){
                curStr = bots.get(i).getBotModel().getBotOnImage().toString();
                writer.write(curStr + "\n");
            }
            writer.flush();
            writer.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static int countBotsInFile(String settingsFileParth){
        int count = 0;
        BufferedReader reader;
        String str = "";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFileParth)));
            while (reader.readLine() != null){
                count++;
            }
        }catch (FileNotFoundException ex){System.out.println(ex.getMessage());}
        catch (IOException ex){System.out.println(ex.getMessage());}

        return count - 1;
    }

    public void runPanzerCamfWagen(int panzerCamfWagenNumer, ArrayList<Point> destList){
        botDrivers.set(panzerCamfWagenNumer,new BotDriver(getBot(panzerCamfWagenNumer),destList));
        botDrivers.get(panzerCamfWagenNumer).run();
    }

    public void runPanzerCamfWagen(int panzerCamfWagenNumer, Point dest){
        botDrivers.add(panzerCamfWagenNumer,new BotDriver(getBot(panzerCamfWagenNumer),dest));
        botDrivers.get(panzerCamfWagenNumer).run();
    }
}
