package dev.tr7zw.waveycapes.config.enums;

import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.Basic3DSimulation;
import dev.tr7zw.waveycapes.sim.DungeonsSimulation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum CapeMovement {
    VANILLA(null, null),
    BASIC_SIMULATION(BasicSimulation::new, BasicSimulation.class),
    BASIC_SIMULATION_3D(Basic3DSimulation::new, Basic3DSimulation.class),
    DUNGEONS(DungeonsSimulation::new, DungeonsSimulation.class),;

    private final Supplier<Simulation> simulationCreator;
    public final Class<? extends Simulation> simulationClass;

    CapeMovement(@Nullable Supplier<Simulation> simulation, @Nullable Class<? extends Simulation> simulationClass) {
        this.simulationCreator = simulation;
        this.simulationClass = simulationClass;
    }

    public Simulation createSimulation() {
        if(this.simulationCreator == null) return null;
        return this.simulationCreator.get();
    }
}
