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

import Car.BotTransmitter;
import Car.ObjectsManager;
import Labitint.CrazyFactory;
import Labitint.Square;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import Coordinator.Imshow;
import Coordinator.ObjOnImage;
import Coordinator.VideoCoordinator;

/**
 * Created by Александр on 24.10.2016.
 */
public class CarDDAppForm extends JFrame {//класс формы приложения

    public JLabel IP;
    public JButton connect;
    public JPanel modePanel;
    public JRadioButton mode0RB;
    public JRadioButton mode1RB;
    public JMapArea map;
    public JButton upSpeed;
    public JButton downSpeed;
    public JTextField speed;
    public JLabel isArrived;

    //табличка
    public JPanel coordObjList;
    JTable table;

    public JButton calibrColors;
    public JButton calibrCoordinates;

    //goto режим
    public JPanel goToPanel;
    public JLabel gotoXLabel;
    public JLabel gotoYLabel;
    public JTextField goX;
    public JTextField goY;
    public JButton goToBtn;




    public static String CON_LOST = "Не соединено";
    public static String CON_OK = "Соединено";

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
    public ObjectsManager objectsManager;//объект, содержащий список всех передатчиков и привязанных к ним моделей
                                        // и управляющий всем этим дерьмом
    //public BotTransmitter transmitter;//передатчик
    public VideoCoordinator coordinator;
    public static Grid grid;

    private boolean isGettingFieldCoordinates = false;
    private boolean luc_rdc_clicks = true;// true - left up corner    false - right down corner


    //конструктор
    public CarDDAppForm(String name){
        super(name);
        //car = new BotModel();//создание объекта - модели платформы
        objectsManager = new ObjectsManager(1);//создание объектов платформ, передатчиков для них и их запуск

        this.init();//создание формы

        grid = new Grid(new Point(20,30),new Point(400,400));//here is an info about real field, shown at image

        // tracker running
        try{
            coordinator = new VideoCoordinator(0,"settings.txt");
            coordinator.start();
        }catch (AccessException ex){
            System.out.println(ex.getMessage());
        }

    }
    //инициализация формы
    public void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        IP = new JLabel(CON_LOST);
        IP.setForeground(Color.RED);
        IP.setBackground(Color.GRAY);
        IP.setOpaque(true);
        IP.setVerticalAlignment(JLabel.TOP);

        isArrived = new JLabel("В пути");
        isArrived.setForeground(Color.RED);
        isArrived.setOpaque(true);
        isArrived.setVerticalAlignment(JLabel.TOP);

        connect = new JButton("Соединить");
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
        goToPanel.hide();
        gotoXLabel = new JLabel("X: ");        gotoXLabel.setOpaque(true);
        gotoYLabel = new JLabel("Y: ");        gotoYLabel.setOpaque(true);
        goX = new JTextField(3);
        goY = new JTextField(3);
        goToBtn = new JButton("Go");
        goToPanel.add(gotoXLabel);
        goToPanel.add(gotoYLabel);
        goToPanel.add(goX);
        goToPanel.add(goY);
        goToPanel.add(new JLabel());
        goToPanel.add(goToBtn);

        Image im = null;
        try{
            im = ImageIO.read(new File("icon.jpg"));
        }catch (IOException ex){}
        map = new JMapArea(im);

        map.setBounds(225,5,700,500);
        IP.setBounds(5,5,125,20);
        connect.setBounds(135,5,85,20);
        modePanel.setBounds(5,30,125,90);
        upSpeed.setBounds(135,30,45,45);
        speed.setBounds(135,80,45,25);
        downSpeed.setBounds(135,110,45,45);
        isArrived.setBounds(5,125,125,20);
        coordObjList.setBounds(5,160,215,90);
        calibrColors.setBounds(5,255,82,20);
        calibrCoordinates.setBounds(92,255,82,20);
        goToPanel.setBounds(5,280,215,80);

