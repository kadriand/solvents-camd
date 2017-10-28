package co.unal.camd.view;

import co.unal.camd.availability.CompoundEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MoleculePropertiesTable extends JTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoleculePropertiesTable.class);


    MoleculePropertiesTable(final Object[][] rowData, final Object[] columnNames, List<CompoundEntry> availabilities) {
        super(rowData, columnNames);

        DefaultTableCellRenderer stringRenderer = (DefaultTableCellRenderer) this.getDefaultRenderer(String.class);
        stringRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        MatteBorder border = new MatteBorder(1, 1, 0, 0, getGridColor());
        this.setBorder(border);
        this.setCellSelectionEnabled(true);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() != 2)
                    return;

                /*ZERO BASED INDEXES*/
                int row = getSelectedRow();
                int col = getSelectedColumn();

                //build your address / link
                LOGGER.info("row {}. col {}", row, col);
                try {
                    if (availabilities == null || availabilities.size() == 0)
                        return;

                    URI uri = new URI(availabilities.get(0).itemUrl());
                    if (Desktop.isDesktopSupported())
                        Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException e) {
                    LOGGER.error("Problems with link", e);
                } catch (IOException e) {
                    LOGGER.error("Problems opening external link", e);
                }
            }
        });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
