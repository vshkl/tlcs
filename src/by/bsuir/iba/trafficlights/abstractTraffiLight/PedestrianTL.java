package by.bsuir.iba.trafficlights.abstractTraffiLight;

import by.bsuir.iba.enumerations.TrafficLightColor;
import by.bsuir.iba.enumerations.TrafficLightType;

/**
 * class {@code PedestrianTL} implements AbstractTL
 * Have private fields {@code color} and {@code type}
 * to specify traffic light's type and current color
 *
 * @author Pavel Vashkel
 * @see by.bsuir.iba.trafficlights.abstractTraffiLight.AbstractTL
 */

public class PedestrianTL implements AbstractTL {
    private TrafficLightColor color;
    private TrafficLightType type = TrafficLightType.PEDESTRIAN;

    public PedestrianTL() {
    }

    @Override
    public TrafficLightColor getLight() {
        return color;
    }

    @Override
    public void setLight(TrafficLightColor tlc) {
        tlc = color;
    }

    @Override
    public String test() {
        return "I'm a " + this.getClass().toString();
    }
}