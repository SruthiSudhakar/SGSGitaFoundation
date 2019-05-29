package com.example.meena.sgsgitafoundation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.squareup.okhttp.*;
//TODO: 2 shlokas per view
//store text, audio, etc. in datastrcuture. along with english version.
//generate json files for each language. so parse different languages.
//multi-thread it. as they finish, they insert data in. two threads cant write into strcuture at once. locking data structure
public class TutorApp extends AppCompatActivity {
    MediaPlayer sara;
    SeekBar seekBar;
    SeekBar speedBar;
    Button play_tutorial;
    Button go;
    Spinner chapter_dropdown;
    Spinner tutorial_mode_spinner;
    Spinner language_spinner;
    EditText startRange;
    EditText endRange;
    ArrayAdapter<String> chapter_adapter;
    ArrayAdapter<String> tutorial_adapter;
    ArrayAdapter<String> language_adapter;
    CustomAdapter shlokaListViewAdapter;
    ListView shlokaListView;
    List<String> plainortutor = new ArrayList<>();
    List<String> languageList = new ArrayList<>();
    ArrayList<String> chapterNames = new ArrayList<>();
    List<String> plain_urls = new ArrayList<>();
    List<String> tutorial_urls = new ArrayList<>();
    List<String> plain_json_urls = new ArrayList<>();
    List<String> tutorial_json_urls = new ArrayList<>();
    String finalS = null;
    ArrayList<String> shloka_text_list = new ArrayList<String>();
    ArrayList<String> inners = new ArrayList<>();
    //Switch tutorial_mode;
    ProgressDialog dialog;
    String subscriptionKey = "8820028d6f5049deb6fecaca87bcd385";
    String url = "https://api.cognitive.microsofttranslator.com/transliterate?api-version=3.0&language=hi&fromScript=deva&toScript=latn";
    private Handler mSeekbarUpdateHandler = new Handler();
    int currentShloka = 0;
    OkHttpClient client = new OkHttpClient();
    private ArrayList<String[]> transliterationList;
    private Context mContext = this;
    int count = 0;
    int intalizedCount = 0;
    AssetManager am;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // my_child_toolbar is defined in the layout file
        setSupportActionBar(toolbar);
        toolbar.setTitle("Tutorial Application");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        am = mContext.getAssets();
        go = (Button) findViewById(R.id.go_id);
        seekBar = (SeekBar) findViewById(R.id.seekBar_id);
        speedBar = (SeekBar) findViewById(R.id.speedbar_id);
        startRange = (EditText) findViewById(R.id.startRange_id);
        endRange = (EditText) findViewById(R.id.endRange_id);
        play_tutorial  = (Button) findViewById(R.id.play_tutorial);
        //tutorial_mode = (Switch) findViewById(R.id.tutorial_switch_id);
        tutorial_mode_spinner = (Spinner) findViewById(R.id.tutorials_spinner_id);
        language_spinner = (Spinner) findViewById(R.id.language_spinner_id);
        chapter_dropdown = (Spinner) findViewById(R.id.chapter_spinner_id);
        chapterNames = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.chapter_names_devanagri)));
        plain_urls = Arrays.asList(getResources().getStringArray(R.array.plain_urls_array));
        tutorial_urls = Arrays.asList(getResources().getStringArray(R.array.tutorial_urls_array));
        plain_json_urls = Arrays.asList(getResources().getStringArray(R.array.plain_jsons_array));
        tutorial_json_urls = Arrays.asList(getResources().getStringArray(R.array.tutorial_jsons_array));
        plainortutor.add("Non-Tutorial Mode");
        plainortutor.add("Tutorial Mode");
        languageList.add("Devanagari");
        languageList.add("English");
        chapter_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, chapterNames);
        tutorial_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, plainortutor);
        language_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languageList);
        startRange.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,1)});
        endRange.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,1)});
        Log.d("MEDIA PLAYER RE",""+MediaPlayer.create(TutorApp.this, Uri.parse("http://sgsgitafoundation.org/bg/00/plain_chapter.m4a")));
