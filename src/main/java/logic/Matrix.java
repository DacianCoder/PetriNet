package logic;

import gui.*;
import gui.Point;
import gui.enums.Shape;
import lombok.Data;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static gui.LocationUtils.isInPointArea;
import static gui.LocationUtils.isInTransitionArea;

@Data
public class Matrix {

    private static String POINT_PREFIX = "P";
    private static String TRANSITION_PREFIX = "T";
    private static String ARC_PREFIX = "A";

    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, Arc> arcs = new HashMap<>();

    private int pointCount = 0;
    private int transitionCount = 0;
    private int arcCount = 0;

    public Optional<Arc> getArc(Location location) {
        return arcs.values().stream()
                .filter(arc -> LocationUtils.isInArcTextArea(arc, location))
                .findAny();
    }

    public void render(Graphics g) {
        nodes.forEach((s, n) -> n.render(g));
        arcs.forEach((s, a) -> a.render(g));
    }

    public void addPoint(Location location) {
        gui.Point point = new Point();
        point.setLocation(location);
        String name = POINT_PREFIX + pointCount;
        point.setName(name);
        point.setType(gui.enums.Shape.POINT);
        nodes.put(name, point);
        pointCount++;
    }

    public void addTransition(Location location) {
        Transition transition = new Transition();
        transition.setLocation(location);
        String name = TRANSITION_PREFIX + transitionCount;
        transition.setName(name);
        transition.setType(Shape.TRANSITION);
        nodes.put(name, transition);
        transitionCount++;
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

        arc.setValue(1);
        String name = ARC_PREFIX + arcCount;
        arc.setName(name);
        if (Objects.isNull(arc.getDestination()) || Objects.isNull(arc.getOrigin())) {
            //throw error
            System.out.println("something went wrong");
        }
        arcs.put(name, arc);
        arcCount++;
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
        return nodes.values().stream()
                .filter(node -> LocationUtils.isInTransitionArea())
    }
}
