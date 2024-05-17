package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {

    private Animal _danger_source;
    private final SelectionStrategy _danger_strategy;

    /**
     * Constructor for the Sheep class
     *
     * @param mate_strategy
     * @param danger_strategy
     * @param pos
     */
    public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
        super("Sheep", Diet.HERBIVORE, _sightrangeConst, _speedConst, mate_strategy, pos);
        this._mate_strategy = mate_strategy;
        this._danger_strategy = danger_strategy;
        this._danger_source = null;
    }

    /**
     * Constructor for the Sheep class
     *
     * @param p1
     * @param p2
     */
    protected Sheep(Sheep p1, Animal p2) {
        super(p1, p2);
        this._danger_strategy = p1.get_danger_strategy();
        this._danger_source = null;
    }

    public SelectionStrategy get_danger_strategy() {
        return this._danger_strategy;
    }

    /**
     * Updates the state of the Sheep
     *
     * @param dt The time interval for the update
     */
    @Override
    public void update(double dt) {
        switch (_state) {
            case NORMAL:
                updateAsNormal(dt);
                break;
            case DANGER:
                updateAsDanger(dt);
                break;
            case MATE:
                updateAsMate(dt);
                break;
            case DEAD:
                break;
        }
        if (IsOutOfMap()) {
            _pos = adjust_position(_pos);
            this._state = State.NORMAL;
        }

        if (_energy <= _lowestenergy || _age > _ageLimit) {
            _state = State.DEAD;
        }

        State state = this.get_state();
        if (state != State.DEAD) {
            _energy += this._region_mngr.get_food(this, dt);
            checkEnergy();
        }

    }

    /**
     * Updates the state and behavior of the sheep as a normal behavior.
     * Sheep will move randomly within the region, consume energy, and adjust desire.
     *
     * @param dt The time increment for the update.
     */
    private void updateAsNormal(double dt) {
        _danger_source = null;
        _mate_target = null;
        if (_dest == null || _pos.distanceTo(_dest) < distanceDest) {
            _dest = new Vector2D(Utils._rand.nextDouble() * _region_mngr.get_width(), Utils._rand.nextDouble() * _region_mngr.get_height());
        }
        move(_speed * dt * Math.exp((_energy - _maxenergy) * _movefactor));
        _age += dt;

        //Energy reduction always between 0 and 100
        _energy -= dt * _energyreductionSheep;
        checkEnergy();
        //Desire addition always between 0 and 100
        _desire += _desirereductionSheep * dt;
        checkDesire();


        if (this._danger_source == null) {
            _danger_source = searchForDanger(_region_mngr, this._danger_strategy);
            if (this._danger_source != null) {
                this._state = State.DANGER;
            } else if (this._desire > 65.0) {
                this._state = State.MATE;
            }
        }
    }

    /**
     * Updates the state and behavior of the sheep when in danger.
     * Sheep will attempt to move away from the danger source or search for a new source of danger or mate.
     *
     * @param dt The time increment for the update.
     */
    private void updateAsDanger(double dt) {
        _mate_target = null;
        if (_danger_source != null) {
            if (_danger_source.get_state() == State.DEAD) {
                _danger_source = null;
            } else {
                _dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
                move(_speedFactorSheep * this._speed * dt * Math.exp((_energy - _maxenergy) * _multiplicativeMath));
                this._age += dt;
            }

            _energy -= _energyreductionSheep * _multiplicativeTime * dt;
            checkEnergy();

            _desire += _desirereductionSheep * dt;
            checkDesire();

        }
        if ((_danger_source == null) || (this._pos.distanceTo(_danger_source.get_position()) <= this._sight_range)) {
            _danger_source = searchForDanger(_region_mngr, _danger_strategy);
            if (_danger_source == null) {
                if (_desire < _desireUpperBound) {
                    this._state = State.NORMAL;
                } else {
                    this._state = State.MATE;
                }
            }
        }
    }

    /**
     * Updates the state and behavior of the sheep when searching for a mate.
     * Sheep will search for a mate, move towards it, and attempt to reproduce if in range.
     *
     * @param dt The time increment for the update.
     */
    private void updateAsMate(double dt) {
        _danger_source = null;
        if (this._mate_target != null && (this._state == State.DEAD || this._sight_range < _pos.distanceTo(_mate_target.get_position()))) {
            this._mate_target = null;
        }
        if (this._mate_target == null) {
            _mate_target = searchForMate(_region_mngr, this._mate_strategy);
            if (_mate_target == null) {
                updateAsNormal(dt);
            }
        }
        if (this._mate_target != null) {
            _dest = _mate_target.get_position();
            move(_speedFactorSheep * this._speed * dt * Math.exp((_energy - _maxenergy) * _multiplicativeMath));
            this._age += dt;


            _energy -= _energyreductionSheep * _multiplicativeTime * dt;
            checkEnergy();

            this._desire += _desirereductionSheep * dt;
            checkDesire();

            if (this._pos.distanceTo(_mate_target.get_position()) < distanceDest) {
                this.setDesire(0);
                this._mate_target.setDesire(0);
                if (!is_pregnant() && Utils._rand.nextDouble() < _createBaby) {
                    _baby = new Sheep(this, _mate_target);
                }
                _mate_target = null;
            }
        }

        if (this._danger_source == null) {
            _danger_source = searchForDanger(_region_mngr, this._danger_strategy);
            if (this._danger_source != null) {
                _state = State.DANGER;
            }
            else if (this._desire < _desireUpperBound) {
                this._state = State.NORMAL;
            }
        }


    }
}




