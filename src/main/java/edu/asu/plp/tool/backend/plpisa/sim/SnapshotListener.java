package edu.asu.plp.tool.backend.plpisa.sim;

import edu.asu.plp.tool.backend.isa.events.SimulatorControlEvent;

/**
 * This Interface defined a Snapshot receiving event
 */
public interface SnapshotListener {
    /**
     *
     * @param message
     */
    void receiveSnapshot(SimulatorControlEvent message);
}