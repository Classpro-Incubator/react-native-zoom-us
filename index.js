import { NativeModules } from "react-native";

const InviteOptions = {
  INVITE_DISABLE_ALL: 0,
  INVITE_VIA_SMS: 1,
  INVITE_VIA_EMAIL: 2,
  INVITE_COPY_URL: 4,
  INVITE_ENABLE_ALL: 255,
};

const MeetingViewsOptions = {
  DEFAULT_VIEW: 0,
  NO_BUTTON_VIDEO: 1,
  NO_BUTTON_AUDIO: 2,
  NO_BUTTON_SHARE: 4,
  NO_BUTTON_PARTICIPANTS: 8,
  NO_BUTTON_MORE: 16,
  NO_TEXT_MEETING_ID: 32,
  NO_TEXT_PASSWORD: 64,
  NO_BUTTON_LEAVE: 128,
  NO_BUTTON_SWITCH_CAMERA: 256,
  NO_BUTTON_SWITCH_AUDIO_SOURCE: 512,
};

const DEFAULT_INIT_PARAMS = {
  appKey: "",
  appSecret: "",
  domain: "zoom.us",
};

const DEFAULT_JOIN_MEETING_PARAMS = {
  displayName: "",
  meetingNo: "",
  password: "",
  // vanityID: ""
};

const DEFAULT_JOIN_MEETING_OPTIONS = {
  // custom_meeting_id: "",
  invite_options: InviteOptions.INVITE_DISABLE_ALL,
  meeting_views_options: MeetingViewsOptions.DEFAULT_VIEW,
  no_bottom_toolbar: false,
  no_dial_in_via_phone: false,
  no_dial_out_to_phone: false,
  no_disconnect_audio: false,
  no_driving_mode: false,
  no_invite: false,
  no_meeting_end_message: false,
  no_meeting_error_message: false,
  no_share: false,
  no_titlebar: false,
  no_unmute_confirm_dialog: false,
  no_video: false,
  no_webinar_register_dialog: false,
  // participant_id: 0,
};

const initialize = (initParams) =>
  NativeModules.ZoomSDK.initialize({ ...DEFAULT_INIT_PARAMS, ...initParams });

const joinMeeting = (joinMeetingParams, joinMeetingOptions) =>
  NativeModules.RNZoomUs.joinMeeting(
    { ...DEFAULT_JOIN_MEETING_PARAMS, ...joinMeetingParams },
    { ...DEFAULT_JOIN_MEETING_OPTIONS, ...joinMeetingOptions }
  );

export default {
  InviteOptions,
  MeetingViewsOptions,
  initialize,
  joinMeeting,
  startMeeting: NativeModules.RNZoomUs.startMeeting,
};
