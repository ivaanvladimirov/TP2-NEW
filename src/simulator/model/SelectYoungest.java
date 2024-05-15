package simulator.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Interface for selection strategies
 */
public class SelectYoungest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
        if (as.isEmpty())
            return null;

        Animal youngest = as.get(0);
        for (Animal animal : as) {
            if (animal.get_age() < youngest.get_age()) {
                youngest = animal;
            }
        }
        return youngest;
    }
}
