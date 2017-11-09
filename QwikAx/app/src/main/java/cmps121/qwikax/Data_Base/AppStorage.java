package cmps121.qwikax.Data_Base;

import java.io.Serializable;

/**
 * Created by andrew on 10/23/2017.
 */

public class AppStorage implements Serializable {

    // CONSTRUCTORS

    public AppStorage(AccessibilityLevels accessLevel, String absoluteName, String relativeName){
        _accessabilityLevel = accessLevel;
        _absoluteName = absoluteName;
        _relativeName = relativeName;
        _timesAccessed = 0;
    }

    // CONSTRUCTORS

    // ENUMERATIONS

    public enum AccessibilityLevels implements Serializable{
        LOW (1),
        MEDIUM (2),
        HIGH(3),
        NONE(0);

        private final int value;
        AccessibilityLevels(int value) {this.value = value;}
        public int getValue() {return value;}
    }

    // ENUMERATIONS

    // FIELDS

    private String _absoluteName;
    private String _relativeName;
    private int _timesAccessed;
    private AccessibilityLevels _accessabilityLevel;

    private static final long serialVersionUID = 3128594851129501740L;

    // FIELDS

    // GETTERS

    public String get_abesoluteName(){return _absoluteName;}
    public AccessibilityLevels get_accessabilityLevel(){return _accessabilityLevel;}
    public String get_relativeName(){return _relativeName;}
    public int get_timesAccessed(){return _timesAccessed;}

    // GETTERS

    // METHODS

    public void IncrementTimesAccessed(){
        _timesAccessed++;
    }

    // METHODS
}
