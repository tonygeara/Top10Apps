package com.example.tony.top10apps;

import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
private int feedLimit = 10;
private String feedCachedUrl = "INVALIDATEDD";
public static final String STATE_URL = "feedUrl";
public static final String STATE_LIMIT = "feedLimit";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApps = (ListView)findViewById(R.id.xmlListView);

        if(savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl,feedLimit));

//        Log.d(TAG, "onCreate:  starting AsynTask");
//        DownloadData downloaddata = new DownloadData();
//        downloaddata.execute();
//        Log.d(TAG, "onCreate:  done");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch(id){
            case R.id.mnufree:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "setting feedLimit to " + feedLimit);

                }else{
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit unchanged" );

                }
                break;
            case R.id.mnureffresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl,feedLimit));
        return true;
//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);

        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl) {
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)){


        Log.d(TAG, "downloadUrl:  starting AsynTask");
        DownloadData downloaddata = new DownloadData();
        downloaddata.execute(feedUrl);
        feedCachedUrl = feedUrl;
        Log.d(TAG, "downloadUrl:  done");
    }
    else{
            Log.d(TAG, "downloadUrl: URL not changed");
        }
    }


    private class  DownloadData extends AsyncTask<String,Void,String>{
           private static final String TAG = "DownloadData";

           @Override
           protected void onPostExecute(String s) {
               super.onPostExecute(s);
//               Log.d(TAG, "onPostExecute: parameter is " + s);
               ParseApplications parseApplications = new ParseApplications();
               parseApplications.parse(s);

//               ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this, R.layout.list_item, parseApplications.getApplications());
//               listApps.setAdapter(arrayAdapter);

               FeefAdapter<FeedEntry> feedAdapter = new FeefAdapter<>(MainActivity.this,R.layout.list_record,parseApplications.getApplications());
               listApps.setAdapter(feedAdapter);
           }

           @Override
            protected String doInBackground(String... strings) {
               Log.d(TAG, "doInBackground: starte with" + strings[0]);
               String RssFeed = downloadXML(strings[0]);
               if(RssFeed == null){
                   Log.e(TAG, "doInBackground: Error Downloading");
               }
                return RssFeed;
            }

           private String downloadXML(String urlPath    ) {
              StringBuilder xmlresult = new StringBuilder();

              try{
                  URL url = new URL(urlPath);
                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                  int response = connection.getResponseCode();
                  Log.d(TAG, "downloadXML: the response code was: " + response);
//                  InputStream inputstream = connection.getInputStream();
//                  InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
//                  BufferedReader reader = new BufferedReader(inputstreamreader);

                  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                  int charsRead;
                  char[] inputBuffer = new char[500];
                  while(true){

                      charsRead = reader.read(inputBuffer);
                      if (charsRead<0){
                          break;
                      }
                      if (charsRead > 0){
                          xmlresult.append(String.copyValueOf(inputBuffer, 0 , charsRead));
                      }
                  }
                  reader.close();

                  return xmlresult.toString();

              } catch(MalformedURLException e){
                  Log.e(TAG, "downloadXML: INVALID URL " + e.getMessage() );
              } catch (IOException e) {
                  Log.e(TAG, "downloadXML: IO EXCEPTION READING DATA" + e.getMessage() );
              } catch(SecurityException e){
                  Log.e(TAG, "downloadXML: Security exception needs permission? " + e.getMessage() );
//                  e.printStackTrace();
              }

              return null;
           }

       }
}
