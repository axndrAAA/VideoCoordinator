package Coordinator;

import Bot.BotsManager;
import Form.CarDDAppForm;
import Form.Grid;
import com.sun.xml.internal.bind.v2.TODO;
import org.opencv.core.*;

import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

import org.opencv.videoio.Videoio;

import java.io.*;
import java.rmi.AccessException;
import java.util.ArrayList;

public class VideoCoordinator extends Thread {

    //initial min and max HSV filter values.
    //these will be changed using trackbars
    public CalibrationWindow calibrationWindow;
    private int H_MIN = 7;
    private int H_MAX = 124;
    private int S_MIN = 46;
    private int S_MAX = 170;
    private int V_MIN = 0;
    private int V_MAX = 251;

    //default capture width and height
    private int FRAME_WIDTH = 640;
    private int FRAME_HEIGHT = 480;

    //minimum and maximum object area
    private int MIN_OBJECT_AREA = 20*20;
    private int MAX_OBJECT_AREA = (int)(FRAME_HEIGHT*FRAME_WIDTH/1.5);
    private int MAX_NUM_OBJECTS = 4;

    private double cameraHeigh = 200;//cm
    private double cameraFocus = 0.03;//cm

    private String trackbarWindowName = "Trackbars";
    private String thresholdedWIndowName = "Thresholded Image";
    private String orgImWindowName = "Original Image";

    private boolean calibrationMode;
    private boolean isWritingCoordinatesToFile = true;
    private String coordinatesOut = "coordinates.txt";


    private VideoCapture capture;
    public Grid grid;

    private BotsManager botsManager;

    private Mat cameraFeed;
    private Imshow threshShow;

