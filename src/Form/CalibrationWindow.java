package Form;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CalibrationWindow extends JFrame
{
    public static final int DEFAULT_WIDTH = 650;
    public static final int DEFAULT_HEIGHT = 510;
    public ArrayList<Integer> param;

    private JPanel sliderPanel;
    private boolean isSetupOk = false;
    private boolean isSetupOkAndFinished = false;

    private JButton addButton;
    private JButton okButton;

    public CalibrationWindow(String calWinName,int Hmin,int Hmax,int Smin,int Smax,int Vmin, int Vmax)
    {
        setTitle(calWinName);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        param = new ArrayList<Integer>(6);
        param.add(0,Hmin);
        param.add(1,Hmax);
        param.add(2,Smin);
        param.add(3,Smax);
        param.add(4,Vmin);
        param.add(5,Vmax);
        addSlider(0,"H_MIN",0,179,1);//like in opncv docs
        addSlider(1,"H_MAX",0,179,1);//like in opncv docs
        addSlider(2,"S_MIN",0,256,1);
        addSlider(3,"S_MAX",0,256,1);
        addSlider(4,"V_MAX",0,256,1);
        addSlider(5,"V_MAX",0,256,1);


        addButtons();

        add(sliderPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }


    private void addSlider(int numderInList, String description,int min,int max,int step)
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

    private void addButtons(){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        addButton = new JButton("Add");
        okButton = new JButton("Ok");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSetupOk = true;
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSetupOkAndFinished = true;
            }
        });
        buttonPanel.add(addButton);
        buttonPanel.add(okButton);
        sliderPanel.add(buttonPanel);

    }

    public boolean isSetupOk() {    return isSetupOk;    }

    public void setSetupOk(boolean setupOk) {        isSetupOk = setupOk;    }


    public boolean isSetupOkAndFinished() { return isSetupOkAndFinished;    }

}