        //прослушивание кнопки connect
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                try{
//                    if(transmitter == null){
//                        //создание и старт передатчика
//                        transmitter = new BotTransmitter(car);
//                        transmitter.start();
//                        setIP("IP:" + car.getIP(),true,IP);
//                        connect.setText("Disconnect");
//
//                    }else {
//                        transmitter.restore();
//                        transmitter.interrupt();
//                        transmitter = null;
//                        setIP(CON_LOST,false,IP);
//                        connect.setText("Connect");
//                     }
//                } catch (IOException ex){}

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
//                try{
//                    double x =  Double.parseDouble(goX.getText());
//                    double y =  Double.parseDouble(goY.getText());
//                    botModel.setYdest((int)y);
//                    botModel.setXdest((int)x);
//                    botModel.setMode((byte)1);
//                }catch (NumberFormatException ex){
//                    JOptionPane.showMessageDialog(null,"Не парсится:(" );//
//                }
                Square[] squareList;//Лист, в который будет записан путь
                Square beginingSqr = new Square(6, 1);//Квдарат с координатами входа  в Лабиринт
                Square endingSqr = new Square(6, 4); // Квадрат с координатами выхода из Лабиринта
                int[][] mapArr = {
                        {1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 0, 1},
                        {1, 0, 0, 0, 1, 1, 1, 0, 1},
                        {1, 0, 1, 1, 1, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 0, 1, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                squareList = CrazyFactory.runWaveAlgorithm(mapArr, beginingSqr, endingSqr, false); //Получаем последовательный список с координатами клеток кратчайшего пути
                JOptionPane.showMessageDialog(null,"Маршрут построен.");
//                CrazyFactory.runWarMachine(botModel, squareList, 2, endingSqr); // Запускаем робота в путь




            }
        });
        upSpeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed  = new Integer(CarDDAppForm.this.speed.getText());
                speed++;
                if(speed  > objectsManager.getGlobalSpeedLimit())
                    speed = objectsManager.getGlobalSpeedLimit();
                objectsManager.setGlobalSpeedLimit((byte) speed);
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
                objectsManager.setGlobalSpeedLimit((byte) speed);
                CarDDAppForm.this.speed.setText(String.valueOf(speed));
            }
        });
        mode0RB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // car.setMode((byte)0);
                goToPanel.hide();
            }
        });
        mode1RB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //car.setMode((byte)1);
                goToPanel.show();
            }
        });

        map.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //JOptionPane.showMessageDialog(null,"hehehehe" + e.getX() + " " + e.getY());
                int x = e.getX();
                int y = e.getY();
                if(isGettingFieldCoordinates){
                    if(luc_rdc_clicks){
                        grid.setUpLeftCorner(new Point(x,y));
                    }else {
                        grid.setDownRightCorner(new Point(x,y));
                        isGettingFieldCoordinates = false;
                        calibrCoordinates.setBackground(null);
                    }
                    luc_rdc_clicks = !luc_rdc_clicks;
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

        panel.add(IP);
        panel.add(connect);
        panel.add(modePanel);
        panel.add(upSpeed);
        panel.add(downSpeed);
        panel.add(speed);
        panel.add(map);
        panel.add(isArrived);

        panel.add(coordObjList);
        panel.add(calibrColors);
        panel.add(calibrCoordinates);
        add(goToPanel);
        this.getContentPane().add(panel);
        //setBounds(30,30,300,300);
        setPreferredSize(new Dimension(1000,550));//(930,510)
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
//            if (e.getID() == KeyEvent.KEY_PRESSED) {//нажатие
//                if(e.getKeyCode() == 38)
//                    car.goForward();
//                if(e.getKeyCode() == 40)
//                    car.goBack();
//                if(e.getKeyCode() == 37)
//                    car.turnLeft();
//                if (e.getKeyCode() == 39)
//                    car.turnRight();
//                if(e.getKeyCode() == KeyEvent.VK_ADD)
//                    upSpeed.doClick();
//                if(e.getKeyCode() == KeyEvent.VK_SUBTRACT)
//                    downSpeed.doClick();
////                if(e.getKeyCode() == KeyEvent.VK_W)
////                    CrazyFactory.EnterPresed = true;
//
//            }
//            if(e.getID() == KeyEvent.KEY_RELEASED){//отпускане
//                if (e.getKeyCode() == 38 | e.getKeyCode() == 40)
//                    car.stop();
//                if(e.getKeyCode() == 37 | e.getKeyCode() == 39)
//                    car.goStraight();
//            }
            return false;
        }
    };
    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                int Answer = JOptionPane.showConfirmDialog(null, "Are you sure want to exit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (Answer == JOptionPane.YES_OPTION) {
                    objectsManager.closeConnection(-1);
                    coordinator.interrupt();
                    objectsManager = null;
                    coordinator = null;
                    System.exit(0);
                }
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
    public static void setStatus(CarDDAppForm form, boolean isarrived){
        if(isarrived){
            form.isArrived.setForeground(Color.GREEN);
            form.isArrived.setText("Прибыл на точку");
        }else{
            form.isArrived.setForeground(Color.RED);
            form.isArrived.setText("В пути");
        }

    }

    //функция обновления информации о координатах в таблице
    public void setObjCoordinates(ArrayList<ObjOnImage> objs){
        //refreshing table data
        for(int i = 0; i < objs.size();i++){
            this.table.setValueAt(Integer.toString(objs.get(i).getRGBColor().getRed()) + ";" +
                                            Integer.toString(objs.get(i).getRGBColor().getGreen()) + ";" +
                                                Integer.toString(objs.get(i).getRGBColor().getBlue())
                                                                ,i,0);
            this.table.setValueAt(Integer.toString(objs.get(i).getxPos()),i,1);
            this.table.setValueAt(Integer.toString(objs.get(i).getyPos()),i,2);
        }
    }

    //update image function
    public void updateImage(){
        try{
            if(coordinator.getCameraFeed().rows() == 0){
                return;
            }
            Mat cameraFeed = coordinator.getCameraFeed();
            grid.getGridedCameraFeed(cameraFeed);
            BufferedImage bfIm = Imshow.toBufferedImage(cameraFeed);
            map.setImage(bfIm);
            map.repaint();
        }catch (IllegalAccessError ex){
            System.out.println(ex.getMessage());
        }
    }

    public void update(){
       updateImage();
       setObjCoordinates(coordinator.objectsToTrack);
    }
}

