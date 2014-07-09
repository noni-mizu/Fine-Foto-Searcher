package org.azurespot.gridimagesearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;



public class AdvancedOptionsActivity extends Activity {

    private Spinner colorFilter, imageSize, imageType;
    protected static final String FILTERS = "FilterPreferences";
    private Button btnSubmit;
    private ArrayAdapter<CharSequence> imageSizeAdapter, colorFilterAdapter, imageTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_options);

        setupViews();
        restoreFilterPreferences();

        // Resets all spinners
        Button resetFilters = (Button) findViewById(R.id.resetButton);
        resetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageType.setSelection(0);
                colorFilter.setSelection(0);
                imageSize.setSelection(0);
            }
        });
    }

    private void setupViews() {

        imageType = (Spinner) findViewById(R.id.image_type);
        imageTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.image_type, android.R.layout.simple_spinner_item);
        imageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageType.setAdapter(imageTypeAdapter);

        colorFilter = (Spinner) findViewById(R.id.color_filter);
        colorFilterAdapter = ArrayAdapter.createFromResource(this,
                R.array.color_filter, android.R.layout.simple_spinner_item);
        colorFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorFilter.setAdapter(colorFilterAdapter);

        imageSize = (Spinner) findViewById(R.id.image_size);
        imageSizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.image_size, android.R.layout.simple_spinner_item);
        imageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSize.setAdapter(imageSizeAdapter);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_OK, i);

                finish();
            }
        });
    }



    private void restoreFilterPreferences() {
        SharedPreferences filters = getSharedPreferences(FILTERS, 0);

        int imageSizeSelection = filters.getInt("image_size", 0);
        imageSize.setSelection(imageSizeSelection);

        int colorFilterSelection = filters.getInt("color_filter", 0);
        colorFilter.setSelection(colorFilterSelection);

        int imageTypeSelection = filters.getInt("image_type", 0);
        imageType.setSelection(imageTypeSelection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFilterPreferences();
    }

    private void saveFilterPreferences() {
        SharedPreferences filters = getSharedPreferences(FILTERS, 0);
        SharedPreferences.Editor editor = filters.edit();
        editor.putInt("image_size", imageSize.getSelectedItemPosition());
        editor.putInt("color_filter", colorFilter.getSelectedItemPosition());
        editor.putInt("image_type", imageType.getSelectedItemPosition());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.advanced_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
