package com.ashutosh.ashutoshcleartaxsampleapp.helpers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.ashutosh.ashutoshcleartaxsampleapp.model.TwitterData;
import com.ashutosh.ashutoshcleartaxsampleapp.model.TwitterEntry;
import com.ashutosh.ashutoshcleartaxsampleapp.presenter.TwitterDataPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ashutosh.k on 7/13/2016.
 */
public class TwitterDataAsynDownloadHelper extends AsyncTask<Void, Void, TwitterData> {
    private final Context context;
    private final TwitterDataPresenter presenter;
    private final static String SearchTerm = "cleartax -#Cleartax -#cleartax_in -@Cleartax -@cleartax_in";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private String Key = null;
    private String Secret = null;
    private String AccessToken = null;
    private String AccessTokenSecret = null;
    private final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    private final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";

    public TwitterDataAsynDownloadHelper(TwitterDataPresenter presenter) {
        this.presenter = presenter;
        this.context = presenter.getAssociatedActivity();
        this.Key = getStringFromManifest("CONSUMER_KEY");
        this.Secret = getStringFromManifest("CONSUMER_SECRET");
        this.AccessToken = getStringFromManifest("ACCESS_TOKEN");
        this.AccessTokenSecret = getStringFromManifest("ACCESS_TOKEN_SECRET");
    }

    @Override
    protected TwitterData doInBackground(Void... voids) {
        QueryResult result = null;
        List<Map.Entry<String, Integer>> topRes = null;
        result = searchTweetsWith4j();
        if (result != null && result.getCount() > 0) {
            topRes = findTopThreeWords(result);
        }
        TwitterData data= new TwitterData();
        ArrayList<TwitterEntry> entriesList = new ArrayList<TwitterEntry>();
        // Copy all data to this format, its only size 3

        for(Map.Entry<String, Integer> topResEntry : topRes) {
            TwitterEntry twitterEntry = new TwitterEntry();
            twitterEntry.entry = topResEntry;
            entriesList.add(twitterEntry);
        }
        data.list = entriesList;
        return data;
    }

    @Override
    protected void onPostExecute(TwitterData result) {
            presenter.updateModel(result);
    }

    private List<Map.Entry<String, Integer>> findTopThreeWords(QueryResult result) {

        HashMap<String, Integer> hashMap = new HashMap<>();
        List<twitter4j.Status> statuses = result.getTweets();
        for (twitter4j.Status status : statuses) {
            String[] splittedString = status.getText().split(" ");
            int count = 1;
            for (String s1 : splittedString) {
                // We are not considering references to shared URLs, if needed just comment out below code
                if (!s1.startsWith("https://") && !s1.startsWith("http://")) {
                    s1= s1.toLowerCase();
                    count = hashMap.containsKey(s1) ? count + 1 : 1;
                    hashMap.put(s1, count);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = null;
        list = sortByValue(hashMap);
        // We need only 3 entries, clear others
        list.subList(3 , list.size()).clear();
        hashMap.clear();
        return list;
    }

    private List<Map.Entry<String, Integer>> sortByValue(Map<String, Integer> wordMap) {

        Set<Map.Entry<String, Integer>> set = wordMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        return list;
    }

    private String getStringFromManifest(String key) {
        String results = null;

        try {
            Context applicationContext = this.context.getApplicationContext();
            ApplicationInfo ai = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);
            results = (String) ai.metaData.get(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return results;
    }

    private QueryResult searchTweetsWith4j() {
        QueryResult result = null;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Key)
                .setOAuthConsumerSecret(Secret)
                .setOAuthAccessToken(AccessToken)
                .setOAuthAccessTokenSecret(AccessTokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter4j.Twitter twitter = tf.getInstance();
        try {
            Query query = new Query(SearchTerm);
            query.setCount(100);

            result = twitter.search(query);
            List<twitter4j.Status> tweets = result.getTweets();
            System.out.println("Total count is" + result.getCount());
            for (twitter4j.Status tweet : tweets) {
                System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
            }

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        } finally {
            return result;
        }

    }


}
