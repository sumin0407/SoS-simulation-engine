package action.firefighteraction;

import agents.Bridgehead;
import agents.FireFighter;
import agents.Patient;
import misc.ElasticHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

/**
 * Project: NewSimulator
 * Created by IntelliJ IDEA
 * Author: Sumin Park <smpark@se.kaist.ac.kr>
 * Github: https://github.com/sumin0407/NewSimulator.git
 */

public class FireFighterTransferToBridgehead extends FireFighterAction {

    Bridgehead bridgehead;
    public Patient targetPatient;

    int prevMoveDelay;
    private static Logger LOGGER = LoggerFactory.getLogger(FireFighterTransferToBridgehead.class);
    private ElasticHelper elasticHelperInstance = ElasticHelper.getElasticHelperInstance();

    public FireFighterTransferToBridgehead(FireFighter target, Bridgehead bridgehead, Patient targetPatient) {
        super(target);

        name = "TransferToBridgehead";

        this.bridgehead = bridgehead;
        this.targetPatient = targetPatient;

        fireFighter.transferImage.visible(true);
        fireFighter.firstAid.visible(false);
        fireFighter.defaultImage.visible(false);

        prevMoveDelay = fireFighter.moveDelay;
        fireFighter.moveDelay = prevMoveDelay * 3;          // Reduce the speed while transferring the patient
    }

    @Override
    public void onUpdate() {
        fireFighter.observe();
        fireFighter.moveTo(bridgehead.position);                                // Transfer the patient to the Bridgehead
        fireFighter.markVisitedTiles();
        if(fireFighter.isArrivedAt(bridgehead.position)) {                      // When the Firefighter arrived at the Bridgehead
            bridgehead.arrivedPatient(targetPatient);
            LinkedHashMap<String, String> logArgs = new LinkedHashMap<>();
            logArgs.put("action", "firefighter.transfertobridgehead");
            logArgs.put("firefighter", fireFighter.name);
            logArgs.put("patient", targetPatient.name);
            logArgs.put("bridehead", bridgehead.name);
            elasticHelperInstance.indexLogs(this.getClass(), logArgs);
            LOGGER.info(fireFighter.name + " successfully transferred " + targetPatient.name + " to " + bridgehead.name);
            fireFighter.moveDelay = prevMoveDelay;
            fireFighter.changeAction(new FireFighterSearch(fireFighter));       // Change the Firefighter's action to "Search"
            fireFighter.transferImage.visible(false);
            fireFighter.defaultImage.visible(true);
            world.rescuedPatientCount++;
        }
    }
}