package logic;

import enums.HistoryAction;
import gui.elements.*;
import gui.elements.Point;
import gui.elements.utils.LocationUtils;
import lombok.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static gui.elements.utils.Constants.ARC_DEFAULT_VALUE;
import static gui.elements.utils.LocationUtils.isInPointArea;
import static gui.elements.utils.LocationUtils.isInTransitionArea;
import static enums.HistoryAction.ADD;
import static enums.HistoryAction.DELETE;

@Data
public class Matrix {

    private static String POINT_PREFIX = "P";
    private static String TRANSITION_PREFIX = "T";
    private static String ARC_PREFIX = "A";

    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, Arc> arcs = new HashMap<>();

    private MatrixBackup backup = new MatrixBackup();

    private int pointCount = 0;
    private int transitionCount = 0;
    private int arcCount = 0;
    private LinkedList<HistoryEvent> history = new LinkedList<>();
    private int currentHistoryPoint = 0;

    public void saveState() {
        this.backup = new MatrixBackup(nodes, arcs, pointCount, transitionCount, arcCount);
    }

    public void loadLastState() {
        arcs.values().forEach(arc -> arc.setValue(this.backup.getArcs().get(arc.getName())));
        nodes.values().forEach(node -> node.setValue(this.backup.getNodes().get(node.getName())));
        this.pointCount = this.backup.getPointCount();
        this.transitionCount = this.backup.getTransitionCount();
        this.arcCount = this.backup.getArcCount();
    }

    public Optional<Arc> getArc(Location location) {
        return arcs.values().stream()
                .filter(arc -> LocationUtils.isInArcTextArea(arc, location))
                .findAny();
    }

    public void render(Graphics g) {
        nodes.forEach((s, n) -> n.render(g));
        arcs.forEach((s, a) -> a.render(g));
    }

    public void addPoint(Location location, String forcedName) {
        Point point = new Point();
        boolean isUndo = forcedName != null;
        point.setLocation(location);
        String name = isUndo ? forcedName : POINT_PREFIX + pointCount;
        point.setName(name);
        nodes.put(name, point);
        pointCount++;
        if (!isUndo) {
            addHistory(point, ADD);
        }
    }

    public void addTransition(Location location, String forcedName) {
        Transition transition = new Transition();
        boolean isUndo = forcedName != null;
        String name = isUndo ? forcedName : TRANSITION_PREFIX + transitionCount;
        transition.setLocation(location);
        transition.setName(name);
        nodes.put(name, transition);
        transitionCount++;
        if (!isUndo) {
            addHistory(transition, ADD);
        }
    }


    public void addArc(String from, String to) {
        Arc arc = new Arc();
        Node origin = nodes.get(from);
        Node destination = nodes.get(to);
        arc.setOrigin(origin);
        arc.setDestination(destination);
        if (origin instanceof Transition) {
            ((Transition) origin).addGoingArc(arc);
        } else {
            ((Transition) destination).addComingArc(arc);
        }

        arc.setValue(ARC_DEFAULT_VALUE);
        String name = ARC_PREFIX + arcCount;
        arc.setName(name);
        if (Objects.isNull(arc.getDestination()) || Objects.isNull(arc.getOrigin())) {
            //throw error
            System.out.println("something went wrong");
        }
        arcs.put(name, arc);
        arcCount++;
        addHistory(arc, ADD);
    }

    public Optional<String> getPointName(Location location) {
        return nodes.values().stream()
                .filter(Point.class::isInstance)
                .filter(node -> isInPointArea(node.getLocation(), location))
                .map(Node::getName)
                .findAny();
    }

    public Optional<String> getTransitionName(Location location) {
        return nodes.values().stream()
                .filter(Transition.class::isInstance)
                .filter(stringNodeEntry -> isInTransitionArea(stringNodeEntry.getLocation(), location))
                .map(Node::getName)
                .findAny();
    }