//        sara = new MediaPlayer();
        sara = MediaPlayer.create(TutorApp.this, Uri.parse("http://sgsgitafoundation.org/bg/00/plain_chapter.m4a"));
        if (sara == null) {
            Log.d("MEDIA PLAYER RE", "SARA IS NULL: " + sara);
            Toast.makeText(this,"NO INTERNET CONNCTION", Toast.LENGTH_LONG);
        }
        sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
        go.setEnabled(false);
        play_tutorial.setEnabled(false);
//        sara.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mp.start();
//                    Log.d("Life Cycle", "Prepared Listener");
//                    go.setEnabled(true);
//                    play_tutorial.setEnabled(true);
//            }
//        });
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading File");
        shlokaListViewAdapter = new CustomAdapter(this, R.layout.activity_shloka_listview, shloka_text_list);
        shlokaListView=(ListView) findViewById(R.id.shlokaListView_id);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        ViewGroup.LayoutParams params = shlokaListView.getLayoutParams();
        params.width = widthPixels - 50;
        shlokaListView.setLayoutParams(params);
        shlokaListView.requestLayout();
        chapter_dropdown.setAdapter(chapter_adapter);
        tutorial_mode_spinner.setAdapter(tutorial_adapter);
        language_spinner.setAdapter(language_adapter);
        shlokaListView.setAdapter(shlokaListViewAdapter);
        new LoadTextCells().execute("");
        new PreLoadTransliterations().execute("");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    sara.seekTo((int)(Double.parseDouble(inners.get(progress)))*1000);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        speedBar.setMax(5);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!sara.isPlaying()) {
                            sara.setPlaybackParams(sara.getPlaybackParams().setSpeed(1.0f + progress / 10f));
                            sara.pause();
//                            Log.d("speedBar go", "is playing");
                        } else {
                            sara.setPlaybackParams(sara.getPlaybackParams().setSpeed(1.0f + progress / 10f));
//                            Log.d("speedBar go", "paused");
                        }
                    } else {
                        Toast.makeText(TutorApp.this, "Update to Version 6.0, Marshmallow, or above to control playback speed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sara.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                play_tutorial.setText("Play");
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }
        });
        shlokaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Todo: change the value of the seekbar not the media player
                sara.seekTo((int)(Double.parseDouble(inners.get(position)))*1000);
            }
        });

        setSupportActionBar(toolbar);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startRange.getText().toString().isEmpty()) {
                    int startTime = Integer.valueOf(startRange.getText().toString());
                    if ((startTime < 0) || (startTime > (shloka_text_list.size() - 1))) {
                        Toast.makeText(TutorApp.this, "Range is out of bounds", Toast.LENGTH_SHORT).show();
                    } else {
                        //TODO: how to export to send over
                        //TODO:Hilighting of text (json file gives you hilighting positon)
                        sara.seekTo((int) (Double.parseDouble(inners.get(startTime))) * 1000);
                        sara.start();
                        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 2000);
                        play_tutorial.setText("Pause");
                    }
                } else {
                    Toast.makeText(TutorApp.this, "Must enter number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        play_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sara.isPlaying()) {
                    play_tutorial.setText("Play");
                    sara.pause();
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                } else {
                    sara.start();
                    play_tutorial.setText("Pause");
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar,2000);
                }
            }
        });
        tutorial_mode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sara.stop();
                seekBar.setEnabled(false);
                speedBar.setEnabled(false);
                play_tutorial.setEnabled(false);
                go.setEnabled(false);
                shlokaListView.setEnabled(false);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                dialog.show();
                if (position == 1) {
                    new LoadTextCells().execute("");
                    sara.stop();
                    play_tutorial.setText("Play");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(tutorial_urls.get(chapter_dropdown.getSelectedItemPosition())));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } else {
                    new LoadTextCells().execute("");
                    sara.stop();
                    play_tutorial.setText("Play");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(plain_urls.get(chapter_dropdown.getSelectedItemPosition())));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        chapter_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sara.stop();
                seekBar.setEnabled(false);
                speedBar.setEnabled(false);
                play_tutorial.setEnabled(false);
                go.setEnabled(false);
                shlokaListView.setEnabled(false);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                dialog.show();
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                if (tutorial_mode_spinner.getSelectedItemPosition() == 1) {
                    new LoadTextCells().execute("");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(tutorial_urls.get(position)));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } else {
                    new LoadTextCells().execute("");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(plain_urls.get(position)));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                play_tutorial.setText("Play");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sara.stop();
                seekBar.setEnabled(false);
                speedBar.setEnabled(false);
                play_tutorial.setEnabled(false);
                go.setEnabled(false);
                shlokaListView.setEnabled(false);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                dialog.show();
                if (tutorial_mode_spinner.getSelectedItemPosition() == 1) {
                    new LoadTextCells().execute("");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(tutorial_urls.get(chapter_dropdown.getSelectedItemPosition())));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } else {
                    new LoadTextCells().execute("");
                    sara = MediaPlayer.create(TutorApp.this, Uri.parse(plain_urls.get(chapter_dropdown.getSelectedItemPosition())));
                    sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                play_tutorial.setText("Play");

                if (position == 0) {
                    chapterNames = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.chapter_names_devanagri)));
                    chapter_dropdown.setPrompt("Choose Chapter");
                    Log.d("CHAPTER LANGUAGE CHANGE", "There was a chapter change to devanagri");
                } else {
                    chapterNames = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.chapter_names)));
                    chapter_dropdown.setPrompt("Choose Chapter");
                    Log.d("CHAPTER LANGUAGE CHANGE", "There was a chapter change to english");
                }
                chapter_adapter.clear();
                chapter_adapter.addAll(chapterNames);
                chapter_adapter.notifyDataSetChanged();
                Log.d("CHAPTER LANGUAGE CHANGE", "notfied of change");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            Double i = sara.getCurrentPosition()/1000.0;
            Double d = Double.parseDouble(inners.get(0));
            int x = 0;
            while ((i > d) && (x < (inners.size()-1))) {
                x++;
                d = Double.parseDouble(inners.get(x));
            }
            seekBar.setProgress(x-1);
            shlokaListView.smoothScrollToPosition(x-1);
            try {
                if (x > 0) {
//                    Log.d("CLASS NAMES: ", x-1 + "   " + shlokaListView.getFirstVisiblePosition() );
//                    ((TextView)(shlokaListView.getChildAt(x - shlokaListView.getFirstVisiblePosition() ).findViewById(R.id.individual_shloka_textview_id))).setTextColor(Color.rgb(0,0,255));
                }
            } catch (Exception e) {
//                Log.d("CLASS NAMES: ", e.getMessage());
            }

            mSeekbarUpdateHandler.postDelayed(this, 500);
            if (!sara.isPlaying()) {
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }
        }
    };
    public String Post(String s) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \""+s+"\"\n}]");
        Request request = new Request.Builder()
                .url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    public static String prettify(String json_text) {
        /*JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();*/
        JSONParser jparser = new JSONParser();
        Object obj;

        String text = "";
        try {
            Log.d("PRETTIFY", "1");
            obj = jparser.parse(json_text);
            Log.d("PRETTIFY", "2: "+obj);
            JSONArray entry = (JSONArray) obj;
            Log.d("PRETTIFY", "3: "+entry);
            text = (String) ((JSONObject)entry.iterator().next()).get("text");
            Log.d("PRETTIFY", "4: "+text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return text;
    }

    private class LoadTextCells extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute()
        {
            if (!dialog.isShowing()) {
                dialog.show();
            }
//            Log.d("loadData()","SHOWING");
        }
        protected String doInBackground(String ... fileName) {
            shloka_text_list.clear();
            inners.clear();
            try {
                finalS="";
                URL yahoo;
                if (tutorial_mode_spinner.getSelectedItemPosition() == 1) {
//                    Log.d("loadData() json info: "," chapter loading: "+tutorial_json_urls.get(chapter_dropdown.getSelectedItemPosition())+"");
                    yahoo = new URL(tutorial_json_urls.get(chapter_dropdown.getSelectedItemPosition()));
                    if (chapter_dropdown.getSelectedItemPosition() == 0) {
                        yahoo = new URL(plain_json_urls.get(chapter_dropdown.getSelectedItemPosition()));
                        //Log.d("SHOWING TOAST", "SHOWING TOAST");
                    }
                } else {
//                    Log.d("loadData() json info: "," chapter loading: "+plain_json_urls.get(chapter_dropdown.getSelectedItemPosition())+"");
                    yahoo = new URL(plain_json_urls.get(chapter_dropdown.getSelectedItemPosition()));
                }
//                Log.d("CHECKTIME: ","before buffered reader--- " + Calendar.getInstance().getTime());
//                BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream(),"UTF-8"));
                BufferedReader in;
//                if (intalizedCount == 0) {
//                    in = new BufferedReader(new InputStreamReader(am.open("pl.json"), "UTF-8"));
//                } else {
                    in = new BufferedReader(new InputStreamReader(yahoo.openStream(),"UTF-8"));
//                }
                String inputLine;
//                Log.d("CHECKTIME: ","before while--- " + Calendar.getInstance().getTime());
                while ((inputLine = in.readLine()) != null) {
                    finalS += inputLine;
                }
//                Log.d("CHECKTIME: ","after while--- " + Calendar.getInstance().getTime());
                byte[] bytes = finalS.getBytes();
                in.close();
                finalS = new String(bytes, "UTF-8");
//                Log.d("loadData() json info: ","finals: "+finalS);
                // CREATING JSON OBJECt
                JSONParser jparser = new JSONParser();
                Object obj = jparser.parse(finalS);
//                Log.d("loadData() json info: ","post obj");

                JSONObject jsonObject = (JSONObject) obj;
//                Log.d("loadData() json info: ","post jsonObj");

                JSONArray shlokas = (JSONArray) jsonObject.get("shloka");
//                Log.d("loadData() json info: ","post shlokas");

                Iterator<JSONObject> iterator = shlokas.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    String returnString = "";
                    JSONObject js = iterator.next();
                    String s = js.get("shlokaNum").toString();
                    JSONArray entry = (JSONArray) js.get("entry");
                    Iterator<JSONObject> eiterator = entry.iterator();
                    //future clean up
                    boolean firstTime = true;
                    while (eiterator.hasNext()) {
                        JSONObject line = eiterator.next();
                        String startTime = line.get("startTime").toString();
                        if (firstTime) {
                            inners.add(startTime);
                            firstTime = false;
                        }
                        String endTime = line.get("endTime").toString();
                        String text = line.get("text").toString();
                        if (tutorial_mode_spinner.getSelectedItemPosition() == 0) {
                            returnString = returnString + text + "\n";
                        }
                        else {
                            if (chapter_dropdown.getSelectedItemPosition() != 0) {
                                if (line.get("teacher").toString().contains("YS")) {
                                    returnString = returnString + text + "\n";
                                }
                            }
                            else {
                                returnString = returnString + text + "\n";
                            }
                        }
                    }
//                    Log.d("RETURN STRING: ",returnString);
//                    try {
//                        String response = Post(returnString);
//                        Log.d("TRANSLATION: ","IN TRANSLATION: "+response);
//                        returnString = prettify(response);
//                        Log.d("TRANSLATION: ","RESPONSE: \n"+returnString);
//                    } catch (Exception e) {
//                        Log.d("TRANSLATION ERROR 1: ",""+e.getMessage());
//                    }
                    //TODO: if its zero it crashes bc two async tasks running at once
                    if (language_spinner.getSelectedItemPosition() == 1) {
                        returnString = transliterate(returnString);
                    }
//                    Log.d("CHECKTIME: ","after parse--- " + Calendar.getInstance().getTime());
                    shloka_text_list.add(returnString);
                    index++;
                }
            } catch (ParseException e) {
                Log.d("loadData() json info: ", " parse error : "+e.toString());
            } catch (UnsupportedEncodingException e) {
                Log.d("loadData() json info: ", " encoding error : "+e.toString());
            } catch (FileNotFoundException e) {
                Log.d("loadData() json info: ", "not found error : "+e.toString());
            } catch (IOException e) {
                Log.d("loadData() json info: ", "io error : "+e.toString());
            }
            return "";
        }
        protected void onProgressUpdate(Void...voids) { }
        protected void onPostExecute(String voids) {
            shlokaListViewAdapter.notifyDataSetChanged();
//            Log.d("stats in method: ", "done");
            intalizedCount = 1;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if ((tutorial_mode_spinner.getSelectedItemPosition() == 1)&&(chapter_dropdown.getSelectedItemPosition() == 0)) {
                Toast.makeText(TutorApp.this,"No tutorial mode for Dhyana Shloka. Playing plain mode.", Toast.LENGTH_LONG).show();
                Log.d("SHOWING TOAST", "SHOWING TOAST");
            }
            startRange.setText(String.valueOf(0));
            endRange.setText(String.valueOf(shloka_text_list.size()-2));
//            Log.d("EDIT TEXTS: ", startRange.getText().toString());
            seekBar.setMax(shloka_text_list.size()-1);
            speedBar.setMax(5);
            seekBar.setProgress(0);
            speedBar.setProgress(0);
            currentShloka = 0;
            seekBar.setEnabled(true);
            speedBar.setEnabled(true);
            play_tutorial.setEnabled(true);
            go.setEnabled(true);
            shlokaListView.setEnabled(true);
        }
    }
    private class PreLoadTransliterations extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {}
        protected String doInBackground(String ... fileName) {
            String bigS = "";
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(am.open("CharTest.txt"), "UTF8"));
                String str;
                while ((str = in.readLine()) != null) {
                    bigS += str + "\n";
                }
                in.close();
                Log.d("PreLoad: ", "finalS: " + bigS);
                String[] parts = bigS.split("\n");
                transliterationList = new ArrayList<>();
                for (int i = 0; i < parts.length; i++) {
                    System.out.println("Parts i: " + parts[i]);
                    transliterationList.add(parts[i].split(" "));
                }
