package cmps121.qwikax.Data_Base;

import android.app.Activity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import cmps121.qwikax.MainActivity;
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

    // Used in the creation of the tree / adding a new item to it. This should only be used when adding.
    // AppStorage contains the Movement class thusly we do not need to worry about adding it in anywhere else.
    public void AddNewItemToTree(AppStorage item){
        Movement movements = item.get_movement();
        DataBaseNode temp = _masterNode;

        for (Movement.MovementType type: movements.get_movementsMade()) {
            if(temp.MoveToDesiredDataBaseNode(type) == null){
                DataBaseNode[] currentNodeArray = temp.get_pointers();
                currentNodeArray[type.getValue()] = new DataBaseNode(temp);
            }

            temp = temp.MoveToDesiredDataBaseNode(type);
        }

        temp.AddAppStorageToList(item);
    }

    private void FindAllPossibleApplicationsInTree(DataBaseNode node){
        for(int count = 0; count < 8; count++){
            DataBaseNode currentNode = node.MoveToDesiredDataBaseNode(Movement.MovementType.Convert(count));
            if( currentNode != null) {
                if(currentNode.get_appStorage().size() != 0) {
                    _possibleMatches.addAll(currentNode.get_appStorage());
                    RemoveDuplicatesFromList();
                }

                FindAllPossibleApplicationsInTree(currentNode);
            }
        }
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(Movement.MovementType type) {
        DataBaseNode nextNode;
        if (_traversableNode == null)
            _traversableNode = _masterNode.MoveToDesiredDataBaseNode(type);
        else {
            switch (type) {
                case BOTTOM_LEFT:
                case BOTTOM_RIGHT:
                case TOP_LEFT:
                case TOP_RIGHT:
                    _traversableNode = _traversableNode.MoveToDesiredDataBaseNode(Movement.MovementType.INITIAL_POSITION);
                    nextNode = _traversableNode.MoveToDesiredDataBaseNode(type);
                    break;

                case DOWN:
                case UP:
                case LEFT:
                case RIGHT:
                    nextNode = _traversableNode.MoveToDesiredDataBaseNode(type);

                default:
                    nextNode = null;
            }

            if (nextNode != null)
                _traversableNode = nextNode;
            else {
                _traversableNode.SetDesiredDataBaseNode(type, new DataBaseNode(_traversableNode));
                _traversableNode = _traversableNode.MoveToDesiredDataBaseNode(type);
            }

        }

        FindAllPossibleApplicationsInTree(_traversableNode);
        SortPossibleApplicationsList();
    }

    private void RemoveDuplicatesFromList(){
        ArrayList<String> itemNames = new ArrayList<>();
        int count = 0;
        while(count < _possibleMatches.size()) {
            String name = _possibleMatches.get(count).get_abesoluteName();
            if (itemNames.contains(name))
                _possibleMatches.remove(count);
            else
                itemNames.add(name);

            count++;
        }
    }

    private void SortPossibleApplicationsList() {
        ArrayList<AppStorage> comparison = new ArrayList<>();
        for (int count = 0; count < _possibleMatches.size(); count++) {
            if (comparison.size() == 0)
                comparison.add(_possibleMatches.get(count));
            else {
                AppStorage temp = _possibleMatches.get(count);
                Boolean added = false;
                for (int i = 0; i < comparison.size(); i++) {
                    AppStorage.AccessabilityLevels level = comparison.get(i).get_accessabilityLevel();
                    if (level.getValue() < temp.get_accessabilityLevel().getValue()) {
                        comparison.add(i, temp);
                        added = true;
                    } else if ((level.getValue() == temp.get_accessabilityLevel().getValue()) && (comparison.get(i).get_timesAccessed() < temp.get_timesAccessed())) {
                        comparison.add(i, temp);
                        added = true;
                    }
                }

                if(!added)
                    comparison.add(temp);
            }
        }

        _possibleMatches = comparison;
    }

    // METHODS
}
