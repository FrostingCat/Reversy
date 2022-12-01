package allreverse;

import java.util.ArrayList;

public class Game
{
    Matrix startMap;
    private static ArrayList<Coord> advices = new ArrayList<>();
    private static ArrayList<Coord> purples = new ArrayList<>();
    private static ArrayList<Coord> blues = new ArrayList<>();
    private GameState state;
    int purple = 1;
    public GameState getState()
    {
        return state;
    }
    public Game (int cols, int rows)
    {
        Ranges.setSize(new Coord (cols, rows));
    }

    public void start ()
    {
        startMap = new Matrix(Box.CELL);
        startMap.set (new Coord (4, 4), Box.PURPLE);
        startMap.set (new Coord (3, 3), Box.PURPLE);
        startMap.set (new Coord (3, 4), Box.BLUE);
        startMap.set (new Coord (4, 3), Box.BLUE);
        startMap.set (new Coord (2, 4), Box.ADVICE);
        startMap.set (new Coord (3, 5), Box.ADVICE);
        startMap.set (new Coord (4, 2), Box.ADVICE);
        startMap.set (new Coord (5, 3), Box.ADVICE);
        purples.add (new Coord (4, 4));
        purples.add (new Coord (3, 3));
        blues.add (new Coord (3, 4));
        blues.add (new Coord (4, 3));
        advices.add (new Coord (2, 4));
        advices.add (new Coord (3, 5));
        advices.add (new Coord (4, 2));
        advices.add (new Coord (5, 3));
        state = GameState.PLAYING;
    }

    public Box getBox (Coord coord) // что будет в той или иной части экрана
    {
        return startMap.get(coord);
    }

    public void pressLeftButton (Coord coord)
    {
        //if (getBox(coord) == Box.ADVICE) {
        startMap.set(coord, Box.PURPLE);
        purples.add(new Coord(coord.x, coord.y));
        int i = 0;
        for (Coord check : advices)
        {
            if (check.x == coord.x && check.y == coord.y)
            {
                advices.remove(i);
                break;
            }
            i++;
        }

        checkIfBlue(coord, Box.BLUE, Box.PURPLE);
        placeBlue();
        if (purples.size() + blues.size() == 64) {
            checkWinner();
        }
        //checkAdvices();
        //changeAdvices();
        //}
    }
    public void pressLeftButtonHuman (Coord coord)
    {
        //if (getBox(coord) == Box.ADVICE)
        //{
        if (purple == 1)
        {
            startMap.set (coord, Box.PURPLE);
            purples.add (new Coord(coord.x, coord.y));
            checkIfBlue (coord, Box.BLUE, Box.PURPLE);
            purple = 0;
        } else {
            startMap.set (coord, Box.BLUE);
            blues.add (new Coord(coord.x, coord.y));
            checkIfBlue (coord, Box.PURPLE, Box.BLUE);
            purple = 1;
        }
        if (purples.size() + blues.size() == 64) {
            checkWinner ();
        }
    }

    public void checkWinner ()
    {
        if (purples.size() > blues.size())
        {
            state = GameState.WINNER;
        } else {
            state = GameState.LOST;
        }
        purples.clear();
        blues.clear();
        advices.clear();
    }

    public int getCount ()
    {
        if (state == GameState.WINNER) {
            return purples.size();
        } else {
            return blues.size();
        }
    }

    public double funcSum (int n, int isSide, int putCorner, int putSide)
    {
        double sum;
        sum = n * isSide + 0.8 * putCorner + 0.4 * putSide;
        return sum;
    }

