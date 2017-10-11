package cmps121.qwikax.Node_Related;


/**
 * Created by andrew on 10/7/2017.
 */

public class IndividualNode {

    // FIELDS

    // Image view associated to each node
    private int _image;
    // Determines if we have selected this item inside the grid.
    private boolean _isHighlighted;
    // The current location and orientation of the Node
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

    // Sets the status if we have highlighted this Node
    public void setHighLight(boolean isHighlighted)
    {
        _isHighlighted = isHighlighted;
    }

    // Sets our image inside the image view
    public void setImage(int image)
    {
        _image = image;
    }

    // Determines if we are highlighted
    public boolean isHighLight()
    {
        return _isHighlighted;
    }

    // METHODS
}
