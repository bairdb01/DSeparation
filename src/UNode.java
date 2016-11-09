import java.util.ArrayList;
import java.util.HashSet;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-06
 * Last Updated on: 2016-11-08
 * Filename: UNode
 * Description: Undirected graph node
 */
public class UNode {

    private int id;
    private String name;
    private ArrayList<String> domain;
    private HashSet<Integer> paths;
    private ArrayList<Double> probs;

    public UNode() {
        id = -1;
        domain = new ArrayList<>();
        paths = new HashSet<>();
        probs = new ArrayList<>();
        name = "NoName";
    }


    public UNode(int id, String name, ArrayList<String> domain, HashSet<Integer> paths, ArrayList<Double> probs) {
        this.id = id;
        this.domain = domain;
        this.paths = paths;
        this.probs = probs;
        this.name = name;
    }


    public UNode(DAGNode node) {
        this.id = node.getId();
        this.domain = node.getDomain();
        this.paths = new HashSet<>();
        if (node.getChildren().size() > 0)
            this.paths.addAll(node.getChildren());
        if (node.getParents().size() > 0)
            this.paths.addAll(node.getParents());
        this.probs = node.getProbs();
        this.name = node.getName();
    }

    public String toString(){
        StringBuilder newString = new StringBuilder();
        newString.append("NodeId: " + id + "\n");
        newString.append("NodeName: " + name + "\n");
//        newString.append("DomainSize: " + domain.size() + "\n");
//        newString.append("Domain: ");
//        for (String var:
//                domain) {
//            newString.append(var + " ");
//        }
//        newString.append("\n");

        newString.append("Paths Size: " + paths.size() + "\n");
        newString.append("Paths: ");

        for (Integer path:
                paths) {
            newString.append(path + " ");
        }
        newString.append("\n");


//        newString.append("Probs Size: " + probs.size() + "\n");
//        newString.append("Probs: ");
//        for (Double prob:
//                probs) {
//            newString.append(prob + " ");
//        }
//        newString.append("\n");
        return newString.toString();
    }

    public void addPath(Integer toAdd) {
        this.paths.add(toAdd);
    }

    public void setDomain(ArrayList<String> domain) {
        if (domain == null)
            return;
        this.domain = domain;
    }

    public void setPaths(HashSet<Integer> paths) {
        this.paths = paths;
    }

    public void setProbs(ArrayList<Double> probs) {
        this.probs = probs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<String> getDomain() {
        return this.domain;
    }

    public HashSet<Integer> getPaths() {
        return this.paths;
    }

    public ArrayList<Double> getProbs() {
        return this.probs;
    }

    public int getId() {
        return this.id;
    }

    public boolean removePath(Integer toRemove){
        return this.paths.remove(toRemove);
    }
}
