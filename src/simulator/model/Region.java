package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Region implements Entity, FoodSupplier, RegionInfo, Constants {
    protected List<Animal> animals;
    private int countHerbivorous;

    /**
     * Constructs a region with an empty list of animals.
     */
    public Region() {
        animals = new ArrayList<>();
    }

    @Override
    public void update(double dt) {   }

    @Override
    public double get_food(Animal a, double dt) {
        return 0;
    }

    /**
     * Adds an animal to the region.
     *
     * @param a The animal to add
     */
    public final void add_animal(Animal a) {
        animals.add(a);
        if(a.get_diet() == Diet.HERBIVORE)
            countHerbivorous++;
    }

    /**
     * Removes an animal from the region.
     *
     * @param a The animal to remove
     */
    public void remove_animal(Animal a) {
        animals.remove(a);
        if(a.get_diet() == Diet.HERBIVORE)
            countHerbivorous--;
    }

    /**
     * Gets the list of animals in the region.
     *
     * @return The list of animals
     */
    public final List<Animal> getAnimals() {
        return animals;
    }

    /**
     * Converts the region information to a JSON object.
     *
     * @return The JSON object representing the region
     */
    public JSONObject as_JSON() {
        JSONObject ObjectAnimal = new JSONObject();
        JSONArray ArrayAnimals = new JSONArray();
        for (int i = 0; i < animals.size(); i++) {
            ArrayAnimals.put(animals.get(i).as_JSON());
        }
        ObjectAnimal.put("animals", ArrayAnimals);
        return ObjectAnimal;
    }

    /**
     * Gets the number of herbivorous animals in the region.
     *
     * @return The number of herbivorous animals
     */
    public int getHerbivorousSize() {
        return countHerbivorous;
    }

    public List<AnimalInfo> getAnimalsInfo() {
        return Collections.unmodifiableList(animals);
    }
}