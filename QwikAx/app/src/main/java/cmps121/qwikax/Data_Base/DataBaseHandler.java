package cmps121.qwikax.Data_Base;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
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
        _errorThrown = false;
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

    private static final long serialVersionUID = 3128594851129501738L;

    // FIELDS

    // GETTERS AND SETTERS

    public DataBaseNode get_masterNode(){return _masterNode;}
    public ArrayList<AppStorage> get_currentMatches(){return _currentMatches;}
    public ArrayList<DataBaseNode> get_traversalNode(){return _traversalNode;}
    public boolean get_errorThrown(){return _errorThrown;}
    public String get_MostlikelyMatchName() {
        String appName = null;
        AppStorage app;

        if (_currentMatches.size() > 0) {
            app = _currentMatches.get(0);
            app.IncrementTimesAccessed();
            appName = app.get_abesoluteName();
        }
        /*else if(_currentMatches.size() > 1){
            double maxPercent = Double.MIN_VALUE;
            ArrayList<Double> percents = new ArrayList<>();
            for (AppStorage apps:_currentMatches)
                percents.add(CompareLists(runMoves, apps.get_appMovements()));

            for (double percent:percents)
                if(maxPercent < percent)
                    maxPercent = percent;

            app = _currentMatches.get(percents.indexOf(maxPercent));
            appName = app.get_abesoluteName();
        }*/

        return appName;
    }

    // GETTERS AND SETTERS

    // METHODS

    private void AddingRoutesToTraversableNode(DataBaseNode node, Movement.MovementType currentMove){
        try {
            int value = currentMove.getValue();
            for (int count = 0; count < 8; count++) {
                if (count != (value + 4) % 8)
                    Search(node, Movement.MovementType.Convert(count));
            }
        }catch (Exception ex){
            Log.e("ERROR", "Adding Routes to traversable node inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    // Used in the creation of the tree / adding a new item to it. This should only be used when adding.
    // AppStorage contains the Movement class thusly we do not need to worry about adding it in anywhere else.
    public boolean AddNewItemToTree(AppStorage item){
        boolean added = false;
        try {
            DataBaseNode temp = _masterNode;
            for (Movement.MovementType type : item.get_appMovements()) {
                if (type != Movement.MovementType.INITIAL_POSITION) {
                    DataBaseNode temp2 = temp;
                    if ((temp = temp.MoveToDesiredDataBaseNode(type)) == null) {
                        temp = temp2;
                        temp.get_pointers()[type.getValue()] = new DataBaseNode(temp);
                        temp = temp.MoveToDesiredDataBaseNode(type);
                    }
                }
            }

            //TODO: add in search for addition in the future.
            /*if(AbstractSearch(item.get_appMovements())) {
                _message = "The gesture was too similar to: " + _currentMatches.get(0).get_relativeName();
                added = false;
            }else {
                if (!temp.AddAppStorageToList(item)) {
                    _message = "The gesture was too similar to: " + temp.get_appStorage().get_relativeName();
                    added = false;
                }else
                    added = true;
            }*/
            if (!temp.AddAppStorageToList(item))
                added = false;
            else
                added = true;

        }catch (Exception ex) {
            Log.e("ERROR", "Add new item to tree inside data base handler had an error\n" + ex.getMessage());
            _errorThrown = true;
        }

        return added;
    }

    private double CompareLists(ArrayList<Movement.MovementType> runMoves, ArrayList<Movement.MovementType> appList){
        if(runMoves.contains(Movement.MovementType.INITIAL_POSITION))
            runMoves.remove(Movement.MovementType.INITIAL_POSITION);

        int size = (runMoves.size() > appList.size()) ? appList.size() : runMoves.size();
        int count = 0;
        for(int listCount = 0; listCount < size; listCount++){
            if(runMoves.get(listCount) == appList.get(listCount))
                count++;
        }

        return (count / size) * 100;
    }

    private ArrayList<Movement.MovementType> CompressMovements(ArrayList<Movement.MovementType> list){
        ArrayList<Movement.MovementType> values = new ArrayList<>();
        Movement.MovementType current = null;
        for (Movement.MovementType test:list) {
            if(test != current) {
                values.add(test);
                current = test;
            }
        }

        return values;
    }

    public void DeleteItemFromTree(String relativeAppName){
        DeleteItemFromTree(_masterNode, relativeAppName);
    }

    public void DeleteItemFromTree(DataBaseNode node, String relativeAppName) {
        try {
            for (int count = 0; count < 8; count++) {
                DataBaseNode currentNode = node.get_pointers()[count];
                if (currentNode != null) {
                    AppStorage currentApp = currentNode.get_appStorage();
                    if (currentApp.get_relativeName().compareTo(relativeAppName) == 0)
                        currentNode.set_appStorage(null);

                    DeleteItemFromTree(currentNode, relativeAppName);
                }
            }
        }catch (Exception ex){
            Log.e("ERROR", "Delete Item From tree in data base handler had an error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    public boolean FinalSearch(ArrayList<Movement.MovementType> list, ArrayList<DataBaseNode> nodes ){
        boolean found = false;

        return found;
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
                    if(currentNode.get_appStorage() != null) {
                        list.add(currentNode.get_appStorage());
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
        ReinstateDataBase();
        _traversalNode.add(_masterNode);
        _predicitveStepsCount.add(0,0);
        FindAllPossibleApplicationsPastNode(_traversalNode,_currentMatches);
    }

    // Used for comparison of a run mode based item only.
    public void NextMovement(ArrayList<Movement.MovementType> types) {
        try {
            Movement.MovementType[] test = {Movement.MovementType.DOWN,Movement.MovementType.DOWN,Movement.MovementType.DOWN,Movement.MovementType.DOWN,Movement.MovementType.DOWN,Movement.MovementType.DOWN,Movement.MovementType.RIGHT,Movement.MovementType.RIGHT,Movement.MovementType.RIGHT,Movement.MovementType.RIGHT,Movement.MovementType.RIGHT};
            for (Movement.MovementType current : types) {
                ArrayList<DataBaseNode> tempList = (ArrayList<DataBaseNode>) _traversalNode.clone();
                for (DataBaseNode node : tempList) {
                    _isFirst = true;
                    _index = _traversalNode.indexOf(node);
                    if ((node != null) && (_predicitveStepsCount.get(_index) <= 5)) {
                        if (node.MoveToDesiredDataBaseNode(current) == null)
                            //if(node.CheckPreviousNodeForSeries(current))
                                PredictiveStep(node);
                        else {
                            _movementTrend = current;
                            AddingRoutesToTraversableNode(node, current);
                            _predicitveStepsCount.set(_index, 0);
                        }
                    }else{
                        _traversalNode.remove(_index);
                        _predicitveStepsCount.remove(_index);
                    }
                }
            }

            _currentMatches.clear();
            FindAllPossibleApplicationsPastNode(_traversalNode, _currentMatches);
            //_currentMatches = SortPossibleApplicationsList(_currentMatches);
        }catch(Exception ex){
            Log.e("ERROR", "Next Movement inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    private void PredictiveStep(DataBaseNode node) {
        try {
            if (_index > _predicitveStepsCount.size())
                _predicitveStepsCount.set(_index, 1);
            else
                _predicitveStepsCount.set(_index, _predicitveStepsCount.get(_index) + 1);

            SearchMostProbableRoutes(node);

        } catch (Exception ex) {
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

    public void ReinstateDataBase(){
        _traversalNode = new ArrayList<>();
        _currentMatches = new ArrayList<>();
        _errorThrown = false;
        _index = 0;
        _predicitveStepsCount = new ArrayList<>();
    }

    private void Search(DataBaseNode node, Movement.MovementType check){
        try{
            if((node = node.MoveToDesiredDataBaseNode(check)) != null) {
                if (_isFirst) {
                    _traversalNode.set(_index, node);
                    _isFirst = !_isFirst;
                } else {
                    _traversalNode.add(++_index, node);
                    _predicitveStepsCount.add(_index, 0);
                }
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
                AddingRoutesToTraversableNode(node, _movementTrend);

                // If we did not find anything at that node we want to remove the node from possibilities
                if ((_traversalNode.size() == size) && (_traversalNode.get(_index) == temp) && (_predicitveStepsCount.get(_index) > 5)) {
                    _traversalNode.remove(_index);
                    _predicitveStepsCount.remove(_index);
                }
            }
        }catch (Exception ex){
            Log.e("ERROR", "Search most probable routes inside of Data Base Handler had and error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    // TODO: if you have the time smooth out the movements.
    private ArrayList<Movement.MovementType> SmoothMovements(ArrayList<Movement.MovementType> list, int rows, int columns){
        ArrayList<Movement.MovementType> value = new ArrayList<>();
        ArrayList<Movement.MovementType> compressedList = CompressMovements(list);
        int[] movementsCount = new int[8];
        for (Movement.MovementType move:list) {

        }

        return value;
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
