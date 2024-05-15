package simulator.model;

import org.json.JSONObject;
import simulator.factories.Factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulator implements Observable<EcoSysObserver>, JSONable {

    private final Factory<Animal> animalsFactory;
    private final Factory<Region> regionsFactory;
    private RegionManager regionManager;
    private List<Animal> animals;
    private double currentTime;
    private List<EcoSysObserver> observers;

    /**
     * Constructor for the Simulator class
     *
     * @param width          The width of the map
     * @param height         The height of the map
     * @param cols           The number of columns in the map
     * @param rows           The number of rows in the map
     * @param animalsFactory The factory for creating animals
     * @param regionsFactory The factory for creating regions
     */
    public Simulator(int width, int height, int cols, int rows, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
        this.animalsFactory = animalsFactory;
        this.regionsFactory = regionsFactory;
        this.regionManager = new RegionManager(cols, rows, width, height);
        this.animals = new ArrayList<>();
        this.currentTime = 0.0;
        this.observers = new ArrayList<>();
    }

    /**
     * Sets the region at the specified row and column to the given region.
     *
     * @param row The row index of the region.
     * @param col The column index of the region.
     * @param r   The region to be set.
     */
    private void set_region(int row, int col, Region r) {
        regionManager.set_region(row, col, r);
        for (EcoSysObserver observer : observers) {
            observer.onRegionSet(row, col, regionManager, r);
        }
    }

    /**
     * Sets the region at the specified row and column to the given region.
     *
     * @param row    The row index of the region.
     * @param col    The column index of the region.
     * @param r_json The JSON object representing the region to be set.
     */
    public void set_region(int row, int col, JSONObject r_json) {
        if (r_json != null) {
            Region region = regionsFactory.createInstance(r_json);
            set_region(row, col, region);
        }

    }

    /**
     * Adds the provided animal to the simulator.
     *
     * @param a The JSON object representing the animal to be added.
     */
    private void add_animal(Animal a) {
        animals.add(a);
        regionManager.register_animal(a);
        for (EcoSysObserver observer : observers) {
            observer.onAnimalAdded(currentTime, regionManager, new ArrayList<>(animals), a);
        }
    }

    public void add_animal(JSONObject a_json) {
        Animal animal = animalsFactory.createInstance(a_json);
        add_animal(animal);
    }

    /**
     * Returns the information about the map (regions) managed by the simulator.
     *
     * @return The region manager containing map information.
     */
    public MapInfo get_map_info() {
        return regionManager;
    }

    /**
     * Retrieves an unmodifiable list of animals currently in the simulator.
     *
     * @return An unmodifiable list of animal information.
     */

    public List<Animal> getAnimals() {
        return animals;
    }
    public List<? extends AnimalInfo> get_animals() {
        return Collections.unmodifiableList(animals);
    }

    public double get_time() {
        return currentTime;
    }

    /**
     * Advances the simulation by the specified time increment.
     * Updates the time, animals' states, and their regions accordingly.
     *
     * @param dt The time increment for the simulation advancement.
     */

    public void advance(double dt) {
        currentTime += dt;
        for (int i = 0; i < animals.size(); i++) {
            Animal animal = animals.get(i);
            if (animal.get_state() == Animal.State.DEAD) {
                animals.remove(animal);
                regionManager.unregister_animal(animal);
            } else {
                animal.update(dt);
                regionManager.update_animal_region(animal);
                regionManager.update_all_regions(dt);

                if (animal.is_pregnant()) {
                    Animal baby = animal.deliver_baby();
                    add_animal(baby);
                }
            }
        }

        for (EcoSysObserver observer : observers) {
            observer.onAdvanced(currentTime, regionManager, new ArrayList<>(animals), dt);
        }
    }

    @Override
    public JSONObject as_JSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", currentTime);
        jsonObject.put("state", regionManager.as_JSON());
        return jsonObject;
    }

    public void reset(int cols, int rows, int width, int height) {
        regionManager = new RegionManager(cols, rows, width, height);
        animals.clear();
        currentTime = 0.0;

        for (EcoSysObserver observer : observers) {
            observer.onReset(currentTime, regionManager, new ArrayList<>(animals));
        }
    }

    @Override
    public void addObserver(EcoSysObserver o) {
        if (!observers.contains(o)) {
            observers.add(o);
            o.onRegister(currentTime, regionManager, new ArrayList<>(animals));
        }
    }

    @Override
    public void removeObserver(EcoSysObserver o) {
        observers.remove(o);
    }
}

