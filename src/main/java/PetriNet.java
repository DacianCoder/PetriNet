import gui.*;
import gui.enums.Actions;
import logic.Matrix;
import logic.RunNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static gui.enums.Actions.*;

public class PetriNet {
    private JButton pointButton;
    private JButton arcButton;
    private JButton transitionButton;
    private JButton runButton;
    private JButton editButton;
    private List<JButton> mainButtons = Arrays.asList(pointButton, arcButton, transitionButton, runButton, editButton);

    private JPanel panel1;
    private JPanel board;
    private JTextArea info;
    private JButton clearButton;
    private JCheckBox continuousItemsCheckBox;
    private Matrix matrix = new Matrix();
    private Actions action = NONE;
    private RunNet runNet = new RunNet(matrix);

    private String currentArcOrigin = "";
    private boolean isOriginPoint = false;

    public PetriNet() {
        pointButton.addActionListener(actionEvent -> {
            action = DRAW_POINT;
            setActiveButton(pointButton);
        });
        arcButton.addActionListener(actionEvent -> {
            action = DRAW_ARC_ORIGIN;
            setActiveButton(arcButton);
        });
        transitionButton.addActionListener(actionEvent -> {
            action = DRAW_TRANSITION;
            setActiveButton(transitionButton);
        });
        runButton.addActionListener(actionEvent -> {
            action = RUN;
            setActiveButton(runButton);
            runNet.validateTransitions();
            matrix.render(panel1.getGraphics());
        });
        editButton.addActionListener(aE -> {
            action = EDIT;
            setActiveButton(editButton);
        });
        continuousItemsCheckBox.setSelected(true);

        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Location location = new Location(e.getX(), e.getY());
                doAction(location);
                setText();
                matrix.render(panel1.getGraphics());
            }
        });

        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                matrix.deleteData();
                clearBoard();
            }
        });
    }

    private void setText() {
        switch (action) {
            case NONE:
                break;
            case DRAW_POINT:
                info.setText("Select a valid position");
                break;
            case DRAW_TRANSITION:
                info.setText("Select a valid position");
                break;
            case DRAW_ARC_ORIGIN:
                info.setText("Select a origin");
                break;
            case DRAW_ARC_DESTINATION:
                info.setText("Select a destination");
                break;
            case RUN:
                info.setText("Select valid transitions");
                break;
            case EDIT:
                info.setText("");
                break;
        }
    }

    private void doAction(Location location) {

        switch (action) {
            case DRAW_POINT:
                if(!matrix.shouldAddNode(location)){
                    return;
                }
                matrix.addPoint(location);
                if (!continuousItemsCheckBox.isSelected()) {
                    pauseEditing();
                }
                break;
            case DRAW_TRANSITION:
                matrix.addTransition(location);
                if (!continuousItemsCheckBox.isSelected()) {
                    pauseEditing();
                }
                break;
            case DRAW_ARC_DESTINATION:
                if (isOriginPoint) {
                    arcOriginToTransition(location);
                    break;
                }
                arcTransitionToOrigin(location);
                break;
            case DRAW_ARC_ORIGIN:
                currentArcOrigin = "";
                info.setText("");
                drawArcOrigin(location);
                break;
            case RUN:
                caseRun(location);
                break;
            case NONE:
                break;
            case EDIT:
                editCase(location);
                break;
        }
    }

    private void caseRun(Location location) {
        Optional<Transition> maybeTransition = matrix.getRunnableTransition(location);
        if (!maybeTransition.isPresent()) {
            info.setText("Please select a valid transition");
            return;
        }
        maybeTransition.get().runTransition();
        clearBoard();
        matrix.render(panel1.getGraphics());
    }

    private void drawArcOrigin(Location location) {
        Optional<String> maybeOriginPoint = matrix.getPointName(location);
        if (maybeOriginPoint.isPresent()) {
            currentArcOrigin = maybeOriginPoint.get();
            action = DRAW_ARC_DESTINATION;
            isOriginPoint = true;
            info.setText("Select a transition please");
            return;
        }
        Optional<String> maybeOriginTransition = matrix.getTransitionName(location);
        if (maybeOriginTransition.isPresent()) {
            currentArcOrigin = maybeOriginTransition.get();
            action = DRAW_ARC_DESTINATION;
            info.setText("Select a point please");
            isOriginPoint = false;
        }
    }

    private void arcTransitionToOrigin(Location location) {
        Optional<String> maybeFinalPoint = matrix.getPointName(location);
        if (!maybeFinalPoint.isPresent() || maybeFinalPoint.get().equals(currentArcOrigin)) {
            action = continuousItemsCheckBox.isSelected() ? DRAW_ARC_ORIGIN : NONE;
            info.setText("Please try again");
            return;
        }
        arcDestination(maybeFinalPoint.get());
    }

    private void arcOriginToTransition(Location location) {
        Optional<String> maybeNextTransition = matrix.getTransitionName(location);
        if (!maybeNextTransition.isPresent() || currentArcOrigin.equals(maybeNextTransition.get())) {
            action = continuousItemsCheckBox.isSelected() ? DRAW_ARC_ORIGIN : NONE;
            info.setText("Please try again");
            return;
        }
        arcDestination(maybeNextTransition.get());
    }

    private void arcDestination(String nextTransition) {
        matrix.addArc(currentArcOrigin, nextTransition);
        currentArcOrigin = "";
        if (!continuousItemsCheckBox.isSelected()) {
            pauseEditing();

        } else {
            action = DRAW_ARC_ORIGIN;
        }
    }


    private void editCase(Location location) {
        Optional<?> maybeElement = matrix.getArc(location);
        maybeElement.ifPresent(o -> new EditItem(panel1, o));
        maybeElement = matrix.getNode(location);
        maybeElement.ifPresent(o -> new EditItem(panel1, o));
        clearBoard();
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setContentPane(new PetriNet().panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private void setActiveButton(JButton button) {
        if (action != RUN) {
            clearBoard();
            runNet.unvalidateTransitions();
            matrix.render(panel1.getGraphics());
        }
        runNet.unvalidateTransitions();

        mainButtons.forEach(jButton -> {
            if (jButton == button) {
                button.setBackground(Color.RED);
                return;
            }
            jButton.setBackground(Color.WHITE);
        });
        setText();
    }

    private void pauseEditing() {
        action = NONE;
        setActiveButton(null);
    }

    private void clearBoard() {
        Graphics g = panel1.getGraphics();
        runNet.unvalidateTransitions();
        g.setColor(Color.WHITE);
        g.fillRect(0, pointButton.getY() + pointButton.getHeight(), clearButton.getX() + clearButton.getWidth(), clearButton.getY() - clearButton.getHeight());
    }

}
