package co.unal.camd;

import co.unal.camd.availability.MongodbClient;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.CamdSetupWindow;

import javax.swing.*;

public class CamdSolvents {

    /**
     * 0. Is the void main executable of the program
     *
     * @param args
     */
    public static void main(String[] args) {
        MongodbClient.ENABLE_DB = true;

        // TODO Auto-generated method stub
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println(("IS RUNNING? " + CamdRunner.AVAILABILITY_FINDER.isRunning()));

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                CamdSetupWindow camdSetupWindow = new CamdSetupWindow();
                camdSetupWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                camdSetupWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        CamdRunner.AVAILABILITY_FINDER.close();
                        System.exit(0);
                    }
                });
                camdSetupWindow.setVisible(true);
            } catch (Exception e) {
                CamdRunner.AVAILABILITY_FINDER.close();
                e.printStackTrace();
            }
        });
    }

}
