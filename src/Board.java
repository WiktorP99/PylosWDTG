import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;


public class Board extends JFrame {

    public int[][] level0;
    public int[][] level1;
    public int[][] level2;
    public int[][] level3;
    private final ArrayList<Coordinate> allCoordinates;

    public int whiteBalls;
    public int blackBalls;

    public int score =0;

    public Board() { //tworzenie tablicy i nadanie wartosc 0
        level0 = new int[4][4];
        level1 = new int[3][3];
        level2 = new int[2][2];
        level3 = new int[1][1];

        whiteBalls = 15;
        blackBalls = 15;
        allCoordinates = getAllCoordinates();
    }

    public Board(Board another) {
        level0 = new int[4][4];
        level1 = new int[3][3];
        level2 = new int[2][2];
        level3 = new int[1][1];
        allCoordinates = another.allCoordinates;
        setBoard(another);

    }

    public void setBoard(Board another) {
        for(int i = 0; i < another.level0.length; i++) level0[i] = Arrays.copyOf(another.level0[i], another.level0[i].length);
        for(int i = 0; i < another.level1.length; i++) level1[i] = Arrays.copyOf(another.level1[i], another.level1[i].length);
        for(int i = 0; i < another.level2.length; i++) level2[i] = Arrays.copyOf(another.level2[i], another.level2[i].length);
        for(int i = 0; i < another.level3.length; i++) level3[i] = Arrays.copyOf(another.level3[i], another.level3[i].length);

        this.whiteBalls = another.whiteBalls;
        this.blackBalls = another.blackBalls;
    }

    //zwraca tablice 2d z planszy dla danego poziomu
    public int[][] getLevelTable(int level) {
        switch(level) {
            case 0 :
                return level0;
            case 1 :
                return level1;
            case 2 :
                return level2;
            case 3 :
                return level3;
        }
        return null;
    }

    public boolean gameFinished() {
        return !(whiteBalls > 0 && blackBalls > 0 && level3[0][0] == 0);
    }

    private  ArrayList<Coordinate> getAllCoordinates() {
        ArrayList<Coordinate> all = new ArrayList<Coordinate>();
        String characters = "abcdefghij";
        int x = 0;
        for(int i = 0; i < characters.length(); i++) {
            for(int j = 1; j < 5-x; j++) {
                all.add(new Coordinate(characters.substring(i, i+1) + j, this));
            }
            //dla poziomu 1 plansza 3x3, poziom 2 to 2x2 ...
            if(i == 3 || i == 6 || i == 8 ) x++;
        }
        return all;
    }

    private  ArrayList<Move> getValidMoves(int player) {
        ArrayList<Move> validMoves = new ArrayList<Move>();
        //przejście przez wszystkie współrzędne i spwardzanie czy mozna dodac nową kule, jak tak to jest dodawana jako prawidłowy ruch
        for(Coordinate nc : allCoordinates) {
            Move m = new Move(new Board(this), PylosGame.Action.PLACE, player, nc, null);
            if(Move.checkValidMove(m)) validMoves.add(m);
        }
        // przejscie przez prawidlowe ruchy, przechodzi przez wszystkie współrzędne i sprawdza czy te z niższego poziomu mogą być przeniesione do współrzędnej PLACE
        int size = validMoves.size();
        for(int i = 0; i < size; i++) {
            Coordinate nc = validMoves.get(i).newCoordinate;
            for(Coordinate oc : allCoordinates) {
                if(nc.level > oc.level) {
                    Move m = new Move(new Board(this), PylosGame.Action.PROMOTE, player, nc, oc);
                    if(Move.checkValidMove(m)) validMoves.add(m);
                }
            }
        }
        return validMoves;
    }

