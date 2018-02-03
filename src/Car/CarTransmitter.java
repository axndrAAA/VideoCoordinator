package Car;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
;

/**
 * Created by Александр on 26.10.2016.
 */
public class CarTransmitter extends Thread{//класс - передатчик. Принимает и передает сообщения с платформы и на неё
    public Car car;//модель платформы
    public int requestFrequency;//задржка необходимая для принятия всей строки с платформы

    private ServerSocket serverSocket;
    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;


    public boolean isOk;

    public CarTransmitter(Car car)throws IOException{//конструктор
        this.car = car;
        this.requestFrequency = 10;


        this.isOk = true;
        serverSocket = new ServerSocket(car.getPort());
        socket = serverSocket.accept();

        car.setIP(socket.getInetAddress().getHostName().toString());
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        //this.start();
    }



    public void destruct(){
       try {
           isOk = false;

           inputStream.close();
           outputStream.close();
           car.setIP("");
           socket.close();
           serverSocket.close();

       }catch (IOException e){
           isOk = false;
       }catch (NullPointerException ex){}

    }

    @Override
    public void run(){
        while(Thread.currentThread().isAlive()){


           try {


               System.out.println(car.getMessage());
               outputStream.write(car.getMessage().getBytes());
               outputStream.flush();

               byte[] line = new byte[10];
               inputStream.read(line);
               String s = new String(line);

               String[] sarr = s.split("/");
               if(sarr.length == 1){
                   interrupt();
                   destruct();
                   continue;
               }
               car.parseStatus(sarr);
               System.out.println(s);
                if (car.isNavigate()){

                    //car.calcCoordinates(d1,d2);
                }
               //System.out.println();


                Thread.currentThread().sleep(requestFrequency);
           }catch (IOException ex){
               continue;
           }catch (NullPointerException e){
               continue;
           }catch (InterruptedException e){
               continue;
           }
        }
    }

}
