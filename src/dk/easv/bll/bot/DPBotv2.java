package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.*;

/**
 * @Author Dennispc-bit
 */

public class DPBotv2 implements IBot {
    String[][] macro;
    String[][] microBoard;
    String[][] board;
    List<int[]> macroPrio = Arrays.asList(new int[]{1, 1},
            new int[]{0, 0}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 2}
            , new int[]{1, 2}, new int[]{2, 1}, new int[]{0, 1}, new int[]{1, 0});

    List<int[]> microPrio = Arrays.asList(
            new int[]{0, 0}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 2}, new int[]{1, 1}
            , new int[]{1, 2}, new int[]{2, 1}, new int[]{0, 1}, new int[]{1, 0});

    private static final String BOTNAME = "DP Bot V2";

    @Override
    public IMove doMove(IGameState state) {
        int player = state.getMoveNumber() % 2;
        macro = state.getField().getMacroboard();
        board = state.getField().getBoard();
        for (int a = 0; a < macroPrio.size(); a++) {
            int i = macroPrio.get(a)[0];
            int j = macroPrio.get(a)[1];

            if (i == 1 && j == 1 && board[j * 3 + 1][i * 3 + 1] == ".") {
                System.out.println(1);
                return new Move(j * 3 + 1, i * 3 + 1);
            } else {
                for (int b = 0; b < microPrio.size(); b++) {
                    int n = microPrio.get(b)[0];
                    int m = microPrio.get(b)[1];

                    microBoard = new String[3][3];

                    if (macro[j][i].equals("-1")) {
                        for (int l = 0; l < 3; l++) {
                            for (int k = 0; k < 3; k++) {
                                microBoard[k][l] = board[j * 3 + k][i * 3 + l];
                                System.out.print(microBoard[k][l]);
                            }
                            System.out.println();
                        }
                        Move move = checkForWeakSides(microBoard, player);
                        if (move != null) {
                            System.out.println(2);
                            System.out.println("play: " + move.toString());
                            return new Move(move.getX() + j * 3, move.getY() + i * 3);
                        }
                        if (board[j * 3 + n][i * 3 + m].equals(".")) {
                            System.out.println(3);
                            return new Move(j * 3 + m, i * 3 + n);
                        }
                    }

                    //System.out.println("Bug fix: No moves found and therefore stuck and waiting for player input.");
                    return state.getField().getAvailableMoves().get(0);
                }
            }
        }

        //System.out.println("Bug fix: Unreachable.");
        return state.getField().getAvailableMoves().get(0);
    }

    public Move checkForWeakSides(String[][] arr, int player) {
        Move i = getMove(arr, String.valueOf(Math.abs(player - 1)));
        if (i != null) return i;
        Move j = getMove(arr, String.valueOf(player));
        if (j != null) return j;
        return null;
    }

    private Move getMove(String[][] arr, String j) {
        //diagonals
        if (arr[1][1].equals(j) && arr[0][0].equals(j) && arr[2][2].equals(".")) {
            return new Move(2, 2);
        }
        if (arr[2][2].equals(j) && arr[0][0].equals(j) && arr[1][1].equals(".")) {
            return new Move(1, 1);
        }
        if (arr[2][2].equals(j) && arr[1][1].equals(j) && arr[0][0].equals(".")) {
            return new Move(0, 0);
        }

        if (arr[0][2].equals(j) && arr[2][0].equals(j) && arr[1][1].equals(".")) {
            return new Move(1, 1);
        }
        if (arr[2][0].equals(j) && arr[1][1].equals(j) && arr[0][2].equals(".")) {
            return new Move(0, 2);
        }
        if (arr[0][2].equals(j) && arr[1][1].equals(j) && arr[2][0].equals(".")) {
            return new Move(2, 0);
        }

        for (int i = 0; i < 3; i++) {
            //lines
            if (arr[i][0].equals(j) && arr[i][1].equals(j) && arr[i][2].equals("."))
                return new Move(i, 2);
            if (arr[i][2].equals(j) && arr[i][1].equals(j) && arr[i][0].equals("."))
                return new Move(i, 0);
            if (arr[i][2].equals(j) && arr[i][0].equals(j) && arr[i][1].equals("."))
                return new Move(i, 1);

            if (arr[0][i].equals(j) && arr[1][i].equals(j) && arr[2][i].equals("."))
                return new Move(2, i);
            if (arr[2][i].equals(j) && arr[1][i].equals(j) && arr[0][i].equals("."))
                return new Move(0, i);
            if (arr[0][i].equals(j) && arr[2][i].equals(j) && arr[1][i].equals("."))
                return new Move(1, i);
        }
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
