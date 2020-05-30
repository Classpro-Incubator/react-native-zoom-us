import { NativeModules, NativeEventEmitter } from 'react-native';

const { RNZoomUs } = NativeModules;
const RNZoomUsEventEmitter = new NativeEventEmitter(RNZoomUs);

const ZoomUs = {
  initialize: RNZoomUs.initialize,
  startMeeting: RNZoomUs.startMeeting,
  joinMeeting: RNZoomUs.joinMeeting,
  eventType: {MeetingStatus: "EventMeetingStatus"}, 
  addListener: (eventType, callback) => RNZoomUsEventEmitter.addListener(eventType, callback),
  removeListener: (eventType, callback) => RNZoomUsEventEmitter.removeListener(eventType, callback),
  removeAllListeners: () => RNZoomUsEventEmitter.removeAllListeners(),
  MeetingStatus: {CONNECTED: "CONNECTED", DISCONNECTED: "DISCONNECTED"},
};

export default ZoomUs;
