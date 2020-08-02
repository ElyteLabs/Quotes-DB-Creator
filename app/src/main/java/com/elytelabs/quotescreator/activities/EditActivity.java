package com.elytelabs.quotescreator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elytelabs.quotescreator.MainActivity;
import com.elytelabs.quotescreator.R;
import com.elytelabs.quotescreator.database.DatabaseHelper;
import com.elytelabs.quotescreator.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class EditActivity extends AppCompatActivity {

    DatabaseHelper mDBHelper;
    EditText edQuote;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Quote");

        mDBHelper = new DatabaseHelper(this);
        edQuote = findViewById(R.id.editTextEditQuote);
        fabSave = findViewById(R.id.fab_save);

        // Get Data
        final String id = getIntent().getStringExtra(Constants.STRING_EXTRA_ID);
        final String quote = getIntent().getStringExtra(Constants.STRING_EXTRA_QUOTE);

        edQuote.setText(quote);

        fabSave.setOnClickListener(view -> {
            boolean isUpdated = mDBHelper.editQuote(id, edQuote.getText().toString().trim());
            if (isUpdated) {

                Snackbar.make(view, "Quote Updated", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.coloWhite))
                        .setBackgroundTint(getResources().getColor(R.color.colorSuccess)).show();
                edQuote.getText().clear();

                startActivity(new Intent(EditActivity.this, MainActivity.class));
                finishAffinity();

            } else {
                Snackbar.make(view, "Quote Not Updated", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.coloWhite))
                        .setBackgroundTint(getResources().getColor(R.color.design_default_color_error)).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        // Android default back button
        if (id == android.R.id.home) {
            onBackPressed();
            return true;


        }
        return super.onOptionsItemSelected(item);
    }
}