package simulator.factories;

import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.*;

/**
 * Builder for the Sheep class object.
 */
public class SheepBuilder extends Builder<Animal> {
    private final Factory<SelectionStrategy> _strategy;
    private SelectionStrategy mateStrategy;
    private SelectionStrategy dangerStrategy;

    public SheepBuilder(Factory<SelectionStrategy> strategy) {
        super("sheep", "Creates a Sheep instance.");
        _strategy = strategy;
    }

    /**
     * Creates a new Sheep instance.
     *
     * @param data JSON object containing data for the instance creation
     * @return a new Sheep instance
     * @throws IllegalArgumentException if the data is invalid
     */
    @Override
    protected Animal create_instance(JSONObject data) throws IllegalArgumentException {
        JSONObject posData = data.optJSONObject("pos");
        Vector2D pos = posData != null ? new Vector2D(
                Vector2D.get_random_vector(
                        posData.getJSONArray("x_range").getDouble(0),
                        posData.getJSONArray("x_range").getDouble(1),
                        posData.getJSONArray("y_range").getDouble(0),
                        posData.getJSONArray("y_range").getDouble(1))) : null;
        fill_in_data(data);

        return new Sheep(mateStrategy, dangerStrategy, pos);
    }

    /**
     * Fills in additional data into the provided JSON object.
     *
     * @param o JSON object to fill in data
     */
    @Override
    protected void fill_in_data(JSONObject o) {
        //For mate strategy
        JSONObject mateStrategyData = o.optJSONObject("mate_strategy");
        if(mateStrategyData == null) {
        	mateStrategy = _strategy.createInstance(new SelectFirstBuilder().get_info());
        }
        else{
            String mateType = mateStrategyData.getString("type");
            switch (mateType) {
                case "closest" -> mateStrategy =_strategy.createInstance(new SelectClosestBuilder().get_info());
                case "first" -> mateStrategy = _strategy.createInstance(new SelectFirstBuilder().get_info());
                case "youngest" -> mateStrategy = _strategy.createInstance(new SelectYoungestBuilder().get_info());
            }
        }

        //For danger strategy
        JSONObject dangerStrategyData = o.optJSONObject("danger_strategy");
        if(dangerStrategyData == null) {
        	dangerStrategy = _strategy.createInstance(new SelectFirstBuilder().get_info());
        }
        else{
            String dangerType = dangerStrategyData.getString("type");
            switch (dangerType) {
                case "closest" -> dangerStrategy = _strategy.createInstance(new SelectClosestBuilder().get_info());
                case "first" -> dangerStrategy = _strategy.createInstance(new SelectFirstBuilder().get_info());
                case "youngest" -> dangerStrategy = _strategy.createInstance(new SelectYoungestBuilder().get_info());
            }
        }

    }
}
