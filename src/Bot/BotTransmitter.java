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
    public int requestFrequency;//задржка необходимая для принятия всей строки с платформы

    private ServerSocket serverSocket;
    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;


    public boolean isConnectionOk;

    public BotTransmitter(BotModel botModel)throws IOException{//конструктор
        this.botModel = botModel;
        this.requestFrequency = 10;

        this.isConnectionOk = false;
        serverSocket = new ServerSocket(botModel.getPort());
        botModel.setIP(serverSocket.getInetAddress().getHostName().toString());
        this.setName("BotTransmitter" + botModel.getPort());
    }



    public void restore(){
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
            if (!isConnectionOk){
                try {
                    System.out.println("Соединеие перезапущено " + getBotModel().getIP() + ":" + getBotModel().getPort());
                    socket = serverSocket.accept();
                    botModel.setIP(socket.getInetAddress().getHostName().toString());
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    isConnectionOk = true;

                }catch (IOException ex){
                    try {
                        Thread.sleep(3000);
                    }catch (InterruptedException inter_ex){}
                }catch (NullPointerException ex){
                    return;
                }
            }else {
                try {

                    //System.out.println(botModel.getMessage());
                    outputStream.write(botModel.getMessage().getBytes());
                    outputStream.flush();

                    byte[] line = new byte[10];
                    inputStream.read(line);
                    String s = new String(line);

                    String[] sarr = s.split("/");
                    if(sarr.length < 3){
                        restore();
                        continue;
                    }
                    botModel.parseStatus(sarr);
                    //System.out.println(s);

                    Thread.currentThread().sleep(requestFrequency);
                }catch (IOException ex){
                    restore();
                }catch (NullPointerException e){
                    continue;
                }catch (InterruptedException e){
                    restore();
                }
            }
        }
        destruct();

    }

    public BotModel getBotModel() {
        return botModel;
    }
}
