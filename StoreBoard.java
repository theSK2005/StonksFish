class StoreBoard {
    static Piece[][] board = new Piece [8][8];
    static boolean color;
    public StoreBoard (Piece[][] nBoard, boolean curColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8 ; j++) {
                board[i][j] = nBoard[i][j];
            }
        }

        color = curColor;
    }

    public static Piece getBoardPos (int xPos, int yPos) {
        return board[xPos][yPos];
    }

    public static Piece[][] getBoard () {
        return board;
    }
    
    public static boolean getColor () {
        return color;
    }
}