    public ArrayList<Board> getPossibleBoards(int player) {
        //wyłączenie standardowego wyjscia zeby zatrzymac przenoszenie niepoprawnych wiadomosci
        enableStdout(false);
        ArrayList<Board> possibleBoards = new ArrayList<Board>();
        ArrayList<Move> validMoves = getValidMoves(player);

        int size = validMoves.size();
        for(int i = 0; i < size; i++) {
            Move m = validMoves.get(i);
            m.execute(m.gameBoard);
            possibleBoards.add(m.gameBoard);
            if(Move.checkSpecialMove(m)) {
                for(Coordinate oc : allCoordinates) {
                    Move deleteMove = new Move(new Board(m.gameBoard), PylosGame.Action.REMOVE, player, null, oc);
                    if(Move.checkValidMove(deleteMove)) {
                        deleteMove.execute(deleteMove.gameBoard);
                        possibleBoards.add(deleteMove.gameBoard);
                        for(Coordinate oc2 : allCoordinates) {
                            Move deleteMove2 = new Move(new Board(deleteMove.gameBoard), PylosGame.Action.REMOVE, player, null, oc2);
                            if(Move.checkValidMove(deleteMove2)) {
                                deleteMove2.execute(deleteMove2.gameBoard);
                                possibleBoards.add(deleteMove2.gameBoard);
                            }
                        }
                    }
                }

            }
        }
        enableStdout(true);
        return possibleBoards;
    }
    public static void enableStdout(Boolean on) {
        if(on) {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        } else {
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                }
            }));
        }
    }

    public int getBoardScore(int player) {
        int score = 0;
        score = (whiteBalls-blackBalls)*100;


        for(int k = 0; k < 4; k++) {
            int[][] levelTable = getLevelTable(k);
            int length = levelTable.length;
            for(int i = 0; i < length; i++) {
                int vcount = 0;
                for(int j = 0; j < length; j++) {
                    if(levelTable[i][j] == player) {
                        vcount++;
                    } else if(levelTable[i][j] == -player){
                        vcount = 0;
                        break;
                    }
                }
                score += vcount*20;
                for(int j = 0; j < length; j++) {
                    if(levelTable[j][i] == player) {
                        vcount++;
                    } else if(levelTable[i][j] == -player) {
                        vcount = 0;
                        break;
                    }
                }
                score += vcount*20;
            }
        }
        return score;
    }

    private void paintLevel(Graphics g, int size, int offset, int[][] level) {

        //rysowanie planszy
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(100+offset, 100, size*100, size*100);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(100+offset, 100, size*100, size*100);
        for(int x = 100+offset; x <= 100*size+offset; x+=200){
            for(int y = 100; y <= 100*size; y+=200){
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, 100, 100);

            }
        }
        for(int x = 200+offset; x <= 100*size+offset; x+=200){
            for(int y = 200; y <= 100*size; y+=200){
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, 100, 100);

            }
        }

        //rysowanie kul
        for(int i = 0; i<size; i++) {
            for(int j = 0; j<size; j++) {
                int player = level[i][j];
                if(player > 0){
                    g.setColor(Color.WHITE);
                    g.fillOval((i+1)*100+5+offset, (j+1)*100+5, 90, 90);
                    g.setColor(Color.BLACK);
                    g.drawOval((i+1)*100+5+offset, (j+1)*100+5, 90, 90);
                }
                else if(player < 0){
                    g.setColor(Color.BLACK);
                    g.fillOval((i+1)*100+5+offset, (j+1)*100+5, 90, 90);
                    g.drawOval((i+1)*100+5+offset, (j+1)*100+5, 90, 90);
                }
            }
        }
    }

    public void paint(Graphics g){

        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 14));
        int offset = 0;
        paintLevel(g, 4, 0, level0);
        g.setColor(Color.BLACK);
        g.drawString("a", 145, 80);
        g.drawString("b", 245, 80);
        g.drawString("c", 345, 80);
        g.drawString("d", 445, 80);
        g.drawString("1", 75, 150);
        g.drawString("2", 75, 250);
        g.drawString("3", 75, 350);
        g.drawString("4", 75, 450);
        g.drawString("Poziom 0", 280, 530);

        offset = 500;
        paintLevel(g, 3, offset, level1);
        g.setColor(Color.BLACK);
        g.drawString("e", 145+offset, 80);
        g.drawString("f", 245+offset, 80);
        g.drawString("g", 345+offset, 80);
        g.drawString("1", 75+offset, 150);
        g.drawString("2", 75+offset, 250);
        g.drawString("3", 75+offset, 350);
        g.drawString("Poziom 1", 730, 430);

        offset = 900;
        paintLevel(g, 2, offset, level2);
        g.setColor(Color.BLACK);
        g.drawString("h", 145+offset, 80);
        g.drawString("i", 245+offset, 80);
        g.drawString("1", 75+offset, 150);
        g.drawString("2", 75+offset, 250);
        g.drawString("Poziom 2", 1080, 330);

        offset = 1200;
        paintLevel(g, 1, offset, level3);
        g.setColor(Color.BLACK);
        g.drawString("j", 145+offset, 80);
        g.drawString("1", 75+offset, 150);
        g.drawString("Poziom 3", 1320, 230);


        //rysowanie tablicy z wynikiem
        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 18));
        g.drawString("Pozostałe kule ", 1320, 330);


        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 30));

        g.setColor(Color.WHITE);
        g.fillOval(1330, 350, 100, 100);

        g.setColor(Color.BLACK);
        g.fillOval(1330, 470, 100, 100);
        g.drawOval(1330, 350, 100, 100);

        g.drawString(Integer.toString(whiteBalls), 1364, 415);
        g.setColor(Color.WHITE);
        g.drawString(Integer.toString(blackBalls), 1364, 530);
    }

}