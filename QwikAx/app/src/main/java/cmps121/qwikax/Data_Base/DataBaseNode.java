package cmps121.qwikax.Data_Base;

import android.os.Parcel;
import android.os.Parcelable;
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
        _appStorage = null;
    }

    // This should be used for most constructors.
    public DataBaseNode(DataBaseNode previousNode){
        _pointers = new DataBaseNode[9];
        _pointers[8] = previousNode;
        _appStorage = null;
    }


    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode[] _pointers;
    private AppStorage _appStorage;

    private static final long serialVersionUID = 3128594851129501739L;

    // FIELDS

    // GETTERS

    public DataBaseNode[] get_pointers(){return _pointers;}
    public AppStorage get_appStorage(){return _appStorage;}
    public void set_appStorage(AppStorage appStorage){_appStorage = appStorage; }

    // GETTERS

    // METHODS

    public boolean CheckPreviousNodeForSeries(Movement.MovementType move) {
        int count = 0;
        boolean value = false;
        boolean done = false;
        DataBaseNode node = this;
        DataBaseNode previousNode;
        while (((previousNode = node.MoveToDesiredDataBaseNode(Movement.MovementType.INITIAL_POSITION)) != null) && (count < 2) && !done) {
            if (previousNode.MoveToDesiredDataBaseNode(move) == node) {
                node = previousNode;
                count++;
            }else
                done = true;
        }

        if (count >= 2)
            value = true;


        return value;
    }

    public DataBaseNode MoveToDesiredDataBaseNode(Movement.MovementType type) {
        DataBaseNode node = null;
        if(type != null)
            node =  _pointers[type.getValue()];

        return node;
    }

    public void SetDesiredDataBaseNode(Movement.MovementType type, DataBaseNode node){
        if(_pointers[type.getValue()] == null)
            _pointers[type.getValue()] = node;

    }


    public boolean AddAppStorageToList(AppStorage app){
        boolean inserted = false;
        if(_appStorage == null) {
            _appStorage = app;
            inserted = true;
        }

        return inserted;
    }

    // METHODS
}
