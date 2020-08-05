package com.elytelabs.quotescreator.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elytelabs.quotescreator.R;
import com.elytelabs.quotescreator.database.DatabaseHelper;
import com.elytelabs.quotescreator.utils.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class AddQuoteActivity extends AppCompatActivity {

    EditText edtquote;
    FloatingActionButton buttonSaveQuote;
    DatabaseHelper mDBHelper;
    boolean isAutoPasteEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Quote");

        mDBHelper = new DatabaseHelper(this);
        isAutoPasteEnabled = Helper.isAutoPasteEnabled(this);

        edtquote = findViewById(R.id.editTextAddQuote);
        buttonSaveQuote = findViewById(R.id.buttonSaveQuote);

        if (isAutoPasteEnabled) {
            // Get clipboard data and set it to edit text
            String pasteData = Helper.getClipboardData(this);
            edtquote.setOnClickListener(view -> edtquote.setText(pasteData));
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_quote, menu);
        MenuItem menuItem = menu.findItem(R.id.action_auto_paste);
        if (isAutoPasteEnabled) {

            menuItem.setChecked(false);
            menuItem.setTitle(R.string.disable_auto_paste);
        } else {

            menuItem.setChecked(true);
            menuItem.setTitle(R.string.enable_auto_paste);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // Android default back button
        if (id == android.R.id.home) {
            onBackPressed();
            return true;

        }
        if (id == R.id.action_auto_paste) {
            if (isAutoPasteEnabled) {

                //item.setChecked(false);
                item.setTitle(R.string.disable_auto_paste);
                Helper.setAutoPasteEnabled(this, false);
            } else {

                //  item.setChecked(true);
                item.setTitle(R.string.enable_auto_paste);
                Helper.setAutoPasteEnabled(this, true);
            }
            return true;


        }
        return super.onOptionsItemSelected(item);
    }
}