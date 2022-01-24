class Piece {
    public int height;
    public char color;
    public char type;
    public Piece (int height, char color, char type) {
        this.height = height;
        this.color = color;
        this.type = type;
    }

    
    public static void main(String[] args) {
    }
    
    public int getHeight () {
        return height;
    }

    public char getColor () {
        return color;
    }

    public char getType () {
        return type;
    }

    public void setHeight (int height) {
        this.height = height;
    }

    public void setColor (char color) {
        this.color = color;
    }

    public void setType (char type) {
        this.type = type;
    }
}