import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-06
 * Last Updated on: 2016-11-06
 * Filename: TestDsep
 * Description: Checks if D-separation <X|Z|Y>G holds in a DAG of a Bayesian network S = (V,G,P)
 *              and X, Y, Z are disjoint subsets of variables in V
 *
 */
public class TestDsep {
    public static void main(String [] args) {
        ArrayList<DAGNode> bn;
        ArrayList<String> x = new ArrayList<>();
        ArrayList<String> y = new ArrayList<>();
        ArrayList<String> z = new ArrayList<>();

        if (args.length != 2) {
            System.out.println("Specify the path to Bayesian network and XYZ files only.");
            return;
        }
        try {
            FileReader frBN = new FileReader(args[0]);
            FileReader frXYZ = new FileReader(args[1]);
            BufferedReader brBN = new BufferedReader(frBN);
            BufferedReader brXYZ = new BufferedReader(frXYZ);
            String line;
            String [] toks;
            String delim = " +";
            int nodeCounter = 0;
            // Read the bayesian network

            // Number of nodes
            line = brBN.readLine();
            toks = line.split(" ");
            int numNodes = Integer.parseInt(toks[0]);
            bn = new ArrayList<>(numNodes);
            line = brBN.readLine();

            // Read all the nodes
            while (line != null & nodeCounter < numNodes) {
                // DAGNode variables
                int domainSize;
                int numParents;
                int numChildren;
                int numProbs;
                int id ;
                String name = "";
                ArrayList<String> domain;
                HashSet<Integer> children;
                HashSet<Integer> parents ;
                ArrayList<Double> probs;

                // domainSize nodeId
                line = brBN.readLine();
                toks = line.split(delim);
                domainSize = Integer.parseInt(toks[0]);
                id = Integer.parseInt(toks[1].split("_")[4]);

                // nodeName
                line = brBN.readLine();
                toks = line.split(delim);
                name = toks[0];

                // Domain variableNames
                domain = new ArrayList<>(domainSize);
                for ( int i = 0; i < domainSize; i++) {
                    line = brBN.readLine();
                    toks = line.split(delim);
                    domain.add(toks[0]);
                }

                // Children
                line = brBN.readLine();
                toks = line.split(delim);
                numChildren = Integer.parseInt(toks[0]);
                children = new HashSet<>(numChildren);
                for (int i = 0; i < numChildren; i++) {
                    children.add(Integer.parseInt(toks[i+1]));
                }

                // Parents
                line = brBN.readLine();
                toks = line.split(delim);
                numParents = Integer.parseInt(toks[0]);
                parents = new HashSet<>(numParents);
                for (int i = 0; i < numParents; i++) {
                    parents.add(Integer.parseInt(toks[i+1]));
                }

                brBN.readLine(); // Coordinates

                // Probabilities
                line = brBN.readLine();
                toks = line.split(delim);
                numProbs = Integer.parseInt(toks[0]);
                probs = new ArrayList<>(numProbs);
                line = brBN.readLine();
                toks = line.split(delim);
                int i = 0;
                while (toks.length > 0 && !toks[0].equals("")) {
                    for(String token : toks){
                        probs.add(Double.parseDouble(token));
                        i++;
                    }

                    line = brBN.readLine();
                    if (line != null)
                        toks = line.split(delim);
                    else
                        break;
                }


                bn.add(new DAGNode(id, name, domain, parents, children, probs));
                nodeCounter++;
            }

            line = brXYZ.readLine();
            if (line != null)
                x.addAll(Arrays.asList(line.split(delim)));

            line = brXYZ.readLine();
            if (line != null)
                y.addAll(Arrays.asList(line.split(delim)));

            line = brXYZ.readLine();
            if (line != null)
                z.addAll(Arrays.asList(line.split(delim)));

            System.out.println("Bayesian Network variable names: ");
            for (DAGNode dagNode : bn) {
//                System.out.println("\tNode " + dagNode.getId() + ":" + dagNode.getName());
                System.out.println(dagNode);
            }

            System.out.println("XYZ File variable names: ");
            System.out.println("\tSET X: ");
            System.out.print("\t\t");
            for (String var : x) {
                System.out.print(var + " ");
            }
            System.out.println();
            System.out.println("\tSET Y: ");
            System.out.print("\t\t");
            for (String var : y) {
                System.out.print(var + " ");
            }
            System.out.println();
            System.out.println("\tSET Z: ");
            System.out.print("\t\t");
            for (String var : z) {
                System.out.print(var + " ");
            }

            if (z.size() == 0)
                System.out.println("{}");

            System.out.println();
            System.out.println("<X|Z|Y>G : " + dSeparated(bn, x, z, y));

        } catch (Exception error){
            error.printStackTrace();
        }
    }

