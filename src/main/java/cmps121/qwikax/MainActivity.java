package cmps121.qwikax;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // FIELDS
    private GridView _gridView;
    private int _rows;
    private int _columns;
    // FIELDS

    // ENUMS

    // ENUMS

    // METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _gridView = (GridView) findViewById(R.id.gridView);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        _rows = 4;
        _columns = 3;
        _gridView.setColumnWidth(display.widthPixels / _columns);
        _gridView.setAdapter(new CustomGridAdapter(this, _rows, _columns));

        _gridView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Drawable background = view.getBackground();
                int color = Color.TRANSPARENT;
                if(background instanceof ColorDrawable)
                    color = ((ColorDrawable)background).getColor();

                if(color == Color.TRANSPARENT)
                    view.setBackgroundColor(Color.RED);
                else
                    view.setBackgroundColor(Color.TRANSPARENT);

                return false;
            }
        });

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Drawable background = view.getBackground();
                int color = Color.TRANSPARENT;
                if(background instanceof ColorDrawable)
                    color = ((ColorDrawable)background).getColor();

                if(color == Color.TRANSPARENT)
                    view.setBackgroundColor(Color.RED);
                else
                    view.setBackgroundColor(Color.TRANSPARENT);

                Toast.makeText(getApplicationContext(), "row: " + ((i / _columns) + 1) + " column: " + ((i % _columns) + 1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "you clicked settings", Toast.LENGTH_LONG).show();
                // I believe i need to use intents and then launch the layout wiht the intent
                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // METHODS
}
