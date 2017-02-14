package com.applepluot.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etItemText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etItemText = (EditText) findViewById(R.id.etEditItem);
        String item = (String) getIntent().getSerializableExtra(MainActivity.ITEM_KEY);
        etItemText.setText(item);
        saveButton = (Button) findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare data intent
                Intent data = new Intent();
                data.putExtra(MainActivity.ITEM_KEY, etItemText.getText().toString());
                data.putExtra(MainActivity.CODE_KEY, 200); // ints work too
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }
        });

    }
}
