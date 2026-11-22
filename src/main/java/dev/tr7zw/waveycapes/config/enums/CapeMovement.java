package dev.tr7zw.waveycapes.config.enums;

import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.Basic3DSimulation;
import dev.tr7zw.waveycapes.sim.DungeonsSimulation;

import java.util.function.Supplier;

public enum CapeMovement {
    VANILLA("Classic"),
    BASIC_SIMULATION("Simulation", BasicSimulation::new, BasicSimulation.class),
    BASIC_SIMULATION_3D("Simulation V2", Basic3DSimulation::new, Basic3DSimulation.class),
    DUNGEONS("Dungeons", DungeonsSimulation::new, DungeonsSimulation.class),;

    private final Supplier<Simulation> simulationCreator;
    public final Class<? extends Simulation> simulationClass;
    public final String displayName;

    CapeMovement(
            String displayName,
            Supplier<Simulation> simulation,
            Class<? extends Simulation> simulationClass
    ) {
        this.simulationCreator = simulation;
        this.simulationClass = simulationClass;
        this.displayName = displayName;
    }

    CapeMovement(
            String displayName
    ) {
        this(displayName, ()->null, null);
    }

    public Simulation createSimulation() {
        return this.simulationCreator.get();
    }
}