    // Create Moral Graph
    // Let G be a DAG. For each child in G,
    // connect its parents pairwise and drop directions of links.
    public static ArrayList<UNode> genMoralGraph (ArrayList<DAGNode> graph) {
        ArrayList<UNode> moralG = new ArrayList<UNode>();
        // Convert a DAG to Undirected graph
        for(DAGNode node : graph) {
            moralG.add(new UNode(node));
        }

        // Go through all the nodes
        for (DAGNode node : graph) {
            // Go through all of a nodes parents
            for (Integer parentId : node.getParents()) {
                UNode parent = moralG.get(parentId);
                // Create a link to the other parents
                for (Integer siblingId : node.getParents()) {
                    if (siblingId != parentId) {
                        parent.addPath(siblingId);
                    }
                }
            }
        }
        return moralG;
    }

    // Finds the ancestral subgraph of w in g
    public static ArrayList<DAGNode> getAncestralSubGraph(ArrayList<DAGNode> g, ArrayList<DAGNode> w){
        ArrayList<DAGNode> ag = new ArrayList<DAGNode>(g);
        boolean done = false;
        while (!done) {
            done = true;
            for (DAGNode node : g){
                if ((node.getChildren().size() == 0) && !w.contains(node)) {
                    // Delete non-ancestor node links in parents
                    for (Integer parentId : node.getParents()) {
                        DAGNode parent = ag.get(parentId);
                        parent.removeChild(node.getId());
                    }
                    done =false;
                }
            }
        }
        return ag;
    }

    // Checks u separation
    public static boolean uSeparated(ArrayList<UNode> g, ArrayList<String> x, ArrayList<String> z, ArrayList<String> y) {
        ArrayList<UNode> gClone = new ArrayList<>(g);
        // delete Z and links incident to Z from G;
        for(String varName : z){
            Iterator it = gClone.iterator();
            while ( it.hasNext() ) {
                UNode node = (UNode) it.next();
                // Remove links to node in Z and remove the node
                if (node.getName().equals(varName)) {
                    for (Integer path : node.getPaths()) {
                        gClone.get(path).removePath(node.getId());
                    }
                    it.remove();
                }
            }

        }

        //add nodes adjacent to X and X+ to X+ recursively;
        HashSet<Integer> xPaths = new HashSet<>();
        for ( UNode node : gClone) {
            if (x.contains(node.getName()))
                xPaths.addAll(node.getPaths());
        }

        Iterator it = xPaths.iterator();
        while (it.hasNext()) {
            Integer nodeId =  (Integer)it.next();
            UNode node = gClone.get(nodeId);

            for ( Integer path: node.getPaths()) {
                if (!xPaths.contains(path)) {
                    xPaths.add(path);
                    it = xPaths.iterator();
                }
            }
        }


        // Check if new X intersects with Y
        for (String varName: y) {
            if (xPaths.contains(varName))
                return false;
        }
        return true;
    }

    // Checks d-separation
    public static boolean dSeparated(ArrayList<DAGNode> g, ArrayList<String> x, ArrayList<String> z, ArrayList<String> y) {
        ArrayList<DAGNode> union = new ArrayList<>();
        HashSet<String> allVarNames = new HashSet<>();
        allVarNames.addAll(x);
        allVarNames.addAll(y);
        allVarNames.addAll(z);
        for (DAGNode node:
             g) {
            if (allVarNames.contains(node.getName())) {
                union.add(node);
            }
        }

        ArrayList<DAGNode> ancestralGraph = getAncestralSubGraph(g, union) ;
        ArrayList<UNode> moralGraphAG = genMoralGraph(ancestralGraph);
        return uSeparated(moralGraphAG, x, z, y);
    }
}
