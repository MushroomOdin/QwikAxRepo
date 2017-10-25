package cmps121.qwikax.Data_Base;

import java.io.Serializable;
import java.util.ArrayList;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/24/2017.
 */

public class DataBaseNode implements Serializable {

    // CONSTRUCTORS

    public DataBaseNode(){
        _pointers = new DataBaseNode[8];
        _appStorage = new ArrayList<>();
    }

    public DataBaseNode(AppStorage appStorage){
        _pointers = new DataBaseNode[8];
        _appStorage = new ArrayList<>();
        _appStorage.add(appStorage);
    }

    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode[] _pointers;
    private ArrayList<AppStorage> _appStorage;

    // FIELDS

    // GETTERS

    public DataBaseNode[] get_pointers(){return _pointers;}
    public ArrayList<AppStorage> get_appStorage(){return _appStorage;}

    // GETTERS

    // METHODS

    public DataBaseNode GetDesiredDataBaseNode(Movement.MovementType type){
        return _pointers[type.getValue()];
    }

    public void SetDesiredDataBaseNode(Movement.MovementType type, DataBaseNode node){
        if(_pointers[type.getValue()] == null)
            _pointers[type.getValue()] = node;

    }

    public void AddAppStorageToList(AppStorage app){
        _appStorage.add(app);
    }

    // METHODS
}
