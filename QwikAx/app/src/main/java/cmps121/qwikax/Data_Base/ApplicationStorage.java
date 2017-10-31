package cmps121.qwikax.Data_Base;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 10/23/2017.
 */

public class ApplicationStorage {

    // FIELDS

    private Movement _movement;
    private String _absoluteName;
    private String _relativeName;
    private int _timesAccessed;

    // FIELDS

    // Constructor
    public ApplicationStorage(Movement movement, String absoluteName, String relativeName){
        _movement = movement;
        _absoluteName = absoluteName;
        _relativeName = relativeName;
        _timesAccessed = 0;
    }

    // METHODS
    public String get_absoluteName(){
        return _absoluteName;
    }


    //TODO: FIX movementEquals()
    public boolean movementEquals(Movement input){
        ArrayList<Movement.MovementType> inputMoves = input.get_movementsMade();
        ArrayList<Movement.MovementType> storedMoves = this._movement.get_movementsMade();

        int count = 0;
        boolean equal = true;
        for(Movement.MovementType stored : storedMoves){
            if(inputMoves.get(count) != storedMoves.get(count)){
                equal = false;
                Log.d("false", "FALSE");
            }
            count++;
        }
        return equal;
    }
    // METHODS

}
