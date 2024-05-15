package simulator.model;

import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.List;
import java.util.function.Predicate;

public abstract class Animal implements Entity, AnimalInfo, Constants {
    protected String _genetic_code;
    protected Diet _diet;
    protected State _state;
    protected Vector2D _pos;
    protected Vector2D _dest;
    protected double _energy;
    protected double _speed;
    protected double _age;
    protected double _desire;
    protected double _sight_range;
    protected Animal _mate_target;
    protected Animal _baby;
    protected AnimalMapView _region_mngr;
    protected SelectionStrategy _mate_strategy;

    public enum State {
        NORMAL, MATE, HUNGER, DANGER, DEAD
    }
    /**
     * Initializes the attributes of the animal with the specified genetic code, diet, sight range, initial speed, mate strategy, and position.
     *
     * @param genetic_code  The genetic code of the animal
     * @param diet          The diet of the animal
     * @param sight_range   The sight range of the animal
     * @param init_speed    The initial speed of the animal
     * @param mate_strategy The mate strategy of the animal
     * @param pos           The position of the animal
     * @throws IllegalArgumentException If the genetic code is null or empty, sight range is not positive, initial speed is not positive, or mate strategy is null
     */
    protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {
        // Check for null or empty genetic_code, sight_range and init_speed are positive, and mate_strategy is not null
        if (genetic_code == null || genetic_code.isEmpty() || sight_range <= 0 || init_speed <= 0 || mate_strategy == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        // Assign values to attributes
        _genetic_code = genetic_code;
        _diet = diet;
        _state = State.NORMAL;
        _pos = pos;
        _dest = null;
        _energy = _maxenergy;
        _speed = Utils.get_randomized_parameter(init_speed, _toleranceSpeed);
        _age = 0;
        _desire = _lowestdesire;
        _sight_range = sight_range;
        _mate_target = null;
        _baby = null;
        _region_mngr = null;
        _mate_strategy = mate_strategy;
    }
    /**
     * Initializes the attributes of the animal with the genetic code, diet, sight range, speed, and mate strategy of the parents.
     *
     * @param p1 The first parent animal
     * @param p2 The second parent animal
     */
    protected Animal(Animal p1, Animal p2) {
        _genetic_code = p1.get_genetic_code();
        _diet = p1.get_diet();
        _state = State.NORMAL;
        _pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(_multiplicativeFactor * (Utils._rand.nextGaussian() + 1)));
        _dest = null;
        _energy = (p1.get_energy() + p2.get_energy()) / 2.0;
        _speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, _tolerance);
        _age = 0;
        _desire = _lowestdesire;
        _sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, _tolerance);
        _mate_target = null;
        _baby = null;
        _region_mngr = null;
        _mate_strategy = p2.get_mate_strategy();
    }
    /**
     * Initializes the animal with the specified animal map view.
     *
     * @param reg_mngr The animal map view to be associated with the animal
     */
    void init(AnimalMapView reg_mngr) {
        _region_mngr = reg_mngr;
        if (this._pos == null) {
            _pos = Vector2D.get_random_vector(0, _region_mngr.get_width() - 1, 0, _region_mngr.get_height() - 1);
        } else {
            if (IsOutOfMap()) {
                _pos = adjust_position(_pos);
            }
        }
        this._dest = Vector2D.get_random_vector(0, _region_mngr.get_width() - 1, 0, _region_mngr.get_height() - 1);
    }
    /**
     * Adjusts the position of the animal to ensure it remains within the bounds of the map.
     *
     * @param pos The current position of the animal
     * @return The adjusted position of the animal
     */
    public Vector2D adjust_position(Vector2D pos) {
        double cols = pos.getX();
        double rows = pos.getY();

        double width = _region_mngr.get_width();
        double height = _region_mngr.get_height();

        while (cols >= width)
            cols = (cols - width);

        while (cols < 0)
            cols = (cols + width);

        while (rows >= height)
            rows = (rows - height);

        while (rows < 0)
            rows = (rows + height);

        return new Vector2D(cols, rows);
    }
    /**
     * Checks if the animal is out of the map bounds.
     *
     * @return True if the animal is out of the map bounds, false otherwise
     */
    protected boolean IsOutOfMap() {
        return this._pos.getX() < 0 || this._pos.getX() > _region_mngr.get_width() || this._pos.getY() < 0 || this._pos.getY() > _region_mngr.get_height();
    }
    /**
     * Delivers the baby if the animal is pregnant.
     *
     * @return The baby animal if the animal is pregnant, null otherwise
     */
    public Animal deliver_baby() {
        if (this.is_pregnant()) {
            Animal baby = _baby;
            _baby = null;
            return baby;
        }
        return null;
    }
    /**
     * Moves the animal with the specified speed.
     *
     * @param speed The speed with which the animal moves
     */
    protected void move(double speed) {
        _pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
    }
    /**
     * Converts the animal's attributes to a JSONObject.
     *
     * @return A JSONObject representing the animal's attributes
     */
    public JSONObject as_JSON() {
        JSONObject json = new JSONObject();
        Vector2D pos = this.get_position();

        json.put("pos", pos.toString());
        json.put("gcode", get_genetic_code());
        json.put("diet", get_diet().toString());
        json.put("state", get_diet().toString());

        return json;
    }
    /**
     * Updates the animal's state based on the specified time interval.
     *
     * @param dt The time interval for the update
     */
    public void update(double dt) {
    }

    @Override
    public State get_state() {
        return this._state;
    }

    @Override
    public Vector2D get_position() {
        return this._pos;
    }

    @Override
    public String get_genetic_code() {
        return this._genetic_code;
    }

    @Override
    public Diet get_diet() {
        return this._diet;
    }

    @Override
    public double get_speed() {
        return this._speed;
    }

    @Override
    public double get_sight_range() {
        return this._sight_range;
    }

    @Override
    public double get_energy() {
        return this._energy;
    }

    @Override
    public double get_age() {
        return this._age;
    }

    @Override
    public boolean is_pregnant() {
        return this._baby != null;
    }

    public SelectionStrategy get_mate_strategy() {
        return this._mate_strategy;
    }
    /**
     * Searches for a mate animal within the specified animal map view using the given selection strategy.
     *
     * @param reg_mngr The animal map view to search within
     * @param strategy The selection strategy to use
     * @return The selected mate animal, or null if no mate is found
     */
    public Animal searchForMate(AnimalMapView reg_mngr, SelectionStrategy strategy) {
        Predicate<Animal> filter = a -> a.get_genetic_code().equals(this._genetic_code) && !a.is_pregnant() && a.get_state() == State.MATE && a != this;

        List<Animal> animalsInRange = reg_mngr.get_animals_in_range(this, filter);

        return strategy.select(this, animalsInRange);
    }
    /**
     * Searches for a dangerous animal within the specified animal map view using the given selection strategy.
     *
     * @param reg_mngr The animal map view to search within
     * @param strategy The selection strategy to use
     * @return The selected dangerous animal, or null if none is found
     */
    public Animal searchForDanger(AnimalMapView reg_mngr, SelectionStrategy strategy) {

        Predicate<Animal> filter = a -> a.get_diet() == Diet.CARNIVORE;

        List<Animal> animalsInRange = reg_mngr.get_animals_in_range(this, filter);


        return strategy.select(this, animalsInRange);
    }
    /**
     * Searches for a hunt target animal within the specified animal map view using the given selection strategy.
     *
     * @param reg_mngr The animal map view to search within
     * @param strategy The selection strategy to use
     * @return The selected hunt target animal, or null if none is found
     */
    public Animal searchForHuntTarget(AnimalMapView reg_mngr, SelectionStrategy strategy) {
        Predicate<Animal> filter = a -> a.get_diet() == Diet.HERBIVORE;

        List<Animal> animalsInRange = reg_mngr.get_animals_in_range(this, filter);


        return strategy.select(this, animalsInRange);
    }
    /**
     * Checks and adjusts the energy level of the animal if it exceeds the maximum or falls below the lowest energy level.
     */
    protected void checkEnergy() {
        if (_energy > _maxenergy) {
            _energy = _maxenergy;
        } else if (_energy < _lowestenergy) {
            _energy = _lowestenergy;
        }
    }
    /**
     * Checks and adjusts the desire level of the animal if it exceeds the maximum or falls below the lowest desire level.
     */
    protected void checkDesire() {
        if (_desire > _maxdesire) {
            _desire = _maxdesire;
        } else if (_desire < _lowestdesire) {
            _desire = _lowestdesire;
        }
    }
    protected void setState(State state) {
        this._state = state;
    }
    /**
     * Sets the desire level of the animal to the specified value.
     *
     * @param d The desire level to set
     */
    protected void setDesire(double d) {
        this._desire = d;
    }
}
