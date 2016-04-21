package com.fuhu.test.smarthub.middleware.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import com.fuhu.test.smarthub.callback.IFTTTCallback;
import com.fuhu.test.smarthub.middleware.componet.ActionPreferences;
import com.fuhu.test.smarthub.middleware.componet.Log;
import com.fuhu.test.smarthub.middleware.componet.MailItem;
import com.fuhu.test.smarthub.middleware.manager.IFTTTManager;
import com.fuhu.test.smarthub.middleware.manager.TextToSpeechManager;

public class GoogleRecognizeReceiver extends BroadcastReceiver {
    private static final String TAG = GoogleRecognizeReceiver.class.getSimpleName();
    private Context mContext;
    private TextToSpeechManager mSpeechManager;
    private TextView mTextView;

    public GoogleRecognizeReceiver(Context context, TextToSpeechManager speechManager, TextView textView) {
        this.mContext = context;
        this.mSpeechManager = speechManager;
        this.mTextView = textView;
    }

    public static IntentFilter getFilter() {
        // create a new BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionPreferences.RECEIVE_RECOGNIZE_RESULT);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "receive broadcast");
        if (intent != null && intent.getExtras() != null) {
            String response = intent.getStringExtra("result");
            Log.d(TAG, "result: " + response);

            if (mTextView != null) {
                mTextView.setText(response);
            }

            // speak response
            if (mSpeechManager != null) {
                mSpeechManager.speakOut(response);
            }

            if (mContext != null) {
                // Send to IFTTT
                IFTTTManager.sendToIFTTT(mContext,
                        new IFTTTCallback() {
                            public void onIftttReceived(MailItem mailItem) {

                            }

                            ;

                            public void onFailed(String status, String message) {

                            }

                            ;
                        },
                        response);
            }
        }
    }
}