    public void placeBlue ()
    {
        ArrayList<Double> allSums = new ArrayList<>();
        ArrayList<Coord> variant = new ArrayList<>();
        ArrayList<Coord> oneBlue = new ArrayList<>();
        ArrayList<Integer> cases = new ArrayList<>();
        double[] res = new double[9];
        for (int i = 0; i < blues.size(); i ++)
        {
            int x = blues.get(i).x;
            int y = blues.get(i).y;
            int countPurple = 0;
            int stop = 0;
            int isSide = 1;
            int putCorner = 0;
            int putSide = 0;
            // случай 1: слева сверху
            Coord coord = new Coord(x - 1, y - 1);
            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 0 || coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                coord.y -= 1;
                countPurple += 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if (coord.x == 0 && coord.y == 0) { // проверяем, угол ли
                    putCorner = 1;
                } else if (coord.x == 0 || coord.y == 0) { // проверяем, с краю ли (но не угол)
                    putSide = 1;
                }
                res[0] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 2: слева снизу
            coord.x = x - 1;
            coord.y = y + 1;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 0 || coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                coord.y += 1;
                countPurple += 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if (coord.x == 0 && coord.y == 7) {
                    putCorner = 1;
                } else if (coord.x == 0 || coord.y == 7) {
                    putSide = 1;
                }
                res[1] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 3: справа сверху
            coord.x = x + 1;
            coord.y = y - 1;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 7 || coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
                coord.y -= 1;
                countPurple += 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if (coord.x == 7 && coord.y == 0) {
                    putCorner = 1;
                } else if (coord.x == 7 || coord.y == 0) {
                    putSide = 1;
                }
                res[2] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 4: справа снизу
            coord.x = x + 1;
            coord.y = y + 1;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 7 || coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
                coord.y += 1;
                countPurple += 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if (coord.x == 7 && coord.y == 7) {
                    putCorner = 1;
                } else if (coord.x == 7 || coord.y == 7) {
                    putSide = 1;
                }
                res[3] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 5: сверху
            coord.x = x;
            coord.y = y - 1;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.y -= 1;
                countPurple += 1;
            }
            if (coord.y != y - 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if ((coord.x == 0 && coord.y == 0) || (coord.x == 7 && coord.y == 0)){
                    putCorner = 1;
                    isSide = 2;
                } else if (coord.x == 0 || coord.x == 7) {
                    putSide = 1;
                }
                res[4] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 6: слева
            coord.x = x - 1;
            coord.y = y;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 0) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                countPurple += 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if ((coord.x == 0 && coord.y == 0) || (coord.x == 0 && coord.y == 7)) {
                    putCorner = 1;
                    isSide = 2;
                } else if (coord.y == 0 || coord.y == 7) {
                    putSide = 1;
                }
                res[5] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 7: снизу
            coord.x = x;
            coord.y = y + 1;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.y += 1;
                countPurple += 1;
            }
            if (coord.y != y + 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if ((coord.x == 0 && coord.y == 7) || (coord.x == 7 && coord.y == 7)) {
                    putCorner = 1;
                    isSide = 2;
                } else if (coord.x == 0 || coord.x == 7) {
                    putSide = 1;
                }
                res[6] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            // случай 8: справа
            coord.x = x + 1;
            coord.y = y;
            stop = 0;
            putCorner = 0;
            putSide = 0;
            countPurple = 0;

            while (getBox(coord) == Box.PURPLE)
            {
                if (coord.x == 7) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
                countPurple += 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if ((coord.x == 7 && coord.y == 0) || (coord.x == 7 && coord.y == 7)) {
                    putCorner = 1;
                    isSide = 2;
                } else if (coord.y == 0 || coord.y == 7) {
                    putSide = 1;
                }
                res[7] = funcSum(countPurple, isSide, putCorner, putSide);
            }
            oneBlue.add(new Coord(coord.x, coord.y));
            double maxNum = res[0]; // максимальное значение у одной фишки
            int maxPlace = 0;
            for (int j = 0; j < 9; j ++) {
                if (res[j] > maxNum) {
                    maxNum = res[j];
                    maxPlace = j;
                }
            }
            variant.add(oneBlue.get(maxPlace));
            allSums.add(maxNum);
            cases.add(maxPlace);
            oneBlue.clear();
        }
        double max = allSums.get(0);
        int maxPlace = 0;
        for (int j = 0; j < allSums.size(); j ++) {
            if (allSums.get(j) > max) {
                max = allSums.get(j);
                maxPlace = j;
            }
        }
        if (max > 0)
        {
            startMap.set (variant.get(maxPlace), Box.BLUE);
            placeAllBlues(cases.get(maxPlace), variant.get(maxPlace));
        }
        allSums.clear();
        variant.clear();
        oneBlue.clear();
        cases.clear();
    }

    public void placeAllBlues (Integer oneCase, Coord coord)
    {
        int x, y;
        if (oneCase == 0) {
            x = - 1;
            y = - 1;
        } else if (oneCase == 1) {
            x = - 1;
            y = 1;
        } else if (oneCase == 2) {
            x = 1;
            y = - 1;
        } else if (oneCase == 3) {
            x = 1;
            y = 1;
        } else if (oneCase == 4) {
            x = 0;
            y = - 1;
        } else if (oneCase == 5) {
            x = - 1;
            y = 0;
        } else if (oneCase == 6) {
            x = 0;
            y = 1;
        } else {
            x = 1;
            y = 0;
        }
        addSecond (Box.BLUE, coord);
        coord.x -= x;
        coord.y -= y;
        while (getBox(coord) == Box.PURPLE)
        {
            startMap.set (coord, Box.BLUE);
            addSecond (Box.BLUE, coord);
            coord.x -= x;
            coord.y -= y;
        }
    }

    public void checkIfBlue (Coord check, Box first, Box intoSecond)
    {
        int x = check.x;
        int y = check.y;
        int stop = 0;

        // случай 1: слева сверху
        Coord coord = new Coord(x - 1, y - 1);
        while (getBox(coord) == first)
        {
            if (coord.x == 0 || coord.y == 0) {
                stop = 1;
                break;
            }
            coord.x -= 1;
            coord.y -= 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x - 1) {
            coord.x = x - 1;
            coord.y = y - 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x -= 1;
                coord.y -= 1;
            }
        }
        // случай 2: слева снизу
        coord.x = x - 1;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.x == 0 || coord.y == 7) {
                stop = 1;
                break;
            }
            coord.x -= 1;
            coord.y += 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x - 1) {
            coord.x = x - 1;
            coord.y = y + 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x -= 1;
                coord.y += 1;
            }
        }
        // случай 3: справа сверху
        coord.x = x + 1;
        coord.y = y - 1;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.x == 7 || coord.y == 0) {
                stop = 1;
                break;
            }
            coord.x += 1;
            coord.y -= 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y - 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x += 1;
                coord.y -= 1;
            }
        }
        // случай 4: справа снизу
        coord.x = x + 1;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.x == 7 || coord.y == 7) {
                stop = 1;
                break;
            }
            coord.x += 1;
            coord.y += 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y + 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x += 1;
                coord.y += 1;
            }
        }
        // случай 5: сверху
        coord.x = x;
        coord.y = y - 1;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.y == 0) {
                stop = 1;
                break;
            }
            coord.y -= 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.y != y - 1) {
            coord.x = x;
            coord.y = y - 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.y -= 1;
            }
        }
        // случай 6: слева
        coord.x = x - 1;
        coord.y = y;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.x == 0) {
                stop = 1;
                break;
            }
            coord.x -= 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x - 1) {
            coord.x = x - 1;
            coord.y = y;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x -= 1;
            }
        }
        // случай 7: снизу
        coord.x = x;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.y == 7) {
                stop = 1;
                break;
            }
            coord.y += 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.y != y - 1) {
            coord.x = x;
            coord.y = y + 1;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.y += 1;
            }
        }
        // случай 8: справа
        coord.x = x + 1;
        coord.y = y;
        stop = 0;

        while (getBox(coord) == first)
        {
            if (coord.x == 7) {
                stop = 1;
                break;
            }
            coord.x += 1;
        }
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y;
            while (getBox(coord) == first)
            {
                startMap.set (coord, intoSecond);
                addSecond (intoSecond, coord);
                coord.x += 1;
            }
        }
    }

    public void addSecond (Box addInto, Coord coord)
    {
        if (addInto == Box.PURPLE) {
            purples.add(new Coord(coord.x, coord.y));
            int i = 0;
            for (Coord check : blues)
            {
                if (check.x == coord.x && check.y == coord.y)
                {
                    blues.remove(i);
                    break;
                }
                i++;
            }
        } else {
            blues.add(new Coord(coord.x, coord.y));
            int j = 0;
            for (Coord check : purples)
            {
                if (check.x == coord.x && check.y == coord.y)
                {
                    purples.remove(j);
                    break;
                }
                j++;
            }
        }
    }

    public void checkAdvices ()
    {
        for (int i = 0; i < blues.size(); i ++)
        {
            int x = blues.get(i).x;
            int y = blues.get(i).y;
            int stop = 0;
            // случай 1: слева сверху
            Coord coord = new Coord(x - 1, y - 1);
            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 0 || coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                coord.y -= 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x + 1, y + 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 2: слева снизу
            coord.x = x - 1;
            coord.y = y + 1;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 0 || coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                coord.y += 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x + 1, y - 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 3: справа сверху
            coord.x = x + 1;
            coord.y = y - 1;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 7 || coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
                coord.y -= 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x - 1, y + 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 4: справа снизу
            coord.x = x + 1;
            coord.y = y + 1;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 7 || coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
                coord.y += 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x - 1, y - 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 5: сверху
            coord.x = x;
            coord.y = y - 1;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.y -= 1;
            }
            if (coord.y != y - 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x, y + 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 6: слева
            coord.x = x - 1;
            coord.y = y;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 0) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
            }
            if (coord.x != x - 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x + 1, y);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 7: снизу
            coord.x = x;
            coord.y = y + 1;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.y == 7) {
                    stop = 1;
                    break;
                }
                coord.y += 1;
            }
            if (coord.y != y + 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x, y - 1);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
            // случай 8: справа
            coord.x = x + 1;
            coord.y = y;
            stop = 0;

            while (getBox(coord) == Box.BLUE)
            {
                if (coord.x == 7) {
                    stop = 1;
                    break;
                }
                coord.x += 1;
            }
            if (coord.x != x + 1 && stop != 1 && getBox(coord) == Box.PURPLE) {
                Coord check = new Coord(x - 1, y);
                startMap.set (check, Box.ADVICE);
                advices.add(new Coord(check.x, check.y));
            }
        }
    }

    public void changeAdvices ()
    {
        for (Coord coord : advices)
        {
            startMap.set (coord, Box.CELL);
        }
    }
}
