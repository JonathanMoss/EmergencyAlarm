package emergency_alarm;

/**
 * This Class provides an interface to an NX Panel Emergency Alarm Control.
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public interface NX_PanelEmergencyAlarm {

    /**
     * This public method is called to simulate that an incoming emergency alarm has been received from a remote Signaller.
     */
    public void receiveIncomingEmergencyAlarm();
    
    /**
     * This public method is called to simulate the emergency alarm that was previously received, has been cancelled by a remote Signaller.
     */
    public void cancelIncomingEmergencyAlarm();
    
    /**
     * This public method is called to simulate acknowledgement of a previously sent emergency alarm by a remote Signaller.
     */
    public void outgoingEmergencyAlarmAcknowledged();
     
}
