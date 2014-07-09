package org.azurespot.gridimagesearch;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Abstract class enabling endless scrolling of an AdapterView (e.g., ListView or GridView). 
 * Class designed to be extended as a listener object--for instance, as an anonymous class 
 * implementing the abstract method onLoadMore(int, int).  
 *
 * To avoid unexpected problems, set up the listener in the onCreate(Bundle) method of the
 * host activity and not later. 
 */

public abstract class EndlessScrollListener implements OnScrollListener {

    private int visibleThreshold = 5;	     // Min number of items below current scroll position before loading more
    private int currentPage = 0;		     // Current offset index of data already loaded
    private int previousTotalItemCount = 0;	 // Total number of items in the dataset following last load
    private int startingPageIndex = 0;	     // Sets starting page index
    private boolean loading = true;		     // True if waiting for the last dataset to load

    public EndlessScrollListener() {

    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    /*
     * Callback method for user scrolling.
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // If condition true, assumes list has been invalidated and should be reset to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {loading = true;}
        }
        // Condition tests for cessation of loading, which is assumed if the inequality holds
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }
        // Condition tests whether more data needs to be loaded. If so, calls onLoadMore(int, int) to
        // fetch more data.
        if (!loading && (totalItemCount - visibleItemCount) <=
                (firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
//			Log.i("currentPage+1", String.valueOf(currentPage + 1));
            loading = true;
        }
    }

    /*
     * Defines process for loading more data, based on page and any applicable API protocol
     */
    public abstract void onLoadMore(int page, int totalItemCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }

}
