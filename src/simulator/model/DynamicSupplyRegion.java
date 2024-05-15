package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region implements RegionInfo {
    private final double _growthRate;
    private double _food;

    /**
     * Constructs a dynamic supply region with the given initial food amount and growth rate.
     *
     * @param food       The initial amount of food
     * @param growthRate The growth rate of food supply
     */
    public DynamicSupplyRegion(double food, double growthRate) {
        this._food = food;
        this._growthRate = growthRate;
    }

    /**
     * Calculates the amount of food available for an animal in the region and updates the food supply.
     *
     * @param a  The animal for which the food amount is calculated
     * @param dt The time step
     * @return The amount of food available for the animal
     */
    public double get_food(Animal a, double dt) {
        int n = getHerbivorousSize();

        if (a._diet == Diet.CARNIVORE) {
            return 0.0;
        } else {
            double food = Math.min(_food, _multiplicativeFactor * Math.exp(-Math.max(0, n - _substractionNumHerb) * _speedFactorSheep) * dt);
            this._food -= food;
            return this._food;
        }
    }

    /**
     * Updates the food supply of the region based on the growth rate.
     *
     * @param dt The time step
     */
    @Override
    public void update(double dt) {
        if (Utils._rand.nextDouble() < 0.5) {
            this._food += this._growthRate * dt;
        }
    }

    @Override
    public String toString() {
        return "Dynamic Supply Region";
    }

}
