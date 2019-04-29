package logic;

import gui.elements.Arc;
import gui.elements.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MatrixBackup {
    private Map<String, Integer> nodes = new HashMap<>();
    private Map<String, Integer> arcs = new HashMap<>();

    private int pointCount = 0;
    private int transitionCount = 0;
    private int arcCount = 0;

    public MatrixBackup(Map<String, Node> nodes, Map<String, Arc> arcs, int pointCount, int transitionCount, int arcCount) {
        this.nodes = nodes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
        this.arcs = arcs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
        this.pointCount = pointCount;
        this.transitionCount = transitionCount;
        this.arcCount = arcCount;
    }
}
