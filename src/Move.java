public class Move {

    public Board gameBoard;
    public PylosGame.Action action;
    public int player;
    public Coordinate newCoordinate = null;
    public Coordinate oldCoordinate = null;

    Move(Board b, PylosGame.Action a, int p, Coordinate nc, Coordinate oc) {
        gameBoard = b;
        action = a;
        player = p;
        newCoordinate = nc;
        oldCoordinate = oc;
    }


    private void updateSpheres(int change) {
        if(player == PylosGame.BLACK) {
            gameBoard.blackBalls += change;
        } else gameBoard.whiteBalls += change;

    }

    public boolean execute(Board b) {
        gameBoard = b;
        switch(this.action) {
            case PLACE :
                updateSpheres(-1);
                return place(this);
            case PROMOTE :
                return promote(this);
            case REMOVE :
                updateSpheres(1);
                return remove(this);
        }
        return false;
    }

    private static boolean place(Move m) {
        try {
            setSquare(m.gameBoard, m.newCoordinate, m.player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean promote(Move m) {
        try {
            setSquare(m.gameBoard, m.newCoordinate, m.player);
            setSquare(m.gameBoard, m.oldCoordinate, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean remove(Move m) {
        try {
            setSquare(m.gameBoard, m.oldCoordinate, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //sprawdzanie czy wspolrzedne są poprawne
    public static boolean checkValidMove(Move m) {
        boolean valid = false;
        if(m.newCoordinate != null) {
            //Pionek awansuje, sprawdzanie czy nowy poziom jest wyższy od poprzedniego
            if(m.oldCoordinate != null) {
                if(m.newCoordinate.level <= m.oldCoordinate.level) {
                    return false;
                }
            }
            //sprawdzanie czy kratka jest zajęta
            if(m.gameBoard.getLevelTable(m.newCoordinate.level)[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex] != 0) {
                return false;
            }
            //Jeśli jest wolna to sprawdzenie czy nie potrzeba innych kul
            if(m.newCoordinate.level > 0) {
                //sprawdza czy są 4 kule
                if(canPlaceOnTop(m)) {
                    valid = true;
                } else {
                    return false;
                }

            }
            //dodanie kuli na poziom 0 bez awansu
            valid = true;
        }

        if(m.oldCoordinate != null) {
            ///pobranie wartości kratki
            int spherePlayer = m.gameBoard.getLevelTable(m.oldCoordinate.level)[m.oldCoordinate.firstIndex][m.oldCoordinate.secondIndex];
            //sprawdzenie czy kula na danej kratce może być przeniesiony albo usunięty
            if(spherePlayer == 0) {
                return false;
            }
            if(spherePlayer != m.player) {
                return false;
            }
            //sprawdzenie czy na poziomie wyżej nie ma kulek blokujących usunięcie
            if(m.oldCoordinate.level < 3) {
                if(valid = freeAbove(m)) {
                }
            }
        }
        return valid;
    }

    //sprawdzenie czy poziom niżej są cztery kule
    private static boolean canPlaceOnTop(Move m) {

        int[][] levelBelow = m.gameBoard.getLevelTable(m.newCoordinate.level-1);
        //sorawdzenie czy kula którą poruszamy jest jedną z kul podtrzymujących
        if(m.oldCoordinate != null) {
            if(m.oldCoordinate.firstIndex == m.newCoordinate.firstIndex) {
                if(m.oldCoordinate.secondIndex == m.newCoordinate.secondIndex || m.oldCoordinate.secondIndex == m.newCoordinate.secondIndex + 1) return false;
            }
            if(m.oldCoordinate.firstIndex == m.newCoordinate.firstIndex + 1)
                if(m.oldCoordinate.secondIndex == m.newCoordinate.secondIndex || m.oldCoordinate.secondIndex == m.newCoordinate.secondIndex + 1) return false;
        }
        if(levelBelow[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex] != 0) {
            if(levelBelow[m.newCoordinate.firstIndex+1][m.newCoordinate.secondIndex] != 0) {
                if(levelBelow[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex+1] != 0) {
                    if(levelBelow[m.newCoordinate.firstIndex+1][m.newCoordinate.secondIndex+1] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //sprawdzenie czy na poziomie powyżej są kule które uniemożliwiają przesuwanie kuli
    private static boolean freeAbove(Move m) {
        //wartość powyżej przenoszonej albo usuwanej kuli
        int[][] levelAbove = m.gameBoard.getLevelTable(m.oldCoordinate.level+1);
        for(int i = 0; i < levelAbove.length; i++) {
            for(int j = 0; j < levelAbove.length; j++) {
                if(levelAbove[i][j] != 0) {
                    if(i == m.oldCoordinate.firstIndex && j == m.oldCoordinate.secondIndex) {
                        return false;
                    }
                    else if(i+1 == m.oldCoordinate.firstIndex && j == m.oldCoordinate.secondIndex) {
                        return false;
                    }
                    else if(i == m.oldCoordinate.firstIndex && j+1 == m.oldCoordinate.secondIndex) {
                        return false;
                    }
                    else if(i+1 == m.oldCoordinate.firstIndex && j+1 == m.oldCoordinate.secondIndex) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean checkSpecialMove(Move m) {
        int[][] levelTable = m.gameBoard.getLevelTable(m.newCoordinate.level);
        boolean special = false;

        //tymczasowe umieszczenie kuli na planszy
        int origValue = levelTable[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex];
        levelTable[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex] = m.player;

        //sprawdzenie kratki
        for(int i = 0; i < levelTable.length-1; i++) {
            for(int j = 0; j < levelTable.length-1; j++) {
                //sprawdzenie czy kratka jest zajeta przez gracza
                if(levelTable[i][j] == m.player) {
                    //sprawdzenie czy kula która została umieszczona znajduje sie w kwadracie
                    if((i == m.newCoordinate.firstIndex) && (j == m.newCoordinate.secondIndex)
                            || (i+1 == m.newCoordinate.firstIndex) && (j+1 == m.newCoordinate.secondIndex)
                            || (i+1 == m.newCoordinate.firstIndex) && (j == m.newCoordinate.secondIndex)
                            || (i == m.newCoordinate.firstIndex) && (j+1 == m.newCoordinate.secondIndex)) {
                        if(levelTable[i+1][j] == m.player && levelTable[i][j+1] == m.player && levelTable[i+1][j+1] == m.player) {
                            special = true;
                        }
                    }

                }
            }
        }
        //Sprawdzenie prostych linii
        int hcount = 0;
        int vcount = 0;
        for(int i = 0; i < levelTable.length; i++) {
            if(levelTable[m.newCoordinate.firstIndex][i] == m.player) {
                vcount++;
            }
            if(levelTable[i][m.newCoordinate.secondIndex] == m.player) {
                hcount++;
            }
        }
        if(hcount == levelTable.length || vcount == levelTable.length) {
            special = true;
        }
        //usuniecie tymczasowo dodanej kuli
        levelTable[m.newCoordinate.firstIndex][m.newCoordinate.secondIndex] = origValue;
        return special;
    }

    private static void setSquare(Board b, Coordinate nc, int value) {
        int[][] level = b.getLevelTable(nc.level);
        level[nc.firstIndex][nc.secondIndex] = value;
        b.repaint();
    }
}
