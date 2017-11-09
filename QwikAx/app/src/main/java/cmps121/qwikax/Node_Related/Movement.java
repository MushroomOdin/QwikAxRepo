package cmps121.qwikax.Node_Related;

import java.util.ArrayList;

/**
 * Created by andrew on 10/18/2017.
 * Fundamental purpose of this class is to handle the movements of the user. Will be used by the data base object to for storing
 * saved movements.
 */


public class Movement {

    // ENUMERATION

    public enum MovementType{
        RIGHT (0),
        LEFT (1),
        UP (2),
        DOWN (3),
        BOTTOM_RIGHT (4),
        BOTTOM_LEFT (5),
        TOP_RIGHT (6),
        TOP_LEFT (7),
        INITIAL_POSITION (8);

        private final int value;
        MovementType (int value) {this.value = value;}
        public int getValue() {return value;}

        public static MovementType Convert(int position){
            MovementType type = null;
            switch (position){
                case 0:
                    type = RIGHT;
                    break;

                case 1:
                    type = LEFT;
                    break;

                case 2:
                    type = UP;
                    break;

                case 3:
                    type = DOWN;
                    break;

                case 4:
                    type = BOTTOM_RIGHT;
                    break;

                case 5:
                    type = BOTTOM_LEFT;
                    break;

                case 6:
                    type = TOP_RIGHT;
                    break;

                case 7:
                    type = TOP_LEFT;
                    break;

                case 8:
                    type = INITIAL_POSITION;
                    break;
            }

            return type;
        }
    }

    // ENUMERATION

    // FIELDS

    private CoordinateSystem[][] _items;
    private int[] _xPossiblePositions;
    private int[] _yPossiblePositions;
    private int[] _previousPosition;

    private int _rows;
    private int _columns;
    private ArrayList<MovementType> _movementsMade;
    private ArrayList<MovementType> _currentMovements;

    // FIELDS

    // CONSTRUCTORS

    public Movement(CoordinateSystem[][] items, int rows, int columns){
        _items = items;
        _rows = rows;
        _columns = columns;
        _currentMovements = new ArrayList<>();
        Reset();
    }

    public Movement(CoordinateSystem[][] items, int rows, int columns, ArrayList<MovementType> types){
        _items = items;
        _rows = rows;
        _columns = columns;
        _movementsMade = types;
        _currentMovements = new ArrayList<>();
    }

    // CONSTRUCTORS

    // METHODS

    private void CheckForDiagonal(ArrayList<MovementType> movements){
        MovementType currentMovement = movements.get(movements.size() - 1);
        MovementType change = null;
        if(movements.size() >= 2) {
            MovementType lastMovement = movements.get(movements.size() - 2);
            switch (lastMovement) {
                case UP:
                    if (currentMovement == MovementType.RIGHT)
                        change = MovementType.TOP_RIGHT;
                    else if (currentMovement == MovementType.LEFT)
                        change = MovementType.TOP_LEFT;
                    break;

                case DOWN:
                    if (currentMovement == MovementType.RIGHT)
                        change = MovementType.BOTTOM_RIGHT;
                    else if (currentMovement == MovementType.LEFT)
                        change = MovementType.BOTTOM_LEFT;
                    break;

                case LEFT:
                    if (currentMovement == MovementType.UP)
                        change = MovementType.TOP_LEFT;
                    else if (currentMovement == MovementType.DOWN)
                        change = MovementType.BOTTOM_LEFT;
                    break;

                case RIGHT:
                    if (currentMovement == MovementType.UP)
                        change = MovementType.TOP_RIGHT;
                    else if (currentMovement == MovementType.DOWN)
                        change = MovementType.BOTTOM_RIGHT;
                    break;

                case BOTTOM_LEFT:
                case BOTTOM_RIGHT:
                case TOP_LEFT:
                case TOP_RIGHT:
                    // Does nothing
                    break;
            }
        }

        if(change != null){
            _movementsMade.remove(_movementsMade.size() - 1);
            _movementsMade.remove(_movementsMade.size() - 1);
            _movementsMade.add(change);
        }
    }

    // Coppies the current object to presever the original.
    public Movement Copy(){
       return new Movement(this._items, this._rows, this._columns, this._movementsMade);
    }

    // Finds the location of the view depending on the initial x,y location.
    public int[] FindInitialViewByLocation(float x, float y) {
        int[] positions = {-1,-1};
        // Find out how to tell location from
        for (int row = 0; row < _rows; row++) {
            for(int column = 0; column < _columns; column++){
                if(_items[row][column].isWithinBounds(x,y)){
                    positions[0] = row;
                    positions[1] = column;
                    row = _rows;
                    column = _columns;
                }
            }
        }

        return positions;
    }

