package simulator.view;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.control.Controller;
import simulator.model.*;
import simulator.launcher.Main;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
    private final DefaultComboBoxModel<String> _regionsModel;
    private final DefaultComboBoxModel<String> _fromRowModel;
    private final DefaultComboBoxModel<String> _toRowModel;
    private final DefaultComboBoxModel<String> _fromColModel;
    private final DefaultComboBoxModel<String> _toColModel;
    private DefaultTableModel _dataTableModel;
    private final Controller _ctrl;
    private List<JSONObject> _regionsInfo;
    private int _status;
    private final String[] _headers = { "Key", "Value", "Description"};

    ChangeRegionsDialog(Controller ctrl) {
        super((Frame)null, true);
        _ctrl = ctrl;

        _regionsModel = new DefaultComboBoxModel<>();
        _fromRowModel = new DefaultComboBoxModel<>();
        _toRowModel = new DefaultComboBoxModel<>();
        _fromColModel = new DefaultComboBoxModel<>();
        _toColModel = new DefaultComboBoxModel<>();

        initGUI();
        _ctrl.addObserver(this);
    }

    private void initGUI() {
        setTitle("Change Regions");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel helpPanel = new JPanel();
        JLabel helpLabel = new JLabel("Select the region type, the rows/cols interval, and provide values for the parameters in the Value column (default values are used for parameters with no value).");
        helpPanel.add(helpLabel);
        mainPanel.add(helpPanel);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(10, 20));
        this.add(separator);

        // _regionsInfo will be used to set the information in the table
        _regionsInfo = Main.region_factory.get_info();

        // _dataTableModel is a table model that includes all the parameters of the region
        _dataTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // make only column 1 editable
                return column == 1;
            }
        };
        _dataTableModel.setColumnIdentifiers(_headers);

        // create a JTable that uses _dataTableModel and add it to dialog

        JTable table = new JTable(_dataTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(scrollPane);

        // _regionsModel is a combobox model that includes the region types
        // add the description of all the regions to _regionsModel, using
        // the key “desc” or “type” of the JSONObject in _regionsInfo, since
        // these give us information about what the factory can create.
        for (JSONObject regionInfo : _regionsInfo) {
            String type = regionInfo.optString("type", regionInfo.optString("desc", ""));
            _regionsModel.addElement(type);
        }

        // create a combobox que uses _regionsModel and add it to dialog.
        JComboBox<String> regionsComboBox = new JComboBox<>(_regionsModel);
        mainPanel.add(regionsComboBox);

        //create 4 combobox models for _fromRowModel, _toRowModel, _fromColModel, and _toColModel
        //create 4 comboboxes that use these models and add them to the dialog
        JComboBox<String> fromRowComboBox = new JComboBox<>(_fromRowModel);
        JComboBox<String> toRowComboBox = new JComboBox<>(_toRowModel);
        JComboBox<String> fromColComboBox = new JComboBox<>(_fromColModel);
        JComboBox<String> toColComboBox = new JComboBox<>(_toColModel);

        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboBoxPanel.add(new JLabel("Region type:"));
        comboBoxPanel.add(regionsComboBox);
        comboBoxPanel.add(new JLabel("Row from/to:"));
        comboBoxPanel.add(fromRowComboBox);
        comboBoxPanel.add(toRowComboBox);
        comboBoxPanel.add(new JLabel("Column from/to:"));
        comboBoxPanel.add(fromColComboBox);
        comboBoxPanel.add(toColComboBox);
        mainPanel.add(comboBoxPanel);


        //create the OK and Cancel buttons and add them to the dialog
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        regionsComboBox.addActionListener(e -> {
            for (int i = _dataTableModel.getRowCount() - 1; i >= 0; i--) {
                _dataTableModel.removeRow(i);
            }

            String selectedRegionType = (String) regionsComboBox.getSelectedItem();
            if ("dynamic".equals(selectedRegionType)) {
                for (JSONObject regionInfo : _regionsInfo) {
                    JSONObject data = regionInfo.optJSONObject("data");
                    if (data != null) {
                        for (String key : data.keySet()) {
                            String value = "";
                            String desc = data.optString(key, "");
                            _dataTableModel.addRow(new String[]{key, value, desc});
                        }
                    }
                }
            }
        });
        okButton.addActionListener(e -> {
            // Implement OK button functionality
            String regionType = (String) regionsComboBox.getSelectedItem();
            int selectedRegionIndex = regionsComboBox.getSelectedIndex();
            JSONObject regionInfo = _regionsInfo.get(selectedRegionIndex);
            JSONArray rowArray = new JSONArray();
            JSONArray colArray = new JSONArray();

            // Obtain row and column information
            String fromRow = (String) fromRowComboBox.getSelectedItem();
            String toRow = (String) toRowComboBox.getSelectedItem();
            String fromCol = (String) fromColComboBox.getSelectedItem();
            String toCol = (String) toColComboBox.getSelectedItem();

            rowArray.put(Integer.parseInt(fromRow));
            rowArray.put(Integer.parseInt(toRow));
            colArray.put(Integer.parseInt(fromCol));
            colArray.put(Integer.parseInt(toCol));

            JSONObject spec = new JSONObject();
            spec.put("type", regionType);
            spec.put("data", regionInfo.getJSONObject("data"));

            JSONArray regionsArray = new JSONArray();
            JSONObject regionObject = new JSONObject();
            regionObject.put("row", rowArray);
            regionObject.put("col", colArray);
            regionObject.put("spec", spec);
            regionsArray.put(regionObject);

            JSONObject regionsObject = new JSONObject();
            regionsObject.put("regions", regionsArray);

            

            try {
                _ctrl.set_regions(regionsObject);
                _status = 1;
                 setVisible(false);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg(ex.getMessage());
            }
        });



        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _status = 0;
                setVisible(false);
            }
        });

        setPreferredSize(new Dimension(1000, 400)); 
        pack();
        setResizable(false);
        setVisible(false);
    }


    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        // Update the list of options in the row and column comboBoxes
        updateRowColumnComboBoxes(map.get_rows(), map.get_cols());
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        // Update the list of options in the row and column comboBoxes
        updateRowColumnComboBoxes(map.get_rows(), map.get_cols());
    }

    private void updateRowColumnComboBoxes(int rows, int cols) {
        _fromRowModel.removeAllElements();
        _toRowModel.removeAllElements();
        _fromColModel.removeAllElements();
        _toColModel.removeAllElements();

        for (int i = 0; i < rows; i++) {
            _fromRowModel.addElement(String.valueOf(i));
            _toRowModel.addElement(String.valueOf(i));
        }

        for (int j = 0; j < cols; j++) {
            _fromColModel.addElement(String.valueOf(j));
            _toColModel.addElement(String.valueOf(j));
        }
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void open(Component parent) {
        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }

    @Override
    public void onAdvanced(double currentTime, MapInfo mapInfo, List<AnimalInfo> animals, double dt) {

    }



    public Controller getController() {
        return _ctrl;
    }
}

