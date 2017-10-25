package cmps121.qwikax.Data_Base;

import java.io.Serializable;
import java.util.ArrayList;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/24/2017.
 */

public class DataBaseHandler implements Serializable {

    // CONSTRUCTORS

    public DataBaseHandler(){
        _masterNode = new DataBaseNode();
        _traversableNode = null;
        _possibleMatches = new ArrayList<>();
    }

    public DataBaseHandler(DataBaseNode master){
        _masterNode = master;
        _traversableNode = null;
        _possibleMatches = new ArrayList<>();
    }

    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode _masterNode;
    private DataBaseNode _traversableNode;
    private ArrayList<AppStorage> _possibleMatches;

    // FIELDS

    // GETTERS

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_possibleMatches(){return _possibleMatches;}
    public DataBaseNode get_traversableNode(){return _traversableNode;}

    // GETTERS

    // METHODS

    private void FindAllPossibleApplicationsInTree(DataBaseNode node){
        for(int count = 0; count < 8; count++){
            DataBaseNode currentNode = node.GetDesiredDataBaseNode(Movement.MovementType.Convert(count));
            if( currentNode != null) {
                if(currentNode.get_appStorage().size() != 0)
                    _possibleMatches.addAll(currentNode.get_appStorage());

                FindAllPossibleApplicationsInTree(currentNode);
            }
        }
    }

    public void NextMovement(Movement.MovementType type){
        if(_traversableNode == null)
            _traversableNode = _masterNode.GetDesiredDataBaseNode(type);
        else{
            DataBaseNode nextNode = _traversableNode.GetDesiredDataBaseNode(type);
            if(nextNode != null)
                _traversableNode = nextNode;
            else{
                _traversableNode.SetDesiredDataBaseNode(type, new DataBaseNode());
            }
        }
            _traversableNode = _traversableNode.GetDesiredDataBaseNode(type);

        FindAllPossibleApplicationsInTree(_traversableNode);
    }


    // METHODS
}
