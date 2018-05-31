package Coordinator;

import Bot.BotModel;
import Bot.BotsManager;
import Form.CarDDAppForm;
import Form.Grid;
import org.opencv.core.*;

import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

import org.opencv.videoio.Videoio;

import javax.swing.plaf.synth.Region;
import java.io.*;
import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Collections;

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
    private int MIN_OBJECT_AREA = 15*15;
    private int MAX_OBJECT_AREA = 40*40;//(int)(FRAME_HEIGHT*FRAME_WIDTH/1.5);
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
    private Mat originalCameraFeed;
    private Imshow threshShow;

    private void writeCoordinatesToFile(){
           try {
            FileWriter writer = new FileWriter(coordinatesOut,false);
            String curStr = " ";
            for(int i = 0; i < botsManager.getBotsList().size();i++){
                curStr =Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getNumber()) + " " +
                        Double.toString(botsManager.getBotsList().get(i).getBotModel().getMetricCoordinates(cameraHeigh,cameraFocus,grid).x) + " " +
                        Double.toString(botsManager.getBotsList().get(i).getBotModel().getMetricCoordinates(cameraHeigh,cameraFocus,grid).y);
                writer.write(curStr + "\n");
            }
            writer.flush();
            writer.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public int getFRAME_WIDTH() {return FRAME_WIDTH;}
    public int getFRAME_HEIGHT() {return FRAME_HEIGHT;}

    public void setCalibrationMode(boolean calibrationMode) {
        this.calibrationMode = calibrationMode;
    }

    public boolean isCalibrationMode() {
        return calibrationMode;
    }

    public synchronized Mat getCameraFeed() {
        return originalCameraFeed;
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
//        this.capture.set(Videoio.CAP_PROP_FRAME_WIDTH,grid.getDownRightCorner().x - grid.getUpLeftCorner().x );
//        this.capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,grid.getDownRightCorner().y - grid.getUpLeftCorner().y);

        cameraFeed = new Mat(FRAME_WIDTH,FRAME_HEIGHT,0);
        originalCameraFeed = new Mat(FRAME_WIDTH,FRAME_HEIGHT,0);

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
                calibrationWindow = new CalibrationWindow(this,botsManager,H_MIN,H_MAX,S_MIN,S_MAX,V_MIN,V_MAX);
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

    public ArrayList<Integer> getAutoParameters(int delta){

        Rect ROI = new Rect((int)grid.getUpLeftCorner().x + 1,(int)grid.getUpLeftCorner().y + 1,
                grid.getXsquareSize(),grid.getYsquareSize());
        Mat calibrationField = cameraFeed.submat(ROI);

        Scalar hsvMin = new Scalar(0,0,0);
        Scalar hsvMax = new Scalar(0,0,0);
        ArrayList<Integer> ret = new ArrayList<Integer>(6);
        autoTune(calibrationField,delta,hsvMin,hsvMax);

        ret.add((int)hsvMin.val[0]);
        ret.add((int)hsvMax.val[0]);

        ret.add((int)hsvMin.val[1]);
        ret.add((int)hsvMax.val[1]);

        ret.add((int)hsvMin.val[2]);
        ret.add((int)hsvMax.val[2]);

        return  ret;

    }

    private static Scalar getCheckedRange(int val,int delta,int min, int max){
        Scalar ret = new Scalar(val - delta,val + delta,0);//min , max , notUsed
        if(ret.val[0] < min ){
            ret.val[0] = min;
        }
        if(ret.val[1] > max){
            ret.val[1] = max;
        }
        return ret;
    }

    private void autoTune(Mat img,int delta,Scalar outHSVmin,Scalar outHSVmax){

        Mat hsvImage = new Mat();
        Imgproc.cvtColor(cameraFeed,hsvImage,Imgproc.COLOR_BGR2HSV);

        ArrayList<Integer> Hhist = new ArrayList<>(Collections.nCopies(180,0));
        ArrayList<Integer> Shist = new ArrayList<>(Collections.nCopies(256,0));
        ArrayList<Integer> Vhist = new ArrayList<>(Collections.nCopies(256,0));

        int val = 0;
        //значения гистограмы
        for (int i = 0; i < img.rows();i++)
            for(int j = 0;j<img.cols();j++){
            val = (int)hsvImage.get(i,j)[0];
            Hhist.set(val,Hhist.get(val) + 1);

            val = (int)hsvImage.get(i,j)[1];
            Shist.set(val,Shist.get(val) + 1);

            val = (int)hsvImage.get(i,j)[2];
            Vhist.set(val,Vhist.get(val) + 1);
            }

        //outHSVmin = new Scalar(0,0,0);
        //outHSVmax = new Scalar(179,256,256);

        val = Collections.max(Hhist);//Hue
        int index = Hhist.indexOf(val);//Hue value index, that appears most often
        Scalar rng = getCheckedRange(index,delta,0,179);// H range
        outHSVmin.val[0] = rng.val[0];
        outHSVmax.val[0] = rng.val[1];

        val = Collections.max(Shist);//Saturation
        index = Shist.indexOf(val);//Saturation value index, that appears most often
        rng = getCheckedRange(index,delta,0,255);// S range
        outHSVmin.val[1] = rng.val[0];
        outHSVmax.val[1] = rng.val[1];

        val = Collections.max(Vhist);//Value
        index = Vhist.indexOf(val);//Saturation value index, that appears most often
        rng = getCheckedRange(index,delta,0,255);// V range
        outHSVmin.val[2] = rng.val[0];
        outHSVmax.val[2] = rng.val[1];

        hsvImage = null;

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
                        //bot.getMetricCoordinates(cameraHeigh,cameraFocus,grid);
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

    private void trackFilteredObject(BotModel theBot, Mat thresh, Mat HSV, Mat cameraFeed){

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
                        //
                        //здесь должна проводиться фильтрация
                        theBot.setX((int)(moment.m10/area));
                        theBot.setY((int)(moment.m01/area));
                        objectFound = true;
                    }else
                        objectFound = false;
                }
                if(objectFound)
                    drawObjectOnScreen(theBot.getBotOnImage(),cameraFeed);
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

    void cameraRead(VideoCapture capture){
        capture.read(originalCameraFeed);
        Rect roi = new Rect((int)grid.getUpLeftCorner().x,(int)grid.getUpLeftCorner().y,
                (int)(grid.getDownRightCorner().x - grid.getUpLeftCorner().x),(int)(grid.getDownRightCorner().y - grid.getUpLeftCorner().y));
        cameraFeed = originalCameraFeed.submat(roi);

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
        Imshow debugImshow = new Imshow("debug");


        while (!this.isInterrupted()){

            //this.capture.read(cameraFeed);
            cameraRead(this.capture);
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
                        debugImshow.showImage(threshold);
                        trackFilteredObject(botsManager.getBotsList().get(i).getBotModel(),threshold,HSV,cameraFeed);
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

    public Mat getWalsImage(BotOnImage wals){
        if(wals == null)
            return null;






        if(!this.capture.isOpened()){
            System.out.println("Camera did not opened");
            return null;
        }
        Mat threshold = new Mat();//бинаризованная матрица
        Mat HSV = new Mat();//матрица для HSV представления
        Mat statisticImg = cameraFeed.clone();//матрица для набора статистики

        //TODO: debug
        Imshow debugImshow = new Imshow("debug1");

        //наберем статистику из кадров
        for (int i = 0; i < 50; i++){
            cameraRead(this.capture);
            //сложение матриц(КОООКК!!!)
            for (int i1 = 0; i1 < cameraFeed.rows();i1++){
                for (int j1 = 0; j1 < cameraFeed.cols();j1++){
                    statisticImg.put(i1,j1,(statisticImg.get(i1,j1)[0]+cameraFeed.get(i1,j1)[0])/50);
                }
            }
        }

        //бинаризуем по заданным параметрам
        try{

                    Imgproc.cvtColor(cameraFeed,HSV,Imgproc.COLOR_BGR2HSV);
                    Core.inRange(HSV,wals.getHSVmin(),wals.getHSVmax(),threshold);
                    morphOps(threshold);
                    //Debug
                    debugImshow.showImage(threshold);

        }catch (NullPointerException ex){
             System.out.println(ex.getMessage());
        }


        return threshold;
    }
}


