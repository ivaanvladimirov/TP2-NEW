package simulator.model;

import simulator.misc.Vector2D;

/**
 * Interface representing information about an animal.
 */
public interface AnimalInfo extends JSONable {
    Animal.State get_state();

    Vector2D get_position();

    String get_genetic_code();

    Diet get_diet();

    double get_speed();

    double get_sight_range();

    double get_energy();

    double get_age();

    boolean is_pregnant();

}
