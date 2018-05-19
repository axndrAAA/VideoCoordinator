package Coordinator;

import Bot.BotTransmitter;
import Bot.BotsManager;
import Form.CarDDAppForm;
import org.opencv.core.Scalar;
import sun.plugin2.message.Message;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CalibrationWindow extends JFrame
{
    public static final int DEFAULT_WIDTH = 650;
    public static final int DEFAULT_HEIGHT = 510;
    public ArrayList<Integer> param;



    VideoCoordinator videoCoordinator;
    private boolean isSetupOkAndFinished = false;

    private BotsManager botsManager;

    public CalibrationWindow(VideoCoordinator vidCoord, BotsManager btmng,int Hmin,int Hmax,int Smin,int Smax,int Vmin, int Vmax)
    {
        videoCoordinator = vidCoord;
        botsManager = btmng;
        setTitle("Settings");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        param = new ArrayList<Integer>(6);
        param.add(0,Hmin);
        param.add(1,Hmax);
        param.add(2,Smin);
        param.add(3,Smax);
        param.add(4,Vmin);
        param.add(5,Vmax);
        addSlider(0,"H_MIN",0,179,1,sliderPanel);//like in opncv docs
        addSlider(1,"H_MAX",0,179,1,sliderPanel);//like in opncv docs
        addSlider(2,"S_MIN",0,255,1,sliderPanel);
        addSlider(3,"S_MAX",0,255,1,sliderPanel);
        addSlider(4,"V_MIN",0,255,1,sliderPanel);
        addSlider(5,"V_MAX",0,255,1,sliderPanel);
        addButtons(sliderPanel);
        add(sliderPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(exitListener);

        if(botsManager.getBotsList().get(0).getBotModel().getBotOnImage() != null)
            setUpSettings(botsManager.getBotsList().get(0).getBotModel().getBotOnImage().getParametersList(),sliderPanel);
    }


    private void addSlider(int numderInList, String description,int min,int max,int step,JPanel sliderPanel)
    {
        JSlider slider;
        slider = new JSlider();
        slider.setPreferredSize(new Dimension(DEFAULT_WIDTH - 200,(int)(DEFAULT_HEIGHT/5.2)));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(step);
        slider.setMinimum(min);
        slider.setMaximum(max);

        slider.setValue(param.get(numderInList));
        JTextField textField = new JTextField(Integer.toString(slider.getValue()),5);
        textField.setEditable(false);
       // param.add(numderInList,slider.getValue());

        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent event)
            {
                // update text field when the slider value changes
                JSlider source = (JSlider) event.getSource();
                textField.setText("" + source.getValue());
                param.set(numderInList,new Integer(source.getValue()));

            }
        });

        JPanel panel = new JPanel();
        panel.add(slider);
        panel.add(new JLabel(description));
        panel.add(textField);
        sliderPanel.add(panel);
    }

    private void addButtons(JPanel sliderPanel){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JComboBox editCurBotSettings = new JComboBox();
        for (BotTransmitter bt: botsManager.getBotsList()) {
            if(bt.getBotModel().getBotOnImage() != null){
                String item = bt.getBotModel().getBotOnImage().getName();
                editCurBotSettings.addItem(item);
            }else {
                String item = bt.getBotModel().getName();
                editCurBotSettings.addItem(item);
            }
        }
        editCurBotSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = editCurBotSettings.getSelectedIndex();
                if(botsManager.getBotsList().get(i).getBotModel() != null)
                    setUpSettings(botsManager.getBotsList().get(i).getBotModel().getBotOnImage().getParametersList(),sliderPanel);
            }
        });
        buttonPanel.add(editCurBotSettings);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int curObj = editCurBotSettings.getSelectedIndex();
                botsManager.getBotsList().get(curObj).getBotModel().setBotOnImage(
                        new BotOnImage(new Scalar(param.get(0),param.get(2),param.get(4)),
                                       new Scalar(param.get(1),param.get(3),param.get(5))));
            }
        });
        buttonPanel.add(confirmButton);

        JTextField autoTuneDelta = new JTextField("20",5);
        autoTuneDelta.setEditable(true);
        buttonPanel.add(autoTuneDelta);

        JButton autoTuneButton = new JButton("autoTune");
        autoTuneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    ArrayList<Integer> autoParams = videoCoordinator.getAutoParameters(Integer.parseInt(autoTuneDelta.getText()));
                    setUpSettings(autoParams,sliderPanel);
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null,"Не верное значение радиуса диапозона",
                            "Ошибка",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        buttonPanel.add(autoTuneButton);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSetupOkAndFinished = true;
            }
        });
        buttonPanel.add(okButton);




        buttonPanel.add(autoTuneButton);
        sliderPanel.add(buttonPanel);

    }

    private void setUpSettings(ArrayList<Integer> params,JPanel slidersPanel){
        Component[] generalPanel = slidersPanel.getComponents();
        //ArrayList<Integer> params = boi.getParametersList();
        for(int i = 0; i < params.size();i++){
            setSlider((JPanel)generalPanel[i],params.get(i));
        }
    }

    private void setSlider (JPanel slider,int value){
        Component[] secondPanel = slider.getComponents();
        ((JSlider)secondPanel[0]).setValue(value);
        ((JTextField)secondPanel[2]).setText(String.valueOf(value));
    }


    public boolean isSetupOkAndFinished() { return isSetupOkAndFinished;    }

    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                isSetupOkAndFinished = true;
            }catch (NullPointerException ex){}
        }
    };


}
