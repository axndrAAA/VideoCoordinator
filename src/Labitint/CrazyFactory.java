package Labitint;
import Bot.BotModel;
import Form.Grid;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CrazyFactory {
    public boolean diagonalIsAllowed = false;// - признак разрешения (true) или запрета (fslse) на поиск пути по диагонали
    public Square beginingSqr;//Квдарат с координатами входа  в Лабиринт
    public Square endingSqr; // Квадрат с координатами выхода из Лабиринта
    int[][] mapArr = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1}
    };//массив MxN, содержащий карту лабиринта, представленную в 0-ях (проход) и 1-ах (стена)


    public CrazyFactory(Square beginSquare, Square endSquare){
        beginingSqr = beginSquare;
        endingSqr = endSquare;
    }

    public void setMapArr(int[][] mapArr) {
        this.mapArr = mapArr;
    }

    public ArrayList<Square> runWaveAlgorithm(boolean isDiagonalAlowed) {
        diagonalIsAllowed = isDiagonalAlowed;
        Square[] squareList;
        //Расречатываем Дабиринт
//        for (int i = 0; i < mapArr.length; i++) {
//            for (int j = 0; j < mapArr[i].length; j++) {
//                System.out.print(mapArr[i][j] + "  ");
//            }
//            System.out.println();
//        }
//        System.out.println();
        // Волновой алгоритм
        int d = 2;
        mapArr[beginingSqr.getX()][beginingSqr.getY()] = d; //начальной клетке присваивается значение d
        while (mapArr[endingSqr.getX()][endingSqr.getY()] == 0) { //Пока алгоритм не дошел до точки выхода из лабиринта
            for (int i = 0; i < mapArr.length; i++) { // Перебираем строки
                for (int j = 0; j < mapArr[i].length; j++) {// Перебираем столбцы
                    if (mapArr[i][j] == d) { //ищем клетки, значение которых равно d
                        // Проходим по клеткам вокруг d и, если они пустые, присваиваем им значение d+1
                        for (int g = -1; g < 2; g++)//Столбец
                            for (int v = -1; v < 2; v++) //Строка
                            {
                                if (diagonalIsAllowed || (g != v && g != -v)) // Проверка на возможность поиска пути по диагонали
                                    if (!((i + v < 0) || (i + v >= mapArr.length) || (j + g < 0) || (j + g >= mapArr[i].length))) //Проверка на выход клетки за пределы карты.
                                        if (mapArr[i + v][j + g] == 0) { //Проверка, пуста ли клетка, которую хочет пометить Лабиринт
                                            mapArr[i + v][j + g] = mapArr[i][j] + 1;
                                        }
                            }
                    }
                }
            }
            d++;
        }

//      Распечатываем карту Лабиринта с путями.
//        for (int i = 0; i < mapArr.length; i++) {
//            for (int j = 0; j < mapArr[i].length; j++) {
//                if (mapArr[i][j] > 9) {
//                    System.out.print(mapArr[i][j] + " ");
//                } else {
//                    System.out.print(mapArr[i][j] + "  ");
//                }
//
//            }
//            System.out.println();
//        }
//        System.out.println();
// Формируем список wayPointsList - последовательный лист клеток кратчайшего пути
        d = mapArr[endingSqr.getX()][endingSqr.getY()];
        squareList = new Square[d - 1];
        Square curSquare = endingSqr.clone();
        squareList[squareList.length - 1] = endingSqr;
        for (int i = 0; i < squareList.length - 1; i++) {
            //Ищем квадрат со значением d-1
            outerloop:
            for (int g = -1; g < 2; g++)//Столбец
                for (int v = -1; v < 2; v++) //Строка
                {
                    if (diagonalIsAllowed || (g != v && g != -v)) // Проверка на возможность поиска пути по диагонали
                        if (!((curSquare.getX() + v < 0) || (curSquare.getX() + v >= mapArr.length) || (curSquare.getY() + g < 0) ||
                                (curSquare.getY() + g >= mapArr[0].length))) //Проверка на выход клетки за пределы карты.
                            if (mapArr[curSquare.getX() + v][curSquare.getY() + g] == d - 1) { //Проверка, имеет ли клетка значение на 1 меньше, чем текущая
                                curSquare = new Square(curSquare.getX() + v, g + curSquare.getY());
                                squareList[squareList.length - 2 - i] = curSquare;
                                d--;
                                break outerloop;
                            }
                }
        }

//Распечатываем список с клетками
//        for (Square someSquare : squareList) {
//            System.out.println("x : " + someSquare.getX());
//            System.out.println("y : " + someSquare.getY());
//            System.out.println();
//
//        }

        String fileName = "WaweAlgorithmResult.txt";
        try {
            FileWriter writer = new FileWriter(fileName,false);
            String curStr = " ";
            for (int i = 0; i < squareList.length;i++){
            curStr = i + " - x : " + squareList[i].getX() + "     y : " + squareList[i].getY() + "\n";
                writer.write(curStr);
            }
            writer.flush();
            writer.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        //приведение в адекватную форму с контеййнером
        ArrayList<Square> output = new ArrayList<Square>(squareList.length);
        for (int i = 0; i < squareList.length;i++)
            output.add(squareList[i]);
        return output;
    }

    public ArrayList<Point> map2ImgCoordinates(Grid grid, ArrayList<Square> badMap){
        ArrayList<Point> goodMap = new ArrayList<>(badMap.size());

        //длина ячейки по оси oX сетки в СК opencv(горизонталь)
        double squareXopcvSize =grid.getXsquareSize();
        //аналогично, но по оси оУ (вертикаль)
        double squareYopcvSize =grid.getYsquareSize();

        for (int i = 0; i < badMap.size();i+=2){
            int I = ((badMap.get(i).getY() - 1)/2);
            int J = ((badMap.get(i).getX() - 1)/2);
            double xPoint  = grid.getUpLeftCorner().x + squareXopcvSize * (double)I + squareXopcvSize / 2;
            double yPoint  = grid.getUpLeftCorner().y + squareYopcvSize *  (double)J + squareYopcvSize / 2;

            goodMap.add(new Point(xPoint,yPoint));
        }


        return goodMap;
    }

    public static Square getWaweAlgCoordinatesFromGridCoord(Square gridCoord){

        int Y = gridCoord.getX();//помним, что в волновом алгоритме, и в openCV приняты разныеСК(названия осей обменены местами )
        int X = gridCoord.getY();
        //пересичтаем координаты исходя из того, что в волновом алгоритме
        //каждая стенка и проход - это тоже клетки.
        X = X*2 + 1;
        Y = Y*2 + 1;

        return new Square(X,Y);
    }

    //BotModel myBotModel - виртуальная модель робота
    //int err - допустимая ошибка определения местоположения
    //Square endingSqr - клетка, содержащая x и y точки выхода из лабиринта
    public static void runWarMachine(BotModel myBotModel, Square[] squareList, int err, Square endingSqr) {
        int curPoint = 0;// Номер точки, в которой мы находимся
        int nextPoint = 0; //Номер точки,к которой мы направляемся
        int reqAz = 0; //Азимут на следующую точку
        int difference;//Для вычисления разницы между текущим и требуемым азимутом
        Square nextSqr;
        while (curPoint != squareList.length - 1) {//Пока не добрались до крайней точки
            nextPoint = curPoint + 1;
            //Если несколько клеток распологаются по прямой - делаем серию, следуя сразу к последней её клетке. Серия может быть либо по x, либо по y
            if (squareList[nextPoint].getX() == myBotModel.getX()) {    //Пытаемся сделать серию по x
                while (squareList[nextPoint + 1].getX() == myBotModel.getX()) {
                    nextPoint++;
                    if (squareList[nextPoint].equals(endingSqr))
                        break;//Проверяем, является ли крайний квадрат в серии конечной точкой
                }
            } else if (squareList[nextPoint].getY() == myBotModel.getY()) {//Пытаемся сделать серию по y
                while (squareList[nextPoint + 1].getY() == myBotModel.getY()) {
                    nextPoint++;
                    if (squareList[nextPoint].equals(endingSqr))
                        break;//Проверяем, является ли крайний квадрат в серии конечной точкой
                }
            }
            nextSqr = squareList[nextPoint];
            //Ищем нужный азимут
            if (nextSqr.getX() == myBotModel.getX()) {
                if (nextSqr.getY() > myBotModel.getY()) {
                    reqAz = 90;
                } else if (nextSqr.getY() < myBotModel.getY()) {
                    reqAz = 270;
                }
            } else if (nextSqr.getY() == myBotModel.getY()) {
                if (nextSqr.getX() > myBotModel.getX()) {
                    reqAz = 0;
                } else if (nextSqr.getX() < myBotModel.getX()) {
                    reqAz = 180;
                }
            }
            //Поворачиваем
            difference = reqAz - myBotModel.getAzimut();
            if (difference != 0) {
                switch (Math.abs(difference)) {
                    case 90: {
                        if (difference < 0) myBotModel.turnRight();
                        else myBotModel.turnLeft();
                    }
                    break;
                    case 180: {
                        myBotModel.turnRight();
                        myBotModel.turnRight();

                    }
                    break;
                    case 270: {
                        if (difference < 0)
                            myBotModel.turnLeft();
                        else
                            myBotModel.turnRight();
                    }
                    break;
                }
                //Даем команду на старт
                myBotModel.goForward();
//                while (Math.abs(myBotModel.getX() - nextSqr.getX()) > err || Math.abs(myBotModel.getY() - nextSqr.getY()) > err) {//Условие - ошибка больше допустимой
//                    //просто крутим цикл, пока не доедем
//                }

                myBotModel.stop();
                curPoint = nextPoint;
                myBotModel.setX(squareList[curPoint].getX());
                myBotModel.setX(squareList[curPoint].getY());
            }

        }


    }

    public  static int[][] getMapFromImage(Mat img){
        //TODO: здесь будет перестрока mapArr в соответствии с переданным img
        int limitKoef = 20;
        int sideKoef = 5;
        int[][] retMap = null;
        try {
            retMap = getMapFromImage(img,limitKoef,sideKoef);
            return retMap;
        }catch (Exception ex){
            System.out.println("Something went wrong" + ex.getMessage());
            return retMap;
        }
    }

    /**
     * @param img       - Изображение, по которому строится картинка
     * @param limitKoef - Коэффициент, регулирующий количество точек, необходимо для признания зоны стенкой.
     *                  По умолчанию (значение передается нулевым или отрицательным) = 20. Чем коэффициент меньше,
     *                  тем больше белых точек нужно собрать алгоритму, чтобы признать зону стенкой.
     * @param sideKoef  - Коэффициент, регулирующий количество точек, которые будут просматривться
     *                  по вертикали (горизонтали) от найденной на линии, соединяющей центры квадратов,
     *                  белой точки. По умолчанию (значение передается нулевым или отрицательным)= 5. Чем коэффициент
     *                  меньше, тем больше точек обследуется по сторонам от основной линии.
     * @return - Возвращает карту, которая не пропорциональна лабиринту (требуется приведение)
     */
    public static int[][] getMapFromImage(Mat img, int limitKoef, int sideKoef) {
        byte[] data = new byte[img.height() * img.width() * img.channels()];
        int cols = img.cols();
        int rows = img.rows();
        int chan = img.channels();
        int bufferSize = chan * cols * rows;
        byte[] imgData = new byte[bufferSize];
        img.get(0, 0, imgData);
        int[][] centersOfSquares = new int[2][24];
        for (int i = 0; i < 24; i++) { //Находим центры квадратов в пикселях х,у
            centersOfSquares[0][i] = (int) (((i % 4) * 2 + 1) * cols / 8.0);
            centersOfSquares[1][i] = (int) (((int) (i / 4) * 2 + 1) * rows / 8.0);
        }
        if (limitKoef <= 0) {
            limitKoef = 20;
        }
        int limit = rows / limitKoef + cols / limitKoef; //Порог, после пересечения которого зона будет считаться стенкой.
        if (sideKoef <= 0) {
            sideKoef = 5;
        } else
            sideKoef = (rows / 40 + cols / 40) / sideKoef;
        int whitePixelSumm = 0; //Сумма белых пикселей, которая считается для каждой из 24-х стенок.
        boolean[] walls = new boolean[24];//Массив стенок: сначала идут вертикальные сверху донизу, затем, горизонтальные по тому же принципу
        int firstDot; //Номер точки, от которой ведется поиск
        int secondDot; //Номер точки, по направлению к которой ведется поиск
        int surveyX; // Координата по х, обследуемая в данный момент
        int surveyY; // Координата по у, обследуемая в данный момент
        for (int n = 0; n < 24; n++) {//перебираем стенки
            if (n < 12) {//Сначала вертикальные
                firstDot = n + (int) (n / 3);
                secondDot = firstDot + 1;
                whitePixelSumm = 0;
                surveyX = centersOfSquares[0][firstDot];
                surveyY = centersOfSquares[1][firstDot];
                walls[n] = false;
                outerloop:
                while (surveyX < centersOfSquares[0][secondDot]) {
                    if (getPixelValue(surveyX, surveyY, cols, chan, imgData))
                        for (int i = surveyY - (int) (sideKoef / 2.0); i < surveyY + (int) (sideKoef / 2.0); i++) {
                            if (getPixelValue(surveyX, i, cols, chan, imgData)) {
                                whitePixelSumm++;
                                if (whitePixelSumm >= limit) {
                                    walls[n] = true;
                                    break outerloop;
                                }
                            }
                        }
                    surveyX++;
                }
            } else {//Теперь горизонтальные
                firstDot = n - 12;
                secondDot = firstDot + 4;
                whitePixelSumm = 0;
                surveyX = centersOfSquares[0][firstDot];
                surveyY = centersOfSquares[1][firstDot];
                walls[n] = false;
                outerloop:
                while (surveyY < centersOfSquares[1][secondDot]) {
                    if (getPixelValue(surveyX, surveyY, cols, chan, imgData))
                        for (int i = surveyX - (int) (sideKoef / 2.0); i < surveyX + (int) (sideKoef / 2.0); i++) {
                            if (getPixelValue(i, surveyY, cols, chan, imgData)) {
                                whitePixelSumm++;
                                if (whitePixelSumm >= limit) {
                                    walls[n] = true;
                                    break outerloop;
                                }
                            }
                        }
                    surveyY++;
                }
            }

        }
        int[][] dispropMap = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };//массив MxN, содержащий карту лабиринта, представленную в 0-ях (проход) и 1-ах (стена)
        for (int n = 0; n < 24; n++) {//и снова перебираем стенки
            if (n < 12) {
                if (walls[n]) {//Ставим Вертикальные стенки
                    dispropMap[((int) (n / 3)) * 2 + 1][(n % 3 + 1) * 2] = 1;
                }
            } else {
                if (walls[n]) {//Ставим горизонтальные стенки
                    dispropMap[2 * (((int) ((n - 12) / 4)) + 1)][((n - 12) % 4) * 2 + 1] = 1;
                }
            }
        }
        return dispropMap;
    }

    /**
     * @param x        - Номер пикселя х
     * @param y        - Номер пикселя у
     * @param cols     - Количество пикслей в ряду изображения
     * @param channels - Количество каналов изображения
     * @param byteArr  - Массив байт изображения
     * @return - Возвращает true, если пиксель не черный
     */
    public static boolean getPixelValue(int x, int y, int cols, int channels, byte[] byteArr) {
        return ((byteArr[(x + cols * y) * channels + 1] != 0) || (byteArr[(x + cols * y) * channels + 2] != 0) || (byteArr[(x + cols * y) * channels + 3] != 0));
    }


}
