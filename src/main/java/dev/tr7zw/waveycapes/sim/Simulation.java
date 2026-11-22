package dev.tr7zw.waveycapes.sim;

import java.util.List;

import dev.tr7zw.waveycapes.util.cape.Point;

@SuppressWarnings("unused")
public interface Simulation {

    void simulate();

    void setGravityDirection(float x, float y, float z);

    float getGravity();

    void setGravity(float gravity);

    boolean isSneaking();

    void setSneaking(boolean sneaking);

    /**
     * @return true if it was re-initialized
     */
    boolean init(int partCount);

    boolean empty();

    void applyMovement(float x, float y, float z);

    List<Point> getPoints();

}