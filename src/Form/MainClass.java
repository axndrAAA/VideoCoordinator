package Form;


import Coordinator.MedianFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;



import java.rmi.AccessException;
import java.util.Random;


public class MainClass {



    public static BufferedImage Mat2BufferedImage(Mat m){


        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    public static void displayImage(Image img2){
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon=new ImageIcon(img2);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main (String args[]) throws AccessException{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.VERSION);

        CarDDAppForm form = new CarDDAppForm("Window");

        while (form.botsManager != null){
            form.update();
        }

    }


}


// Пример использования многострочных полей JTextArea
//
//import javax.swing.*;
//import java.awt.Font;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//
//public class MainClass extends JFrame
//{
//    JTextArea area1;
//    JTextArea area2;
//    public MainClass()
//    {
//        super("Пример JTextArea");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        // Cоздание многострочных полей
//         area1 = new JTextArea("Многострочное поле", 8, 10);
//        // Шрифт и табуляция
//        area1.setFont(new Font("Dialog", Font.PLAIN, 14));
//        area1.setTabSize(10);
//        area1.setEditable(false);
//
//         area2 = new JTextArea(15, 10);
//        area2.setText("Второе многострочное поле");
//        // Параметры переноса слов
//        area2.setLineWrap(true);
//        area2.setWrapStyleWord(true);
//
//        // Добавим поля в окно
//        JPanel contents = new JPanel();
//        contents.add(new JScrollPane(area1));
//        contents.add(new JScrollPane(area2));
//        setContentPane(contents);
//
//        // Выводим окно на экран
//        setSize(400, 300);
//        setVisible(true);
//    }
//    public static void main(String[] args) {
//        MainClass obj = new MainClass();
//        String bufer = "buf";
//        InputStream is = new ByteArrayInputStream(bufer.getBytes());
//        System.setIn(is);
//
//        while (true){
//            System.out.println("StdOut");
//            obj.area1.append(bufer);
//            try {
//                Thread.currentThread().sleep(500);
//
//            }catch (InterruptedException ex){}
//        }
//    }
//}