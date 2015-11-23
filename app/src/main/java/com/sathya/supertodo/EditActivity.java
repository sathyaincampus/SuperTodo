package com.sathya.supertodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    EditText etCurrentItem;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        index = getIntent().getIntExtra("index", 0);
        String value = getIntent().getStringExtra("value");
        etCurrentItem = (EditText) findViewById(R.id.etCurrentItem);
        etCurrentItem.setText(value);
    }

    public void onSubmit(View v) {
        // closes the activity and returns to first screen
        Intent data = new Intent();
        data.putExtra("index", index);
//        Toast.makeText(this, ("value : " + etCurrentItem.getText().toString()), Toast.LENGTH_SHORT).show();
        data.putExtra("value", etCurrentItem.getText().toString());
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, data);
        this.finish();
    }

}
