package cmps121.qwikax.Data_Base;

import android.util.Log;

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
        _traversalNode = null;
        _currentMatches = new ArrayList<>();
        _potentialMatches = null;
        _errorThrown = false;
    }


    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode _masterNode;
    private DataBaseNode _traversalNode;
    private ArrayList<AppStorage> _currentMatches;
    private ArrayList<AppStorage> _potentialMatches;
    private boolean _errorThrown;

    private static final long serialVersionUID = 3128594851129501738L;

    // FIELDS

    // GETTERS

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_currentMatches(){return _currentMatches;}
    public DataBaseNode get_traversalNode(){return _traversalNode;}
    public boolean get_errorThrown(){return _errorThrown;}

    // GETTERS

    // METHODS

    // Used in the creation of the tree / adding a new item to it. This should only be used when adding.
    // AppStorage contains the Movement class thusly we do not need to worry about adding it in anywhere else.
    public void AddNewItemToTree(AppStorage item, ArrayList<Movement.MovementType> movementsMade){
        try {
            DataBaseNode temp = _masterNode;

            for (Movement.MovementType type : movementsMade) {
                if (temp.MoveToDesiredDataBaseNode(type) == null) {
                    DataBaseNode[] currentNodeArray = temp.get_pointers();
                    currentNodeArray[type.getValue()] = new DataBaseNode(temp);
                }

                temp = temp.MoveToDesiredDataBaseNode(type);
            }

            temp.AddAppStorageToList(item);
        }catch (Exception ex) {
            Log.e("ERROR", "Add new item to tree inside data base handler had an error\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private void FindAllPossibleApplicationsInTree(DataBaseNode node, ArrayList<AppStorage> list){
        try{
            for(int count = 0; count < 8; count++){
                DataBaseNode currentNode = node.get_pointers()[count];
                if( currentNode != null) {
                    if(currentNode.get_appStorage().size() != 0) {
                        list.addAll(currentNode.get_appStorage());
                        RemoveDuplicatesFromList(list);
                    }

                    FindAllPossibleApplicationsInTree(currentNode, list);
                }
            }
        }catch(Exception ex){
            Log.e("ERROR", "Find all possible applications in tree in data base handler had an error.\n" + ex.getMessage());
            _errorThrown = true;
        }

    }

    public void InitialMovement(){
        _traversalNode = _masterNode;
        _currentMatches.clear();
        _errorThrown = false;
         FindAllPossibleApplicationsInTree(_traversalNode,_currentMatches);
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(ArrayList<Movement.MovementType> type) {
        try {
            for (Movement.MovementType current : type) {
                if (_traversalNode == null)
                    _traversalNode = _masterNode.MoveToDesiredDataBaseNode(current);
                else {
                    _traversalNode = _traversalNode.MoveToDesiredDataBaseNode(current);
                    _currentMatches.clear();
                    FindAllPossibleApplicationsInTree(_traversalNode, _currentMatches);
                    _currentMatches = SortPossibleApplicationsList(_currentMatches);
                }
            }
        }catch(Exception ex){
            Log.e("ERROR", "Next Movement inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private void RemoveDuplicatesFromList(ArrayList<AppStorage> list){
        try {
            ArrayList<String> itemNames = new ArrayList<>();
            int count = 0;
            while (count < list.size()) {
                String name = list.get(count).get_abesoluteName();
                if (itemNames.contains(name))
                    list.remove(count);
                else
                    itemNames.add(name);

                count++;
            }
        }catch(Exception ex) {
            Log.e("ERROR", "Remove Duplicates from list inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private ArrayList<AppStorage> SortPossibleApplicationsList(ArrayList<AppStorage> list) {
        ArrayList<AppStorage> comparison = new ArrayList<>();
        try {
            for (int count = 0; count < list.size(); count++) {
                if (comparison.size() == 0)
                    comparison.add(list.get(count));
                else {
                    AppStorage temp = list.get(count);
                    Boolean added = false;
                    for (int i = 0; i < comparison.size(); i++) {
                        AppStorage.AccessibilityLevels level = comparison.get(i).get_accessabilityLevel();
                        if (level.getValue() < temp.get_accessabilityLevel().getValue()) {
                            comparison.add(i, temp);
                            added = true;
                        } else if ((level.getValue() == temp.get_accessabilityLevel().getValue()) && (comparison.get(i).get_timesAccessed() < temp.get_timesAccessed())) {
                            comparison.add(i, temp);
                            added = true;
                        }
                    }

                    if (!added)
                        comparison.add(temp);
                }
            }
        }catch(Exception ex) {
            Log.e("ERROR", "Sort possible applications list inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }

        return comparison;
    }

    // METHODS
}
