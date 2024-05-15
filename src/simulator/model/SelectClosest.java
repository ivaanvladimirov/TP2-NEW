package simulator.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Selects the closest animal to the current animal
 */
public class SelectClosest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
        if (!as.isEmpty()) {
            Animal closest = as.get(0);
            for (Animal animal : as) {
                if (a.get_position().distanceTo(animal.get_position()) < a.get_position().distanceTo(closest.get_position())) {
                    closest = animal;
                }
            }
            return closest;
        }
        return null;
    }
}
