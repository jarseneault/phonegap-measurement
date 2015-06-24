package com.quantcast.phonegapplugin;

import com.quantcast.measurement.service.QuantcastClient;
import com.quantcast.measurement.service.QCNetworkMeasurement;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

import java.util.Arrays;

public class QuantcastMeasurementPlugin extends CordovaPlugin {

    private static final String VERSION_LABEL = "_sdk.phonegap.android.v115";

    private String storedApiKey = "";
    private String storedNetworkCode = "";
    private String storedUserId = "";
    private boolean storedChildrenFlag = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean retval = true;
        if (action.equals("beginMeasurementSession")) {
            String apiKey = args.getString(0);
            String userId = args.getString(1);
            String[] labels = this.getLabels(args.get(2), VERSION_LABEL);
            String userhash = QuantcastClient.activityStart(cordova.getActivity(), apiKey, userId, labels);
            if(userhash != null){
                callbackContext.success(userhash);
            }
        } else if (action.equals("beginNetworkMeasurementSession")) {
            String apiKey = storedApiKey = args.getString(0);
            Log.d("QuantcastMeasurementPlugin", apiKey);
            if(apiKey != null && apiKey.equals("null")) {
                apiKey = null;
            }
            String networkCode = storedNetworkCode = args.getString(1);
            String userId = storedUserId = args.getString(2);
            String[] appLabels = this.getLabels(args.get(3), VERSION_LABEL);
            String[] networkLabels = this.getLabels(args.get(4), VERSION_LABEL);
            boolean appIsDirectedAtChildren = storedChildrenFlag = args.getBoolean(5);
            String userhash = QCNetworkMeasurement.activityStart(cordova.getActivity(), apiKey, networkCode, userId, appLabels, networkLabels, appIsDirectedAtChildren);
            if(userhash != null){
                callbackContext.success(userhash);
            }
        } else if (action.equals("endMeasurementSession")) {
            QuantcastClient.activityStop(this.getLabels(args.get(0)));
        } else if (action.equals("endNetworkMeasurementSession")) {
            QCNetworkMeasurement.activityStop(this.getLabels(args.get(0)), this.getLabels(args.get(1)));
        } else if (action.equals("pauseMeasurementSession")) {
            QuantcastClient.activityStop(this.getLabels(args.get(0)));
        } else if (action.equals("pauseNetworkMeasurementSession")) {
            QCNetworkMeasurement.activityStop(this.getLabels(args.get(0)), this.getLabels(args.get(1)));
        } else if (action.equals("resumeMeasurementSession")) {
            String[] labels = this.getLabels(args.get(0), VERSION_LABEL);
            QuantcastClient.activityStart(cordova.getActivity(), labels);
        } else if (action.equals("resumeNetworkMeasurementSession")) {
            String[] appLabels = this.getLabels(args.get(0), VERSION_LABEL);
            String[] networkLabels = this.getLabels(args.get(1), VERSION_LABEL);
            QCNetworkMeasurement.activityStart(cordova.getActivity(), storedApiKey, storedNetworkCode, storedUserId, appLabels, networkLabels, storedChildrenFlag);
        } else if (action.equals("recordUserIdentifier")) {
            String userhash = QuantcastClient.recordUserIdentifier(args.getString(0));
            if(userhash != null){
                callbackContext.success(userhash);
            }
        } else if (action.equals("recordNetworkUserIdentifier")) {
            String userhash = QCNetworkMeasurement.recordUserIdentifier(args.getString(0), this.getLabels(args.get(1)), this.getLabels(args.get(2)));
            if(userhash != null){
                callbackContext.success(userhash);
            }
        } else if (action.equals("logEvent")) {
            QuantcastClient.logEvent(args.getString(0), this.getLabels(args.get(1)));
        } else if (action.equals("logNetworkEvent")) {
            QCNetworkMeasurement.logEvent(args.getString(0), this.getLabels(args.get(1)), this.getLabels(args.get(2)));
        } else if (action.equals("setGeolocation")) {
            QuantcastClient.setEnableLocationGathering(args.getBoolean(0));
        } else if (action.equals("setOptOut")) {
            QuantcastClient.setOptOut(cordova.getActivity(), args.getBoolean(0));
        } else if (action.equals("setDebugLogging")) {
            boolean logOn = args.getBoolean(0);
            QuantcastClient.enableLogging(logOn);
        } else if (action.equals("displayUserPrivacyDialog")) {
            QuantcastClient.showAboutQuantcastScreen(cordova.getActivity());
        } else if (action.equals("uploadEventCount")) {
            QuantcastClient.setUploadEventCount(args.getInt(0));
        } else {
            //anything else I don't know
            retval = false;
        }
        return retval;
    }

    private String[] getLabels(Object labels, String appendingItem) throws JSONException {
        String[] label = getLabels(labels);
        if(appendingItem != null){
            label = Arrays.copyOf(label, label.length+1);
            label[label.length-1] = appendingItem;
        }
        return label;
    }

    private String[] getLabels(Object labels) throws JSONException {
        String[] label;
        if (labels instanceof JSONArray) {
            label = new String[((JSONArray) labels).length()];
            for (int i = 0; i < ((JSONArray) labels).length(); ++i) {
                label[i] = ((JSONArray) labels).getString(i);
            }
        } else if (labels instanceof String) {
            label = new String[]{(String) labels};
        } else {
            label = new String[0];
        }
        return label;
    }
}



