import java.awt.Color;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.JFrame;

public class PylosGame {

    final static int WHITE = 1;
    final static int BLACK = -1;
    public int currentPlayer;
    public Board gameBoard;
    private final Scanner userInput;
    private AI player;
    private AI player2;

    public PylosGame() {
        currentPlayer = 1;
        gameBoard = new Board();
        gameBoard.setSize(1500,600);
        gameBoard.setLocationRelativeTo(null);
        gameBoard.setBackground(Color.WHITE);
        gameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameBoard.setVisible(true);
        gameBoard.setLayout(new GridLayout(4,4));

        userInput = new Scanner(System.in);
        player = new AI();
        player2 = new AI();
    }

    public enum Action {
        PLACE, PROMOTE, REMOVE
    }
    public void idle() {
        while(!gameBoard.gameFinished())
        {
            if(currentPlayer == WHITE)
            {
                AIMove(player2);
            }

            else
            {
                AIMove2(player);
            }
        }
        userInput.close();
    }
    private void AIMove(AI p) {
        Board.enableStdout(false);
        gameBoard.setBoard(p.bestBoard(gameBoard, 3, currentPlayer));
        currentPlayer = -currentPlayer;
        gameBoard.repaint();
        Board.enableStdout(true);
    }
    private void AIMove2 (AI p) {
        Board.enableStdout(false);
        gameBoard.setBoard(p.bestBoard(gameBoard, 3, currentPlayer));
        currentPlayer = -currentPlayer;
        gameBoard.repaint();
        Board.enableStdout(true);
    }
    public static void main(String[] args) {
        PylosGame game = new PylosGame();
        game.idle();
        System.exit(0);
    }

}