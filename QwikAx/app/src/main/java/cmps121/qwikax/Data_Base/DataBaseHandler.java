package cmps121.qwikax.Data_Base;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/24/2017.
 */

public class DataBaseHandler implements Serializable {

    // CONSTRUCTORS

    public DataBaseHandler(ArrayList<String> everyAppName){
        _masterNode = new DataBaseNode();
        _traversalNode = null;
        _currentMatches = new ArrayList<>();
        _errorThrown = false;
        _AppNames = everyAppName;
        _comparePercentHigh = 100;
        _comparePercentLow = 90;
    }

    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode _masterNode;
    private DataBaseNode _traversalNode;

    private ArrayList<AppStorage> _currentMatches;
    private ArrayList<String> _AppNames;
    private Movement.MovementType _movementTrend;
    private ArrayList<Movement.MovementType> _movementsMade;

    private boolean _errorThrown;
    private double _comparePercentLow;
    private double _comparePercentHigh;

    private static final long serialVersionUID = 3128594851129501738L;

    // FIELDS

    // GETTERS AND SETTERS

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_currentMatches(){return _currentMatches;}
    public DataBaseNode get_traversalNode(){return _traversalNode;}
    public boolean get_errorThrown(){return _errorThrown;}

    // GETTERS AND SETTERS

    // METHODS

    // Used in the creation of the tree / adding a new item to it. This should only be used when adding.
    // AppStorage contains the Movement class thusly we do not need to worry about adding it in anywhere else.
    public void AddNewItemToTree(AppStorage item){
        try {
            DataBaseNode temp = _masterNode;
            for (Movement.MovementType type : item.get_appMovements())
                if((type != Movement.MovementType.INITIAL_POSITION) && (temp != null))
                        temp = temp.MoveToDesiredDataBaseNode(type);

            temp.AddAppStorageToList(item);
        }catch (Exception ex) {
            Log.e("ERROR", "Add new item to tree inside data base handler had an error\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    public void DeleteItemFromTree(String relativeAppName){
        DeleteItemFromTree(_masterNode, relativeAppName);
    }

    private void DeleteItemFromTree(DataBaseNode node, String relativeAppName) {
        try {
            for (int count = 0; count < 8; count++) {
                DataBaseNode currentNode = node.get_pointers()[count];
                if (currentNode != null) {
                    ArrayList<AppStorage> currentAppList = currentNode.get_appStorage();
                    ArrayList<AppStorage> newAppList = new ArrayList<>();
                    for (AppStorage currentApp : currentAppList)
                        if (currentApp.get_relativeName().compareTo(relativeAppName) != 0)
                            newAppList.add(currentApp);

                    currentNode.set_appStorage(newAppList);
                    DeleteItemFromTree(currentNode, relativeAppName);
                }
            }
        }catch (Exception ex){
            Log.e("ERROR", "Delete Item From tree in data base handler had an error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    public void FindAllPossibleApplicationsPastNode(DataBaseNode node, ArrayList<AppStorage> list){
        try{
            for(int count = 0; count < 8; count++){
                DataBaseNode currentNode = node.get_pointers()[count];
                if( currentNode != null) {
                    if(currentNode.get_appStorage().size() != 0) {
                        list.addAll(currentNode.get_appStorage());
                        RemoveDuplicatesFromList(list);
                    }

                    FindAllPossibleApplicationsPastNode(currentNode, list);
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
         FindAllPossibleApplicationsPastNode(_traversalNode,_currentMatches);
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(ArrayList<Movement.MovementType> types) {
        try {
            for (Movement.MovementType current : types) {
                if (_traversalNode != null) {
                    DataBaseNode temp = _traversalNode;
                    _traversalNode = _traversalNode.MoveToDesiredDataBaseNode(current);
                    _currentMatches.clear();
                    _movementsMade.add(current);

                    if (_traversalNode == null) {
                        // Restore position
                        //_traversalNode = temp;
                        //PredictMovement(current);
                    }

                    FindAllPossibleApplicationsPastNode(_traversalNode, _currentMatches);
                    //_currentMatches = SortPossibleApplicationsList(_currentMatches);
                }
            }
        }catch(Exception ex){
            Log.e("ERROR", "Next Movement inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private boolean PredictiveStep(Movement.MovementType move, DataBaseNode temp){
        boolean value = true;
        if ((_traversalNode = _traversalNode.MoveToDesiredDataBaseNode(move)) == null) {
            _traversalNode = temp;
            value = false;
        }

        return value;
    }

    private void PredictMovement(Movement.MovementType current) {
        DataBaseNode temp = _traversalNode;
        boolean continueSearch = true;

        _movementsMade.remove(_movementsMade.size() - 1);
        if ((_traversalNode = _traversalNode.MoveToDesiredDataBaseNode(_movementTrend)) == null)
            continueSearch = true;

        if (continueSearch) {
            for (int count = 0; count < 8; count++) {
                Movement.MovementType predictiveMovement = Movement.MovementType.Convert(count);
                if ((_movementTrend.getValue() != count) && (current.getValue() != count) && PredictiveStep(predictiveMovement, temp)) {
                    _movementTrend = predictiveMovement;
                    break;
                }
            }

        }
    }


    private ArrayList<AppStorage> RemoveDuplicatesFromList(ArrayList<AppStorage> list){
        ArrayList<AppStorage> values = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
        try {
            int count = 0;
            while (count < list.size()) {
                String name = list.get(count).get_abesoluteName();
                if (!itemNames.contains(name)){
                    itemNames.add(name);
                    values.add(list.get(count));
                }

                count++;
            }
        }catch(Exception ex) {
            Log.e("ERROR", "Remove Duplicates from list inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }

        return values;
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
                        AppStorage.AccessibilityLevels level = comparison.get(i).get_accessibilityLevel();
                        if (level.getValue() < temp.get_accessibilityLevel().getValue()) {
                            comparison.add(i, temp);
                            added = true;
                        } else if ((level.getValue() == temp.get_accessibilityLevel().getValue()) && (comparison.get(i).get_timesAccessed() < temp.get_timesAccessed())) {
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
