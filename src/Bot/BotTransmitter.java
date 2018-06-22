package Bot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
;

/**
 * Created by Александр on 26.10.2016.
 */
public class BotTransmitter extends Thread{//класс - передатчик. Принимает и передает сообщения с платформы и на неё
    private BotModel botModel;//модель платформы
    public int requestFrequency = 5;;//задржка необходимая корректной работы всех нитей [мсек]

    private ServerSocket serverSocket;
    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;


    public boolean isConnectionOk;

    public BotTransmitter(BotModel botModel)throws IOException{//конструктор
        this.botModel = botModel;
        this.isConnectionOk = false;
        serverSocket = new ServerSocket(botModel.getPort());
        botModel.setIP(serverSocket.getInetAddress().getHostName().toString());
        this.setName("BotTransmitter" + botModel.getPort());
    }



    public synchronized void restore(){
       try {
           isConnectionOk = false;
           System.out.println("Соединение сброшено.");
           if(inputStream != null){
               inputStream.close(); inputStream = null;
           }
           if(outputStream != null){
               outputStream.close(); outputStream = null;
           }
           if(socket != null){
               socket.close(); socket = null;
           }

           botModel.setIP("");
       }catch (IOException e){
           isConnectionOk = false;
       }catch (NullPointerException ex){
           System.out.println(ex.getMessage());
       }

    }
    public synchronized void destruct(){
        try {
            isConnectionOk = false;
            System.out.println("Соединение сброшено.");
            if(inputStream != null){
                inputStream.close(); inputStream = null;
            }
            if(outputStream != null){
                outputStream.close(); outputStream = null;
            }
            if(socket != null){
                socket.close(); socket = null;
            }
            if(serverSocket != null){
                serverSocket.close(); serverSocket = null;
            }
            botModel.setIP("");
            this.interrupt();
        }catch (IOException e){
            isConnectionOk = false;
        }
    }

    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try {
                if (isConnectionOk){
                    if(inputStream.available() >= 10){
                        //читаем запрос
                        byte[] line = new byte[10];
                        inputStream.read(line);

                        //пробуем парсить
                        String s = new String(line);
                        String[] sarr = s.split("/");
                        if(sarr.length < 3){
                            //restore();
                            continue;
                        }
                        botModel.parseStatus(sarr);

                        //овечаем
                        outputStream.write(botModel.getMessage().getBytes());
                        outputStream.flush();
                    }
                    //вычищаем буфер
                    clear(inputStream);
                    Thread.currentThread().sleep(requestFrequency);
                }else {
                    try {
                        System.out.println("Соединеие перезапущено " + getBotModel().getIP() + ":" + getBotModel().getPort());
                        socket = serverSocket.accept();
                        botModel.setIP(socket.getInetAddress().getHostName().toString());
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        isConnectionOk = true;
                    }catch (IOException ex){
                        Thread.sleep(3000);
                    }
                }
            }catch (NullPointerException ex){
                System.out.println(ex.getMessage());
                continue;
            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }catch (InterruptedException ex){
                System.out.println(ex.getMessage() + "stopping thread");
                break;
            }
        }
        destruct();
    }

    private void clear(InputStream stream)throws IOException{
        while (stream.available() > 0){
            stream.read();
        }
    }
    public BotModel getBotModel() {
        return botModel;
    }
    public synchronized boolean isConnectionOk(){return isConnectionOk;}
}