    private void FindPossibleMovements(int[] positions, int distance) {
        int tiles = 8 * distance, tileCount = 0;
        int verticalGap = 1 + (2 * (distance - 1)), count = 0;
        int xPos = positions[0], yPos =  positions[1];
        int horizontalGap = (2 * (distance)) + 1;
        _xPossiblePositions = new int[tiles];
        _yPossiblePositions = new int[tiles];
        // This does the horizontal areas around the start position
        while(xPos - distance + count <= xPos + distance) {
            if ((xPos - distance + count >= 0) && (xPos - distance + count < _columns) && (yPos - distance >= 0)) {
                _xPossiblePositions[count] = xPos - distance + count;
                _yPossiblePositions[count] = yPos - distance;
            } else {
                _xPossiblePositions[count] = -1;
                _yPossiblePositions[count] = -1;
            }

            if ((yPos + distance < _rows) && (xPos + distance - count >= 0) && (xPos + distance - count < _columns)) {
                _xPossiblePositions[count + horizontalGap + verticalGap] = xPos + distance - count;
                _yPossiblePositions[count++ + horizontalGap + verticalGap] = yPos + distance;
            } else {
                _xPossiblePositions[count + horizontalGap] = -1;
                _yPossiblePositions[count++ + horizontalGap] = -1;
            }

        }

        count = 0;
        // this does the vertical areas around the start position
        while(yPos - (distance - 1) + count <= yPos + (distance - 1)) {
            if ((yPos - (distance - 1) + count >= 0) && (yPos + (distance - 1) + count < _rows) && (xPos + distance < _columns)) {
                _xPossiblePositions[count + horizontalGap] = xPos + distance;
                _yPossiblePositions[count + horizontalGap] = yPos - (distance - 1) + count;
            } else {
                _xPossiblePositions[count + horizontalGap] = -1;
                _yPossiblePositions[count + horizontalGap] = -1;
            }

            if ((yPos + (distance - 1) - count >= 0) && (yPos + (distance - 1) - count < _rows) && (xPos + distance >= 0)) {
                _xPossiblePositions[count + (horizontalGap * 2) + verticalGap] = xPos - distance;
                _yPossiblePositions[count + (horizontalGap * 2) + verticalGap] = yPos + (distance - 1) + count++;
            } else {
                _xPossiblePositions[count + horizontalGap + verticalGap] = -1;
                _yPossiblePositions[count++ + horizontalGap + verticalGap] = -1;
            }

        }
    }

    // Finds the location of the view we moved to.
    private int[] FindViewByLocation(float x, float y, int distance){
        int[] positions = {-1,-1};
        int count;
        for(count = 0; count < _xPossiblePositions.length; count++){
            int xPos = _xPossiblePositions[count];
            int yPos = _yPossiblePositions[count];
            if(((xPos != -1) && (yPos != -1)) ? _items[xPos][yPos].isWithinBounds(x,y) : false)
                break;
        }

        if(count < _xPossiblePositions.length){
            positions[0] = _xPossiblePositions[count];
            positions[1] = _yPossiblePositions[count];
        }

        return positions;
    }

    private ArrayList<MovementType> FindShortestPath(int[] start, int[] end){
        ArrayList<MovementType> movements = new ArrayList<>();
        int xDifference = start[0] - end[0];
        int yDifference = start[1] - end[1];

        while((xDifference != 0) || (yDifference != 0)){
            if(xDifference > 0){
                movements.add(MovementType.DOWN);
                xDifference--;
            }else if(xDifference < 0){
                movements.add(MovementType.UP);
                xDifference++;
            }

            if(yDifference > 0){
                movements.add(MovementType.LEFT);
                yDifference--;
            }else if(yDifference < 0){
                movements.add(MovementType.RIGHT);
                yDifference++;
            }

            CheckForDiagonal(movements);
        }

        return movements;
    }

    public ArrayList<MovementType> GetMovements(){
        return _currentMovements;
    }

    // Called by touch listener down press, will capture the initial position, and then find where the possible movements are.
    public int[] InitialPosition(float x, float y){
        Reset();
        int[] positions = FindInitialViewByLocation(x,y);
        if(positions[0] != -1){
            _movementsMade.add(MovementType.INITIAL_POSITION);
            _previousPosition = positions;
        }

        return positions;
    }


    // Tells us that a movement has occured, and that we need to figure out where we will go.
    public boolean MovementOccurred(float x, float y) {
        int count = 1;
        boolean moved = false;
        int[] endPosition = new int[2];
        if(!_items[_previousPosition[0]][_previousPosition[1]].isWithinBounds(x,y)) {
            do {
                FindPossibleMovements(_previousPosition, count);
                endPosition = FindViewByLocation(x, y, count++);
            } while (endPosition[0] == -1 && (count < 5));

            if(endPosition[0] == -1)
                endPosition = FindInitialViewByLocation(x,y);

            _currentMovements.clear();
            ArrayList<MovementType> movements = FindShortestPath(_previousPosition, endPosition);
            _currentMovements.addAll(movements);
            _movementsMade.addAll(movements);
            moved = true;
        }else{
            endPosition[0] = _previousPosition[0];
            endPosition[1] = _previousPosition[1];
        }

        return moved;
    }

    // Resets the class so that we start off fresh with a new down click.
    public void Reset(){
        _movementsMade = new ArrayList<>();
    }

    // METHODS

    // GETTERS AND SETTERS

    public ArrayList<MovementType> get_movementsMade(){ return _movementsMade; }
    public CoordinateSystem[][] get_items(){return _items; }

    // GETTERS AND SETTERS
}