    private void writeCoordinatesToFile(){
           try {
            FileWriter writer = new FileWriter(coordinatesOut,false);
            String curStr = " ";
            for(int i = 0; i < botsManager.getBotsList().size();i++){
                curStr =Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getNumber()) + " " +
                        Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getRealCoordinates(cameraHeigh,cameraFocus,grid).x) + " " +
                        Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getRealCoordinates(cameraHeigh,cameraFocus,grid).y);
                writer.write(curStr + "\n");
            }
            writer.flush();
            writer.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void setCalibrationMode(boolean calibrationMode) {
        this.calibrationMode = calibrationMode;
    }

    public boolean isCalibrationMode() {
        return calibrationMode;
    }

    public synchronized Mat getCameraFeed() {
        return cameraFeed;
    }

    public VideoCoordinator(int cameraNum)throws AccessException{
        super();
        this.setName("VideoCooordinator");
        System.out.println(Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //this.objectsToTrack = new  ArrayList<BotOnImage>();
        this.capture = new VideoCapture(cameraNum);
        this.grid = CarDDAppForm.grid;
        if(!this.capture.isOpened()){
            throw new AccessException("Camera did not opened");
        }

        //set height and width of capture frame
        this.capture.set(Videoio.CAP_PROP_FRAME_WIDTH,FRAME_WIDTH);
        this.capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,FRAME_HEIGHT);

        cameraFeed = new Mat(FRAME_WIDTH,FRAME_HEIGHT,0);
        threshShow = new Imshow(thresholdedWIndowName);
    }

    public VideoCoordinator(int cameraNum, BotsManager botsManager)throws AccessException{
        this(cameraNum);
        this.botsManager = botsManager;
        //TODO необходим ввод параметров трекинга для уже имеющихся ботов, а также возможность перестройки...
        //проверка на предмет заданных параметров

    }

    public void OperateCalibrationWindow(){
        if(calibrationMode){
            if(calibrationWindow != null){
                return;
            }else{
                calibrationWindow = new CalibrationWindow(botsManager,H_MIN,H_MAX,S_MIN,S_MAX,V_MIN,V_MAX);
                calibrationWindow.setVisible(true);
            }
        }else {
            if(calibrationWindow != null){
                calibrationWindow.setVisible(false);
                calibrationWindow.dispose();
                calibrationWindow = null;
            }else{
                return;
            }
        }


    }

    private void saveSettings(){
        this.botsManager.saveTrackingSettingsToFile();
    }

    private void morphOps(Mat thresh){
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(8,8));

        Imgproc.erode(thresh,thresh,erodeElement);
        Imgproc.erode(thresh,thresh,erodeElement);

        Imgproc.dilate(thresh,thresh,dilateElement);
        Imgproc.dilate(thresh,thresh,dilateElement);

    }

    public void drawObjectOnScreen(BotOnImage objects, Mat frame){

            Imgproc.circle(frame,new Point(objects.getxPos(),objects.getyPos()),
                    10,new Scalar(0,0,255));
            Imgproc.putText(frame,objects.getName(),
                    new Point(objects.getxPos(),objects.getyPos()-30),
                    1,2,objects.getColour());

    }

    private void trackFilteredObject(Mat thresh,Mat HSV, Mat cameraFeed)throws NullPointerException{

        Mat temp = new Mat();;
        thresh.copyTo(temp);
        //these two vectors needed for output of findContours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarcy = new Mat();
        //find contours of filtered image using openCV findContours function
        Imgproc.findContours(temp,contours,hierarcy,Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);
        //use moments method to find our filtered object
        double refArea = 0;
        boolean objectFound = false;

        if(hierarcy.rows() > 0){
            int numObjects = hierarcy.rows();
            //if number of objects greater than MAX_NUM_OBJECTS we have a noisy filter
            if(numObjects<MAX_NUM_OBJECTS){
                BotOnImage bot = null;
                for (int index = 0; index >= 0; index = (int)hierarcy.get(index,0)[0]){
                    Moments moment = new Moments();
                    moment = Imgproc.moments(contours.get(index),false);
                    double area = moment.get_m00();
                    if(area>MIN_OBJECT_AREA){
                        bot = new BotOnImage();
                        bot.setxPos((int)(moment.m10/area));
                        bot.setyPos((int)(moment.m01/area));
                        bot.getRealCoordinates(cameraHeigh,cameraFocus,grid);
                        objectFound = true;
                    }else
                        objectFound = false;
                }
                if(objectFound)
                    drawObjectOnScreen(bot,cameraFeed);
            }else{
                System.out.println("Too much noize. Adjust filter");
                Imgproc.putText(cameraFeed,"Too much noize. Adjust filter",
                        new org.opencv.core.Point(0,50),1,2,
                        new Scalar(0,0,255),2);
            }
        }

    }

    private void trackFilteredObject(BotOnImage theBot, Mat thresh, Mat HSV, Mat cameraFeed){

        Mat temp = new Mat();;
        thresh.copyTo(temp);
        //these two vectors needed for output of findContours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarcy = new Mat();
        //find contours of filtered image using openCV findContours function
        Imgproc.findContours(temp,contours,hierarcy,Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);
        //use moments method to find our filtered object
        double refArea = 0;
        boolean objectFound = false;

        if(hierarcy.rows() > 0){
            int numObjects = hierarcy.rows();
            //if number of objects greater than MAX_NUM_OBJECTS we have a noisy filter
            if(numObjects<=MAX_NUM_OBJECTS){
                for (int index = 0; index >= 0; index = (int)hierarcy.get(index,0)[0]){
                    Moments moment = new Moments();
                    moment = Imgproc.moments(contours.get(index),false);
                    double area = moment.get_m00();
                    if(area>MIN_OBJECT_AREA){
                        theBot.setxPos((int)(moment.m10/area));
                        theBot.setyPos((int)(moment.m01/area));
                        theBot.getRealCoordinates(cameraHeigh,cameraFocus,grid);
                        objectFound = true;
                    }else
                        objectFound = false;
                }
                if(objectFound)
                    drawObjectOnScreen(theBot,cameraFeed);
            }else{
                System.out.println("Too much noize. Adjust filter");
                Imgproc.putText(cameraFeed,"Too much noize. Adjust filter",
                        new org.opencv.core.Point(0,50),1,2,
                        new Scalar(0,0,255),2);
            }
        }
    }

    void syncCalPframeters(){
        if(calibrationWindow != null){
            H_MIN = calibrationWindow.param.get(0);
            H_MAX = calibrationWindow.param.get(1);
            S_MIN = calibrationWindow.param.get(2);
            S_MAX = calibrationWindow.param.get(3);
            V_MIN = calibrationWindow.param.get(4);
            V_MAX = calibrationWindow.param.get(5);
        }else{
            return;
        }

        if(calibrationWindow.isSetupOkAndFinished()){
            calibrationWindow.dispose();
            calibrationWindow= null;
            saveSettings();
            calibrationMode = false;
        }
    }


    //tracking process
    @Override
    public void run(){
        //calibrationMode = false;
        if(!this.capture.isOpened()){
            System.out.println("Camera did not opened");
            return;
        }
        Mat threshold = new Mat();
        Mat HSV = new Mat();

        while (!this.isInterrupted()){

            this.capture.read(cameraFeed);
            Imgproc.cvtColor(cameraFeed,HSV,Imgproc.COLOR_BGR2HSV);

            try{
                OperateCalibrationWindow();

                if(calibrationMode){
                    syncCalPframeters();
                    Imgproc.cvtColor(cameraFeed,HSV,Imgproc.COLOR_BGR2HSV);
                    Core.inRange(HSV,new Scalar(H_MIN,S_MIN,V_MIN),new Scalar(H_MAX,S_MAX,V_MAX),threshold);
                    morphOps(threshold);

                    threshShow.showImage(threshold);
                    trackFilteredObject(threshold,HSV,cameraFeed);
                }else{
                    //если параметры трекинга для бота не были введены - пропуск итерации и ожидание пока будет вызван процесс калибровки
                    if(botsManager.getBot(0).getBotOnImage() == null)
                        continue;

                    for(int i = 0; i < botsManager.getBotsList().size();i++){
                        Imgproc.cvtColor(cameraFeed,HSV,Imgproc.COLOR_BGR2HSV);
                        Core.inRange(HSV,botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getHSVmin(),
                                         botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getHSVmax(),
                                         threshold);
                        morphOps(threshold);
                        //Debug
                       // threshShow.showImage(threshold);
                        trackFilteredObject(botsManager.getBotsList().get(i).getBotModel().getBotOnImage(),threshold,HSV,cameraFeed);
                    }
                    if(isWritingCoordinatesToFile)
                        writeCoordinatesToFile();
                }
            }catch (NullPointerException ex){
               // System.out.println(ex.getMessage());
            }

            try {
                Thread.currentThread().sleep(30);

            }catch (InterruptedException e){
                //System.out.println(e.getMessage());
            }
        }


    }
}


