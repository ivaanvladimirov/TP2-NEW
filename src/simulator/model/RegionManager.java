package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.*;
import java.util.function.Predicate;

public class RegionManager implements AnimalMapView, Iterable<MapInfo.RegionData>{
    private final int _rows;
    private final int _cols;
    private final int _width;
    private final int _height;
    private final int _region_width;
    private final int _region_height;
    private final Region[][] _regions;
    private final Map<Animal, Region> _animal_region;

    /**
     * Constructs a region manager with the specified parameters.
     *
     * @param cols   Number of columns
     * @param rows   Number of rows
     * @param width  Total width of the region
     * @param height Total height of the region
     */
    public RegionManager(int rows, int cols, int width, int height) {
        this._rows = rows;
        this._cols = cols;
        this._width = width;
        this._height = height;
        this._regions = new Region[_rows][_cols];
        this._region_width = width / cols;
        this._region_height = height / rows;
        this._animal_region = new HashMap<>();

        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _cols; j++) {
                _regions[i][j] = new DefaultRegion();
            }
        }
        iterator();
    }


    public int get_cols() {
        return _cols;
    }

    public int get_rows() {
        return _rows;
    }

    public int get_width() {
        return _width;
    }

    public int get_height() {
        return _height;
    }

    public int get_region_width() {
        return _region_width;
    }

    public int get_region_height() {
        return _region_height;
    }

    /**
     * Sets the region at the specified row and column to the given region.
     * Moves animals from the current region to the new region if applicable.
     *
     * @param row The row index
     * @param col The column index
     * @param r   The region to set
     * @throws IllegalArgumentException if the row or col are out of range
     */
    public void set_region(int row, int col, Region r) {
        // Get the current region at the specified row and column
        Region currentRegion = _regions[row][col];

        // Add all the animals from the current region to the new region
        for (Map.Entry<Animal, Region> entry : _animal_region.entrySet()) {
            if (entry.getValue().equals(currentRegion)) {
                // Add the animal to the new region
                r.add_animal(entry.getKey()); // Assuming you have a method in Region to add an animal

                // Update the _animal_region map
                _animal_region.put(entry.getKey(), r);
            }
        }

        // Set the region at the specified row and column to the new region
        _regions[row][col] = r;
    }

    /**
     * Registers an animal within the region manager.
     *
     * @param a The animal to register
     */
    public void register_animal(Animal a) {
//        if (a == null) {
//            return;
//        }
        a.init(this);
        // Calculate the row and column of the region based on the animal's position
        int row = (int) a.get_position().getY() / _region_height;
        int col = (int) a.get_position().getX() / _region_width;

        // Check if the row and col are within the valid range
        if (row < 0) {
            row = 0;
        } else if (row >= _rows) {
            row = _rows - 1;
        }
        if (col < 0) {
            col = 0;
        } else if (col >= _cols) {
            col = _cols - 1;
        }
        // Get the region at the specified row and column
        Region r = _regions[row][col];
        // Add the animal to the region
        r.add_animal(a);
        // Update the _animal_region map
        _animal_region.put(a, r);

    }

    /**
     * Unregisters an animal from the region manager.
     *
     * @param a The animal to unregister
     */
    public void unregister_animal(Animal a) {
        Region reg = _animal_region.get(a);

        if (reg != null) {
            reg.remove_animal(a);
        }
        _animal_region.remove(a);
    }

    /**
     * Updates the region of the specified animal based on its position.
     *
     * @param a The animal to update
     */
    public void update_animal_region(Animal a) {
        double x = a.get_position().getX();
        double y = a.get_position().getY();

        int row = (int) Utils.constrain_value_in_range(y / _region_height, 0, _rows - 1);
        int col = (int) Utils.constrain_value_in_range(x / _region_width, 0, _cols - 1);
        Region regCurrent = _animal_region.get(a);
        Region regNew = _regions[row][col];

        if (regNew != regCurrent) {
            if (regCurrent != null) {
                regCurrent.remove_animal(a);
            }
            if (regNew != null) {
                regNew.add_animal(a);
                _animal_region.put(a, regNew);
            }
        }
    }

    /**
     * Gets the food available to the specified animal in its current region.
     *
     * @param a  The animal
     * @param dt The time interval
     * @return The amount of food available
     */
    public double get_food(Animal a, double dt) {
        Region reg = _animal_region.get(a);
        double food = 0;
        if (reg != null) {
            food = reg.get_food(a, dt);

        }
        return food;
    }

    /**
     * Updates all regions within the region manager.
     *
     * @param dt The time interval
     */
    void update_all_regions(double dt) {
        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _cols; j++) {
                _regions[i][j].update(dt);
            }
        }
    }

    /**
     * Retrieves a list of animals within the specified range of the given animal position, filtered by the provided predicate.
     *
     * @param a      The reference animal.
     * @param filter The predicate used to filter the animals.
     * @return A list of animals within the specified range and satisfying the filter predicate.
     */
    @Override
    public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
        List<Animal> animals_in_range = new ArrayList<>();
        Vector2D pos = a.get_position();
        double sight_range = a.get_sight_range();
        double col = pos.getX();
        double row = pos.getY();

        int col_mx = (int) (Math.max(0, col + sight_range) / _region_width);
        int col_mn = (int) (Math.max(0, col - sight_range) / _region_width);
        int row_mx = (int) (Math.max(0, row + sight_range) / _region_height);
        int row_mn = (int) (Math.max(0, row - sight_range) / _region_height);

        col_mx = Math.min(col_mx, _cols - 1);
        col_mn = Math.max(col_mn, 0);
        row_mx = Math.min(row_mx, _rows - 1);
        row_mn = Math.max(row_mn, 0);

        for (int f = row_mn; f <= row_mx; f++) {
            for (int c = col_mn; c <= col_mx; c++) {
                Region reg = _regions[f][c];
                for (int i = 0; reg.animals.size() > i; i++) {
                    Animal animal = reg.animals.get(i);
                    if (filter.test(animal)) {
                        animals_in_range.add(animal);
                    }
                }

            }
        }
        return animals_in_range;
    }

    /**
     * Generates a JSON representation of the region manager and its regions.
     *
     * @return A JSON object representing the region manager and its regions.
     */
    public JSONObject as_JSON() {
        JSONObject json = new JSONObject();
        JSONArray regions = new JSONArray();
        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _cols; j++) {
                Region region = _regions[i][j];
                if (region != null) {
                    JSONObject regionJson = region.as_JSON();
                    regionJson.put("row", j);
                    regionJson.put("col", i);
                    regionJson.put("data", region.as_JSON());
                    regions.put(regionJson);
                }
            }
        }
        json.put("regions", regions);
        return json;
    }

    private class RegionManagerIterator implements Iterator<MapInfo.RegionData> {
        private int row = 0;
        private int col = 0;

        @Override
        public boolean hasNext() {
            return row < _rows && col < _cols;
        }

        @Override
        public MapInfo.RegionData next() {
            MapInfo.RegionData regionData = new MapInfo.RegionData(row, col, _regions[row][col]);
            col++;
            if (col >= _cols) {
                col = 0;
                row++;
            }
            return regionData;
        }
    }

    public Iterator<MapInfo.RegionData> iterator() {
        return new RegionManagerIterator();
    }



}
