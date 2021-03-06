package com.bit.bitcall;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class callact extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String data,conc;
    TextToSpeech tts;
    String userin="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callact);
        Intent intent = getIntent();
        data=(String) intent.getExtras().get("NUM");
        conc=getContactName(data,this);
        Log.e(":::final",conc+data);

        AudioManager mobilemode = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        mobilemode.setStreamVolume(AudioManager.STREAM_MUSIC,mobilemode.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        mobilemode.setStreamVolume(AudioManager.STREAM_RING,0,0);


        tts=new TextToSpeech(this,this);
        tts.setSpeechRate(0.5f);
        ConvertTextToSpeech("Biswajit u got a call from "+conc+"     Would you like to accept or reject");
        while(tts.isSpeaking());
        for(int i=0;i<=20000;i++);
        promptSpeechInput();
        //tts.setLanguage(Locale.US);
        //tts.speak("Text to say aloud", TextToSpeech.QUEUE_ADD, null);




    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "RUNNING");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "NOT SUPPORTED",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub
        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak("Biswajit u got a call from "+text+"     Would you like to accept or reject", TextToSpeech.QUEUE_FLUSH, null);

    }
/*
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

*/
    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="NONE";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    userin=result.get(0);
                }
                break;
            }

        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.getDefault());
            if(result==TextToSpeech.LANG_MISSING_DATA ||
                    result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("error", "This Language is not supported");
            }
            else{
                ConvertTextToSpeech("START");
            }
        }
        else
            Log.e("error", "Initilization Failed!");
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
}

