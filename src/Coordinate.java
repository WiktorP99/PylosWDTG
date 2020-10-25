public class Coordinate {

    public int firstIndex;
    public int secondIndex;
    public int level;
    public String value;
    public Board gameBoard;

    Coordinate(String coordinate, Board board) {
        gameBoard = board;
        value = coordinate;
        try {
            firstIndex = letterToIndex(coordinate);
            secondIndex = Integer.parseInt(coordinate.substring(1, 2))-1;
            level = getLevel(coordinate);
        } catch(Exception e) {
        }
    }
    //konwertuje wspolrzedne na indeks tablicy
    private static int letterToIndex(String coordinate) {
        switch(getLevel(coordinate)) {
            case 0 :
                //zwraca indeks tablicy i pobiera wartość znaku ascii i przesuwa go tak by a = 0, b =1 ...
                return (int)coordinate.charAt(0) - 97;
            case 1 :
                return (int)coordinate.charAt(0) - 101;
            case 2 :
                return (int)coordinate.charAt(0) - 104;
            case 3 :
                return (int)coordinate.charAt(0) - 106;
            default :
                return -1;
        }
    }

    private static int getLevel(String coordinate) {
        if("abcd".contains(coordinate.substring(0, 1))) {
            return 0;
        } else if("efg".contains(coordinate.substring(0, 1))) {
            return 1;
        } else if("hi".contains(coordinate.substring(0, 1))) {
            return 2;
        } else if("j".equals(coordinate.substring(0, 1))) {
            return 3;
        }
        return -1;
    }


}
