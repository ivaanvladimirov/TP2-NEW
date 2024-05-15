package simulator.view;

import simulator.model.*;
import simulator.control.Controller;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
    private static final long serialVersionUID = 1L;
    private Controller _ctrl;
    private MapInfo _mapInfo;
    private List<String> _columns;
    private List<List<Object>> _data;

    RegionsTableModel(Controller ctrl) {
        _ctrl = ctrl;
        _data = new ArrayList<>();
        _ctrl.addObserver(this);

        _columns = new ArrayList<>();
        _columns.add("Row");
        _columns.add("Col");
        _columns.add("Desc.");
        for (Diet diet : Diet.values()) {
            _columns.add(diet.toString());
        }
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
    public String getColumnName(int column) {
        return _columns.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return _data.get(rowIndex).get(0);
        } else if (columnIndex == 1) {
            return _data.get(rowIndex).get(1);
        } else if (columnIndex == 2) {
            return _data.get(rowIndex).get(2);
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
        _mapInfo = mapInfo;
        updateData();
        fireTableDataChanged();
    }

    private void updateData() {
        _data.clear();

        for (MapInfo.RegionData regionData : _mapInfo) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(regionData.row());
            rowData.add(regionData.col());
            rowData.add(regionData.r().toString());

            // Initialize
            Map<Diet, Integer> dietCounts = new HashMap<>();
            for (Diet diet : Diet.values()) {
                dietCounts.put(diet, 0);
            }

            // diet
            for (AnimalInfo animal : regionData.r().getAnimalsInfo()) {
                Diet diet = animal.get_diet();
                dietCounts.put(diet, dietCounts.get(diet) + 1);
            }

            for (Diet diet : Diet.values()) {
                rowData.add(dietCounts.get(diet));
            }

            // description
            for (AnimalInfo animal : regionData.r().getAnimalsInfo()) {
                rowData.add(animal.get_sight_range());
            }

            _data.add(rowData);
        }
    }
}
