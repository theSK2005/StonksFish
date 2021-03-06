import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
class Main {
    static Piece[][] board = new Piece [8][8];
    static Piece[][] undoPos = new Piece[8][8];
    static int moveNum = 0;
    static boolean wKHasMoved = false;
    static boolean bKHasMoved = false;
    static boolean wRAHasMoved = false;
    static boolean bRAHasMoved = false;
    static boolean wRHHasMoved = false;
    static boolean bRHHasMoved = false;
    static char pawnTwoMoves = '\0';
    static boolean isEnPassant = false;
    static ArrayList<StoreBoard> boardList = new ArrayList<StoreBoard>();
    
    public static void main(String[] args) {
        board[0][0] = new Piece(2, 'W', 'R');
        board[0][1] = new Piece(2, 'W', 'N');
        board[0][2] = new Piece(2, 'W', 'B');
        board[0][3] = new Piece(2, 'W', 'Q');
        board[0][4] = new Piece(2, 'W', 'K');
        board[0][5] = new Piece(2, 'W', 'B');
        board[0][6] = new Piece(3, 'W', 'N');
        board[0][7] = new Piece(2, 'W', 'R');
        board[1][0] = new Piece(2, 'W', 'P');
        board[1][1] = new Piece(2, 'W', 'P');
        board[1][2] = new Piece(2, 'W', 'P');
        board[1][3] = new Piece(2, 'W', 'P');
        board[1][4] = new Piece(2, 'W', 'P');
        board[1][5] = new Piece(2, 'W', 'P');
        board[1][6] = new Piece(2, 'W', 'P');
        board[1][7] = new Piece(2, 'W', 'P');
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Piece(0, 'E', 'E');
            }
        }
        board[7][0] = new Piece(2, 'B', 'R');
        board[7][1] = new Piece(2, 'B', 'N');
        board[7][2] = new Piece(2, 'B', 'B');
        board[7][3] = new Piece(2, 'B', 'Q');
        board[7][4] = new Piece(2, 'B', 'K');
        board[7][5] = new Piece(2, 'B', 'B');
        board[7][6] = new Piece(2, 'B', 'N');
        board[7][7] = new Piece(2, 'B', 'R');
        board[6][0] = new Piece(2, 'B', 'P');
        board[6][1] = new Piece(2, 'B', 'P');
        board[6][2] = new Piece(2, 'B', 'P');
        board[6][3] = new Piece(2, 'B', 'P');
        board[6][4] = new Piece(2, 'B', 'P');
        board[6][5] = new Piece(2, 'B', 'P');
        board[6][6] = new Piece(2, 'B', 'P');
        board[6][7] = new Piece(2, 'B', 'P');
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                undoPos[i][j] = new Piece();
            }
        }
        printBoard();
        String parsedLine;
        Scanner scan = new Scanner(System.in);
        System.out.println("W to Move\nPrint Chess Move");
        String move = scan.nextLine();

        while (true) {
            parsedLine = notationParse(move);
            doMove(canMove(parsedLine), parsedLine);
            printBoard();
            if (moveNum % 2 == 0) {
                System.out.println("W to Move\nPrint Chess Move");
            }
            else {
                System.out.println("B to Move\nPrint Chess Move");
            }
            move = scan.nextLine();
        }
//        System.out.print(pieceAtArr(posToArr(notationParse(scan.nextLine()).substring(2))));
//        scan.close();

