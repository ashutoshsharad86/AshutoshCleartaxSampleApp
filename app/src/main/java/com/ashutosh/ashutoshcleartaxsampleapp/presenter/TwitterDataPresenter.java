package com.ashutosh.ashutoshcleartaxsampleapp.presenter;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ashutosh.ashutoshcleartaxsampleapp.helpers.NetworkAvailabilityHelper;
import com.ashutosh.ashutoshcleartaxsampleapp.helpers.TwitterDataAsynDownloadHelper;
import com.ashutosh.ashutoshcleartaxsampleapp.model.TwitterData;
import com.ashutosh.ashutoshcleartaxsampleapp.model.TwitterEntry;
import com.ashutosh.ashutoshcleartaxsampleapp.views.MainActivity;

/**
 * Created by ashutosh.k on 7/13/2016.
 */
public class TwitterDataPresenter {


    private MainActivity associatedActivity;
    private TwitterData data;

    public MainActivity getAssociatedActivity() {
        return associatedActivity;
    }

    public TwitterDataPresenter(MainActivity associatedActivity) {
        this.associatedActivity = associatedActivity;
        this.data = null ;
    }

    public void searchForClearTax() {

        associatedActivity.showProgress();
        if (associatedActivity != null && NetworkAvailabilityHelper.getNetworkInfo(associatedActivity) == true) {
            new TwitterDataAsynDownloadHelper(this).execute();
        } else {
            Toast.makeText(associatedActivity, "Network Error, please check your internet connection", Toast.LENGTH_LONG).show();
            this.associatedActivity.getListView().setAdapter(null);
            this.associatedActivity.stopProgress();
        }
    }

    public void updateModel(TwitterData latestData) {
        this.data = latestData;
        presentLatestView();
    }

    public void presentLatestView() {
        // send the tweets to the adapter for rendering
        if(this.associatedActivity != null) {
            ArrayAdapter<TwitterEntry> adapter = new ArrayAdapter<TwitterEntry>
                    (this.associatedActivity, android.R.layout.simple_list_item_1, this.data.list);
            this.associatedActivity.getListView().setAdapter(adapter);
            this.associatedActivity.stopProgress();
        }
    }

}
