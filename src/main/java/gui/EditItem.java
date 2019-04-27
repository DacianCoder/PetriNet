package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditItem extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner value;
    private JTextArea valueTextArea;
    private JTextArea nameTextArea;
    private JTextField name;

    public EditItem(Component parent, Object editableItem) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(parent);
        setSize(300, 400);
        value.setValue(((ValueNameComponent) editableItem).getValue());
        name.setText(((ValueNameComponent) editableItem).getName());

        if (editableItem instanceof Transition) {
            value.setEnabled(false);
        }

        buttonOK.addActionListener(e -> {

            if (editableItem instanceof Transition) {
                ((Transition) editableItem).setName(name.getText());
            } else if (editableItem instanceof Point) {
                int x = (Integer) value.getValue();
                if (x < 0) {
                    valueTextArea.setForeground(Color.RED);
                    return;
                }
                ((Point) editableItem).setName(name.getText());
                ((Point) editableItem).setValue(x);
            } else if (editableItem instanceof Arc) {
                int x = (Integer) value.getValue();
                if (x <= 0) {
                    valueTextArea.setForeground(Color.RED);
                    return;
                }
                ((Arc) editableItem).setName(name.getText());
                ((Arc) editableItem).setValue(x);

            }
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
