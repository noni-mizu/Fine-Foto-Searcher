package org.azurespot.gridimagesearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends Activity {

    private StaggeredGridView gvResults;
    private String filterPreferences = "";
    private String previousFilterPreferences = "";
    private SearchView searchView;
    AsyncHttpClient client;
    ImageView owlImage;

    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        owlImage = (ImageView) findViewById(R.id.imageOwl);
        owlImage.setVisibility(View.VISIBLE);

        // When activity gets destroyed, then this resets your filterPreferences to empty
        if (previousFilterPreferences.equals(filterPreferences)) {
                filterPreferences = "";
        }

        //Search button listener returns search results
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        imageAdapter = new ImageResultArrayAdapter(this, imageResults);
        gvResults.setAdapter(imageAdapter);
        //Here you click on an image and it opens in it's own activity
        gvResults.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                Intent i = new Intent(getApplicationContext(),
                        ImageDisplayActivity.class);
                ImageResult imageResult = imageResults.get(position);
                i.putExtra("result", imageResult);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                loadMoreImages(totalItemCount);
            }
        });

        enterKeyFilterSearch();

    }//end onCreate method

    public void enterKeyFilterSearch() {

        gvResults.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    filterPreferences = getFilterPreferences();
                    String query = searchView.getQuery().toString();
                    Toast.makeText(SearchActivity.this, "Searching for "
                            + query, Toast.LENGTH_SHORT).show();

                    AsyncHttpClient client = new AsyncHttpClient();
                    imageResults.clear();
                    makeAsyncHttpGetRequest(client, 0, query);
                    return true;
                }
                return false;
            }
        });
    }

    // Part of endless scrolling
    public void loadMoreImages(int totalItemCount) {

        String query = searchView.getQuery().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        makeAsyncHttpGetRequest(client, totalItemCount, query);
    }

    //Then makes the HTTP request from the Google API
    private void makeAsyncHttpGetRequest(AsyncHttpClient client, int totalItemCount,
                                         String query){
        owlImage.setVisibility(View.GONE);

        client.get("https://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +
                        filterPreferences + "&start=" + totalItemCount + "&v=1.0&q=" + Uri.encode(query),
                new JsonHttpResponseHandler() { //A handler is set in case the search doesn't work
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONArray imageJsonResults = null;
                        try {
                            imageJsonResults = response.getJSONObject("responseData")
                                    .getJSONArray("results");

                            imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } //End onSuccess
                }); //End client.get()
        previousFilterPreferences = filterPreferences;
    } //end makeAsyncHttpGetRequest()


    //Spells out all my filter preferences by using the API "language" put into variables
    //and ultimately becomes a part of the getFilterPreferences method
    public String getFilterPreferences() {

        String filterPreferences, imageType, imageSize, colorFilter;

        SharedPreferences filters = getSharedPreferences(AdvancedOptionsActivity.FILTERS, 0);

        switch(filters.getInt("image_type", 0)) {
            case 0:  imageType = "";                 break;
            case 1:  imageType = "&imgtype=face";    break;
            case 2:  imageType = "&imgtype=photo";   break;
            case 3:  imageType = "&imgtype=lineart"; break;
            case 4:  imageType = "&imgtype=clipart"; break;
            default: imageType = "";
        }
        switch(filters.getInt("color_filter", 0)) {
            case 0: colorFilter = ""; 				  break;
            case 1: colorFilter = "&imgcolor=blue";   break;
            case 2: colorFilter = "&imgcolor=red";    break;
            case 3: colorFilter = "&imgcolor=green";  break;
            case 4: colorFilter = "&imgcolor=orange"; break;
            case 5: colorFilter = "&imgcolor=gray";   break;
            default: colorFilter = "";
        }
        switch(filters.getInt("image_size", 0)) {
            case 0:  imageSize = "";		      break;
            case 1:  imageSize = "&imgsz=small";  break;
            case 2:  imageSize = "&imgsz=medium"; break;
            case 3:  imageSize = "&imgsz=large";  break;
            case 4:  imageSize = "&imgsz=xlarge"; break;
            default: imageSize = "";
        }


        filterPreferences = imageType + colorFilter + imageSize;
        //If selects nothing, it will still run the filterPreferences variable
        //to include the default selections (filters shown in top row of spinner)
        return filterPreferences;

    }// End getFilterPreferences

    // Calls onRestart when this activity is returned to
    @Override
    public void onRestart(){
        super.onRestart();
        filterPreferences = getFilterPreferences();
        if (!previousFilterPreferences.equals(filterPreferences)) {
            // Make a new HTTP request
            String query = searchView.getQuery().toString();
					Toast.makeText(SearchActivity.this, "Searching for " + query, Toast.LENGTH_SHORT).show();

            AsyncHttpClient client = new AsyncHttpClient();
            imageResults.clear();
            makeAsyncHttpGetRequest(client, 0, query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("search images ...");
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Hides soft keyboard after enter is pressed, but caused a crash
                // upon 2nd search, so removed for now
//              InputMethodManager imm = (InputMethodManager)
//                    getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//              imm.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);

                imageResults.clear();
                filterPreferences = "";
                client = new AsyncHttpClient();
                makeAsyncHttpGetRequest(client, 0, query);
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_filter_prefs:
                Intent i = new Intent(SearchActivity.this, AdvancedOptionsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
