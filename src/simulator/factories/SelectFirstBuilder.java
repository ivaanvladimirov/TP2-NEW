package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

/**
 * Builder for the SelectFirstBuilder class object.
 */
public class SelectFirstBuilder extends Builder<SelectionStrategy> {
    public SelectFirstBuilder() {
        super("first", "Creates a SelectFirst strategy.");
    }

    @Override
    protected SelectionStrategy create_instance(JSONObject data) throws IllegalArgumentException {
        return new SelectFirst();
    }


}
