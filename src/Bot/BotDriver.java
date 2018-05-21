package Bot;

import Form.CarDDAppForm;
import org.opencv.core.Point;

import java.util.ArrayList;

public class BotDriver extends Thread {

    private BotModel bot;
    private ArrayList<Point> map;
    private double eps = 10.0;//область, при достижении котоорой засчитывается достижение ППМ

    public BotDriver(BotModel bot, ArrayList<Point> map) {
        this.bot = bot;
        this.map = map;
        this.setName("BotDriver");

    }

    public BotDriver(BotModel bot, ArrayList<Point> map, double eps){
        this(bot,map);
        this.eps = eps;
    }

    public BotDriver(BotModel bot, Point destPoint){
        this.bot = bot;
        this.map = new ArrayList<Point>(1);
        map.add(destPoint);
        this.setName("BotDriver");

    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            for (Point curPoint : map) {
                bot.goToPoint(curPoint,eps);
            }
            System.out.println("Маршут пройден.");
            CarDDAppForm.grid.showTrackPoints(false,null);
            break;
        }

    }
}
