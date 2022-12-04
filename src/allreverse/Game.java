package allreverse;

import allreverse.Box;
import allreverse.Constants;
import allreverse.GameState;
import allreverse.Ranges;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class Game {
    Matrix startMap;
    private final ArrayList<Coord> advices = new ArrayList<>();
    private final ArrayList<Coord> advicesBlue = new ArrayList<>();
    private final ArrayList<Coord> advicesPurple = new ArrayList<>();
    private final ArrayList<Coord> purples = new ArrayList<>();
    private final ArrayList<Coord> blues = new ArrayList<>();
    private GameState state;
    private boolean purple = true;
    public boolean buttonPressed = false;
    public GameState getState()
    {
        return state;
    }
    public Game (int cols, int rows)
    {
        Ranges.setSize(new Coord (cols, rows));
    }

    public void start (String string) {
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
        state = GameState.PLAYING;
        if (Objects.equals(string, "Human"))
        {
            advicesPurple.add (new Coord (2, 4));
            advicesPurple.add (new Coord (3, 5));
            advicesPurple.add (new Coord (4, 2));
            advicesPurple.add (new Coord (5, 3));
        } else {
            advices.add (new Coord (2, 4));
            advices.add (new Coord (3, 5));
            advices.add (new Coord (4, 2));
            advices.add (new Coord (5, 3));
        }
    }

    public Box getBox(Coord coord) // что будет в той или иной части экрана
    {
        return startMap.get(coord);
    }

    public void pressLeftButton(Coord coord)
    {
        if (advices.size() != 0)
        {
            if (getBox(coord) == Box.ADVICE) {
                mapButton(coord, advices);
                changeAdvices(advices);
                checkIfBlue(coord, Box.BLUE, Box.PURPLE);
                buttonPressed = true;
            }
        }
    }

    public void createBlue(CompletionHandler completionHandler) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                placeBlue();
                checkAdvices(blues, advices, Box.BLUE, Box.PURPLE);
                for (Coord check : advices)
                {
                    startMap.set (check, Box.ADVICE);
                }
                if (purples.size() + blues.size() == 63) {
                    checkWinner();
                }
                if (purples.isEmpty() || blues.isEmpty()){
                    endGame();
                }
                if (advices.isEmpty() && Game.this.getState() == GameState.PLAYING) {
                    createBlue(completionHandler);
                }
                buttonPressed = false;
                completionHandler.handle();
            }
        }.start();
    }

    private void mapButton(Coord coord, ArrayList<Coord> advices) {
        startMap.set(coord, Box.PURPLE);
        mapButtonCheck(coord, advices, purples);
    }

    private void mapButtonCheck(Coord coord, ArrayList<Coord> advices, ArrayList<Coord> purples) {
        removeAdvice(coord, advices, purples);
    }

    private void removeAdvice(Coord coord, ArrayList<Coord> advices, ArrayList<Coord> purples) {
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
    }

    public void pressLeftButtonHuman(Coord coord) {
        if (getBox(coord) == Box.ADVICE) {
            if (purple) {
                if (!advicesPurple.isEmpty()) {
                    mapButton(coord, advicesPurple);
                    checkIfBlue(coord, Box.BLUE, Box.PURPLE);
                    checkAdvices(purples, advicesBlue, Box.PURPLE, Box.BLUE);
                    changeAdvices(advicesPurple);
                    purple = false;
                    for (Coord check : advicesBlue) {
                        startMap.set(check, Box.ADVICE);
                    }
                } if (advicesBlue.isEmpty()) {
                    checkAdvices(blues, advicesPurple, Box.BLUE, Box.PURPLE);
                    changeAdvices(advicesBlue);
                    purple = true;
                    for (Coord check : advicesPurple) {
                        startMap.set(check, Box.ADVICE);
                    }
                }
            } else {
                if (!advicesBlue.isEmpty()) {
                    startMap.set(coord, Box.BLUE);
                    mapButtonCheck(coord, advicesBlue, blues);
                    checkIfBlue(coord, Box.PURPLE, Box.BLUE);
                    checkAdvices(blues, advicesPurple, Box.BLUE, Box.PURPLE);
                    changeAdvices(advicesBlue);
                    purple = true;
                    for (Coord check : advicesPurple) {
                        startMap.set(check, Box.ADVICE);
                    }
                }
                if (advicesPurple.isEmpty()) {
                    checkAdvices(purples, advicesBlue, Box.PURPLE, Box.BLUE);
                    changeAdvices(advicesPurple);
                    purple = false;
                    for (Coord check : advicesBlue) {
                        startMap.set(check, Box.ADVICE);
                    }
                }
            }
            if (advicesBlue.isEmpty() && advicesPurple.isEmpty()) {
                endGame();
            }
            if (purples.isEmpty() || blues.isEmpty()) {
                endGame();
            }
            if (purples.size() + blues.size() == 64) {
                checkWinner();
            }
        }
    }
    private void endGame()
    {
        state = GameState.NOONE;
    }

    private void checkWinner() {
        if (purples.size() > blues.size()) {
            state = GameState.WINNER;
        } else {
            state = GameState.LOSER;
        }
    }

    public int getCount() {
        if (state == GameState.WINNER) {
            return purples.size();
        } else {
            return blues.size();
        }
    }

    private double funcSum (int n, int isSide, int putCorner, int putSide) {
        double sum;
        sum = n * isSide + 0.8 * putCorner + 0.4 * putSide;
        return sum;
    }

    private void placeBlue () {
        ArrayList<Double> allSums = new ArrayList<>();
        ArrayList<Coord> variant = new ArrayList<>();
        ArrayList<Coord> oneBlue = new ArrayList<>();
        ArrayList<Integer> cases = new ArrayList<>();
        double[] res = new double[9];
        for (Coord blue : blues) {
            int x = blue.x;
            int y = blue.y;
            int countPurple = 0;
            int stop = 0;
            int isSide = 1;
            int putCorner = 0;
            int putSide = 0;
            // случай 1: слева сверху
            Coord coord = new Coord(x - 1, y - 1);
            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
                if (coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.y -= 1;
                countPurple += 1;
            }
            if (coord.y != y - 1 && stop != 1 && getBox(coord) != Box.BLUE) {
                if ((coord.x == 0 && coord.y == 0) || (coord.x == 7 && coord.y == 0)) {
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

            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
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

            while (getBox(coord) == Box.PURPLE) {
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
            for (int j = 0; j < 9; j++) {
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

    private void placeAllBlues (Integer oneCase, Coord coord) {
        int x, y;
        if (oneCase == 0) {
            x = -1;
            y = -1;
        } else if (oneCase == 1) {
            x = -1;
            y = 1;
        } else if (oneCase == 2) {
            x = 1;
            y = -1;
        } else if (oneCase == 3) {
            x = 1;
            y = 1;
        } else if (oneCase == 4) {
            x = 0;
            y = -1;
        } else if (oneCase == 5) {
            x = -1;
            y = 0;
        } else if (oneCase == 6) {
            x = 0;
            y = 1;
        } else {
            x = 1;
            y = 0;
        }
        addSecond(Box.BLUE, coord);
        coord.x -= x;
        coord.y -= y;
        while (getBox(coord) == Box.PURPLE) {
            startMap.set (coord, Box.BLUE);
            addSecond(Box.BLUE, coord);
            coord.x -= x;
            coord.y -= y;
        }
    }

    private void checkIfBlue (Coord check, Box first, Box intoSecond) {
        int x = check.x;
        int y = check.y;
        int stop = 0;

        // случай 1: слева сверху
        Coord coord = new Coord(x - 1, y - 1);
        while (getBox(coord) == first) {
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
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x -= 1;
                coord.y -= 1;
            }
        }
        // случай 2: слева снизу
        stop = secCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x - 1) {
            coord.x = x - 1;
            coord.y = y + 1;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x -= 1;
                coord.y += 1;
            }
        }
        // случай 3: справа сверху
        stop = thirdCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y - 1;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x += 1;
                coord.y -= 1;
            }
        }
        // случай 4: справа снизу
        stop = fourthCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y + 1;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x += 1;
                coord.y += 1;
            }
        }
        // случай 5: сверху
        stop = fifthCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.y != y - 1) {
            coord.x = x;
            coord.y = y - 1;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.y -= 1;
            }
        }
        // случай 6: слева
        stop = sixthCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x - 1) {
            coord.x = x - 1;
            coord.y = y;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x -= 1;
            }
        }
        // случай 7: снизу
        stop = seventhCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.y != y - 1) {
            coord.x = x;
            coord.y = y + 1;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.y += 1;
            }
        }
        // случай 8: справа
        stop = eighthCase(first, x, y, coord);
        if (getBox(coord) == intoSecond && stop != 1 && coord.x != x + 1) {
            coord.x = x + 1;
            coord.y = y;
            while (getBox(coord) == first) {
                startMap.set(coord, intoSecond);
                addSecond(intoSecond, coord);
                coord.x += 1;
            }
        }
    }

    private int eighthCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x + 1;
        coord.y = y;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.x == 7) {
                stop = 1;
                break;
            }
            coord.x += 1;
        }
        return stop;
    }

    private int seventhCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.y == 7) {
                stop = 1;
                break;
            }
            coord.y += 1;
        }
        return stop;
    }

    private int sixthCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x - 1;
        coord.y = y;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.x == 0) {
                stop = 1;
                break;
            }
            coord.x -= 1;
        }
        return stop;
    }

    private int fifthCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x;
        coord.y = y - 1;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.y == 0) {
                stop = 1;
                break;
            }
            coord.y -= 1;
        }
        return stop;
    }

    private int fourthCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x + 1;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.x == 7 || coord.y == 7) {
                stop = 1;
                break;
            }
            coord.x += 1;
            coord.y += 1;
        }
        return stop;
    }

    private int thirdCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x + 1;
        coord.y = y - 1;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.x == 7 || coord.y == 0) {
                stop = 1;
                break;
            }
            coord.x += 1;
            coord.y -= 1;
        }
        return stop;
    }

    private int secCase(Box first, int x, int y, Coord coord) {
        int stop;
        coord.x = x - 1;
        coord.y = y + 1;
        stop = 0;

        while (getBox(coord) == first) {
            if (coord.x == 0 || coord.y == 7) {
                stop = 1;
                break;
            }
            coord.x -= 1;
            coord.y += 1;
        }
        return stop;
    }

    private void addSecond (Box addInto, Coord coord) {
        if (addInto == Box.PURPLE) {
            removeAdvice(coord, blues, purples);
        } else {
            mapButtonCheck(coord, purples, blues);
        }
    }
    private void checkBox (Coord check, ArrayList<Coord> adv) {
        if (getBox(check) == Box.CELL || getBox(check) == Box.ADVICE)
        {
            int contains = 0;
            for (Coord one : adv) {
                if (check.x == one.x && check.y == one.y) {
                    contains = 1;
                    break;
                }
            }
            if (contains == 0) {
                adv.add(new Coord(check.x, check.y));
            }
        }
    }

    private boolean checkInRange(Coord coord)
    {
        return 0 <= coord.x && coord.x <= 7 && 0 <= coord.y && coord.y <= 7;
    }

    private void checkAdvices (ArrayList<Coord> dots, ArrayList<Coord> adv, Box colorfrom, Box colorinto) {
        for (Coord dot : dots) {
            int x = dot.x;
            int y = dot.y;
            int stop = 0;
            // случай 1: слева сверху
            Coord coord = new Coord(x - 1, y - 1);
            while (getBox(coord) == colorfrom) {
                if (coord.x == 0 || coord.y == 0) {
                    stop = 1;
                    break;
                }
                coord.x -= 1;
                coord.y -= 1;
            }
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x + 1, y + 1);
                checkBox(check, adv);
            }
            // случай 2: слева снизу
            stop = secCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x + 1, y - 1);
                checkBox(check, adv);
            }
            // случай 3: справа сверху
            stop = thirdCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x - 1, y + 1);
                checkBox(check, adv);
            }
            // случай 4: справа снизу
            stop = fourthCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x - 1, y - 1);
                checkBox(check, adv);
            }
            // случай 5: сверху
            stop = fifthCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x, y + 1);
                checkBox(check, adv);
            }
            // случай 6: слева
            stop = sixthCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x + 1, y);
                checkBox(check, adv);
            }
            // случай 7: снизу
            stop = seventhCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x, y - 1);
                checkBox(check, adv);
            }
            // случай 8: справа
            stop = eighthCase(colorfrom, x, y, coord);
            if (stop != 1 && checkInRange(coord) && getBox(coord) == colorinto) {
                Coord check = new Coord(x - 1, y);
                checkBox(check, adv);
            }
        }
    }

    private void changeAdvices (ArrayList<Coord> list) {
        for (Coord coord : list) {
            startMap.set(coord, Box.CELL);
        }
        list.clear();
    }
}
