package com.elytelabs.quotescreator.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elytelabs.quotescreator.R;
import com.elytelabs.quotescreator.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class AddQuoteActivity extends AppCompatActivity {

    EditText edtquote;
    FloatingActionButton buttonSaveQuote;
    DatabaseHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Quote");

        mDBHelper = new DatabaseHelper(this);

        edtquote = findViewById(R.id.editTextAddQuote);
        buttonSaveQuote = findViewById(R.id.buttonSaveQuote);
        buttonSaveQuote.setOnClickListener(v -> {

            String quote = edtquote.getText().toString().trim();
            if (!TextUtils.isEmpty(quote)) {

                boolean isAdded = mDBHelper.addQuote(quote);
                if (isAdded) {
                    Snackbar.make(v, "Quote Added", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.coloWhite))
                            .setBackgroundTint(getResources().getColor(R.color.colorSuccess)).show();
                    edtquote.getText().clear();
                } else

                    Snackbar.make(v, "Quote Not Added", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.coloWhite))
                            .setBackgroundTint(getResources().getColor(R.color.design_default_color_error)).show();
            } else {
                edtquote.setError("Required");
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