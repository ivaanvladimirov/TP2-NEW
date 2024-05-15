package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

/**
 * Builder for the SelectYoungest class object.
 */
public class SelectYoungestBuilder extends Builder<SelectionStrategy> {
    public SelectYoungestBuilder() {
        super("youngest", "Creates a SelectYoungest strategy.");
    }

    @Override
    protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
        return new SelectYoungest();
    }
}