//                for (int x = 0; x < transliterationList.size(); x++) {
//                    for (int )
//                        Log.d("PreLoad: ", "x: "+x+ "i: "+ parts[x]);
//                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("PreLoad: exceptiojn", "unsupportedencoding");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("PreLoad: exceptiojn", "FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("PreLoad: exceptiojn", "IOException");
            }
            return "";
        }
        protected void onProgressUpdate(Void...voids) { }
        protected void onPostExecute(String voids) { }
    }
    //TODO: on first loading, it calls the async multiple times bc of each listener
//    private class Transliterations extends AsyncTask<String, Void, String> {
//        @Override
//        protected void onPreExecute() {}
//        protected String doInBackground(String ... message) {
//            message[0];message =
//            String bigS = "";
//            BufferedReader in = null;
//            try {
//                in = new BufferedReader(new InputStreamReader(am.open("CharTest.txt"), "UTF8"));
//                String str;
//                while ((str = in.readLine()) != null) {
//                    bigS += str + "\n";
//                }
//                in.close();
//                Log.d("PreLoad: ", "finalS: " + bigS);
//                String[] parts = bigS.split("\n");
//                transliterationList = new ArrayList<>();
//                for (int i = 0; i < parts.length; i++) {
//                    System.out.println("Parts i: " + parts[i]);
//                    transliterationList.add(parts[i].split(" "));
//                }
////                for (int x = 0; x < transliterationList.size(); x++) {
////                    for (int )
////                        Log.d("PreLoad: ", "x: "+x+ "i: "+ parts[x]);
////                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                Log.d("PreLoad: exceptiojn", "unsupportedencoding");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Log.d("PreLoad: exceptiojn", "FileNotFoundException");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("PreLoad: exceptiojn", "IOException");
//            }
//            return "";
//        }
//        protected void onProgressUpdate(Void...voids) { }
//        protected void onPostExecute(String voids) { }
//    }
    private String transliterate(String message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            boolean matched = false;
            for (int x = 0; x < transliterationList.size(); x++) {
                for (int y = 0; y < transliterationList.get(x).length; y++) {
                    if (message.substring(i, i + 1).equals(transliterationList.get(x)[y])) {
//                        if (count == 0) {
//                            Log.d("transliterate method: ", "MATCHED Message of i: " + message.substring(i, i + 1));
//                            Log.d("transliterate method: ", "MATCHED DEV char: " + (transliterationList.get(x)[y]));
//                            if (x != 10) {
//                                Log.d("transliterate method: ", "MATCHED LAT char: " + (transliterationList.get(x + 5)[y]));
//                            } else {
//                                Log.d("transliterate method: ", "MATCHED LAT char: " + (transliterationList.get(x)[y]));
//                            }
//                        }
                        if (x != 10) {
                            if (!transliterationList.get(x + 5)[y].equals("@")) {
                                if (x == 2) {
                                    builder.append(transliterationList.get(x + 5)[y] + "a");
//                                    Log.d("transliterate method: ", "ITS A CONSONANT");
                                } else {
                                    builder.append(transliterationList.get(x + 5)[y]);
                                }
                            }
                            if (transliterationList.get(x + 5)[y].equals("@")){
                                builder.deleteCharAt(builder.length()-1);
                            }
                            if ((x == 0) || (x == 1)) {
                                try {
                                    if ((!builder.substring(builder.length()-2,builder.length() - 1).equals("\n")) && (!builder.substring(builder.length()-2,builder.length() - 1).equals(" "))) {
                                        builder.deleteCharAt(builder.length() - 2);
                                    } else {
//                                        Log.d("transliterate method: ", "not DELETING in else METHOD " + builder.substring(builder.length()-2,builder.length() - 1));
                                    }
                                } catch (Exception e) {
//                                    Log.d("transliterate method: ", "COULDNT DELETE " + message.substring(i, i + 1));
                                }
                            }
                        } else {
                            builder.deleteCharAt(builder.length()-1);
                        }
                        matched = true;
                    }
                }
            }
            if (!matched) {
                builder.append(message.substring(i,i+1));
            }
        }
