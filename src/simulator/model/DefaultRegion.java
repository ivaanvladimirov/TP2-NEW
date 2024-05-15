package simulator.model;

public class DefaultRegion extends Region implements RegionInfo {
    /**
     * Calculates the amount of food available for an animal in the region.
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
            return _multiplicativeFactor * Math.exp(-Math.max(0, n - _substractionNumHerb) * _speedFactorSheep) * dt;
        }
    }
    @Override
    public void update(double dt) {
        // Do nothing
    }
    @Override
    public String toString() {
        return "Default Region";
    }
}
