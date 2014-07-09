package org.azurespot.gridimagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;


public class ImageDisplayActivity extends Activity {

    private static final String tag = "org.azurespot.gridimagesearch.ImageDisplayActivity";

//	public static final ImageView.ScaleType CENTER_INSIDE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
        ScaledImageView ivImage = (ScaledImageView) findViewById(R.id.ivResult);
        Log.i(tag, "result.getFullUrl: " + result.getFullUrl());
        Picasso.with(this).load(result.getFullUrl()).into(ivImage);
//		ivImage.setImageUrl(result.getFullUrl());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_display, menu);
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