//        Log.d("transliterate method: ","in method: "+builder.toString());
        //count++;
        return builder.toString();
    }

    public class CustomAdapter extends ArrayAdapter<String> {
        ArrayList<String> lists;
        Context mainContext;
        int reasource;


        public CustomAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            lists = objects;
            mainContext = context;
            reasource = resource;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();

                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.activity_shloka_listview, null);
                holder.label= (TextView) convertView.findViewById(R.id.individual_shloka_textview_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.label.setText(shloka_text_list.get(position));
            return convertView;
        }
        private class ViewHolder {
            TextView label;
        }
    }
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Life Cycle", "stop");
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
        sara.release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Life Cycle", "Restart");
        play_tutorial.setText("Play");
        if (tutorial_mode_spinner.getSelectedItemPosition() == 1) {
            sara = MediaPlayer.create(TutorApp.this, Uri.parse(tutorial_urls.get(chapter_dropdown.getSelectedItemPosition())));
            sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            sara = MediaPlayer.create(TutorApp.this, Uri.parse(plain_urls.get(chapter_dropdown.getSelectedItemPosition())));
            sara.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Life Cycle", "Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Life Cycle", "Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Life Cycle", "Resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Life Cycle", "Start");
    }
    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.top_layout_id, new MySettingsFragment()).commit();
                startActivity(new Intent(TutorApp.this, SettingsPrefActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutor, menu);
        return true;
    }
