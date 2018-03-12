package Labitint;
import Car.BotModel;

import java.io.FileWriter;
import java.io.IOException;


public class CrazyFactory {
    public static boolean EnterPresed;
    //int[][] mapArr - массив MxN, содержащий карту лабиринта, представленную в 0-ях (проход) и 1-ах (стена)
    //Square beginingSqr - клетка, содержащая x и y начальной точки
    //Square endingSqr - клетка, содержащая x и y точки выхода из лабиринта
    //boolean diagonalIsAllowed - признак разрешения (true) или запрета (fslse) на поиск пути по диагонали
    public static Square[] runWaveAlgorithm(int[][] mapArr, Square beginingSqr, Square endingSqr, boolean diagonalIsAllowed) {
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
        for (int i = 0; i < mapArr.length; i++) {
            for (int j = 0; j < mapArr[i].length; j++) {
                if (mapArr[i][j] > 9) {
                    System.out.print(mapArr[i][j] + " ");
                } else {
                    System.out.print(mapArr[i][j] + "  ");
                }

            }
            System.out.println();
        }
        System.out.println();

// Формируем список squareList - последовательный лист клеток кратчайшего пути
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
        for (Square someSquare : squareList) {
            System.out.println("x : " + someSquare.getX());
            System.out.println("y : " + someSquare.getY());
            System.out.println();

        }

        String fileName = "WaweAlgorithmResult.txt";
        try {
            FileWriter writer = new FileWriter(fileName,false);
            String curStr = " ";
            for (Square someSquare : squareList) {
            curStr = "x : " + someSquare.getX() + "     y : " + someSquare.getY() + "\n";
                writer.write(curStr);
            }
            writer.flush();
            writer.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return squareList;
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
            CrazyFactory.EnterPresed = false;
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
                while (!CrazyFactory.EnterPresed){

                }
                CrazyFactory.EnterPresed = false;
                myBotModel.stop();
                curPoint = nextPoint;
                myBotModel.setX(squareList[curPoint].getX());
                myBotModel.setX(squareList[curPoint].getY());
            }

        }


    }

}