    public void deleteData() {
        nodes = new HashMap<>();
        arcs = new HashMap<>();
        backup = new MatrixBackup();
        transitionCount = 0;
        pointCount = 0;
        arcCount = 0;
    }

    public Optional<?> getNode(Location location) {
        return nodes.values().stream()
                .filter(node -> LocationUtils.isNodeNameClicked(node, location))
                .findAny();
    }

    public Optional<Transition> getTransition(Location location) {
        return getTransitionName(location)
                .map(s -> Optional.of((Transition) nodes.get(s)))
                .orElseGet(() -> nodes.values().stream()
                        .filter(Transition.class::isInstance)
                        .map(Transition.class::cast)
                        .filter(t -> isInTransitionArea(t.getLocation(), location))
                        .findAny());

    }

    //extract duplicate
    public Optional<Transition> getRunnableTransition(Location location) {
        return getTransitionName(location)
                .map(s -> Optional.of((Transition) nodes.get(s)))
                .orElseGet(() -> nodes.values().stream()
                        .filter(Transition.class::isInstance)
                        .map(Transition.class::cast)
                        .filter(t -> isInTransitionArea(t.getLocation(), location))
                        .filter(Transition::canRun)
                        .findAny());

    }

    public boolean shouldAddNode(Location location) {
        return nodes.values().stream().noneMatch(node -> LocationUtils.isOverlapingNode(node.getNodeSpace(), location));
    }


    public boolean shouldAddArc(String from, String to) {
        return arcs.values().stream().noneMatch(arc -> (from.equals(arc.getOrigin().getName()) && to.equals(arc.getDestination().getName())) ||
                (to.equals(arc.getOrigin().getName()) && from.equals(arc.getDestination().getName())));
    }

    public void deleteAttachedArcs(Node node, boolean shouldAddHistory) {
        List<Arc> toDeleteArcs = arcs.values().stream()
                .filter(arc -> node.equals(arc.getOrigin()) || node.equals(arc.getDestination()))
                .collect(Collectors.toList());
        toDeleteArcs.forEach(arc -> {
            if (arc.getOrigin() instanceof Transition) {
                ((Transition) arc.getOrigin()).getLeavingArcs().remove(arc);
            }
            if (arc.getDestination() instanceof Transition) {
                ((Transition) arc.getDestination()).getEnteringArcs().remove(arc);
            }
            if (shouldAddHistory) {
                addHistory(arcs.remove(arc.getName()), DELETE);
            }
        });
    }

    public void undoLastAction() {
        if (history.size() < 1) {
            return;
        }
        HistoryEvent event = history.removeLast();
        Object item = event.getValue();
        switch (event.getAction()) {
            case ADD:
                undoItemCreation(item);
                break;
            case DELETE:
                undoItemDeletion(item);
                break;
            case EDIT:
                break;
        }
    }

    private void undoItemDeletion(Object item) {

        if (item instanceof Arc) {
            addArc(((Arc) item).getOrigin().getName(), ((Arc) item).getDestination().getName());
            return;
        }
        if (item instanceof Point) {
            addPoint(((Point) item).getLocation(), ((Point) item).getName());
            return;
        }
        if (item instanceof Transition) {
            addTransition(((Transition) item).getLocation(), ((Transition) item).getName());
        }
    }

    private void undoItemCreation(Object item) {
        if (item instanceof Arc) {
            arcs.remove(((Arc) item).getName());
            arcCount--;
            return;
        }
        if (item instanceof Point) {
            nodes.remove(((Point) item).getName());
            pointCount--;
            return;
        }
        if (item instanceof Transition) {
            nodes.remove(((Transition) item).getName());
            transitionCount--;
            deleteAttachedArcs((Node) item, false);
        }
    }

    public void addHistory(Object o, HistoryAction action) {
        history.add(new HistoryEvent(action, o));
        currentHistoryPoint++;
    }
}

