import java.util.ArrayList;
import java.util.HashSet;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-06
 * Last Updated on: 2016-11-06
 * Filename: DAGNode
 * Description:
 */
public class DAGNode {
    private int id;
    private String name;
    private ArrayList<String> domain;
    private HashSet<Integer> parents;
    private HashSet<Integer> children;
    private ArrayList<Double> probs;

    public DAGNode() {
        id = -1;
        domain = new ArrayList<String>();
        parents = new HashSet<Integer>();
        children = new HashSet<Integer>();
        probs = new ArrayList<Double>();
        name = "NoName";
    }


    public DAGNode(int id, String name, ArrayList<String> domain, HashSet<Integer> parents, HashSet<Integer> children, ArrayList<Double> probs) {
        this.id = id;
        this.domain = domain;
        this.parents = parents;
        this.children = children;
        this.probs = probs;
        this.name = name;
    }


    public String toString(){
        StringBuilder newString = new StringBuilder();
        newString.append("NodeId: " + id + "\n");
        newString.append("NodeName: " + name + "\n");
        newString.append("DomainSize: " + domain.size() + "\n");
        newString.append("Domain: ");
        for (String var:
             domain) {
            newString.append(var + " ");
        }
        newString.append("\n");

        newString.append("Parents Size: " + parents.size() + "\n");
        newString.append("Parents: ");

        for (Integer parent:
                parents) {
            newString.append(parent + " ");
        }
        newString.append("\n");

        newString.append("Children Size: " + children.size() + "\n");
        newString.append("Children: ");
        for (Integer child:
                children) {
            newString.append(child + " ");
        }
        newString.append("\n");

        newString.append("Probs Size: " + probs.size() + "\n");
        newString.append("Probs: ");
        for (Double prob:
                probs) {
            newString.append(prob + " ");
        }
        newString.append("\n");
        return newString.toString();
    }

    public void addParent(Integer toAdd) {
        this.parents.add(toAdd);
    }

    public void addChild(Integer toAdd) {
        this.children.add(toAdd);
    }

    public void setDomain(ArrayList<String> domain) {
        if (domain == null)
            return;
        this.domain = domain;
    }

    public void setParents(HashSet<Integer> parents) {
        this.parents = parents;
    }

    public void setChildren(HashSet<Integer> children) {
        this.children = children;
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

    public HashSet<Integer> getParents() {
        return this.parents;
    }

    public HashSet<Integer> getChildren() {
        return this.children;
    }

    public ArrayList<Double> getProbs() {
        return this.probs;
    }

    public int getId() {
        return this.id;
    }

    public boolean removeChild(Integer toRemove) {
        return this.children.remove(toRemove);
    }
}
