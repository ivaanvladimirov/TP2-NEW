package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

/**
 * Builder for the SelectClosest class object.
 */
public class SelectClosestBuilder extends Builder<SelectionStrategy> {
    public SelectClosestBuilder() {
        super("closest", "Creates a SelectClosest strategy.");
    }

    @Override
    protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
        return new SelectClosest();
    }
}
