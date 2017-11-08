package cmps121.qwikax.Data_Base;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/24/2017.
 */

public class DataBaseNode implements Serializable {

    // CONSTRUCTORS

    //DO not use this but for a mast node.
    public DataBaseNode(){
        _pointers = new DataBaseNode[9];
        _appStorage = new ArrayList<>();
    }

    // This should be used for most constructors.
    public DataBaseNode(DataBaseNode previousNode){
        _pointers = new DataBaseNode[9];
        _pointers[8] = previousNode;
        _appStorage = new ArrayList<>();
    }

    public DataBaseNode(AppStorage appStorage, DataBaseNode previousNode){
        _pointers = new DataBaseNode[9];
        _pointers[8] = previousNode;
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

    public DataBaseNode MoveToDesiredDataBaseNode(Movement.MovementType type){
        if(_pointers[type.getValue()] == null)
            _pointers[type.getValue()] = new DataBaseNode(this);

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
