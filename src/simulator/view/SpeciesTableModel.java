package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
    private static final long serialVersionUID = 1L;
    private Controller _ctrl;
    private List<AnimalInfo> _animals;
    private List<String> _columns;
    private List<List<Object>> _data;

    SpeciesTableModel(Controller ctrl) {
        // TODO initialise the corresponding data structures
        _ctrl = ctrl;
        _data = new ArrayList<>();
        _animals = new ArrayList<>();
        _ctrl.addObserver(this);

        _columns = new ArrayList<>();
        _columns.add("Species");
        for (Animal.State state : Animal.State.values() ) {     // header row reading the enum class
            _columns.add(state.toString());
        }

        updateData();
    }

    @Override
    public int getRowCount() {
        return _data.size();
    }

    @Override
    public int getColumnCount() {
        return _columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return _data.get(rowIndex).get(0);
        } else {
            return _data.get(rowIndex).get(columnIndex);
        }
    }


    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {

    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {

    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void open(Component parent) {

    }

    @Override
    public void onAdvanced(double currentTime, MapInfo mapInfo, List<AnimalInfo> animals, double dt) {
        _animals = animals;
        updateData();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return _columns.get(column);
    }

    private void updateData() {
        _data.clear();

        Map<String, Map<Animal.State, Integer>> speciesStateCounts = new HashMap<>();

        // Initialize
        for (String species : _ctrl.getAnimalSpecies()) {
            Map<Animal.State, Integer> stateCounts = new HashMap<>();
            for (Animal.State state : Animal.State.values()) {
                stateCounts.put(state, 0);
            }
            speciesStateCounts.put(species, stateCounts);
        }

        // Count states
        for (AnimalInfo animal : _animals) {
            String species = animal.get_genetic_code();
            Map<Animal.State, Integer> stateCounts = speciesStateCounts.get(species);
            Animal.State state = animal.get_state();
            stateCounts.put(state, stateCounts.get(state) + 1);
        }

        for (String species : _ctrl.getAnimalSpecies()) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(species);
            Map<Animal.State, Integer> stateCounts = speciesStateCounts.get(species);
            for (Animal.State state : Animal.State.values()) {
                rowData.add(stateCounts.get(state));
            }
            _data.add(rowData);
        }
    }

}
