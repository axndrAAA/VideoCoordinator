package Bot;

import org.opencv.core.Point;

import java.util.ArrayList;

public class BotDriver extends Thread {

    private BotModel bot;
    private ArrayList<Point> map;
    private double eps;//область, при достижении котоорой засчитывается достижение ППМ

    public BotDriver(BotModel bot, ArrayList<Point> map) {
        this.bot = bot;
        this.map = map;
        this.eps = 20.0;
    }

    public BotDriver(BotModel bot, ArrayList<Point> map, double eps){
        this(bot,map);
        this.eps = eps;
    }

    public BotDriver(BotModel bot, Point destPoint){
        this.bot = bot;
        this.map = new ArrayList<Point>(1);
        map.add(destPoint);
        this.eps = 20.0;
    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            for (Point curPoint : map) {
                bot.goToPoint(curPoint,eps);
            }
            break;
        }

    }
}
