package ch.milosz.reactnative;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.LifecycleEventListener;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDKInitializeListener;

import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;

import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;

import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.InviteOptions;


public class RNZoomUsModule extends ReactContextBaseJavaModule implements ZoomSDKInitializeListener, MeetingServiceListener, LifecycleEventListener {

  private final static String TAG = "RNZoomUs";
  private final ReactApplicationContext reactContext;

  private Boolean isInitialized = false;
  private Promise initializePromise;
  private Promise meetingPromise;

  public RNZoomUsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addLifecycleEventListener(this);
  }

  @Override
  public String getName() {
    return "RNZoomUs";
  }

  @ReactMethod
  public void initialize(final ReadableMap initParams, final Promise promise) {
    if (isInitialized) {
      promise.resolve("Already initialize Zoom SDK successfully.");
      return;
    }

    isInitialized = true;

    try {
      initializePromise = promise;

      reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ZoomSDKInitParams params = new ZoomSDKInitParams();
            params.appKey = initParams.getString("appKey");
            params.appSecret = initParams.getString("appSecret");
            params.domain = initParams.getString("domain");

            ZoomSDK zoomSDK = ZoomSDK.getInstance();
            zoomSDK.initialize(reactContext.getCurrentActivity(),RNZoomUsModule.this, params);
          }
      });
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  @ReactMethod
  public void startMeeting(
    final String displayName,
    final String meetingNo,
    final String userId,
    final int userType,
    final String zoomAccessToken,
    final String zoomToken,
    Promise promise
  ) {
    try {
      meetingPromise = promise;

      ZoomSDK zoomSDK = ZoomSDK.getInstance();
      if(!zoomSDK.isInitialized()) {
        promise.reject("ERR_ZOOM_START", "ZoomSDK has not been initialized successfully");
        return;
      }

      final MeetingService meetingService = zoomSDK.getMeetingService();
      if(meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
        long lMeetingNo = 0;
        try {
          lMeetingNo = Long.parseLong(meetingNo);
        } catch (NumberFormatException e) {
          promise.reject("ERR_ZOOM_START", "Invalid meeting number: " + meetingNo);
          return;
        }

        if(meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
          meetingService.returnToMeeting(reactContext.getCurrentActivity());
          promise.resolve("Already joined zoom meeting");
          return;
        }
      }

      StartMeetingOptions opts = new StartMeetingOptions();
      StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
      params.displayName = displayName;
      params.meetingNo = meetingNo;
      params.userId = userId;
      params.userType = userType;
      params.zoomAccessToken = zoomAccessToken;
      params.zoomToken = zoomToken;

      int startMeetingResult = meetingService.startMeetingWithParams(reactContext.getCurrentActivity(), params, opts);
      Log.i(TAG, "startMeeting, startMeetingResult=" + startMeetingResult);

      if (startMeetingResult != MeetingError.MEETING_ERROR_SUCCESS) {
        promise.reject("ERR_ZOOM_START", "startMeeting, errorCode=" + startMeetingResult);
      }
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  @ReactMethod
  public void joinMeeting(
    ReadableMap joinMeetingParams,
    ReadableMap joinMeetingOptions,
    Promise promise
  ) {
    try {
      meetingPromise = promise;

      ZoomSDK zoomSDK = ZoomSDK.getInstance();
      if(!zoomSDK.isInitialized()) {
        promise.reject("ERR_ZOOM_JOIN", "ZoomSDK has not been initialized successfully");
        return;
      }

      JoinMeetingOptions opts = new JoinMeetingOptions();
      // opts.custom_meeting_id = joinMeetingOptions.getString("custom_meeting_id");
      opts.invite_options = joinMeetingOptions.getInt("invite_options");
      opts.meeting_views_options = joinMeetingOptions.getInt("meeting_views_options");
      opts.no_bottom_toolbar = joinMeetingOptions.getBoolean("no_bottom_toolbar");
      opts.no_dial_in_via_phone = joinMeetingOptions.getBoolean("no_dial_in_via_phone");
      opts.no_dial_out_to_phone = joinMeetingOptions.getBoolean("no_dial_out_to_phone");
      opts.no_disconnect_audio = joinMeetingOptions.getBoolean("no_disconnect_audio");
      opts.no_driving_mode = joinMeetingOptions.getBoolean("no_driving_mode");
      opts.no_invite = joinMeetingOptions.getBoolean("no_invite");
      opts.no_meeting_end_message = joinMeetingOptions.getBoolean("no_meeting_end_message");
      opts.no_meeting_error_message = joinMeetingOptions.getBoolean("no_meeting_error_message");
      opts.no_share = joinMeetingOptions.getBoolean("no_share");
      opts.no_titlebar = joinMeetingOptions.getBoolean("no_titlebar");
      opts.no_unmute_confirm_dialog = joinMeetingOptions.getBoolean("no_unmute_confirm_dialog");
      opts.no_video = joinMeetingOptions.getBoolean("no_video");
      opts.no_webinar_register_dialog = joinMeetingOptions.getBoolean("no_webinar_register_dialog");
      // opts.participant_id = joinMeetingOptions.getString("participant_id");

      JoinMeetingParams params = new JoinMeetingParams();
      params.displayName = joinMeetingParams.getString("displayName");
      params.meetingNo = joinMeetingParams.getString("meetingNo");
      params.password = joinMeetingParams.getString("password");
      // params.vanityID = joinMeetingParams.getString("vanityID");

      final MeetingService meetingService = zoomSDK.getMeetingService();
      int joinMeetingResult = meetingService.joinMeetingWithParams(reactContext.getCurrentActivity(), params, opts);

      Log.i(TAG, "joinMeeting, joinMeetingResult=" + joinMeetingResult);

      if (joinMeetingResult != MeetingError.MEETING_ERROR_SUCCESS) {
        promise.reject("ERR_ZOOM_JOIN", "joinMeeting, errorCode=" + joinMeetingResult);
      }
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  @Override
  public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
    Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
    if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
      initializePromise.reject(
              "ERR_ZOOM_INITIALIZATION",
              "Error: " + errorCode + ", internalErrorCode=" + internalErrorCode
      );
    } else {
      registerListener();
      initializePromise.resolve("Initialize Zoom SDK successfully.");
    }
  }

  @Override
  public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
    Log.i(TAG, "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

    if (meetingPromise == null) {
      return;
    }

    if(meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
      meetingPromise.reject(
              "ERR_ZOOM_MEETING",
              "Error: " + errorCode + ", internalErrorCode=" + internalErrorCode
      );
      meetingPromise = null;
    } else if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
      meetingPromise.resolve("Connected to zoom meeting");
      meetingPromise = null;
    }
  }

  private void registerListener() {
    Log.i(TAG, "registerListener");
    ZoomSDK zoomSDK = ZoomSDK.getInstance();
    MeetingService meetingService = zoomSDK.getMeetingService();
    if(meetingService != null) {
      meetingService.addListener(this);
    }
  }

  private void unregisterListener() {
    Log.i(TAG, "unregisterListener");
    ZoomSDK zoomSDK = ZoomSDK.getInstance();
    if(zoomSDK.isInitialized()) {
      MeetingService meetingService = zoomSDK.getMeetingService();
      meetingService.removeListener(this);
    }
  }

  @Override
  public void onCatalystInstanceDestroy() {
    unregisterListener();
  }

  @Override
  public void onZoomAuthIdentityExpired() {
    unregisterListener();
  }

  // React LifeCycle
  @Override
  public void onHostDestroy() {
    unregisterListener();
  }
  @Override
  public void onHostPause() {}
  @Override
  public void onHostResume() {}
}
