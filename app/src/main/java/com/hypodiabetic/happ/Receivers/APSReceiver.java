package com.hypodiabetic.happ.Receivers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypodiabetic.happ.Constants;
import com.hypodiabetic.happ.MainApp;
import com.hypodiabetic.happ.Notifications;
import com.hypodiabetic.happ.Objects.APSResult;
import com.hypodiabetic.happ.Intents;
import com.hypodiabetic.happ.pumpAction;

/**
 * Created by Tim on 14/02/2016.
 */
@SuppressLint("ParcelCreator")
public class APSReceiver extends ResultReceiver {

    public APSReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        Notifications.clear("newTemp");                                         //Clears any open temp notifications
        Intent intentUpdate = new Intent(Intents.UI_UPDATE);

        switch (resultCode){
            case Constants.STATUS_FINISHED:

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                APSResult apsResult = gson.fromJson(resultData.getString("APSResult"), APSResult.class);

                if (apsResult.tempSuggested) pumpAction.newTempBasal(apsResult.getBasal(), MainApp.instance());

                //Send out updates of new APS run
                Notifications.updateCard();
                Notifications.debugCard(MainApp.instance(), apsResult);

                intentUpdate.putExtra("UPDATE", "NEW_APS_RESULT");
                intentUpdate.putExtra("APSResult", resultData.getString("APSResult"));
                LocalBroadcastManager.getInstance(MainApp.instance()).sendBroadcast(intentUpdate);

                break;
            case Constants.STATUS_ERROR:
                intentUpdate.putExtra("UPDATE", "ERROR_APS_RESULT");
                intentUpdate.putExtra("error", resultData.getString("error"));
                LocalBroadcastManager.getInstance(MainApp.instance()).sendBroadcast(intentUpdate);
                break;
        }

    }
}