//        System.out.print(piece + " " + endSq);
/*        for (int x = 7; x >= 0; x--) {
            for (int y = 0; y <= 7; y++) {
                System.out.println(board[x][y]);
            }
        }
*/
    }

    //prints current board state
    public static void printBoard () {
        for (int x = 7; x >= 0; x--) {
            for (int y = 0; y <= 7; y++) {
                System.out.print(board[x][y].getColor() + "" + board[x][y].getType() + " ");
            }
            System.out.println();
        }
    }

    //converts board position to array position (ex. a1 -> 00)
    public static int posToArr (String position) {
        return (((int) (position.charAt(0) - 97)) + 10 * ((int) (position.charAt(1)) - 49));
    }

    //converts array position to board position (ex. 00 -> a1)
    public static String arrToPos (int arrVal) {
        return "" + ((char) (97 + (arrVal % 10))) + ((char) (49 + (arrVal / 10)));
    }

    //gets piece at array position
    public static String pieceAtArr (int arrVal) {
        return board[arrVal / 10][arrVal % 10].getColor() + "" + board[arrVal / 10][arrVal % 10].getType();
    }

    //you really don't want to know
    public static ArrayList<Integer> canMove (String curLine) {
        ArrayList<Integer> moveFrom = new ArrayList<Integer>();
        char piece = curLine.charAt(0);

        if (curLine.equals("Invalid Notation")
            || curLine.equals("Move Undone")) {
            return moveFrom;    
        }
        //detect queenside castle
        if (curLine.substring(0, 5).equals("0-0-0")) {
            if ((curLine.charAt(6) == 'W'
            && !wKHasMoved)
            && !wRAHasMoved) {
                moveNum++;
                char curTurn = 'W';
                if (!legalPos()) {
                    System.out.println("Cannot castle during check");
                }
                else if (((canMove(("P d1 B")).size() != 0
                || canMove(("N d1 B")).size() != 0)
                || (canMove(("B d1 B")).size() != 0
                || canMove(("R d1 B")).size() != 0))
                || (canMove(("Q d1 B")).size() != 0)
                || canMove(("K d1 B")).size() != 0) {
                    System.out.println("Cannot castle through check");
                }
                else if (((canMove(("P b1 B")).size() != 0
                || canMove(("N c1 B")).size() != 0)
                || (canMove(("B c1 B")).size() != 0
                || canMove(("R c1 B")).size() != 0))
                || (canMove(("Q c1 B")).size() != 0)
                || canMove(("K c1 B")).size() != 0) {
                    System.out.println("Cannot castle into check");
                }
                else if ((board[0][3].getColor() != 'E'
                || board[0][2].getColor() != 'E')
                || board[0][1].getColor() != 'E') {
                    System.out.println("There are pieces between the King and Rook");
                }
                else {
                    moveFrom.add(300);
                }
                return moveFrom;
            }
            if ((curLine.charAt(6) == 'B'
            && !bKHasMoved)
            && !bRAHasMoved) {
            moveNum++;
                char curTurn = 'B';
                if (!legalPos()) {
                    System.out.println("Cannot castle during check");
                }
                else if (((canMove(("P d1 B")).size() != 0
                || canMove(("N d1 B")).size() != 0)
                || (canMove(("B d1 B")).size() != 0
                || canMove(("R d1 B")).size() != 0))
                || (canMove(("Q d1 B")).size() != 0)
                || canMove(("K d1 B")).size() != 0) {
                    System.out.println("Cannot castle through check");
                }
                else if (((canMove(("P b1 B")).size() != 0
                || canMove(("N c1 B")).size() != 0)
                || (canMove(("B c1 B")).size() != 0
                || canMove(("R c1 B")).size() != 0))
                || (canMove(("Q c1 B")).size() != 0)
                || canMove(("K c1 B")).size() != 0) {
                    System.out.println("Cannot castle into check");
                }
                else if ((board[0][3].getColor() != 'E'
                || board[0][2].getColor() != 'E')
                || board[0][1].getColor() != 'E') {
                    System.out.println("There are pieces between the King and Rook");
                }
                else {
                    moveFrom.add(400);
                }
                return moveFrom;
            }
        }

        //detect kingside castle
        else if (curLine.substring(0, 3).equals("0-0")) {
            moveNum++;
            if ((curLine.charAt(4) == 'W'
            && !wKHasMoved)
            && !wRHHasMoved) {
                char curTurn = 'W';
                if (!legalPos()) {
                    System.out.println("Cannot castle during check");
                }
                else if (((canMove(("P f1 B")).size() != 0
                || canMove(("N f1 B")).size() != 0)
                || (canMove(("B f1 B")).size() != 0
                || canMove(("R f1 B")).size() != 0))
                || (canMove(("Q f1 B")).size() != 0)
                || canMove(("K f1 B")).size() != 0) {
                    System.out.println("Cannot castle through check");
                }
                else if (((canMove(("P g1 B")).size() != 0
                || canMove(("N g1 B")).size() != 0)
                || (canMove(("B g1 B")).size() != 0
                || canMove(("R g1 B")).size() != 0))
                || (canMove(("Q g1 B")).size() != 0)
                || canMove(("K g1 B")).size() != 0) {
                    System.out.println("Cannot castle into check");
                }
                else if (board[0][5].getColor() != 'E'
                || board[0][6].getColor() != 'E'){
                    System.out.println("There are pieces between the King and Rook");
                }
                else {
                    moveFrom.add(100);
                }
                return moveFrom;
            }
            if ((curLine.charAt(4) == 'B'
            && !bKHasMoved)
            && !bRHHasMoved) {
                char curTurn = 'B';
                if (!legalPos()) {
                    System.out.println("Cannot castle during check");
                }
                else if (((canMove(("P f8 W")).size() != 0
                || canMove(("N f8 W")).size() != 0)
                || (canMove(("B f8 W")).size() != 0
                || canMove(("R f8 W")).size() != 0))
                || (canMove(("Q f8 W")).size() != 0)
                || canMove(("K f8 W")).size() != 0) {
                    System.out.println("Cannot castle through check");
                }
                else if (((canMove(("P g8 W")).size() != 0
                || canMove(("N g8 W")).size() != 0)
                || (canMove(("B g8 W")).size() != 0
                || canMove(("R g8 W")).size() != 0))
                || (canMove(("Q g8 W")).size() != 0)
                || canMove(("K g8 W")).size() != 0) {
                    System.out.println("Cannot castle into check");
                }
                else if (board[7][5].getColor() != 'E'
                || board[7][6].getColor() != 'E'){
                    System.out.println("There are pieces between the King and Rook");
                }
                else {
                    moveFrom.add(200);
                }
                return moveFrom;
            }
        }
        else {
            char curTurn = curLine.charAt(5);
            String endPos = curLine.substring(2, 4);
            int endArr = posToArr(endPos);
            if (board[endArr / 10][endArr % 10].getColor() != curTurn) {
                //pawn move case
                if (piece == 'P') {
                    if (board[endArr / 10][endArr % 10].getColor() == 'E') {
                        if (endArr / 10 != 0 && curTurn == 'W') {
                            if (arrToPos(endArr).charAt(0) == pawnTwoMoves
                                && arrToPos(endArr).charAt(1) == '6') {
                                if (endArr % 10 != 0) {
                                    if (board[(endArr / 10) - 1][(endArr % 10) - 1].getType() == 'P'
                                    && board[(endArr / 10) - 1][(endArr % 10) - 1].getColor() == 'W') {
                                        System.out.println("Can do en passant");
                                        moveFrom.add(endArr - 11);
                                        isEnPassant = true;
                                    }
                                }
                                
                                if (endArr % 10 != 7) {
                                    if (board[(endArr / 10) - 1][(endArr % 10) + 1].getType() == 'P'
                                    && board[(endArr / 10) - 1][(endArr % 10) + 1].getColor() == 'W') {
                                        System.out.println("Can do en pessant");
                                        moveFrom.add(endArr - 9);
                                        isEnPassant = true;
                                    }
                                }
                            }
                            
                            if (board[endArr / 10 - 1][endArr % 10].getType() == 'P'
                            && board[endArr / 10 - 1][endArr % 10].getColor() == 'W') {
                                moveFrom.add(endArr - 10);
                                pawnTwoMoves = '\0';
                            }
                            else if (endArr / 10 != 1) {
                                if (((board[endArr / 10 - 2][endArr % 10].getType() == 'P') 
                                && (endArr / 10 == 3))
                                && board[endArr / 10 - 2][endArr % 10].getColor() == 'W') {
                                    moveFrom.add(endArr - 20);
                                    pawnTwoMoves = arrToPos(endArr).charAt(0);
                                }
                            }
                        }
                        if (endArr / 10 != 7 && curTurn == 'B') {
                            if (arrToPos(endArr).charAt(0) == pawnTwoMoves
                                && arrToPos(endArr).charAt(1) == '3') {
                                if (endArr % 10 != 0) {
                                    if (board[(endArr / 10) + 1][(endArr % 10) - 1].getType() == 'P'
                                    && board[(endArr / 10) + 1][(endArr % 10) - 1].getColor() == 'B') {
                                        System.out.println("Can do en passant");
                                        moveFrom.add(endArr + 9);
                                        isEnPassant = true;
                                    }
                                }
                                
                                if (endArr % 10 != 7) {
                                    if (board[(endArr / 10) + 1][(endArr % 10) + 1].getType() == 'P'
                                    && board[(endArr / 10) + 1][(endArr % 10) + 1].getColor() == 'B') {
                                        System.out.println("Can do en pessant");
                                        moveFrom.add(endArr + 11);
                                        isEnPassant = true;
                                    }
                                }
                            }
                            if (board[endArr / 10 + 1][endArr % 10].getType() == 'P'
                            && board[endArr / 10 + 1][endArr % 10].getColor() == 'B') {
                                moveFrom.add(endArr + 10);
                                pawnTwoMoves = '\0';
                            }
                            else if (endArr / 10 != 6) {
                                if (((board[endArr / 10 + 2][endArr % 10].getType() == 'P') 
                                && (endArr / 10 == 4)) 
                                && board[endArr / 10 + 2][endArr % 10].getColor() == 'B') {
                                    moveFrom.add(endArr + 20);
                                    pawnTwoMoves = arrToPos(endArr).charAt(0);
                                }        
                            }
                        }
                    }
                    else {
                        if (endArr % 10 != 0) {
                            if (curTurn == 'W') {
                                if (board[(endArr / 10) - 1][(endArr % 10) - 1].getType() == 'P'
                                && board[(endArr / 10) - 1][(endArr % 10) - 1].getColor() == 'W') {
                                    moveFrom.add(endArr - 11);
                                }
                            }
                            if (curTurn == 'B') {
                                if(board[(endArr / 10) + 1][(endArr % 10) - 1].getType() == 'P'
                                && board[(endArr / 10) + 1][(endArr % 10) - 1].getColor() == 'B') {
                                    moveFrom.add(endArr + 9);
                                }
                            }    
                        }
                        if (endArr % 10 != 7) {
                            if (curTurn == 'W') {
                                if (board[(endArr / 10) - 1][(endArr % 10) + 1].getType() == 'P'
                                && board[(endArr / 10) - 1][(endArr % 10) + 1].getColor() == 'W') {
                                    moveFrom.add(endArr - 9);
                                }
                            }
                            if (curTurn == 'B') {
                                if (board[(endArr / 10) + 1][(endArr % 10) + 1].getType() == 'P'
                                && board[(endArr / 10) + 1][(endArr % 10) + 1].getColor() == 'B') {
                                    moveFrom.add(endArr + 11);
                                }
                            }    
                        }
                    }
                }

                //knight move case
                else if (piece == 'N') {
                        if (((endArr / 10) + 1 < 8) && ((endArr % 10) + 2 < 8)) {
                            if (board[(endArr / 10) + 1][(endArr % 10) + 2].getType() == 'N' 
                            && board[(endArr / 10) + 1][(endArr % 10) + 2].getColor() == curTurn) {
                                moveFrom.add(endArr + 12);
                            }

                        }
                        if (((endArr / 10) - 1 >= 0) && ((endArr % 10) + 2 < 8)) {
                            if (board[(endArr / 10) - 1][(endArr % 10) + 2].getType() == 'N' 
                            && board[(endArr / 10) - 1][(endArr % 10) + 2].getColor() == curTurn) {
                                moveFrom.add(endArr - 8);
                            }

                        }
                        if (((endArr / 10) + 1 < 8) && ((endArr % 10) - 2 >= 0)) {
                            if (board[(endArr / 10) + 1][(endArr % 10) - 2].getType() == 'N' 
                            && board[(endArr / 10) + 1][(endArr % 10) - 2].getColor() == curTurn) {
                                moveFrom.add(endArr + 8);
                            }

                        }
                        if (((endArr / 10) - 1 >= 0) && ((endArr % 10) - 2 >= 0)) {
                            if (board[(endArr / 10) - 1][(endArr % 10) - 2].getType() == 'N' 
                            && board[(endArr / 10) - 1][(endArr % 10) - 2].getColor() == curTurn) {
                                moveFrom.add(endArr - 12);
                            }
                        }
                        if (((endArr / 10) + 2 < 8) && ((endArr % 10) + 1 < 8)) {
                            if (board[(endArr / 10) + 2][(endArr % 10) + 1].getType() == 'N' 
                            && board[(endArr / 10) + 2][(endArr % 10) + 1].getColor() == curTurn) {
                                moveFrom.add(endArr + 21);
                            }

                        }
                        if (((endArr / 10) - 2 >= 0) && ((endArr % 10) + 1 < 8)) {
                            if (board[(endArr / 10) - 2][(endArr % 10) + 1].getType() == 'N' 
                            && board[(endArr / 10) - 2][(endArr % 10) + 1].getColor() == curTurn) {
                                moveFrom.add(endArr - 19);
                            }

                        }
                        if (((endArr / 10) + 2 < 8) && ((endArr % 10) - 1 >= 0)) {
                            if (board[(endArr / 10) + 2][(endArr % 10) - 1].getType() == 'N' 
                            && board[(endArr / 10) + 2][(endArr % 10) - 1].getColor() == curTurn) {
                                moveFrom.add(endArr + 19);
                            }

                        }
                        if (((endArr / 10) - 2 >= 0) && ((endArr % 10) - 1 >= 0)) {
                            if (board[(endArr / 10) - 2][(endArr % 10) - 1].getType() == 'N' 
                            && board[(endArr / 10) - 2][(endArr % 10) - 1].getColor() == curTurn) {
                                moveFrom.add(endArr - 21);
                            }

                        }
                    
                }

                //bishop move case
                else if (piece == 'B') {
                        for (int x = 1; (x < 8 - (endArr / 10)) && (x < 8 - (endArr % 10)); x++) {
                            if (board[(endArr / 10) + x][(endArr % 10) + x].getType() != 'E'
                            && (board[(endArr / 10) + x][(endArr % 10) + x].getType() != 'B'
                            || board[(endArr / 10) + x][(endArr % 10) + x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) + x][(endArr % 10) + x].getType() == 'B'
                            && (board[(endArr / 10) + x][(endArr % 10) + x].getColor() == curTurn)) {
                                moveFrom.add(endArr + (x * 11));
                            }
                        }
                        for (int x = 1; (x <= endArr / 10) && (x <= endArr % 10); x++) {
                            if (board[(endArr / 10) - x][(endArr % 10) - x].getType() != 'E'
                            && (board[(endArr / 10) - x][(endArr % 10) - x].getType() != 'B'
                            || board[(endArr / 10) - x][(endArr % 10) - x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) - x][(endArr % 10) - x].getType() == 'B'
                            && (board[(endArr / 10) - x][(endArr % 10) - x].getColor() == curTurn)) {
                                moveFrom.add(endArr - (x * 11));
                            }
                        }
                        for (int x = 1; (x < 8 - (endArr / 10)) && (x <= endArr % 10); x++) {
                            if (board[(endArr / 10) + x][(endArr % 10) - x].getType() != 'E'
                            && (board[(endArr / 10) + x][(endArr % 10) - x].getType() != 'B'
                            || board[(endArr / 10) + x][(endArr % 10) - x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) + x][(endArr % 10) - x].getType() == 'B'
                            && (board[(endArr / 10) + x][(endArr % 10) - x].getColor() == curTurn)) {
                                moveFrom.add(endArr + (x * 9));
                            }
                        }
                        for (int x = 1; (x <= endArr / 10) && (x < 8 - (endArr % 10)); x++) {
                            if (board[(endArr / 10) - x][(endArr % 10) + x].getType() != 'E'
                            && (board[(endArr / 10) - x][(endArr % 10) + x].getType() != 'B'
                            || board[(endArr / 10) - x][(endArr % 10) + x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) - x][(endArr % 10) + x].getType() == 'B'
                            && (board[(endArr / 10) - x][(endArr % 10) + x].getColor() == curTurn)) {
                                moveFrom.add(endArr - (x * 9));
                            }
                        }
                    
                }

                //rook move case
                else if (piece == 'R') {
                        for (int x = (endArr % 10) + 1; x <= 7; x++) {
                            if (board[endArr / 10][x].getType() != 'E'
                            && (board[endArr / 10][x].getType() != 'R'
                            || board[endArr / 10][x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[endArr / 10][x].getType() == 'R'
                            && board[endArr / 10][x].getColor() == curTurn) {
                                moveFrom.add((endArr / 10) * 10 + x);
                            }
                        }

                        for (int x = (endArr % 10) - 1; x >= 0; x--) {
                            if (board[endArr / 10][x].getType() != 'E'
                            && (board[endArr / 10][x].getType() != 'R'
                            || board[endArr / 10][x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[endArr / 10][x].getType() == 'R'
                            && board[endArr / 10][x].getColor() == curTurn) {
                                moveFrom.add((endArr / 10) * 10 + x);
                            }
                        }
                        for (int y = (endArr / 10) + 1; y <= 7; y++) {
                            if (board[y][endArr % 10].getType() != 'E'
                            && (board[y][endArr % 10].getType() != 'R'
                            || board[y][endArr % 10].getColor() != curTurn)) {
                                break;
                            }
                            if (board[y][endArr % 10].getType() == 'R'
                            && board[y][endArr % 10].getColor() == curTurn) {
                                moveFrom.add(y * 10 + (endArr % 10));
                            }
                        }

                        for (int y = (endArr / 10) - 1; y >= 0; y--) {
                            if (board[y][endArr % 10].getType() != 'E'
                            && (board[y][endArr % 10].getType() != 'R'
                            || board[y][endArr % 10].getColor() != curTurn)) {
                                break;
                            }
                            if (board[y][endArr % 10].getType() == 'R'
                            && board[y][endArr % 10].getColor() == curTurn) {
                                moveFrom.add(y * 10 + (endArr % 10));
                            }
                        }     
                }

                //queen move case
                else if (piece == 'Q') {
                        for (int x = 1; (x < 8 - (endArr / 10)) && (x < 8 - (endArr % 10)); x++) {
                            if (board[(endArr / 10) + x][(endArr % 10) + x].getType() != 'E'
                            && (board[(endArr / 10) + x][(endArr % 10) + x].getType() != 'Q'
                            || board[(endArr / 10) + x][(endArr % 10) + x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) + x][(endArr % 10) + x].getType() == 'Q'
                            && (board[(endArr / 10) + x][(endArr % 10) + x].getColor() == curTurn)) {
                                moveFrom.add(endArr + (x * 11));
                            }
                        }
                        for (int x = 1; (x <= endArr / 10) && (x <= endArr % 10); x++) {
                            if (board[(endArr / 10) - x][(endArr % 10) - x].getType() != 'E'
                            && (board[(endArr / 10) - x][(endArr % 10) - x].getType() != 'Q'
                            || board[(endArr / 10) - x][(endArr % 10) - x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) - x][(endArr % 10) - x].getType() == 'Q'
                            && (board[(endArr / 10) - x][(endArr % 10) - x].getColor() == curTurn)) {
                                moveFrom.add(endArr - (x * 11));
                            }
                        }
                        for (int x = 1; (x < 8 - (endArr / 10)) && (x <= endArr % 10); x++) {
                            if (board[(endArr / 10) + x][(endArr % 10) - x].getType() != 'E'
                            && (board[(endArr / 10) + x][(endArr % 10) - x].getType() != 'Q'
                            || board[(endArr / 10) + x][(endArr % 10) - x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) + x][(endArr % 10) - x].getType() == 'Q'
                            && (board[(endArr / 10) + x][(endArr % 10) - x].getColor() == curTurn)) {
                                moveFrom.add(endArr + (x * 9));
                            }
                        }
                        for (int x = 1; (x <= endArr / 10) && (x < 8 - (endArr % 10)); x++) {
                            if (board[(endArr / 10) - x][(endArr % 10) + x].getType() != 'E'
                                && (board[(endArr / 10) - x][(endArr % 10) + x].getType() != 'Q'
                                || board[(endArr / 10) - x][(endArr % 10) + x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[(endArr / 10) - x][(endArr % 10) + x].getType() == 'Q'
                            && (board[(endArr / 10) - x][(endArr % 10) + x].getColor() == curTurn)) {
                                moveFrom.add(endArr - (x * 9));
                            }
                        }
                    
                        for (int x = (endArr % 10) + 1; x <= 7; x++) {
                            if (board[endArr / 10][x].getType() != 'E'
                            && (board[endArr / 10][x].getType() != 'Q'
                            || board[endArr / 10][x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[endArr / 10][x].getType() == 'Q'
                            && board[endArr / 10][x].getColor() == curTurn) {
                                moveFrom.add((endArr / 10) * 10 + x);
                            }
                        }

                        for (int x = (endArr % 10) - 1; x >= 0; x--) {
                            if (board[endArr / 10][x].getType() != 'E'
                            && (board[endArr / 10][x].getType() != 'Q'
                            || board[endArr / 10][x].getColor() != curTurn)) {
                                break;
                            }
                            if (board[endArr / 10][x].getType() == 'Q'
                            && board[endArr / 10][x].getColor() == curTurn) {
                                moveFrom.add((endArr / 10) * 10 + x);
                            }
                        }
                        for (int y = (endArr / 10) + 1; y <= 7; y++) {
                            if (board[y][endArr % 10].getType() != 'E'
                            && (board[y][endArr % 10].getType() != 'Q'
                            || board[y][endArr % 10].getColor() != curTurn)) {
                                break;
                            }
                            if (board[y][endArr % 10].getType() == 'Q'
                            && board[y][endArr % 10].getColor() == curTurn) {
                                moveFrom.add(y * 10 + (endArr % 10));
                            }
                        }

                        for (int y = (endArr / 10) - 1; y >= 0; y--) {
                            if (board[y][endArr % 10].getType() != 'E'
                            && (board[y][endArr % 10].getType() != 'Q'
                            || board[y][endArr % 10].getColor() != curTurn)) {
                                break;
                            }
                            if (board[y][endArr % 10].getType() == 'Q'
                            && board[y][endArr % 10].getColor() == curTurn) {
                                moveFrom.add(y * 10 + (endArr % 10));
                            }
                        }
                    
                }

                //king move case
                else if (piece == 'K') {
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            if (((((endArr / 10) + x) > 7) || (((endArr / 10) + x) < 0))
                            || ((((endArr % 10) + x) > 7) || (((endArr % 10) + x) < 0))) {
                                continue;
                            }
                            if (board[(endArr / 10) + x][(endArr % 10) + y].getType() == 'K'
                            && board[(endArr / 10) + x][(endArr % 10) + y].getColor() == curTurn) {
                                moveFrom.add(endArr + (x * 10) + y);
                            }
                        }
                    }
                }
            }
        }

        //print all pieces that can move to the position and return the arraylist
        for (int canMoveFrom : moveFrom) {
            System.out.println(piece + " move from " + arrToPos(canMoveFrom));
        }
        return moveFrom; 
    }

    //do the move
    public static void doMove (ArrayList<Integer> startArr, String curLine) {
        int s;
        char promTo = '\0';
        Matcher matcher;
        char modifier = curLine.charAt(7);
        //no legal moves returned case
        if (startArr.size() == 0) {
           moveNum--;
            System.out.println("Illegal move was entered");
            return;
        }

        //multiple legal moves returned case
        else if (modifier != '\0') {
            if (modifier > 47 && modifier < 58) {
                if (startArr.size() == 1) {
                    if (arrToPos(startArr.get(0)).charAt(1) == modifier) {
                        s = startArr.get(0);
                    }
                    else {
                        moveNum--;
                        System.out.println("Illegal move was entered");
                        return;
                    }
                }
                else {
                    if (arrToPos(startArr.get(0)).charAt(1) == modifier) {
                        s = startArr.get(0);
                    }
                    else {
                        s = startArr.get(1);
                    }
                }
            }
            else {
                if (startArr.size() == 1) {
                    if (arrToPos(startArr.get(0)).charAt(0) == modifier) {
                        s = startArr.get(0);
                    }
                    else {
                        moveNum--;
                        System.out.println("Illegal move was entered");
                        return;
                    }
                }
                else {
                    if (arrToPos(startArr.get(0)).charAt(0) == modifier) {
                        s = startArr.get(0);
                    }
                    else {
                        s = startArr.get(1);
                    }
                }
            }
        }

        //one legal move returned case
        else {
            s = startArr.get(0);
        }

        //white queenside castles case
        if (s == 100) {
            board[0][6].setType('K');
            board[0][6].setColor('W');
            board[0][6].setHeight('2');
            board[0][5].setType('R');
            board[0][5].setColor('W');
            board[0][5].setHeight('2');
            board[0][7].setType('E');
            board[0][7].setColor('E');
            board[0][7].setHeight('2');
            board[0][4].setType('E');
            board[0][4].setColor('E');
            board[0][4].setHeight('2');
            return;
        }
  
        //black queenside castles case
        else if (s == 200) {
            board[7][6].setType('K');
            board[7][6].setColor('B');
            board[7][6].setHeight('2');
            board[7][5].setType('R');
            board[7][5].setColor('B');
            board[7][5].setHeight('2');
            board[7][7].setType('E');
            board[7][7].setColor('E');
            board[7][7].setHeight('2');
            board[7][4].setType('E');
            board[7][4].setColor('E');
            board[7][4].setHeight('2');
            return;
        }
        
        //white kingside castles case
        else if (s == 300) {
            board[0][2].setType('K');
            board[0][2].setColor('W');
            board[0][2].setHeight('2');
            board[0][3].setType('R');
            board[0][3].setColor('W');
            board[0][3].setHeight('2');
            board[0][0].setType('E');
            board[0][0].setColor('E');
            board[0][0].setHeight('2');
            board[0][4].setType('E');
            board[0][4].setColor('E');
            board[0][4].setHeight('2');
            return;
        }

        //black kingside castles case
        else if (s == 400) {
            board[7][2].setType('K');
            board[7][2].setColor('B');
            board[7][2].setHeight('2');
            board[7][3].setType('R');
            board[7][3].setColor('B');
            board[7][3].setHeight('2');
            board[7][0].setType('E');
            board[7][0].setColor('E');
            board[7][0].setHeight('2');
            board[7][4].setType('E');
            board[7][4].setColor('E');
            board[7][4].setHeight('2');
            return;
        }

        //set previous position to undo poition array
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                undoPos[i][j].setType(board[i][j].getType());
                undoPos[i][j].setColor(board[i][j].getColor());
                undoPos[i][j].setHeight(board[i][j].getHeight());
            }
        }
         
        //set all pieces to new squares and store the original piece locations
        String endPos = curLine.substring(2, 4);
        int endArr = posToArr(endPos);
        char sType = board[s / 10][s % 10].getType();
        char sColor = board[s / 10][s % 10].getColor();
        int sHeight = board[s / 10][s % 10].getHeight();
        char eType = board[endArr / 10][endArr % 10].getType();
        char eColor = board[endArr / 10][endArr % 10].getColor();
        int eHeight = board[endArr / 10][endArr % 10].getHeight();
        if (sType == 'P'
            && ((endArr / 10 == 7 
            && sColor == 'W')
            || (endArr / 10 == 0
            && sColor == 'B'))) {

            promTo = curLine.charAt(9);
            if (promTo == '\0') {
                System.out.println("Illegal Move Entered");
                return;
            }
            board[endArr / 10][endArr % 10].setType(promTo);
        }
            
        else {
            board[endArr / 10][endArr % 10].setType(sType);
        }
        board[endArr / 10][endArr % 10].setColor(sColor);
        board[endArr / 10][endArr % 10].setHeight(sHeight);  
        board[s / 10][s % 10].setHeight(2);
        board[s / 10][s % 10].setColor('E');
        board[s / 10][s % 10].setType('E');
        
        if (isEnPassant) {
            if (arrToPos(endArr).charAt(1) == '6') {
                char enType = board[4][endArr % 10].getType();
                char enColor = board[4][endArr % 10].getColor();
                int enHeight = board[4][endArr % 10].getHeight();
                board[4][endArr % 10].setType('E');
                board[4][endArr % 10].setColor('E');
                board[4][endArr % 10].setHeight(2); 
                if (legalPos()) {
                    return;
                }
                board[s / 10][s % 10].setType(sType);
                board[s / 10][s % 10].setColor(sColor);
                board[s / 10][s % 10].setHeight(sHeight);
                board[endArr / 10][endArr % 10].setType(eType);
                board[endArr / 10][endArr % 10].setColor(eColor);
                board[endArr / 10][endArr % 10].setHeight(eHeight);
                board[4][endArr % 10].setType(enType);
                board[4][endArr % 10].setColor(enColor);
                board[4][endArr % 10].setHeight(enHeight);
                moveNum--;
                return;
            }
            else if (arrToPos(endArr).charAt(1) == '3') {
                char enType = board[3][endArr % 10].getType();
                char enColor = board[3][endArr % 10].getColor();
                int enHeight = board[3][endArr % 10].getHeight();
                board[3][endArr % 10].setType('E');
                board[3][endArr % 10].setColor('E');
                board[3][endArr % 10].setHeight(2); 
                if (legalPos()) {
                    return;
                }
                board[s / 10][s % 10].setType(sType);
                board[s / 10][s % 10].setColor(sColor);
                board[s / 10][s % 10].setHeight(sHeight);
                board[endArr / 10][endArr % 10].setType(eType);
                board[endArr / 10][endArr % 10].setColor(eColor);
                board[endArr / 10][endArr % 10].setHeight(eHeight);
                board[3][endArr % 10].setType(enType);
                board[3][endArr % 10].setColor(enColor);
                board[3][endArr % 10].setHeight(enHeight);
                moveNum--;
                return;
            }
        }
        //check if the resulting position is legal
        if (legalPos()) {
            //if the position is legal and a rook/king moved, then change the corresponding rook/king moves variable for castle detection
            if (sType == 'R') {
                if (s == 0 || endArr == 0) {
                    wRAHasMoved = true;
                }

                if (s == 7 || endArr == 7) {
                    wRHHasMoved = true;
                }

                if (s == 70 || endArr == 70) {
                    bRAHasMoved = true;
                }

                if (s == 77 || endArr == 77) {
                    bRHHasMoved = true;
                }
            }
            else if (sType == 'K') {
                if (sColor == 'W') {
                    wKHasMoved = true;
                }
                if (sColor == 'B') {
                    bKHasMoved = true;
                }
            }

 /*           StoreBoard curBoard = new StoreBoard(board, moveNum % 2 == 0);
            int copyPos;
            for (int i = 0; i < boardList.size(); i++) {
                if (Arrays.equals(boardList.get(i).getBoard(), board)) {
                    for (int j = 0; j < boardList.size(); j++) {
                        if (i != j
                            && Arrays.equals(boardList.get(j).getBoard(), board)) {
                            System.out.println("Game is drawn\nThree Move Repetition");
                            printBoard();
                            System.exit(0);
                        }
                    }
                }
            }
            boardList.add(curBoard);
*/
            return;
            
        }
        //if the resulting position is not legal, then undo the previous moves
        else {
            board[s / 10][s % 10].setType(sType);
            board[s / 10][s % 10].setColor(sColor);
            board[s / 10][s % 10].setHeight(sHeight);
            board[endArr / 10][endArr % 10].setType(eType);
            board[endArr / 10][endArr % 10].setColor(eColor);
            board[endArr / 10][endArr % 10].setHeight(eHeight);
            moveNum--;
            return;
        }
    }

    //detect if current position is legal
    public static boolean legalPos () {
        String kPos = "";
        if (moveNum % 2 == 0) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (board[x][y].getColor() == 'B' && board[x][y].getType() == 'K') {
                        kPos = arrToPos((x * 10) + y);
//                        System.out.println("K on " + kPos);
                    }
                }
            }
            if (((canMove(("P " + kPos + " W")).size() != 0
                || canMove(("N " + kPos + " W")).size() != 0)
                || (canMove(("B " + kPos + " W")).size() != 0
                || canMove(("R " + kPos + " W")).size() != 0))
                || (canMove(("Q " + kPos + " W")).size() != 0)
                || canMove(("K " + kPos + " W")).size() != 0) {
                System.out.println("Illegal Position");
                return false;
            }
        }
        else {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (board[x][y].getColor() == 'W' && board[x][y].getType() == 'K') {
                        kPos = arrToPos((x * 10) + y);
//                        System.out.println("K  on " + kPos);
                    }
                }
            }
            if (((canMove(("P " + kPos + " B")).size() != 0
            || canMove(("N " + kPos + " B")).size() != 0)
            || (canMove(("B " + kPos + " B")).size() != 0
            || canMove(("R " + kPos + " B")).size() != 0))
            || (canMove(("Q " + kPos + " B")).size() != 0)
            || canMove(("K " + kPos + " B")).size() != 0) {
                System.out.println("Illegal Position");
                return false;
            }
        }
        return true;
    }

    //parse the thing lol
    public static String notationParse (String curLine) {
        Matcher matcher;
        char piece = '\0';
        char modifier = '\0';
        char promTo = '\0';
        String endSq = "";
        if (curLine.equals("U")) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board[i][j].setType(undoPos[i][j].getType());
                    board[i][j].setColor(undoPos[i][j].getColor());
                    board[i][j].setHeight(undoPos[i][j].getHeight());
                }
            }
            return "Move Undone";
        }
        else if (curLine.length() == 2) {
            Pattern pattern = Pattern.compile("[a-h][1-8]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine;
            }
        }
        else if (curLine.length() == 3) {
            Pattern pattern = Pattern.compile("[a-h][a-h][1-8]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(1, 3);
                modifier = curLine.charAt(0);
            }
            Pattern pattern1 = Pattern.compile("[NBRQK][a-h][1-8]");
            matcher = pattern1.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(1, 3);
            }
            Pattern pattern2 = Pattern.compile("[a-h][1-8][+#]");
            matcher = pattern2.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(0, 2);
            }
            if (curLine.equals("0-0")) {
                if (moveNum % 2 == 0) {
                    return "0-0 W";
                }
                else {
                    return "0-0 B";
                }
            }
        }
        else if (curLine.length() == 4) {
            Pattern pattern = Pattern.compile("[a-h]x[a-h][1-8]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(0);
            }
            Pattern pattern1 = Pattern.compile("[NBRQK]x[a-h][1-8]");
            matcher = pattern1.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
            }
            Pattern pattern2 = Pattern.compile("[NBRQK][a-h][a-h][1-8]");
            matcher = pattern2.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(1);
            }
            Pattern pattern3 = Pattern.compile("[NBRQK][1-8][a-h][1-8]");
            matcher = pattern3.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(1);
            }
            Pattern pattern4 = Pattern.compile("[a-h][18]=[NBRQ]");
            matcher = pattern4.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(0, 2);
                promTo = curLine.charAt(3);
            }
            Pattern pattern5 = Pattern.compile("[NBRQK][a-h][1-8][+#]");
            matcher = pattern5.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(1, 3);
            }
            if (curLine.equals("0-0+") || curLine.equals("0-0#")) {
                if (moveNum % 2 == 0) {
                    return "0-0 W";
                }
                else {
                    return "0-0 B";
                }
            }
        }
        else if (curLine.length() == 5) {
            Pattern pattern = Pattern.compile("[NBRQK][a-h]x[a-h][1-8]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(3, 5);
                modifier = curLine.charAt(1);
            }
            Pattern pattern1 = Pattern.compile("[NBRQK][1-8]x[a-h][1-8]");
            matcher = pattern1.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(3, 5);
                modifier = curLine.charAt(1);
            }
            Pattern pattern2 = Pattern.compile("[a-h]x[a-h][1-8][+#]");
            matcher = pattern2.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(0);
            }
            Pattern pattern3 = Pattern.compile("[NBRQK]x[a-h][1-8][+#]");
            matcher = pattern3.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
            }
            Pattern pattern4 = Pattern.compile("[NBRQK][a-h][a-h][1-8][+#]");
            matcher = pattern4.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(1);
            }
            Pattern pattern5 = Pattern.compile("[NBRQK][1-8][a-h][1-8][+#]");
            matcher = pattern5.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(1);
            }
            Pattern pattern6 = Pattern.compile("[a-h][18]=[NBRQ][+#]");
            matcher = pattern6.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(0, 2);
                promTo = curLine.charAt(3);
            }
            Pattern pattern7 = Pattern.compile("[a-h][a-h][18]=[NBRQ]");
            matcher = pattern7.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(1, 3);
                modifier = curLine.charAt(0);
                promTo = curLine.charAt(4);
            }
            if (curLine.equals("0-0-0")) {
                if (moveNum % 2 == 0) {
                    return "0-0-0 W";
                }
                else {
                    return "0-0-0 B";
                }
            }
        }
        else if (curLine.length() == 6) {
            Pattern pattern = Pattern.compile("[a-h]x[a-h][18]=[NBRQ]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(0);
                promTo = curLine.charAt(5);
            }
            Pattern pattern1 = Pattern.compile("[NBRQK][a-h]x[a-h][1-8][+#]");
            matcher = pattern1.matcher(curLine);
            if (matcher.matches()) {
                piece = curLine.charAt(0);
                endSq = curLine.substring(3, 5);
                modifier = curLine.charAt(1);
            }
            Pattern pattern2 = Pattern.compile("[NBRQK][1-8]x[a-h][1-8][+#]");
            matcher = pattern2.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(3, 5);
                modifier = curLine.charAt(1);
            }
            if (curLine.equals("0-0-0+") || curLine.equals("0-0-0#")) {
                if (moveNum % 2 == 0) {
                    return "0-0-0 W";
                }
                else {
                    return "0-0-0 B";
                }
            }
            Pattern pattern3 = Pattern.compile("[a-h][a-h][18]=[NBRQ][+#]");
            matcher = pattern3.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(1, 3);
                modifier = curLine.charAt(0);
                promTo = curLine.charAt(4);
            }
        }
        else if (curLine.length() == 7) {
            Pattern pattern = Pattern.compile("[a-h]x[a-h][18]=[NBRQ][+#]");
            matcher = pattern.matcher(curLine);
            if (matcher.matches()) {
                piece = 'P';
                endSq = curLine.substring(2, 4);
                modifier = curLine.charAt(0);
                promTo = curLine.charAt(5);
            }
        }
        if (piece == '\0'){
            moveNum++;
            return "Invalid Notation";
        }
        
        if (moveNum % 2 == 0) {
            moveNum++;
//            System.out.println(piece + " " + endSq + " W");
            return piece + " " + endSq + " W " + modifier + " " + promTo;
        }
        else {
            moveNum++;
//            System.out.println(piece + " " + endSq + " B");
            return piece + " " + endSq + " B " + modifier + " " + promTo;
        }
    }
}