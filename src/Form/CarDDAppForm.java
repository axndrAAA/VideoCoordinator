package Form;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.AccessException;
import java.util.ArrayList;

import Bot.BotsManager;
import Labitint.CrazyFactory;
import Labitint.Square;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import Coordinator.Imshow;
import Coordinator.VideoCoordinator;

/**
 * Created by Александр on 24.10.2016.
 */
public class CarDDAppForm extends JFrame {//класс формы приложения

    public JPanel modePanel;
    public JRadioButton mode0RB;
    public JRadioButton mode1RB;
    public JMapArea map;
    public JButton upSpeed;
    public JButton downSpeed;
    public JTextField speed;
    public JLabel botStatusLabel;

    //табличка
    public JPanel coordObjList;
    JTable table;

    public JButton calibrColors;
    public JButton calibrCoordinates;

    //goto режим
    public JPanel goToPanel;
    public JButton goToBtn;

    public JButton restartConnection;

    private String[] columnNames = {
            "Color",
            "Х",
            "У"
    };

    private String[][] data = {
            {"", "", ""},
            {"", "", ""},
            {"", "", ""},
    };

    //public BotModel botModel;//модель платформы
    public BotsManager botsManager;//объект, содержащий список всех передатчиков и привязанных к ним моделей
    // и управляющий всем этим дерьмом
    //public BotTransmitter transmitter;//передатчик
    public VideoCoordinator coordinator;
    public static Grid grid;

    private boolean isGettingFieldCoordinates = false;
    private boolean luc_rdc_clicks = true;// true - left up corner    false - right down corner


    //конструктор
    public CarDDAppForm(String name){
        super(name);
        //создание объектов платформ, передатчиков для них и их запуск
//        botsManager = new BotsManager(2);
        botsManager = new BotsManager("settings.txt");

        this.init();//создание формы

        //grid = new Grid(new Point(20,30),new Point(400,400));//here is an info about real field, shown at image
        grid = new Grid("gridSettings.txt");

        // tracker running
        int cameraNumer = 1;
        while (true){
            try{
                coordinator = new VideoCoordinator(cameraNumer,botsManager);
                coordinator.start();
                break;
            }catch (AccessException ex){
                System.out.println(ex.getMessage() + "cameraNum = " + cameraNumer);
                cameraNumer--;
            }
        }
        System.out.println("Camera " + cameraNumer + " opened sucsesfully.");

    }
    //инициализация формы
    public void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        botStatusLabel = new JLabel("Ofline");
        botStatusLabel.setForeground(Color.RED);
        botStatusLabel.setOpaque(true);
        botStatusLabel.setVerticalAlignment(JLabel.TOP);

        upSpeed = new JButton(String.valueOf('↑'));
        downSpeed = new JButton(String.valueOf('↓'));
        speed = new JTextField(3);
        speed.setText("0");
        calibrColors = new JButton("Кaл.цв.");
        calibrCoordinates = new JButton("Кaл.кор");

        modePanel = new JPanel();
        Border bordur = BorderFactory.createTitledBorder("Режим");
        modePanel.setBorder(bordur);
        mode0RB = new JRadioButton("Режим 1",true);
        mode1RB = new JRadioButton("Режим 2",false);
        mode0RB.setFocusable(false);
        mode1RB.setFocusable(false);
        ButtonGroup group = new ButtonGroup();
        group.add(mode0RB);
        group.add(mode1RB);
        modePanel.add(mode0RB);
        modePanel.add(mode1RB);

        coordObjList =new JPanel();
        table = new JTable(data,columnNames);
        coordObjList.setLayout(new BorderLayout());
        coordObjList.add(table.getTableHeader(), BorderLayout.PAGE_START);
        coordObjList.add(table, BorderLayout.CENTER);
        coordObjList.setPreferredSize(new Dimension(100,50));
        coordObjList.setVisible(true);

        goToPanel = new JPanel();
        bordur = BorderFactory.createTitledBorder("GoTo");
        goToPanel.setBorder(bordur);
        goToPanel.setLayout(new GridLayout(3,2));
        goToBtn = new JButton("Go");
        goToPanel.add(new JLabel());
        goToPanel.add(goToBtn);

        restartConnection = new JButton("RestartCon");


        Image im = null;
        try{
            im = ImageIO.read(new File("icon.jpg"));
        }catch (IOException ex){}
        map = new JMapArea(im);

        map.setBounds(225,5,640,480);
        modePanel.setBounds(5,5,125,90);
        upSpeed.setBounds(135,5,45,45);
        speed.setBounds(135,55,45,25);
        downSpeed.setBounds(135,85,45,45);
        botStatusLabel.setBounds(5,100,125,20);
        coordObjList.setBounds(5,135,215,90);
        calibrColors.setBounds(5,230,82,20);
        calibrCoordinates.setBounds(92,230,82,20);
        goToPanel.setBounds(5,255,215,80);
        restartConnection.setBounds(5,340,82,20);

        restartConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botsManager.getBotTransmitter(0).restore();
            }
        });

        calibrColors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                coordinator.setCalibrationMode(!coordinator.isCalibrationMode());
            }
        });

        calibrCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGettingFieldCoordinates = !isGettingFieldCoordinates;
                ((JButton)e.getSource()).setBackground(Color.GREEN);
            }
        });

        goToBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //изображение для дальнейшего анализа и построения карты
                Mat walsImg = null;

                //окно демонстрации биаризованного кадра
                Imshow imshow = new Imshow("showWals");

                //особо хитрожопая магия, чтобы запустить процесс настройки фильтрации кадра и получения
                //бинаризованного кадра со стенами в отдльном потоке
                if( (walsImg = coordinator.getWalsThresholdedImage()) != null){
                    imshow.showImage(walsImg);
                }else {
                    coordinator.tryCalcWalsImage();
                    imshow.Window.dispose();
                    imshow = null;
                    return;
                }

                int dialogResult = JOptionPane.showConfirmDialog (null,
                        "Утвердите изображение.","Achtung",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){

                    //если изображение утверждено, рспознаем и устанавливаем как рабочую новую карту
                    CrazyFactory.setMapFromImage(walsImg);

                    imshow.Window.hide();
                    imshow.Window.dispose();
                    imshow = null;

                }else {
                    //если изображение не утверждено, то пропускаем ход, и пробуем еще раз
                    imshow.Window.hide();
                    imshow.Window.dispose();
                    imshow = null;
                    return;
                }


                //определям в какой клетке в данный момент находится бот номер 0
                //Square beginSquare = new Square(7, 1);
                Square beginSquare = CrazyFactory.getWaweAlgCoordinatesFromGridCoord(
                        grid.getSquareBotPlaced(botsManager.getBot(0)));
                JOptionPane.showMessageDialog(null,
                        "bot in [" + beginSquare);

                //TODO:затычка
                if(beginSquare!=null){
                    return;
                }

                //точка назначения всегда одна
                Square endSquare = new Square(7, 3);

                //алгоритм определения маршрута
                CrazyFactory crazyFactory = new CrazyFactory(beginSquare,endSquare);

                //карта маршрута
                ArrayList<Square> squareList = null;
                ArrayList<Point> track = new ArrayList<>(1);
                try {
                    squareList = crazyFactory.runWaveAlgorithm(false); //Получаем последовательный список с координатами клеток кратчайшего пути
                    if(squareList != null){
                        if(squareList.size() > 0){
                            JOptionPane.showMessageDialog(null,"Маршрут построен.");
                            //преобразуем номера клеток в координаты на реальном кадре и отображаем трэк
                            track = crazyFactory.map2ImgCoordinates(grid, squareList);
                            grid.showTrackPoints(true,track);

                            //TODO
                            grid.toGrPointCoord(track);
                            botsManager.runPanzerCamfWagen(0,track);
                        }
                    }
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,
                            "Some shit hapend. Algoritm doesn't work! - " + ex.getMessage());
                }
                }
        });
        upSpeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed  = new Integer(CarDDAppForm.this.speed.getText());
                speed++;
                if(speed  > botsManager.getGlobalSpeedLimit())
                    speed = botsManager.getGlobalSpeedLimit();
                botsManager.getBot(0).setSpeed((byte) speed);
                CarDDAppForm.this.speed.setText(String.valueOf(speed));
            }
        });
        downSpeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed  = new Integer(CarDDAppForm.this.speed.getText());
                speed--;
                if (speed <= 0)
                    speed = 0;
                botsManager.getBot(0).setSpeed((byte) speed);
                CarDDAppForm.this.speed.setText(String.valueOf(speed));
            }
        });

        map.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                if(isGettingFieldCoordinates){
                    if(luc_rdc_clicks){
                        grid.setUpLeftCorner(new Point(x,y));
                    }else {
                        grid.setDownRightCorner(new Point(x,y));
                        isGettingFieldCoordinates = false;

                        calibrCoordinates.setBackground(null);
                        grid.saveSettingsToFile("gridSettings.txt");
                    }
                    luc_rdc_clicks = !luc_rdc_clicks;
                }
                Point destPoint = new Point(x,y);

                if(mode1RB.isSelected()){
                    botsManager.stopAllDrivers();
                    grid.showTrackPoints(false,null);
                    //JOptionPane.showMessageDialog(null,"X:" + x + "   Y:" + y);
                    int dialogResult = JOptionPane.showConfirmDialog (null, "Проход в точку: (" + grid.getGridPointCoord(destPoint).x + ":" + grid.getGridPointCoord(destPoint).y + ").","Achtung",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION){
                        if(x > grid.getDownRightCorner().x){
                            x = (int)grid.getDownRightCorner().x;
                        }
                        if(y > grid.getDownRightCorner().y){
                            y = (int)grid.getDownRightCorner().x;
                        }

                        ArrayList<Point> track = new ArrayList<>(1);
                        track.add(destPoint);
                        grid.showTrackPoints(true,track);
                        botsManager.runPanzerCamfWagen(0,grid.getGridPointCoord(destPoint));

                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        panel.add(modePanel);
        panel.add(upSpeed);
        panel.add(downSpeed);
        panel.add(speed);
        panel.add(map);
        panel.add(botStatusLabel);

        panel.add(coordObjList);
        panel.add(calibrColors);
        panel.add(calibrCoordinates);
        add(goToPanel);
        add(restartConnection);
        this.getContentPane().add(panel);
        //setBounds(30,30,300,300);
        setPreferredSize(new Dimension(880,520));//(930,510)
        setResizable(false);
        setFocusable(true);
        requestFocusInWindow();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        this.addWindowListener(exitListener);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    //прослушивание клавиатуры
    KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(final KeyEvent e) {

            if (e.getID() == KeyEvent.KEY_PRESSED) {//нажатие
                if(e.getKeyCode() == 38)
                    botsManager.getBot(0).goForward();
                if(e.getKeyCode() == 40)
                    botsManager.getBot(0).goBack();
                if(e.getKeyCode() == 37)
                    botsManager.getBot(0).turnLeft();
                if (e.getKeyCode() == 39)
                    botsManager.getBot(0).turnRight();
                if(e.getKeyCode() == KeyEvent.VK_ADD)
                    upSpeed.doClick();
                if(e.getKeyCode() == KeyEvent.VK_SUBTRACT)
                    downSpeed.doClick();

            }
            if(e.getID() == KeyEvent.KEY_RELEASED){//отпускане
                if (e.getKeyCode() == 38 | e.getKeyCode() == 40)
                    botsManager.getBot(0).stop();
                if(e.getKeyCode() == 37 | e.getKeyCode() == 39)
                    botsManager.getBot(0).goStraight();
            }
            return false;
        }
    };
    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                botsManager.closeConnection(-1);
                coordinator.interrupt();
                botsManager = null;
                coordinator = null;
                System.exit(0);
            }catch (NullPointerException ex){}
        }
    };
    //функция смены надписи
    public  void setIP(String s, boolean isConnected, JLabel ip_l){
        ip_l.setText(s);
        if (isConnected){
            ip_l.setForeground(Color.GREEN);
        }else
            ip_l.setForeground(Color.RED);
    }
    //функция смены статуса
    public static void setStatus(CarDDAppForm form, BotsManager botsManager){
        if(botsManager.getBotTransmitter(0).isConnectionOk()){
            form.botStatusLabel.setForeground(Color.GREEN);
            form.botStatusLabel.setText(botsManager.getBot(0).getName() + " online.");
        }else{
            form.botStatusLabel.setForeground(Color.RED);
            form.botStatusLabel.setText(botsManager.getBot(0).getName() + " ofline");
        }

    }

    //функция обновления информации о координатах в таблице
    public void setObjCoordinates(){
        //refreshing table data
        try {
            for(int i = 0; i < botsManager.getBotsList().size();i++){
                this.table.setValueAt(Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getRGBColor().getRed()) + ";" +
                                Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getRGBColor().getGreen()) + ";" +
                                Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getRGBColor().getBlue())
                        ,i,0);
                this.table.setValueAt(Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getxPos()),i,1);
                this.table.setValueAt(Integer.toString(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getyPos()),i,2);
            }
        }catch (NullPointerException ex){}
    }

    //update image function
    public void updateImage(){
        try{
            if(coordinator.getCameraFeed().rows() == 0){
                return;
            }
            Mat cameraFeed = coordinator.getCameraFeed().clone();
            grid.getGridedCameraFeed(cameraFeed);
            BufferedImage bfIm = Imshow.toBufferedImage(cameraFeed);
            map.setImage(bfIm);
            map.repaint();
        }catch (IllegalAccessError ex){
            System.out.println(ex.getMessage());
        }
        catch (Exception ex){System.out.println(ex.getMessage());}
    }

    public void update(){
        updateImage();
        setObjCoordinates();
        setStatus(this,this.botsManager);
    }
}

