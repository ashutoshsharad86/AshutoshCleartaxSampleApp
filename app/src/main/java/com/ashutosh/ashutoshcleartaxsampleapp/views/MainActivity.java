package com.ashutosh.ashutoshcleartaxsampleapp.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ashutosh.ashutoshcleartaxsampleapp.R;
import com.ashutosh.ashutoshcleartaxsampleapp.presenter.TwitterDataPresenter;

/**
 * Created by ashutosh.k on 7/13/2016.
 */

/**
 * Demonstrates how to use a twitter application keys to search
 */
public class MainActivity extends AppCompatActivity {

    private TwitterDataPresenter presenter;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private ListView listView = null;
    private Button btn = null ;
    private ProgressDialog progress_horizontal;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        listView = (ListView)findViewById(R.id.listview);
        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.searchForClearTax();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(presenter == null) {
            presenter = new TwitterDataPresenter(this);
        }
        presenter.searchForClearTax();

    }

    public void showProgress() {
        if(progress_horizontal==null || !progress_horizontal.isShowing()) {
            if(progress_horizontal == null) {
                progress_horizontal = new ProgressDialog(this);
                progress_horizontal.setMessage("Loading twitter trending words ...");
                progress_horizontal.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress_horizontal.setIndeterminate(true);
                progress_horizontal.setCancelable(false);
            }

            progress_horizontal.show();
        }
    }

    public void stopProgress() {
        if (progress_horizontal.isShowing())
            progress_horizontal.dismiss();
    }

    public ListView getListView() {
        return listView;
    }

}
