package simulator.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Interface for selection strategies
 */
public interface SelectionStrategy {
    Animal select(Animal a, List<Animal> as);
}
