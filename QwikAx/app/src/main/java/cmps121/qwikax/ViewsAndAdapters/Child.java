package cmps121.qwikax.ViewsAndAdapters;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by matthew on 12/4/17.
 */

public class Child {

    private String Name;
    private Drawable Image;
    private int Image2;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Drawable getImage() {
        return Image;
    }

    public int getImage2(){
        return Image2;
    }

    public void setImage(Drawable Image) {
        this.Image = Image;
    }

    public void setImage2(int Image2){
        this.Image2 = Image2;
    }

}
