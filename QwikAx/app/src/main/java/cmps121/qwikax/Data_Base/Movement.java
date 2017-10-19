package cmps121.qwikax.Data_Base;

import android.view.MotionEvent;

import java.util.List;

import cmps121.qwikax.Node_Related.IndividualNode;

/**
 * Created by andrew on 10/18/2017.
 * Fundamental purpose of this class is to handle the movements of the user. Will be used by the data base object to for storing
 * saved movements.
 */


public class Movement {

    // FIELDS

    private List<IndividualNode> _items;
    private int _initialPosition;

    // FIELDS

    // CONSTRUCTORS

    public Movement(List<IndividualNode> items){
        _items = items;
    }

    // CONSTRUCTORS

    // METHODS

    // Called by touch listener down press, will capture the initial position, and then find where the possible movements are.
    public void InitialMovement(int x, int y){

    }

    // METHODS
}
