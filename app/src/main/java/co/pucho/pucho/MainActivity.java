package co.pucho.pucho;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    public static final String TAG = "MainActivity";
    public final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 9001;
    public static final String state = "RecyclerView_State";
    public static Bundle recyclerState ;
    LogHandler logHandler;

    private View mLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<String> list;

    TextView textView;
    TextToSpeech textToSpeech ;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        recyclerState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        recyclerState.putParcelable(state, listState);

        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(home);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AIConfiguration config = new AIConfiguration("9e29f2b3d45843d5b7d4a1b23b1166e6",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        final AIService aiService = AIService.getService(this, config);
        aiService.setListener(this);

        ImageButton listenButton = (ImageButton) findViewById(R.id.imageButton);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiService.startListening();
            }
        });

        textView = (TextView) findViewById(R.id.textView);

        textToSpeech = new TextToSpeech( this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        list = new ArrayList<>();
        mAdapter = new MyAdapter(list);
        mRecyclerView.setAdapter(mAdapter);

        mLayout = findViewById(R.id.main_layout);

        logHandler = new LogHandler(this, null, null, 1);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, R.string.permission_explanation,
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                }).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        List<String> l = logHandler.getValues();
        list = l;
        //Log.d(TAG, list.toString());
        mAdapter = new MyAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mLayout, R.string.permission_granted, Snackbar.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(mLayout, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
                }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("LOGOUT_STATUS", 4001);
                startActivity(intent);
                return true;
            case R.id.clear:
                clearConversation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResult(final AIResponse result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, result.toString());
                Result r = result.getResult();
                String resolvedQuery = r.getResolvedQuery();
                String resolvedQuery1 = resolvedQuery.substring(0,1).toUpperCase() + resolvedQuery.substring(1);
                String speech = r.getFulfillment().getSpeech();
                list.add(resolvedQuery1);
                list.add(speech);

                DataModel dataModel1 = new DataModel("User", resolvedQuery1);
                DataModel dataModel2 = new DataModel("Agent", speech);
                logHandler.add(dataModel1);
                logHandler.add(dataModel2);

                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

                textToSpeech.speak(speech, TextToSpeech.QUEUE_ADD, null);
            }
        });

    }

    @Override
    public void onError(AIError error) {
        Log.d(TAG, "Error: " + error.getMessage());
        Toast.makeText(this, error.getMessage() + " .. Try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Log.d(TAG, "Listening started");
    }

    @Override
    public void onListeningCanceled() {
        Log.d(TAG, "Listening cancelled");
    }

    @Override
    public void onListeningFinished() {

        Log.d(TAG, "Listening finished");

    }

    public void clearConversation() {
        list.clear();
        logHandler.clear();
        mAdapter.notifyDataSetChanged();
    }


}
