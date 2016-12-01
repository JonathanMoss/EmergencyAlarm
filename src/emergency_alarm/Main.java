package emergency_alarm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        EmergencyAlarm alarm = new EmergencyAlarm();
        primaryStage.setScene(new Scene(alarm));
        primaryStage.setTitle ("Emergency Alarm");
        primaryStage.show();
        
        

        
        alarm.receiveIncomingEmergencyAlarm();
        
        new Thread(() -> {
        
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {}
               
            alarm.cancelIncomingEmergencyAlarm();
            
        }).start();
        

        
        
    }
}
