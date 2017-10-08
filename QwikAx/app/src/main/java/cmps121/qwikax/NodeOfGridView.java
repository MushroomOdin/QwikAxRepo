package cmps121.qwikax;

/**
 * Created by andrew on 10/7/2017.
 */

public class NodeOfGridView {

    // FIELDS

    private int _image;
    private boolean _isHighlighted;

    // FIELDS

    // CONSTRUCTOR

    public NodeOfGridView(int image, boolean highlighted)
    {
        _image = image;
        _isHighlighted = highlighted;
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
