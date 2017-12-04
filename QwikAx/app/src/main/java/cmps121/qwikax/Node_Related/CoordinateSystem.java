package cmps121.qwikax.Node_Related;

/**
 * Created by andrew on 10/10/2017.
 */

// Defines where we are and how we are looking at it.
// TODO: Change the name, it does not fit what it does/is
public class CoordinateSystem {

    // Top left x position.
    private double _xCoord;
    // Top left y position.
    private double _yCoord;
    // Length of the individual view, these should all be the same.
    private double _xDistance;
    // Height of the individual view, these should all be the same.
    private double _yDistance;
    // Current orientation for the given coordinate system.
    private int _screenOrientation;

    public CoordinateSystem(double xCoord, double yCoord, double xDist, double yDist, int screenOrientation) {
        _xCoord = xCoord;
        _yCoord = yCoord;
        _xDistance = xDist;
        _yDistance = yDist;
        _screenOrientation = screenOrientation;
    }

    // Checks if the given x,y coordinates are within the bounds of this view.
    public boolean isWithinBounds(double xPos, double yPos) {

        boolean value = false;
        if ((xPos >= _xCoord) && (xPos < _xCoord + _xDistance) && (yPos >= _yCoord) && (yPos < _yCoord + _yDistance))
            value = true;

        return value;
    }

    public double get_xCoord() {
        return _xCoord;
    }

    public double get_yCoord() {
        return _yCoord;
    }

    public double get_xDistance() {
        return _xDistance;
    }

    public double get_yDistance() {
        return _yDistance;
    }

    public int get_screenOrientation() {
        return _screenOrientation;
    }

    // Should be using getWindowManager().getDefaultDisplay().getRotation() to compare to the current'
    // Checks if the current orientation is the same as the recorded. If it is not, then we we adjust the coordinate system to reflect the changes.
    // Returns a true or false depending on if we changed the coordinate system or if we did not.
    public boolean requestOrientationChange(int current) {
        boolean value = false;
        if (_screenOrientation != current) {
            correctCoordinateOrientation(current);
            _screenOrientation = current;

            value = true;
        }

        return value;
    }

    // Determines the route we should take to correct the coordinates.
    // Surfase enum is integer value 0: Rotation_0, 1: Rotation_90, 2: Rotation_180, and 3: Rotation_270.
    // From that we can determine the value we need to change from our current to the new orientation
    private void correctCoordinateOrientation(int newOrientation)
    {
        switch (_screenOrientation - newOrientation) {
            case 1:
            case -3:
                rotate90DegreesCW(_xCoord,_yCoord,_xDistance,_yDistance);
                break;
            case 2:
            case -2:
                rotate180Degrees(_xCoord,_yCoord,_xDistance,_yDistance);
                break;

            case -1:
            case 3:
                rotate90DegreesCCW(_xCoord,_yCoord,_xDistance,_yDistance);
                break;
        }
    }

    // Rotates our axis by 90 degrees counter clockwise
    // TODO: get this working
    private void rotate90DegreesCCW(double xCoord, double yCoord, double xDist, double yDist) {
        _yDistance = xDist;
        _xDistance = yDist;
        _xCoord = xCoord - _xDistance;
        return;
    }

    // Rotates our axis by 90 degrees clockwise
    // TODO: get this working
    private void rotate90DegreesCW(double xCoord, double yCoord, double xDist, double yDist) {
        _yDistance = xDist;
        _xDistance = yDist;
        _yCoord = yCoord - _yDistance;
        return;
    }

    // Rotates our axis by 180 degrees
    // TODO: get this working
    private void rotate180Degrees(double xCoord, double yCoord, double xDist, double yDist) {
        _xCoord = xCoord - _xDistance;
        _yCoord = yCoord - _yDistance;
        return;
    }
}
