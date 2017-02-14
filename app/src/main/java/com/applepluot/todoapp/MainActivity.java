package com.applepluot.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> todoItems = new ArrayList<>();
    //translates source of data to its view representation
    ArrayAdapter<String> todoAdapter;
    ListView lvItems;
    EditText etAddItemText;
    int currentPosition;
    String currentItem;
    static final String ITEM_KEY = "ITEM";
    static final String CODE_KEY = "CODE";
    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(todoAdapter);
        etAddItemText = (EditText) findViewById(R.id.etAddItemText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = todoItems.get(position);
                currentPosition = position;
                launchEditItemView();
            }
        });
    }

    public void launchEditItemView() {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra(ITEM_KEY, currentItem);
        startActivityForResult(i, REQUEST_CODE); // brings up the second activity
    }

    private void populateArrayItems() {
        readItems();
        todoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    public void onAddItem(View view) {
        todoItems.add(etAddItemText.getText().toString());
        etAddItemText.setText("");
        writeItems();
    }

    // adb shell more /data/data/com.applepluot.todoapp/files/todo.txt
    private void readItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.txt");
        try {
            todoItems = new ArrayList<>(FileUtils.readLines(file));
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    private void writeItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String updatedItem = data.getExtras().getString(ITEM_KEY);
            int code = data.getExtras().getInt(CODE_KEY, 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, updatedItem, Toast.LENGTH_SHORT).show();
            todoItems.set(currentPosition, updatedItem);
            todoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }
}