//    public static class MySettingsFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(@Nullable Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.preferences);
//            findPreference("language").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    String stringValue = newValue.toString();
//                    ListPreference listPreference = (ListPreference) preference;
//                    int index = (listPreference).findIndexOfValue(stringValue);
//                    listPreference.setSummary(listPreference.getEntries()[index]);
//                    if (index == 0) {
//                        ((ListPreference) findPreference("chapter")).setEntries(R.array.chapter_names_devanagri);
//                        findPreference("chapter").setSummary(((ListPreference)findPreference("chapter")).getEntries()[index]);
//                    } else {
//                        ((ListPreference) findPreference("chapter")).setEntries(R.array.chapter_names);
//                        findPreference("chapter").setSummary(((ListPreference)findPreference("chapter")).getEntries()[index]);
//
//                    }
//                    // Set the summary to reflect the new value.
//                    return true;
//                }
//            });
//            findPreference("chapter").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    String stringValue = newValue.toString();
//                    ListPreference listPreference = (ListPreference) preference;
//                    int index = (listPreference).findIndexOfValue(stringValue);
//                    listPreference.setSummary(listPreference.getEntries()[index]);
//                    // Set the summary to reflect the new value.
//                    return true;
//                }
//            });
//            findPreference("mode").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    String stringValue = newValue.toString();
//                    ListPreference listPreference = (ListPreference) preference;
//                    int index = (listPreference).findIndexOfValue(stringValue);
//                    listPreference.setSummary(listPreference.getEntries()[index]);
//                    // Set the summary to reflect the new value.
//                    return true;
//                }
//            });
//        }
//    }
}





