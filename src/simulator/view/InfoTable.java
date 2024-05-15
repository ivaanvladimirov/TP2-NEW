package simulator.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.*;

public class InfoTable extends JPanel {
    String _title;
    TableModel _tableModel;

    InfoTable(String title, TableModel tableModel) {
        _title = title;
        _tableModel = tableModel;
        initGUI();
    }

    private void initGUI() {
        // Change panel layout to BorderLayout
        setLayout(new BorderLayout());

        // Add a border with a title to the panel
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, _title);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        setBorder(titledBorder);

        // Add a JTable with vertical scroll bar using _tableModel
        JTable table = new JTable(_tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}

