import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import gui.*;
import gui.elements.Location;
import gui.elements.Transition;
import enums.Actions;
import logic.Matrix;
import logic.RunNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

import static enums.Actions.*;

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
    private JButton undoButton;
    private Matrix matrix = new Matrix();
    private Actions action = NONE;
    private RunNet runNet = new RunNet(matrix);

    private String currentArcOrigin = "";
    private boolean isOriginPoint = false;
    private boolean shouldRerender = true;

    public PetriNet() {
        pointButton.addActionListener(actionEvent -> {
            loadLastState();
            action = DRAW_POINT;
            setActiveButton(pointButton);
        });
        arcButton.addActionListener(actionEvent -> {
            loadLastState();
            action = DRAW_ARC_ORIGIN;
            setActiveButton(arcButton);
        });
        transitionButton.addActionListener(actionEvent -> {
            loadLastState();
            action = DRAW_TRANSITION;
            setActiveButton(transitionButton);
        });
        runButton.addActionListener(actionEvent -> {
            action = RUN;
            matrix.saveState();
            setActiveButton(runButton);
            runNet.validateTransitions();
            matrix.render(panel1.getGraphics());
        });
        editButton.addActionListener(aE -> {
            loadLastState();
            action = EDIT;
            setActiveButton(editButton);
        });
        continuousItemsCheckBox.setSelected(true);

        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setText();
                doAction(new Location(e.getX(), e.getY()));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setText();
                    }
                }, 2000);
                if (shouldRerender) {
                    matrix.render(panel1.getGraphics());
                }
            }
        });

        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                matrix.deleteData();
                clearBoard();
            }
        });
        undoButton.addActionListener(actionEvent -> {
            matrix.undoLastAction();
            clearBoard();
            matrix.render(panel1.getGraphics());
        });
    }

    private void loadLastState() {
        if (action == RUN) {
            this.matrix.loadLastState();
        }
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
                Consumer<Location> pointConsumer = location1 -> matrix.addPoint(location1, null);
                addBasicElement(location, pointConsumer);
                break;
            case DRAW_TRANSITION:
                Consumer<Location> transitionConsumer = location1 -> matrix.addTransition(location1, null);
                addBasicElement(location, transitionConsumer);
                break;
            case DRAW_ARC_DESTINATION:
                drawArcDestination(location);
                shouldRerender = true;
                break;
            case DRAW_ARC_ORIGIN:
                currentArcOrigin = "";
                info.setText("");
                drawArcOrigin(location);
                shouldRerender = false;
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

    private void drawArcDestination(Location location) {
        if (isOriginPoint) {
            arcOriginToTransition(location);
            return;
        }
        arcTransitionToOrigin(location);
    }

    private void addBasicElement(Location location, Consumer<Location> consumer) {
        if (!matrix.shouldAddNode(location)) {
            return;
        }
        consumer.accept(location);
        if (!continuousItemsCheckBox.isSelected()) {
            pauseEditing();
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
        runNet.validateTransitions();
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

    private void arcDestination(String nextNode) {
        if (matrix.shouldAddArc(currentArcOrigin, nextNode)) {
            matrix.addArc(currentArcOrigin, nextNode, null);
        }
        currentArcOrigin = "";
        if (!continuousItemsCheckBox.isSelected()) {
            pauseEditing();

        } else {
            action = DRAW_ARC_ORIGIN;
        }
    }


    private void editCase(Location location) {
        Optional<?> maybeElement = matrix.getArc(location);
        maybeElement.ifPresent(o -> new EditItem(panel1, o, matrix));
        maybeElement = matrix.getNode(location);
        maybeElement.ifPresent(o -> new EditItem(panel1, o, matrix));
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
        clearBoard();
        shouldRerender = true;
        if (action != RUN) {
            runNet.unvalidateTransitions();
        }
        matrix.render(panel1.getGraphics());

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
        Rectangle boardRect = board.getBounds();
        Graphics g = panel1.getGraphics();
        if (action != RUN) {
            runNet.unvalidateTransitions();
        }
        g.setColor(Color.WHITE);
        g.fillRect(boardRect.x, boardRect.y, boardRect.width, boardRect.height);
    }

}
