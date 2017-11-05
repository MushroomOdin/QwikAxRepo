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
        _currentMatches = new ArrayList<>();
        _potentialMatches = null;
    }

    public DataBaseHandler(DataBaseNode master){
        _masterNode = master;
        _traversableNode = null;
        _currentMatches = new ArrayList<>();
        _potentialMatches = null;
    }

    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode _masterNode;
    private DataBaseNode _traversableNode;
    private ArrayList<AppStorage> _currentMatches;
    private ArrayList<AppStorage> _potentialMatches;

    // FIELDS

    // GETTERS

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_currentMatches(){return _currentMatches;}
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

    private void FindAllPossibleApplicationsInTree(DataBaseNode node, ArrayList<AppStorage> list){
        for(int count = 0; count < 8; count++){
            DataBaseNode currentNode = node.MoveToDesiredDataBaseNode(Movement.MovementType.Convert(count));
            if( currentNode != null) {
                if(currentNode.get_appStorage().size() != 0) {
                    list.addAll(currentNode.get_appStorage());
                    RemoveDuplicatesFromList(list);
                }

                FindAllPossibleApplicationsInTree(currentNode, list);
            }
        }
    }

    public void InitialMovement(){
        _traversableNode = _masterNode;
        _currentMatches.clear();
         FindAllPossibleApplicationsInTree(_traversableNode,_currentMatches);
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(Movement.MovementType type, ArrayList<Movement> movements) {
        DataBaseNode nextNode;
        if (_traversableNode == null)
            _traversableNode = _masterNode.MoveToDesiredDataBaseNode(type);
        else {
            switch (type) {
                case BOTTOM_LEFT:
                case BOTTOM_RIGHT:
                case TOP_LEFT:
                case TOP_RIGHT:
                case DOWN:
                case UP:
                case LEFT:
                case RIGHT:
                    nextNode = _traversableNode.MoveToDesiredDataBaseNode(type);
                break;

                default:
                    nextNode = null;
            }

            if (nextNode != null) {
                _traversableNode = nextNode;
                _currentMatches.clear();
                FindAllPossibleApplicationsInTree(_traversableNode, _currentMatches);
                _currentMatches = SortPossibleApplicationsList(_currentMatches);
            }else {
                _traversableNode.SetDesiredDataBaseNode(type, new DataBaseNode(_traversableNode));
                _traversableNode = _traversableNode.MoveToDesiredDataBaseNode(type);
            }
        }
    }

    private void RemoveDuplicatesFromList(ArrayList<AppStorage> list){
        ArrayList<String> itemNames = new ArrayList<>();
        int count = 0;
        while(count < list.size()) {
            String name = list.get(count).get_abesoluteName();
            if (itemNames.contains(name))
                list.remove(count);
            else
                itemNames.add(name);

            count++;
        }
    }

    private ArrayList<AppStorage> SortPossibleApplicationsList(ArrayList<AppStorage> list) {
        ArrayList<AppStorage> comparison = new ArrayList<>();
        for (int count = 0; count < list.size(); count++) {
            if (comparison.size() == 0)
                comparison.add(list.get(count));
            else {
                AppStorage temp = list.get(count);
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

        return comparison;
    }

    // METHODS
}
