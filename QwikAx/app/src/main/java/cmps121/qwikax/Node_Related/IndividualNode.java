package cmps121.qwikax.Node_Related;

import android.app.Activity;
import android.view.Surface;

/**
 * Created by andrew on 10/7/2017.
 */

public class IndividualNode {

    // FIELDS

    private int _image;
    private boolean _isHighlighted;
    private CoordinateSystem _coordinateSystem;

    // FIELDS

    // CONSTRUCTOR

    public IndividualNode(int image, boolean highlighted, CoordinateSystem coordinateSystem)
    {
        _image = image;
        _isHighlighted = highlighted;
        _coordinateSystem = coordinateSystem;
    }

    // CONSTRUCTOR

    // METHODS

    public void setHighLight(boolean isHighlighted)
    {
        _isHighlighted = isHighlighted;
    }

    public void setImage(int image)
    {
        _image = image;
    }

    public boolean isHighLight()
    {
        return _isHighlighted;
    }

    // METHODS
}
