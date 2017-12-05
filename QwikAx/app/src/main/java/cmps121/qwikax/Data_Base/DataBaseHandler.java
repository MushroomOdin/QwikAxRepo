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

    public DataBaseHandler(){
        _masterNode = new DataBaseNode();
        _traversalNode = null;
        _currentMatches = new ArrayList<>();
        _errorThrown = false;
        _comparePercentHigh = 100;
        _comparePercentLow = 90;
    }

    public DataBaseHandler(DataBaseNode node, ArrayList<DataBaseNode> travel, ArrayList<AppStorage> current,
                           boolean error, double percentHigh, double percentLow){
        _masterNode = node;
        _traversalNode = travel;
        _currentMatches = current;
        _errorThrown = error;
        _comparePercentHigh = percentHigh;
        _comparePercentLow = percentLow;
    }

    // CONSTRUCTORS

    // FIELDS

    private DataBaseNode _masterNode;
    private ArrayList<DataBaseNode> _traversalNode;

    private ArrayList<AppStorage> _currentMatches;
    private Movement.MovementType _movementTrend;

    private int _index;
    private ArrayList<Integer> _predicitveStepsCount;

    private boolean _isFirst;
    private boolean _errorThrown;
    private double _comparePercentLow;
    private double _comparePercentHigh;

    private static final long serialVersionUID = 3128594851129501738L;

    // FIELDS

    // GETTERS AND SETTERS
    public DataBaseHandler Copy(){
        return new DataBaseHandler(this._masterNode, this._traversalNode, this._currentMatches,
                this._errorThrown, this._comparePercentHigh,this._comparePercentLow);
    }

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_currentMatches(){return _currentMatches;}
    public ArrayList<DataBaseNode> get_traversalNode(){return _traversalNode;}
    public boolean get_errorThrown(){return _errorThrown;}

    public String get_MostlikelyMatchName(){
        String appName  = null;
        if(_currentMatches.size() >= 0){
            AppStorage app = _currentMatches.get(0);
            app.IncrementTimesAccessed();
            appName = app.get_abesoluteName();
        }

        return appName;
    }

    // GETTERS AND SETTERS

    // METHODS

    // Used in the creation of the tree / adding a new item to it. This should only be used when adding.
    // AppStorage contains the Movement class thusly we do not need to worry about adding it in anywhere else.
    public void AddNewItemToTree(AppStorage item){
        try {
            DataBaseNode temp = _masterNode;
            for (Movement.MovementType type : item.get_appMovements()) {
                if ((type != Movement.MovementType.INITIAL_POSITION) && (temp != null))
                    temp = temp.MoveToDesiredDataBaseNode(type);
                else
                    temp.get_pointers()[type.getValue()] = new DataBaseNode(temp);
            }

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

    public void FindAllPossibleApplicationsPastNode(ArrayList<DataBaseNode> nodes, ArrayList<AppStorage> list){
        for (DataBaseNode node:nodes) {
            FindAllPossibleApplicationsPastNode(node, list);
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
        _traversalNode = new ArrayList<>();
        _traversalNode.add(_masterNode);
        _currentMatches.clear();
        _errorThrown = false;
        _index = 0;
        _predicitveStepsCount = new ArrayList<>();
        _predicitveStepsCount.add(0,0);

        FindAllPossibleApplicationsPastNode(_traversalNode,_currentMatches);
    }

    public boolean LookAtPreviousMovements(int size){
        boolean foundItems = false;
        for(DataBaseNode node: _traversalNode){
            for(int count = 0; count < size / 5; count++){
                DataBaseNode temp = node.MoveToDesiredDataBaseNode(Movement.MovementType.INITIAL_POSITION);
                ArrayList<AppStorage> list =  temp.get_appStorage();
                if(list.size() > 0){
                    _currentMatches.addAll(list);
                    if(!foundItems)
                        foundItems = false;
                }
            }
        }

        if(foundItems) {
            RemoveDuplicatesFromList(_currentMatches);
            //_currentMatches = SortPossibleApplicationsList(_currentMatches)
        }

        return foundItems;
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(ArrayList<Movement.MovementType> types) {
        try {
            for (Movement.MovementType current : types) {
                ArrayList<DataBaseNode> tempList = _traversalNode;
                for (DataBaseNode node:tempList) {
                    _isFirst = true;
                    _index = _traversalNode.indexOf(node);
                    if ((node != null) && (_predicitveStepsCount.get(_index) <= 3)) {
                        DataBaseNode restore = node;
                        _traversalNode.set(_index, node.MoveToDesiredDataBaseNode(current));
                        _currentMatches.clear();
                        if (_traversalNode.get(_index) == null) {
                            // Restore position
                            _traversalNode.set(_index, restore);
                            PredictiveStep(node);
                        }else {
                            _movementTrend = current;
                            _predicitveStepsCount.set(_index, 0);
                        }
                    }
                }

                FindAllPossibleApplicationsPastNode(_traversalNode, _currentMatches);
                //_currentMatches = SortPossibleApplicationsList(_currentMatches);
            }
        }catch(Exception ex){
            Log.e("ERROR", "Next Movement inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private void PredictiveStep(DataBaseNode node) {
        try {
            DataBaseNode temp = node;
            if (_index > _predicitveStepsCount.size())
                _predicitveStepsCount.set(_index, 1);
            else
                _predicitveStepsCount.set(_index, _predicitveStepsCount.get(_index) + 1);

            if ((node = node.MoveToDesiredDataBaseNode(_movementTrend)) == null) {
                node = temp;
                SearchMostProbableRoutes(node);
            } else
                _traversalNode.set(_index, node);
        }catch (Exception ex){
            Log.e("ERROR", "Predictive step inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
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

    private void Search(DataBaseNode node, Movement.MovementType check){
        try{
            if((node = node.MoveToDesiredDataBaseNode(check)) != null) {
                if (_isFirst) {
                    _traversalNode.set(_index, node);
                    _isFirst = !_isFirst;
                } else
                    _traversalNode.add(++_index, node);
            }
        }catch (Exception ex){
            Log.e("ERROR", "Search inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    // Uses  node and _current trend to find a route that is most probable for the search.
    private void SearchMostProbableRoutes(DataBaseNode node){
        try {
            int size = _traversalNode.size();
            DataBaseNode temp = _traversalNode.get(_index);
            if (_movementTrend != null) {
                int value = _movementTrend.getValue();
                // Right adjacent movement
                Search(node, Movement.MovementType.Convert(value + 1 % 8));
                // Left adjacent movement
                Search(node, Movement.MovementType.Convert(value + 7 % 8));
                // 2 positions right
                Search(node, Movement.MovementType.Convert(value + 2 % 8));
                // 2 Positions left
                Search(node, Movement.MovementType.Convert(value + 6 % 8));

                // If we did not find anything at that node we want to remove the node from possibilities
                if ((_traversalNode.size() == size) && (_traversalNode.get(_index) == temp)) {
                    _traversalNode.remove(_index);
                    _predicitveStepsCount.remove(_index);
                }
            }
        }catch (Exception ex){
            Log.e("ERROR", "Search most probable routes inside of Data Base Handler had and error.\n" + ex.getMessage());
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
