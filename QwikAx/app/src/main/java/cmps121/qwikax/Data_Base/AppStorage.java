package cmps121.qwikax.Data_Base;

import java.io.Serializable;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/23/2017.
 */

public class AppStorage implements Serializable{

    // CONSTRUCTORS

    public AppStorage(Movement movement, AccessabilityLevels accessLevel, String absoluteName, String relativeName){
        _movement = movement;
        _accessabilityLevel = accessLevel;
        _abesoluteName = absoluteName;
        _relativeName = relativeName;
        _timesAccessed = 0;
    }

    // CONSTRUCTORS

    // ENUMERATIONS

    public enum AccessabilityLevels{
        LOW (1),
        MEDIUM (2),
        HIGH(3),
        NONE(0);

        private final int value;
        AccessabilityLevels (int value) {this.value = value;}
        public int getValue() {return value;}
    }

    // ENUMERATIONS

    // FIELDS

    private Movement _movement;
    private String _abesoluteName;
    private String _relativeName;
    private int _timesAccessed;
    private AccessabilityLevels _accessabilityLevel;

    // FIELDS

    // GETTERS

    public String get_abesoluteName(){return _abesoluteName;}
    public AccessabilityLevels get_accessabilityLevel(){return _accessabilityLevel;}
    public Movement get_movement(){return _movement;}
    public String get_relativeName(){return _relativeName;}
    public int get_timesAccessed(){return _timesAccessed;}

    // GETTERS

    // METHODS

    public void IncrementTimesAccessed(){
        _timesAccessed++;
    }

    // METHODS

}
