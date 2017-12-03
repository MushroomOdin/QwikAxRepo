package cmps121.qwikax.Delete_List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import cmps121.qwikax.Data_Base.AppStorage;
import cmps121.qwikax.R;
import cmps121.qwikax.Data_Base.DataBaseNode;
import cmps121.qwikax.App_List.ListOps;
import cmps121.qwikax.Data_Base.DataBaseHandler;

/**
 * Created by Juve on 11/29/2017.
 */

public class ListDelete extends AppCompatActivity {
    private ListView _listView;
    private DataBaseHandler _dataBase;
    private ListOps _apps;

    ArrayList<AppStorage> _deletion_list = new ArrayList<>();

    private void LoadDataBaseFromFile(String fileName, ArrayList<String> appList) {
        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            _dataBase = (DataBaseHandler) is.readObject();
            is.close();
            fis.close();
        }catch(Exception ex){
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(getApplicationContext(), "Data base was not loaded.", Toast.LENGTH_LONG).show();
            _dataBase = new DataBaseHandler(appList);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.delete_list);
    }
}
