package action.ambulanceaction;

import agents.Ambulance;
import agents.SafeZone;
import core.Msg;

/**
 * Project: NewSimulator
 * Created by IntelliJ IDEA
 * Author: Sumin Park <smpark@se.kaist.ac.kr>
 * Github: https://github.com/sumin0407/NewSimulator.git
 */

public class AmbulanceFree extends AmbulanceAction {

    public AmbulanceFree(Ambulance target) {
        super(target);
        name = "Free";
    }

    boolean isFirstUpdate = true;

    int timeout = 10;               // timeout duration
    int frameCounter = timeout;

    int maxTimeout = 3;             // timeoutCounter, number of timeout
    int timeoutCounter = maxTimeout;


    @Override
    public void onUpdate() {
        if(isFirstUpdate) {
            isFirstUpdate = false;
            router.route(new Msg()
                    .setFrom(ambulance.name)
                    .setTo("Organization")
                    .setTitle("free state start")                           // announce to the organization "Free"
                    .setData(ambulance));
        }
        if(frameCounter <= 0) {
            frameCounter = timeout;
            if(timeoutCounter <= 0) {
                timeoutCounter = maxTimeout;
                ambulance.changeAction(new AmbulanceSearch(ambulance));     // Search at Safe Zone
            }
            timeoutCounter--;
        }
        frameCounter--;
    }

    @Override
    // When Ambulance receive the message
    public void recvMsg(Msg msg) {
        if(msg.title == "move to safezone") {
            SafeZone safeZone = (SafeZone)msg.data;
            ambulance.changeAction(new AmbulanceMoveToSafeZone(ambulance, safeZone));
        }
    }
}