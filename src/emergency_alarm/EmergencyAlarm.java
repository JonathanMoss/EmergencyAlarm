package emergency_alarm;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * This Class provides the functionality of an NX Panel Emergency Alarm Control.
 * 
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public class EmergencyAlarm extends AnchorPane implements NX_PanelEmergencyAlarm {
    
    @FXML private AnchorPane pane;
    @FXML private Label alarmsTo;
    @FXML private Label alarmsFrom;
    @FXML private Label topButtonLabel;
    @FXML private Label bottomButtonLabel;
    @FXML private Label topLightLabel;
    @FXML private Label bottomLightLabel;
    @FXML private Circle bottomButtonClickTarget;
    @FXML private Circle topButtonClickTarget;
    @FXML private volatile ArrayList <Rectangle> topLightBox;
    @FXML private volatile ArrayList <Rectangle> bottomLightBox;
    
    private volatile ObjectProperty <SendAlarmState> sendAlarmState = new SimpleObjectProperty<>(SendAlarmState.CANCELLED);
    private synchronized SendAlarmState getSendAlarmState () {return this.sendAlarmState.get();}
    private synchronized void setSendAlarmState (SendAlarmState sendAlarmState) {this.sendAlarmState.set (sendAlarmState);}
    
    private volatile ObjectProperty <ReceiveAlarmState> receiveAlarmState = new SimpleObjectProperty<>(ReceiveAlarmState.CANCELLED);
    private synchronized ReceiveAlarmState getReceiveAlarmState () {return this.receiveAlarmState.get();}
    private synchronized void setReceiveAlarmState (ReceiveAlarmState receiveAlarmState) {this.receiveAlarmState.set (receiveAlarmState);}
    
    private BooleanProperty topToGive = new SimpleBooleanProperty(true);
    public Boolean getTopToGive () {return this.topToGive.get();}
    public synchronized void setTopToGive (Boolean topToGive) {this.topToGive.set(topToGive);}
    
    private StringProperty alarmsTwixt = new SimpleStringProperty("STOKE-ON-TRENT SCC");
    public String getAlarmsTwixt () {return this.alarmsTwixt.get();}
    public void setAlarmsTwixt (String alarmsTwixt) {this.alarmsTwixt.set(alarmsTwixt);}
    
    private IntegerProperty redValue = new SimpleIntegerProperty(108);
    public int getRedValue () {return redValue.get();}
    public void setRedValue (int rValue) {redValue.set(rValue);}
    
    private IntegerProperty greenValue = new SimpleIntegerProperty(202);
    public int getGreenValue () {return greenValue.get();}
    public void setGreenValue (int rValue) {greenValue.set(rValue);}
    
    private IntegerProperty blueValue = new SimpleIntegerProperty(126);
    public int getBlueValue () {return blueValue.get();}
    public void setBlueValue (int rValue) {blueValue.set(rValue);}
    
    private FXMLLoader fxmlLoader;
    private volatile Boolean sendFlashing = false;
    private volatile Boolean receiveFlashing = false;
    
    private final static String ALARM_CLIP = "../resources/alarm.wav";
    private final URL audioClipFile = getClass().getResource(ALARM_CLIP);
    private final AudioClip alarmAudio = new AudioClip(audioClipFile.toString());
    
    private DoubleProperty audioClipVolume = new SimpleDoubleProperty (0.5);
    public double getAudioClipVolume() {return this.audioClipVolume.get();}
    public void setAudioClipVolume(double volume) {this.audioClipVolume.set(volume);}
    
    /**
     * This method sets the background colour of the Emergency Alarm control.
     * 
     * This method should be called each time either the redValue, greenValue or blueValue values are changed.
     */
    private void setbackgroundColour () {
        
        this.pane.setStyle (String.format ("-fx-background-color: rgb(%d, %d, %d)", 
            this.getRedValue(), this.getGreenValue(), this.getBlueValue()));
        
    }
    
    /**
     * This is the Constructor Method for an Emergency Alarm representation on an NX Panel.
     */
    public EmergencyAlarm () {
        
        // Get a reference to the FXML file.
        this.fxmlLoader = new FXMLLoader(getClass().getResource("EmergencyAlarm.fxml"));
        
        // Set the root and Controller Objects
        this.setRoot();
        this.setController();
        
        // Attempt to load the FXML file.
        try {
            fxmlLoader.load();
        } catch (IOException e) {}
        
        this.setbackgroundColour();
        
        this.redValue.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                setbackgroundColour();
                
            }
        });
        
        this.alarmsFrom.textProperty().bind(this.alarmsTwixt);
        this.alarmsTo.textProperty().bind(this.alarmsTwixt);
        this.alarmAudio.volumeProperty().bindBidirectional(this.audioClipVolume);
        
        this.topToGive.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            
                if ((Boolean)newValue) {
                    
                    topButtonLabel.setText("GIVE ALARM");
                    bottomButtonLabel.setText("ACKNOWLEDGE ALARM");
                    topLightLabel.setText("ALARM SENT TO");
                    bottomLightLabel.setText("ALARM RECEIVED FROM");
                    
                } else {
                    
                    topButtonLabel.setText("ACKNOWLEDGE ALARM");
                    bottomButtonLabel.setText("GIVE ALARM");
                    topLightLabel.setText("ALARM RECEIVED FROM");
                    bottomLightLabel.setText("ALARM SENT TO");
                    
                }

            }

        });
        
        this.greenValue.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                setbackgroundColour();
                
            }
        });
        
        this.blueValue.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                setbackgroundColour();
                
            }
        });
        
        // This code block defines the click event for the top button click target.
        this.topButtonClickTarget.setOnMouseClicked(e -> {
        
            if (this.getTopToGive()) { // The top button is the Send Button
                
                if (e.getButton() == MouseButton.PRIMARY) { // Pushed the Send Button
                
                    buttonOperation (ButtonOperation.PUSH, ButtonType.SEND);
                    
                } else if (e.getButton () == MouseButton.SECONDARY) { // Pulled the Send Button (Cancelling action)
                
                    buttonOperation (ButtonOperation.PULL, ButtonType.SEND);
                    
                }
                
            } else { // The top button is the Receive Button
                
                if (e.getButton() == MouseButton.PRIMARY) { //Pushed the Acknowledge Button
                
                    buttonOperation (ButtonOperation.PUSH, ButtonType.RECEIVE);
                     
                }
                
            }
            
        });
        
        // This code block defines the click event for the bottom button click target.
        this.bottomButtonClickTarget.setOnMouseClicked(e -> {
        
            if (this.getTopToGive()) { // The top button is the Send Button
            
                if (e.getButton() == MouseButton.PRIMARY) { //Pushed the Acknowledge Button
                
                    buttonOperation (ButtonOperation.PUSH, ButtonType.RECEIVE);
                     
                }

            } else { // The top button is the Receive Button

                if (e.getButton() == MouseButton.PRIMARY) { // Pushed the Send Button
                
                    buttonOperation (ButtonOperation.PUSH, ButtonType.SEND);
                    
                } else if (e.getButton () == MouseButton.SECONDARY) { // Pulled the Send Button (Cancelling action)
                
                    buttonOperation (ButtonOperation.PULL, ButtonType.SEND);
                    
                }
                
            }

        });
        
        // This code block adds a listener to the 'Received Alarm State'.
        this.receiveAlarmState.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            
                switch ((ReceiveAlarmState) newValue) {
                
                    case CANCELLED: //No Illumination, no Audiable Alarm.
                        
                        receiveFlashing = false;
                        setAudibleAlarm (false);
                        
                        try {
                            
                            Thread.sleep(333);
                            
                        } catch (InterruptedException ex) {}
         
                        if (getTopToGive()) {
                            
                            bottomLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);}); 
                            
                        } else {
                            
                           topLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);}); 
                            
                        }
                        
                        break;
                        
                    case RECEIVED: // Flashing Red Lights and Audiable Alarm.
                        
                        receiveFlashing = true;
                        setAudibleAlarm (true);
                        
                        new Thread (() -> {

                            while (receiveFlashing) {

                                try {

                                    Thread.sleep (333);
                                    
                                    if (getTopToGive()) {
                                        
                                        if (bottomLightBox.get(0).getFill().equals(Color.RED)) {
                                        
                                            bottomLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);});
                                            
                                        } else {
                                        
                                            bottomLightBox.forEach((value) -> { value.setFill (Color.RED);});
                                            
                                        }

                                    } else {
                                        
                                        if (topLightBox.get(0).getFill().equals(Color.RED)) {
                                        
                                            topLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);});
                                            
                                        } else {
                                        
                                            topLightBox.forEach((value) -> { value.setFill (Color.RED);});
                                            
                                        }

                                    }

                                } catch (InterruptedException ex) {} 
                            }

                        }).start();
                        
                        break;
                        
                    case RECEIVED_ACKNOWLEDGED: // Steady Red Lights, no Audiable Alarm.
                        
                        receiveFlashing = false;
                        setAudibleAlarm (false);
                        
                        try {

                            Thread.sleep (333);

                        } catch (InterruptedException ex) {}
                        
                        if (getTopToGive()) {
                            
                            bottomLightBox.forEach((value) -> { value.setFill (Color.RED);});

                        } else {
                            
                            topLightBox.forEach((value) -> { value.setFill (Color.RED);}); 
                            
                        }
                        
                        break;
                }
            }
        });
        
        this.sendAlarmState.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                switch ((SendAlarmState) newValue) {
                    
                    case CANCELLED: // No Illumination.

                        sendFlashing = false;
                        
                        try {
                            
                            Thread.sleep(333);
                            
                        } catch (InterruptedException ex) {}
                        
                        if (getTopToGive()) {
                            
                            topLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);});
                            
                        } else {
                            
                            bottomLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);}); 
                            
                        }
                        
                        break;
                        
                    case SENT: // Flashing Red Lights.
                       
                        sendFlashing = true;

                        new Thread (() -> {

                            while (sendFlashing) {

                                try {

                                    Thread.sleep (333);
                                    
                                    if (getTopToGive()) {
                                        
                                        if (topLightBox.get(0).getFill().equals(Color.RED)) {
                                        
                                            topLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);});
                                            
                                        } else {
                                        
                                            topLightBox.forEach((value) -> { value.setFill (Color.RED);});
                                            
                                        }
                                        
                                    } else {
                                        
                                        if (bottomLightBox.get(0).getFill().equals(Color.RED)) {
                                        
                                            bottomLightBox.forEach((value) -> { value.setFill (Color.SLATEGREY);});
                                            
                                        } else {
                                        
                                            bottomLightBox.forEach((value) -> { value.setFill (Color.RED);});
                                            
                                        }
                                        
                                    }

                                } catch (InterruptedException ex) {} 

                            }

                        }).start();

                        break;
                        
                    case SENT_ACKNOWLEDGED: // Steady Red Lights.

                        sendFlashing = false;
                        
                        try {
                            
                            Thread.sleep(333);
                            
                        } catch (InterruptedException ex) {}
                        
                        if (getTopToGive()) {
                            
                            topLightBox.forEach((value) -> { value.setFill (Color.RED);});

                        } else {
                            
                            bottomLightBox.forEach((value) -> { value.setFill (Color.RED);}); 
                            
                        }
                        
                        break;
                } 
               
            }
            
        });

    }
         
    /**
     * This method sets the FXML Controller object.
     */
    private void setController() {this.fxmlLoader.setController(this);}
    
    /**
     * This method sets the root object.
     */
    private void setRoot() {this.fxmlLoader.setRoot(this);}
    
    /**
     * This method processes user actions - presses and pulls of the Emergency Alarm Control Buttons.
     * 
     * @param userCommand <code>ButtonOperation</code> Indicates if the button has received a <i>'PUSH'</i>, or a <i>'PULL'</i>.
     * @param sendOrReceive <code>ButtonType</code> Indicates if the button belongs to the <i>'SEND'</i> Control, or the <i>'RECEIVE'</i> Control.
     */
    private void buttonOperation (ButtonOperation userCommand, ButtonType sendOrReceive) {
        
        switch (sendOrReceive) {
            
            case SEND:
                
                switch (userCommand) {
                    
                    case PUSH:
                        
                        if (this.getSendAlarmState().equals(SendAlarmState.CANCELLED)) {
                        
                            this.setSendAlarmState(SendAlarmState.SENT);
                        
                        }
                        
                        break;
                        
                    case PULL:
                        
                        if (this.getSendAlarmState() != SendAlarmState.CANCELLED) { // State != Cancelled
                    
                            this.setSendAlarmState(SendAlarmState.CANCELLED); // Cancel the Emergency Alarm.
                        
                        }
                        
                        break;
                }
                
                break;
                
            case RECEIVE:
                
                switch (userCommand) {
                    
                    case PUSH:
                        
                        if (this.getReceiveAlarmState().equals(ReceiveAlarmState.RECEIVED)) {
                            
                            this.setReceiveAlarmState(ReceiveAlarmState.RECEIVED_ACKNOWLEDGED);
                            
                        }
                        
                        break;
                }
                
                break;
        }
    }
    
    /**
     * This method sets whether or not an Alarm is sounding.
     * @param soundAlarm <code>Boolean</code> Value indicating if the alarm is sounding <i>'true'</i>, otherwise <i>'false'</i>.
     */
    private synchronized void setAudibleAlarm(Boolean soundAlarm) {
        
        this.alarmAudio.setCycleCount(INDEFINITE);
        this.alarmAudio.setVolume(0.5);
        
        if (soundAlarm) {
            
            this.alarmAudio.play();
            
        } else {
            
            this.alarmAudio.stop();
            
        }
        
    }

    @Override
    public synchronized void receiveIncomingEmergencyAlarm() {
        
        if (this.getReceiveAlarmState().equals(ReceiveAlarmState.CANCELLED)) {
            
            this.setReceiveAlarmState(ReceiveAlarmState.RECEIVED);
            
        }
        
    }

    @Override
    public synchronized void cancelIncomingEmergencyAlarm() {
        
        if (this.getReceiveAlarmState() != ReceiveAlarmState.CANCELLED) {
            
            this.setReceiveAlarmState(ReceiveAlarmState.CANCELLED);
            
        }
        
    }

    @Override
    public synchronized void outgoingEmergencyAlarmAcknowledged() {
        
        if (this.getSendAlarmState().equals(SendAlarmState.SENT)) {
            
            this.setSendAlarmState(SendAlarmState.SENT_ACKNOWLEDGED);
            
        }
        
    }
}
