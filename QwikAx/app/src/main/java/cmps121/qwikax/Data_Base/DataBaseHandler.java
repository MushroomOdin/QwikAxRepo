package cmps121.qwikax.Data_Base;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private int[] _indexs;
    private ArrayList<AppStorage> _currentMatches;
    private boolean _errorThrown;
    private ArrayList<String> _AppNames;
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
                if(type != Movement.MovementType.INITIAL_POSITION)
                        temp = temp.MoveToDesiredDataBaseNode(type);

            temp.AddAppStorageToList(item);
        }catch (Exception ex) {
            Log.e("ERROR", "Add new item to tree inside data base handler had an error\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    // Removes all repetition inside of the list.
    private ArrayList<Movement.MovementType> CompressList(ArrayList<Movement.MovementType>list){
        ArrayList<Movement.MovementType> compressedList = new ArrayList<>();
        int[] counters = new int[9];
        for (int count = 0; count < list.size(); count++) {
            int movementCount = list.get(count).getValue();
            if(counters[movementCount]++ == 0){
                compressedList.add(list.get(count));
                for(int index = 0; index < 9; index++){
                    if((index != movementCount) && (counters[index] != 0))
                        counters[index] = 0;
                }
            }
        }

        return compressedList;
    }

    // compares the two lists and returns a percent.
    private double CompareLists(ArrayList<Movement.MovementType> list1,ArrayList<Movement.MovementType> list2){
        int min = (list1.size() <= list2.size()) ? list1.size() : list2.size();
        int max = (list1.size() >= list2.size()) ? list1.size() : list2.size();
        int similar = 0;
        for(int count = 0; count < min; count++)
            if(list1.get(count) == list2.get(count))
                similar++;

        return (similar / max) * 100;
    }


    public void FindAllPossibleApplicationsPastNode(DataBaseNode node, ArrayList<AppStorage> list){
        try{
            for(int count = 0; count < 8; count++){
                DataBaseNode currentNode = node.get_pointers()[count];
                if( currentNode != null) {
                    if(currentNode.get_appStorage().size() != 0) {

                        list.addAll(currentNode.get_appStorage());
                        //RemoveDuplicatesFromList(list);
                    }

                    FindAllPossibleApplicationsPastNode(currentNode, list);
                }
            }
        }catch(Exception ex){
            Log.e("ERROR", "Find all possible applications in tree in data base handler had an error.\n" + ex.getMessage());
            _errorThrown = true;
        }

    }

    public void FindAppByAbstraction(ArrayList<Movement.MovementType> movementsMade){
        try{
            ArrayList<AppStorage> list = new ArrayList<>();
            ArrayList<Double> percentList = new ArrayList<>();
            FindAllPossibleApplicationsPastNode(_masterNode, list);
            movementsMade = CompressList(movementsMade);

            for (AppStorage app: list) {
                ArrayList<Movement.MovementType> appMovements = app.get_appMovements();
                appMovements = CompressList(appMovements);
                percentList.add(CompareLists(movementsMade, appMovements));
            }

            Collections.sort(percentList);
            Collections.reverse(percentList);

            for (int count = 0; count < percentList.size(); count++){
                double percent = percentList.get(count);
                if((percent >= _comparePercentLow) && (percent <= _comparePercentHigh))
                    _currentMatches.add(0, list.get(count));
            }

        }catch (Exception ex){
            Log.e("ERROR", "Find Apps by abstraction in data base handler had an error.\n" + ex.getMessage());
            _errorThrown = true;
        }
    }

    public void InitialMovement(){
        _traversalNode = _masterNode;
        _currentMatches.clear();
        _errorThrown = false;
        _indexs = new int[8];
         FindAllPossibleApplicationsPastNode(_traversalNode,_currentMatches);
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
                    FindAllPossibleApplicationsPastNode(_traversalNode, _currentMatches);
                    //_currentMatches = SortPossibleApplicationsList(_currentMatches);
                }
            }
        }catch(Exception ex){
            Log.e("ERROR", "Next Movement inside of Data Base Handler had and error.\n" + ex.getMessage());
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
