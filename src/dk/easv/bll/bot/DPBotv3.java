package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.*;

public class DPBotv3 implements IBot {
    String[][] macro;
    String[][] microBoard;
    String[][] board;
    private final boolean print = false;

    /**
     * Prio in the macro board
     */
    List<int[]> macroPrio = new ArrayList<>(Arrays.asList(
            new int[]{0, 0}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 2}, new int[]{1, 1}
            , new int[]{1, 2}, new int[]{2, 1}, new int[]{0, 1}, new int[]{1, 0}));

    /**
     * Prio in the micro board
     */
    List<int[]> microPrio = new ArrayList<>(Arrays.asList(
            new int[]{0, 0}, new int[]{2, 0}, new int[]{0, 2}, new int[]{2, 2}
            , new int[]{1, 2}, new int[]{2, 1}, new int[]{0, 1}, new int[]{1, 0}
            , new int[]{1, 1}));

    private static final String BOTNAME = "DP Bot V3";

    /**
     * Plays moves, counters moves, and tries to win
     *
     * @param state the current dk.easv.bll.game state
     * @return a move
     */
    @Override
    public IMove doMove(IGameState state) {
        int player = state.getMoveNumber() % 2;
        macro = state.getField().getMacroboard();
        board = state.getField().getBoard();
        setMacroPrio(macro, String.valueOf(player));
        for (int a = 0; a < macroPrio.size(); a++) {
            int i = macroPrio.get(a)[0];
            int j = macroPrio.get(a)[1];

            if (state.getMoveNumber() == 0) {
                if (print)
                    System.out.println(1);
                return new Move(4, 4);
            } else {
                for (int b = 0; b < microPrio.size(); b++) {
                    int n = microPrio.get(b)[0];
                    int m = microPrio.get(b)[1];

                    microBoard = new String[3][3];

                    if (macro[j][i].equals("-1")) {
                        if (print) {
                            System.out.println(" Macro: ");
                            for (int x = 0; x < 3; x++) {
                                for (int y = 0; y < 3; y++) {
                                    System.out.printf("%2s", macro[x][y]);
                                }
                                System.out.println();
                            }
                            System.out.println();
                        }
                        if (print)
                            System.out.printf("%s( %s , %s )%n", " Micro: ", j, i);
                        for (int l = 0; l < 3; l++) {
                            for (int k = 0; k < 3; k++) {
                                microBoard[k][l] = board[j * 3 + k][i * 3 + l];
                                if (print)
                                    System.out.printf("%2s", microBoard[k][l]);
                            }
                            if (print)
                                System.out.println();
                        }
                        Move move = checkForWeakSides(microBoard, player);
                        if (move != null) {
                            if (print)
                                System.out.println("3 play: " + move.toString());
                            return new Move(move.getX() + j * 3, move.getY() + i * 3);
                        }
                    }
                }
            }
        }
        if (print)
            System.out.println(5);
        return state.getField().getAvailableMoves().get(0);
    }

    public Move checkForWeakSides(String[][] arr, int player) {
        Move i = getMove(arr, String.valueOf(Math.abs(player - 1)));
        if (i != null) return i;
        Move j = getMove(arr, String.valueOf(player));
        if (j != null) return j;

        String validator = ".";
        int z = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (arr[x][y].equals(validator))
                    z++;
            }
        }
        if (z == 8) {
            if (!arr[0][0].equals(validator))
                return new Move(2, 2);

            if (!arr[0][2].equals(validator))
                return new Move(2, 0);

            if (!arr[2][0].equals(validator))
                return new Move(0, 2);

            if (!arr[2][2].equals(validator))
                return new Move(0, 0);
        }
        return null;
    }

    private Move getMove(String[][] arr, String j) {
        String validator = ".";

        //diagonals
        if (arr[0][2].equals(j) && arr[2][0].equals(j) && arr[1][1].equals(validator)) {
            return new Move(1, 1);
        }
        if (arr[2][2].equals(j) && arr[0][0].equals(j) && arr[1][1].equals(validator)) {
            return new Move(1, 1);
        }
        if (arr[1][1].equals(j) && arr[0][0].equals(j) && arr[2][2].equals(validator)) {
            return new Move(2, 2);
        }
        if (arr[2][2].equals(j) && arr[1][1].equals(j) && arr[0][0].equals(validator)) {
            return new Move(0, 0);
        }
        if (arr[2][0].equals(j) && arr[1][1].equals(j) && arr[0][2].equals(validator)) {
            return new Move(0, 2);
        }
        if (arr[0][2].equals(j) && arr[1][1].equals(j) && arr[2][0].equals(validator)) {
            return new Move(2, 0);
        }

        for (int i = 0; i < 3; i++) {
            //lines
            if (arr[i][0].equals(j) && arr[i][1].equals(j) && arr[i][2].equals(validator)) {
                return new Move(i, 2);
            }
            if (arr[i][2].equals(j) && arr[i][1].equals(j) && arr[i][0].equals(validator)) {
                return new Move(i, 0);
            }
            if (arr[i][2].equals(j) && arr[i][0].equals(j) && arr[i][1].equals(validator)) {
                return new Move(i, 1);
            }

            if (arr[0][i].equals(j) && arr[1][i].equals(j) && arr[2][i].equals(validator)) {
                return new Move(2, i);
            }
            if (arr[2][i].equals(j) && arr[1][i].equals(j) && arr[0][i].equals(validator)) {
                return new Move(0, i);
            }
            if (arr[0][i].equals(j) && arr[2][i].equals(j) && arr[1][i].equals(validator)) {
                return new Move(1, i);
            }
        }
        return null;
    }

    private void changePrio(int s, int a) {
        macroPrio.remove(new int[]{a, s});
        macroPrio.add(0, new int[]{a, s});
    }

    private void setMacroPrio(String[][] arr, String j) {
        String validator = "-1";
        //diagonals
        if (arr[1][1].equals(j) && arr[0][0].equals(j) && arr[2][2].equals(validator)) {
            changePrio(2, 2);
        }
        if (arr[2][2].equals(j) && arr[0][0].equals(j) && arr[1][1].equals(validator)) {
            changePrio(1, 1);
        }
        if (arr[2][2].equals(j) && arr[1][1].equals(j) && arr[0][0].equals(validator)) {
            changePrio(0, 0);
        }

        if (arr[0][2].equals(j) && arr[2][0].equals(j) && arr[1][1].equals(validator)) {
            changePrio(1, 1);
        }
        if (arr[2][0].equals(j) && arr[1][1].equals(j) && arr[0][2].equals(validator)) {
            changePrio(0, 2);
        }
        if (arr[0][2].equals(j) && arr[1][1].equals(j) && arr[2][0].equals(validator)) {
            changePrio(2, 0);
        }
        for (int i = 0; i < 3; i++) {
            //lines
            if (arr[i][0].equals(j) && arr[i][1].equals(j) && arr[i][2].equals(validator)) {
                changePrio(i, 2);
            }
            if (arr[i][2].equals(j) && arr[i][1].equals(j) && arr[i][0].equals(validator)) {
                changePrio(i, 0);
            }
            if (arr[i][2].equals(j) && arr[i][0].equals(j) && arr[i][1].equals(validator)) {
                changePrio(i, 1);
            }

            if (arr[0][i].equals(j) && arr[1][i].equals(j) && arr[2][i].equals(validator)) {
                changePrio(2, i);
            }
            if (arr[2][i].equals(j) && arr[1][i].equals(j) && arr[0][i].equals(validator)) {
                changePrio(0, i);
            }
            if (arr[0][i].equals(j) && arr[2][i].equals(j) && arr[1][i].equals(validator)) {
                changePrio(1, i);
            }
        }
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
