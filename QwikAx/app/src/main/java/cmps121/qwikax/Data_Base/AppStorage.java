package cmps121.qwikax.Data_Base;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

import cmps121.qwikax.Node_Related.Movement;

/**
 * Created by andrew on 10/23/2017.
 */

public class AppStorage implements Serializable {

    // CONSTRUCTORS

    public AppStorage(AccessibilityLevels accessLevel, String absoluteName, String relativeName, ArrayList<Movement.MovementType> appMovements, Bitmap image){
        _accessibilityLevel = accessLevel;
        _absoluteName = absoluteName;
        _relativeName = relativeName;
        _timesAccessed = 0;
        _appMovements = appMovements;
        _image = image;
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

    private AccessibilityLevels _accessibilityLevel;
    private String _absoluteName;
    private String _relativeName;
    private int _timesAccessed;
    private ArrayList<Movement.MovementType> _appMovements;
    private Bitmap _image;

    private static final long serialVersionUID = 3128594851129501740L;

    // FIELDS

    // GETTERS

    public String get_abesoluteName(){return _absoluteName;}
    public AccessibilityLevels get_accessibilityLevel(){return _accessibilityLevel;}
    public String get_relativeName(){return _relativeName;}
    public int get_timesAccessed(){return _timesAccessed;}
    public ArrayList<Movement.MovementType> get_appMovements(){return _appMovements; }
    public Bitmap get_image(){return _image; }

    // GETTERS

    // METHODS

    public void IncrementTimesAccessed(){
        _timesAccessed++;
    }


    // METHODS
}
