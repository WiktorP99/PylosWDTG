import java.util.ArrayList;


public class AI {
    final static int MAX = 1;
    final static int MIN = -1;

    public Board bestBoard(Board state, int depth, int player) {
        return ab(depth, state, Integer.MIN_VALUE, Integer.MAX_VALUE, player);

    }

    private Board ab(final int depth, Board b, int alpha, int beta, int player) {

        if (depth == 0) {
            b.score = b.getBoardScore(-player);
            return b;
        }
        if(player == MAX) {
            Board v = new Board(b);
            v.score = Integer.MIN_VALUE;
            ArrayList<Board> boards = b.getPossibleBoards(player);
            for (Board currentBoard: boards) {
                currentBoard.score = currentBoard.getBoardScore(player);

                Board result = ab(depth - 1, currentBoard, beta, alpha, -player);

                if (v.score < result.score) {
                    v = currentBoard;
                }
                if(alpha < v.score) {
                    alpha = v.score;
                }
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        }
        else {
            Board v = new Board(b);
            v.score = Integer.MAX_VALUE;
            ArrayList<Board> boards = b.getPossibleBoards(player);
            for (Board currentBoard: boards) {
                currentBoard.score = currentBoard.getBoardScore(player);
                Board result = ab(depth - 1, currentBoard, beta, alpha, -player);
                if (v.score > result.score) {
                    v = currentBoard;
                }
                if(beta > v.score) {
                    beta = v.score;
                }
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        }
    }

}