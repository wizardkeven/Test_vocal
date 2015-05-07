package com.example.wizar_000.test_vocal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.*;


public class VoiceRecognition extends Activity implements View.OnClickListener,OnInitListener {


   private String localLanguage = Locale.getDefault().getLanguage();
    private TextView outputText = null;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    //text to speech definition
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this,this);
        setContentView(R.layout.activity_voice_recognition);
        ImageButton speakButton = (ImageButton) findViewById(R.id.btn_speak);
        Button backHomeButton = (Button) findViewById(R.id.btn_back);
        outputText = (TextView) findViewById(R.id.text_output);
        speakButton.setOnClickListener(new speakOnClickListner());
        backHomeButton.setOnClickListener(new backHomeButtenClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voice_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }


    /*
    Initialization for textToSpeech
     */
    @Override
    public void onInit(int status) {
        if (status == SUCCESS) {

            int result;
//            Toast ss;
//            ss = Toast.makeText(getApplicationContext(),
//                    tts.isLanguageAvailable(Locale.FRENCH)+"",
//                    Toast.LENGTH_SHORT);
//            ss.show();
//            Log.e("Text to speech start!",tts.isLanguageAvailable(Locale.FRENCH)+"");
            result = tts.setLanguage(Locale.FRENCH);

            if (result == LANG_MISSING_DATA
                    || result == LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }else {
//                Toast t;
//                t = Toast.makeText(getApplicationContext(),
//                        "Your device supports text to Speech!",
//                        Toast.LENGTH_SHORT);
//                t.show();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy(){
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private class backHomeButtenClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class speakOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent speakIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speakIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    getClass().getPackage().getName());
            speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speakIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,localLanguage);
            try{
                startActivityForResult(speakIntent,VOICE_RECOGNITION_REQUEST_CODE);
                outputText.setText("");
            }catch (ActivityNotFoundException a){
                Toast t;
                t = Toast.makeText(getApplicationContext(),
                        "Oops, ta apparail ne supporte pas Speech to Text",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        String wordStr;
        String[] words;
        String firstWord;
        String robotNotif;
//        String secondWord;

        switch (requestCode){
            case VOICE_RECOGNITION_REQUEST_CODE:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> text = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    wordStr = text.get(0);
                    robotNotif = getString(R.string.InputNotif_FR,wordStr);
                    //print the vocal input
                    outputText.setText(robotNotif);
                    words = wordStr.split(" ");
                    firstWord = words[0];
//                    robotNotif = (localLanguage.toLowerCase() == "fr")
//                            ? getString(R.string.InputNotif_FR) : getString(R.string.InputNotif_EN);

                    speakOut(robotNotif);
                    if (firstWord.toLowerCase().equals(getString(R.string.LaunchAppKeyword_FR)) && words.length>1){
                        vocalCommandHandler(words);
                    }
                }
                break;
            }
        }
    }

    private void vocalCommandHandler(String[] words) {
//        String[] words;
        String appName = words[1];
//        String firstWord;
//        String secondWord;
//        words = wordStr.split(" ");
//        firstWord = words[1];
//        secondWord = words[2];
        if(words.length>2){
            for (int i=0;i<words.length-2;i++) {
                appName.concat(words[i+2]);
            }
        }

//        Toast t;
//        t = Toast.makeText(getApplicationContext(),
//                "L'appli a ouvrir est:"+appName,
//                Toast.LENGTH_SHORT);
//        t.show();
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        int size = packs.size();
        for (int v = 0; v < size; v++) {
            PackageInfo p = packs.get(v);
            String tmpAppName = p.applicationInfo.loadLabel(packageManager).toString();
            String pname = p.packageName;
//                urlAddress = urlAddress.toLowerCase();
//            tmpAppName = formalizeAppName(tmpAppName);
            tmpAppName = tmpAppName.toLowerCase();
//            Toast t;
//            t = Toast.makeText(getApplicationContext(),
//                    "L'appli a ouvrir est:"+appName+" "+tmpAppName,
//                    Toast.LENGTH_SHORT);
//            t.show();

            if (tmpAppName.trim().toLowerCase().equals(appName.trim().toLowerCase())) {
                PackageManager pm = this.getPackageManager();
                Log.d("VocalCommandHandler",tmpAppName);
                Intent appStartIntent = pm.getLaunchIntentForPackage(pname);

                if (null != appStartIntent) {
                    try {
                        this.startActivity(appStartIntent);
                    } catch (Exception ignored) {

                    }
                    }
                }
            }
    } // end of activityOnResult method

    /*
    This method will assemble the application name in a word without space and bar
     @author:Guokai
     */
//    private String formalizeAppName(String wordStr) {
//        String formalizedAppName;
//        String[] words = wordStr.split(" ");
//        formalizedAppName = words[0];
//        for (int namePointer=1;!words[namePointer].isEmpty();namePointer++){
//            formalizedAppName.concat(words[namePointer]);
//        }
//
//        if(formalizedAppName.contains("-")){
//            formalizedAppName.replace("-","");
//        }
//        return formalizedAppName;
//    }

    /*
    Text to speech parametered by a string
     */
    private void speakOut(String textToSpeek) {
        tts.speak(textToSpeek, TextToSpeech.QUEUE_FLUSH, null);
    }
}
