package simulator.model;

import java.util.List;
import java.util.function.Predicate;

public interface AnimalMapView extends MapInfo, FoodSupplier {

    List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter);

}
