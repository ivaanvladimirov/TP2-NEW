package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.*;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final Simulator _sim;

    public Controller(Simulator sim) {
        this._sim = sim;
    }

    /**
     * Loads simulation data from the provided JSON object.
     *
     * @param data JSON object containing simulation data
     */
    public void load_data(JSONObject data) {
        if (data.has("regions")) {
            load(data);
        }

        JSONArray animals = data.getJSONArray("animals");
        for (int i = 0; i < animals.length(); i++) {
            JSONObject animalSpec = animals.getJSONObject(i);
            int amount = animalSpec.getInt("amount");
            JSONObject spec = animalSpec.getJSONObject("spec");
            for (int j = 0; j < amount; j++) {
                _sim.add_animal(spec);
            }
        }
    }

    private void load(JSONObject regions) {
        JSONArray regionsArray = regions.getJSONArray("regions");
        for (int i=0; i < regionsArray.length(); ++i){
            JSONObject r_json = regionsArray.getJSONObject(i);

            JSONArray rowArray = r_json.optJSONArray("row");
            JSONArray colArray = r_json.optJSONArray("col");
            JSONObject spec = r_json.getJSONObject("spec");

            if(rowArray != null && colArray != null){
                int startRow = rowArray.getInt(0);
                int endRow = rowArray.getInt(1);
                int startCol = rowArray.getInt(0);
                int endCol = rowArray.getInt(1);

                for (int j = startRow; j < endRow; j++) {
                    for (int k = startCol; k < endCol; k++) {
                        _sim.set_region(j, k, spec);
                    }
                }

            }
        }
    }

    /**
     * Runs the simulation for the specified duration.
     *
     * @param t   Duration of the simulation
     * @param dt  Time step for the simulation
     * @param sv  Indicates whether to display the simulation visually
     * @param out Output stream to write simulation results
     */
    public void run(double t, double dt, boolean sv, OutputStream out) {
        JSONObject init_state = _sim.as_JSON();
        JSONObject final_state;
        SimpleObjectViewer view = null;
        if (sv) {
            MapInfo m = _sim.get_map_info();
            view = new SimpleObjectViewer("ECOSYSTEM", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
            view.update(to_animals_info(_sim.getAnimals()), _sim.get_time(), dt);

        }

        while (_sim.get_time() < t) {
            _sim.advance(dt);
            if (sv) {
                view.update(to_animals_info(_sim.getAnimals()), _sim.get_time(), dt);
            }
        }
        final_state = _sim.as_JSON();
        JSONObject output = new JSONObject();
        output.put("in", init_state);
        output.put("out", final_state);


        try {
            out.write(output.toString(2).getBytes());
        } catch (Exception e) {
            System.err.println("Error while writing the output file: " + e.getLocalizedMessage());
        }
        if (sv)
            view.close();
    }

    /**
     * Converts list of animal information to list of object information.
     *
     * @param animals List of animal information
     * @return List of object information
     */
    private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
        List<ObjInfo> ol = new ArrayList<>(animals.size());
        for (AnimalInfo animal : animals) {
            ol.add(new ObjInfo(animal.get_genetic_code(), (int) animal.get_position().getX(), (int) animal.get_position().getY(), (int) Math.round(animal.get_age()) + 2));
        }
        return ol;
    }

    public void reset(int cols, int rows, int width, int height) {
        _sim.reset(cols, rows, width, height);
    }
    public void set_regions(JSONObject rs) {
        JSONArray regionsArray = rs.getJSONArray("regions");

        for (int i = 0; i < regionsArray.length(); i++) {
            JSONObject regionObject = regionsArray.getJSONObject(i);
            JSONArray rowArray = regionObject.getJSONArray("row");
            JSONArray colArray = regionObject.getJSONArray("col");
            JSONObject spec = regionObject.getJSONObject("spec");

            int startRow = rowArray.getInt(0);
            int endRow = rowArray.getInt(1);
            int startCol = colArray.getInt(0);
            int endCol = colArray.getInt(1);

            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    _sim.set_region(row, col, spec);
                }
            }
        }
    }
    public void advance(double dt) {
        _sim.advance(dt);
    }

    public void addObserver(EcoSysObserver o) {
        _sim.addObserver(o);
    }

    public void removeObserver(EcoSysObserver o){
        _sim.removeObserver(o);
    }

    public List<String> getAnimalSpecies() {
        List<String> species = new ArrayList<>();
        List<Animal> animals = _sim.getAnimals();
        for (AnimalInfo animal : animals) {
            String speciesName = animal.get_genetic_code();
            if (!species.contains(speciesName)) {
                species.add(speciesName);
            }
        }
        return species;
    }
}

