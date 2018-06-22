package Bot;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by Александр on 27.10.2016.
 */
public class TestBotClient implements Runnable{

    public boolean flag;
    public int carPort;
    public  int s1Port;
    public  int s2Port;

    public String IP;
    public byte mode;


    public String getMes(){
        String ret = "b1/";
        if(flag){
            flag = false;
            ret += "1";
        }else {
            flag = true;
            ret += "0";
        }
        ret += "/150/e";
        return ret;
    }

    public TestBotClient(){
        flag = true;
        mode = 0;
        carPort = 777;
        s1Port = 778;
        s2Port = 779;

        IP = "localhost";
    }

    public void run(){

    }

    public void refresh(){
        try{
            InetAddress ipAddres = InetAddress.getByName(IP);

            Socket carSocket = null;
            Socket s1Socket = null;
            Socket s2Socket = null;

            while (carSocket == null){
                try{
                    carSocket = new Socket(ipAddres,carPort);
                    s1Socket = new Socket(ipAddres,s1Port);
                    s2Socket = new Socket(ipAddres,s2Port);
                    System.out.println("connected to: "+carSocket.getInetAddress().getHostName());
                }catch (IOException e) {
                 try {
                    Thread.sleep(2000);
                }catch (InterruptedException ex){}
                }
            }
            InputStream instream = carSocket.getInputStream();
            OutputStream outstream = carSocket.getOutputStream();

            InputStream s1InStream = null;
            OutputStream s1OutStream = null;
            InputStream s2InStream = null;
            OutputStream s2OutStream = null;
            if (mode == 1){
                s1InStream = s1Socket.getInputStream();
                 s1OutStream = s1Socket.getOutputStream();

                 s2InStream = s1Socket.getInputStream();
                 s2OutStream = s1Socket.getOutputStream();
            }

            while (true){

                byte[] line = new byte[15];
                instream.read(line);

                String mes = getMes();
                System.out.println( "Got--->" + new String(line));

                if(mode == 1){
                    int gotSymb = s1InStream.read(line);
                    s1OutStream.write("10".getBytes());
                    System.out.println("S1 got--->" + new String(line).substring(0,gotSymb) + "   sent-->10");
                    gotSymb = s2InStream.read(line);
                    s2OutStream.write("20".getBytes());
                    System.out.println("S2 got--->" + new String(line).substring(0,gotSymb) + "   sent-->20");
                }


                byte[] bytes = mes.getBytes();
                System.out.println("     Send--->" + mes );
                outstream.write(bytes);
                outstream.flush();

            }

        }catch (UnknownHostException e){

            return;
        }catch (IOException e){
            System.out.println("disconected.");
            return;
        }
    }

    public static void main(String[] args){
        try {

            Properties prop = System.getProperties();
            ProcessBuilder pb = null;
            if ("Linux".equals(prop.getProperty("os.name"))) {
                pb = new ProcessBuilder("xterm");
            } else {
                pb = new ProcessBuilder("cmd");
            }

            pb.start();
        }catch (IOException e){System.exit(0);}


        TestBotClient tcc = new TestBotClient();
        System.out.println("TestBotClient started.");

        while (true){
            tcc.refresh();
        }
    }
}
