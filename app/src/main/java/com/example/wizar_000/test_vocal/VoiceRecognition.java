package com.example.wizar_000.test_vocal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class VoiceRecognition extends Activity implements View.OnClickListener {

    protected static final int RESULT_SPEECH = 1;
    private ImageButton speakButton = null;
    private Button backHomeButton = null;
    private TextView outputText = null;


    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);
        speakButton = (ImageButton)findViewById(R.id.btn_speak);
        backHomeButton = (Button)findViewById(R.id.btn_back);
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
            speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"fr-FR");
            try{
                startActivityForResult(speakIntent,RESULT_SPEECH);
                outputText.setText("");
            }catch (ActivityNotFoundException a){
                Toast t = Toast.makeText(getApplicationContext(),
                        "Oops, ta apparail ne supporte pas Speech to Text",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case RESULT_SPEECH:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> text = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    outputText.setText("You just said: "+text.get(0));
                }
                break;
            }
        }
    }
}
