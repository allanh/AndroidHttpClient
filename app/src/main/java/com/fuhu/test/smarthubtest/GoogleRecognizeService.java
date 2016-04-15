package com.fuhu.test.smarthubtest;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class GoogleRecognizeService extends Service implements RecognitionListener {
    private static final String TAG = GoogleRecognizeService.class.getSimpleName();

    public static final String ACTION_RECEIVE_RESPONSE = "com.fuhu.test.smarthubtest.receiveResponse";
    // speech recognizer for callbacks
    private SpeechRecognizer recognizer;

    // intent for speech recogniztion
    private Intent intent;

    //handler to post changes to TextView
    private Handler mHandler = new Handler();

    private int readySpeechCount = 0;

    // create a new command parser
    private static ICommandParser mCommandParser = new MockCommandParser();

    @Override
    public void onCreate() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        createRecognizer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createRecognizer() {
        // create a new SpeechRecognizer
        recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        recognizer.setRecognitionListener(this);
        // Starts listening for speech
        recognizer.startListening(intent);
    }

    public void startRecognizing() {
//        if (isRecognizing) {
//        recognizer.stopListening();
        recognizer.cancel();
        recognizer.startListening(intent);
//        } else {
//            recognizer.stopListening();
//        }
    }

    public void stopRecognizing() {
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
            Log.d(TAG, "speech destroy");
        }
    }

    /**
     * Called when the endpointer is ready for the user to start speaking.
     */
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "On ready for speech: " + (++readySpeechCount));
    }

    /**
     * The user has started to speak.
     */
    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "On beginning of speech");
    }

    /**
     * The sound level in the audio stream has changed. There is no guarantee that this method will
     * be called.
     */
    @Override
    public void onRmsChanged(float rmsdB) {

    }

    /**
     * More sound has been received. The purpose of this function is to allow giving feedback to the
     * user regarding the captured audio. There is no guarantee that this method will be called.
     */
    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    /**
     * Called after the user stops speaking.
     */
    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "On end of speech");
    }

    /**
     * A network or recognition error occurred.
     */
    @Override
    public void onError(int error) {
        String errorMessage = getErrorText(error);
        Log.d(TAG, "Failed: " + errorMessage);
//        txtSpeechInput.setText(errorMessage);
//        toggleButton.setChecked(false);
    }

    /**
     * Called when recognition results are ready
     */
    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "On result");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String result = matches.get(0);

        // parse message
        final String response = mCommandParser.parseCommand(result);

        if (CommandType.Exit.equals(response)) {
            // TODO exit
        } else {
            mHandler.post(new Runnable() {
                public void run() {
                    Intent intent = new Intent(ACTION_RECEIVE_RESPONSE);
                    intent.putExtra("response", response);
                    sendBroadcast(intent);
                    startRecognizing();
                }
            });
        }
    }

    /**
     * Called when partial recognition results are available.
     */
    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    /**
     * Reserved for adding future events.
     *
     * @param eventType the type of the occurred event
     * @param params a Bundle containing the passed parameters
     */
    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                startRecognizing();
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                stopRecognizing();
                startRecognizing();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                stopRecognizing();
                createRecognizer();
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onDestroy() {
        stopRecognizing();
        super.onDestroy();
    }
}
