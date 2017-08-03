package co.unal.camd;

import co.unal.camd.view.CamdSetupWindow;

import javax.swing.*;

public class CamdSolvents {

    /**
     * 0. Is the void main executable of the program
     *
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            CamdSetupWindow thisClass = new CamdSetupWindow();
            thisClass.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            thisClass.setVisible(true);
        });
    }

}
