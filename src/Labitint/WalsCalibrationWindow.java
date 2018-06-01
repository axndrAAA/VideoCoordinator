package Labitint;

import Bot.BotTransmitter;
import Coordinator.BotOnImage;
import org.opencv.core.Scalar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static Coordinator.CalibrationWindow.DEFAULT_HEIGHT;
import static Coordinator.CalibrationWindow.DEFAULT_WIDTH;

public class WalsCalibrationWindow extends JFrame{
    private ArrayList<Integer> param;
    private boolean isSetupOkAndFinished = false;

    public WalsCalibrationWindow(){}

    public WalsCalibrationWindow(ArrayList<Integer> _param){
        super("Wals settings");
        this.param = _param;
        setTitle("Wals settings");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
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
        sliderPanel.setVisible(true);
        this.setVisible(true);
        this.repaint();
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

    private void addButtons(JPanel sliderPanel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());


        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSetupOkAndFinished = true;
            }
        });
        buttonPanel.add(okButton);
        sliderPanel.add(buttonPanel);

    }
    public ArrayList<Integer> getParam(){
        return param;
    }
    public boolean isSetupOkAndFinished() {
        return isSetupOkAndFinished;
    }


    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                isSetupOkAndFinished = true;
            }catch (NullPointerException ex){}
        }
    };
}
