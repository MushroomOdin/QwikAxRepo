package cmps121.qwikax.Data_Base;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cmps121.qwikax.Node_Related.IndividualNode;

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
    }


    // ENUMERATION

    // FIELDS

    private ArrayList<IndividualNode> _items;
    private int[] _possiblePositions;
    private int _initialPosition;
    private MovementType _lastMovement;
    private int _rows;
    private int _columns;
    private ArrayList<MovementType> _movementsMade;
    private ArrayList<Integer> _movementPositions;

    // FIELDS

    // CONSTRUCTORS

    public Movement(ArrayList<IndividualNode> items, int rows, int columns){
        _items = items;
        _possiblePositions = new int[4];
        _rows = rows;
        _columns = columns;
        Reset();
    }

    public Movement(Movement movement){
        _initialPosition = movement.get_initialPosition();
        _movementsMade = movement.get_movementsMade();
    }

    // CONSTRUCTORS

    // METHODS

    // Checks if we can make it a shorter distance by adding a diagonal.
    private void CheckForDiagonal(MovementType currentMovement){

        MovementType change = null;
        switch (_lastMovement){
            case UP:
                if(currentMovement == MovementType.RIGHT)
                    change = MovementType.TOP_RIGHT;
                else if(currentMovement == MovementType.LEFT)
                    change = MovementType.TOP_LEFT;
                break;

            case DOWN:
                if(currentMovement == MovementType.RIGHT)
                    change = MovementType.BOTTOM_RIGHT;
                else if(currentMovement == MovementType.LEFT)
                    change = MovementType.BOTTOM_LEFT;
                break;

            case LEFT:
                if(currentMovement == MovementType.UP)
                    change = MovementType.TOP_LEFT;
                else if(currentMovement == MovementType.DOWN)
                    change = MovementType.BOTTOM_LEFT;
                break;

            case RIGHT:
                if(currentMovement == MovementType.UP)
                    change = MovementType.TOP_RIGHT;
                else if(currentMovement == MovementType.DOWN)
                    change = MovementType.BOTTOM_RIGHT;
                break;

            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case TOP_LEFT:
            case TOP_RIGHT:
                // Does nothing
                break;
        }

        if(change != null){
            _lastMovement = change;
            int cont = 1;
            //Removes last two actions (ie {UP RIGHT} => {})
            while(cont < 3){
                _movementsMade.remove(_movementsMade.size() - 1);
                cont++;
            }
            _movementsMade.add(change);
        }

    }

    // Finds the location of the view depending on the initial x,y location.
    private int FindInitialViewByLocation(float x, float y) {
        int count = 0;
        // Find out how to tell location from
        for (IndividualNode node: _items) {
            if((node.get_coordinateSystem().isWithinBounds(x,y)) && (!node.isHighLight())){
                node.setHighLight(true);
                break;
            }

            count++;
        }

        _movementPositions.add(count);

        if(count > _items.size())
            count = -1;

        return count;
    }

    // Finds the possible movements depending on the initial position
    private void FindPossibleMovements(int position){
        _possiblePositions = new int[4];

        if(position + 1 < (_columns * (1 + (int) (position / _columns))))
            _possiblePositions[MovementType.RIGHT.getValue()] = (position + 1);
        else
            _possiblePositions[MovementType.RIGHT.getValue()] = -1;

        if(position - 1 >= (_columns * (int) (position / _columns)))
            _possiblePositions[MovementType.LEFT.getValue()] = (position - 1);
        else
            _possiblePositions[MovementType.LEFT.getValue()] = -1;

        if(position + _columns < _items.size())
            _possiblePositions[MovementType.DOWN.getValue()] = (position + _columns);
        else
            _possiblePositions[MovementType.DOWN.getValue()] = -1;

        if(position - _columns > 0)
            _possiblePositions[MovementType.UP.getValue()] = (position - _columns);
        else
            _possiblePositions[MovementType.UP.getValue()] = -1;
    }

    // Finds the location of the view we moved to.
    private int FindViewByLocation(float x, float y){
        int count = 0;
        for (int position: _possiblePositions) {
            if(position != -1)
                if(_items.get(position).get_coordinateSystem().isWithinBounds(x,y))
                    break;

            count++;
        }

        return count;
    }

    // Called by touch listener down press, will capture the initial position, and then find where the possible movements are.
    public int InitialPosition(float x, float y){
        Reset();
        int count = FindInitialViewByLocation(x,y);
        if(count != -1){
            _movementPositions.add(count);
            _initialPosition = count;
            _movementsMade.add(MovementType.INITIAL_POSITION);
            FindPossibleMovements(count);
        }

        return count;
    }

    // Tells us that a movement has occured, and that we need to figure out where we will go.
    public int MovementOccurred(float x, float y) {
        int position = FindViewByLocation(x, y);
        if (position < _possiblePositions.length) {
            MovementType currentMove = null;

            if (position == MovementType.RIGHT.getValue())
                currentMove = MovementType.RIGHT;
            else if (position == MovementType.LEFT.getValue())
                currentMove = MovementType.LEFT;
            else if (position == MovementType.UP.getValue())
                currentMove = MovementType.UP;
            else
                currentMove = MovementType.DOWN;

            _movementsMade.add(currentMove);
            if (_lastMovement != null)
                CheckForDiagonal(currentMove);

            _lastMovement = currentMove;
            int currentPosition = _possiblePositions[position];
            FindPossibleMovements(_possiblePositions[position]);
            _movementPositions.add(currentPosition);
            position = currentPosition;
        } else
            position = -1;


        return position;
    }

    // Resets the class so that we start off fresh with a new down click.
    public void Reset(){
        _movementPositions = new ArrayList<>();
        _movementsMade = new ArrayList<>();
        _lastMovement = null;
    }

    // METHODS

    // GETTERS AND SETTERS

    public int[] get_possiblePositions() { return _possiblePositions; }
    public ArrayList<MovementType> get_movementsMade(){ return _movementsMade; }
    public ArrayList<Integer> get_movementPositions(){ return _movementPositions; }
    public int get_initialPosition(){return _initialPosition;}

    // GETTERS AND SETTERS
}
