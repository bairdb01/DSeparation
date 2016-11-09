import java.io.*;
import java.util.*;

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
        HashMap<Integer, DAGNode> bn;
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
            bn = new HashMap<>(numNodes);
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


                bn.put(id, new DAGNode(id, name, domain, parents, children, probs));
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
            Iterator it = bn.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DAGNode dagNode = (DAGNode) pair.getValue();
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

            if (z.size() == 1 && z.contains("{}")) {
                return;
            }
            System.out.println();
            System.out.println("<X|Z|Y>G : " + dSeparated(bn, x, z, y));

        } catch (Exception error){
            error.printStackTrace();
        }
    }

    // Create Moral Graph
    // Let G be a DAG. For each child in G,
    // connect its parents pairwise and drop directions of links.
    public static HashMap<Integer, UNode> genMoralGraph ( HashMap<Integer, DAGNode> graph) {
        System.out.println("Generating Moral graph...");
        HashMap<Integer, UNode> moralG = new HashMap<>();

        // Convert a DAG to Undirected graph
        for (Map.Entry pair : graph.entrySet()) {
            DAGNode node = (DAGNode)pair.getValue();
            moralG.put(node.getId(),new UNode(node));
        }

        // Go through all the nodes
        for (Map.Entry pair : graph.entrySet()) {
            DAGNode node = (DAGNode)pair.getValue();
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
    public static HashMap<Integer, DAGNode> getAncestralSubGraph( HashMap<Integer, DAGNode> graph, ArrayList<DAGNode> w){
        System.out.println("Generating ancestral subgraph...");
        HashMap<Integer, DAGNode> ag = new HashMap<>(graph);

        boolean done = false;
        // Go over each node to see if it belongs in w or it's parents
        while (!done) {
            done = true;
            Iterator it = ag.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                DAGNode node = (DAGNode) pair.getValue();
                if ((node.getChildren().size() == 0) && !w.contains(node)) {
                    // Delete non-ancestor node links in parents
                    for (Integer parentId : node.getParents()) {
                        DAGNode parent = ag.get(parentId);
                        parent.removeChild(node.getId());
                    }
                    it.remove();
                    done =false;
                }
            }
        }
        return ag;
    }

    // Checks u separation
    public static boolean uSeparated( HashMap<Integer, UNode> graph, ArrayList<String> x, ArrayList<String> z, ArrayList<String> y) {
        System.out.println("Checking U-Separation...");
        HashMap<Integer, UNode> gClone = new HashMap<>(graph);

        // delete Z and links incident to Z from G;
        for(String varName : z){
            Iterator it = gClone.entrySet().iterator();
            while ( it.hasNext() ) {
                Map.Entry pair = (Map.Entry) it.next();
                UNode node = (UNode) pair.getValue();
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
        for( Map.Entry pair : gClone.entrySet()) {
            UNode node = (UNode) pair.getValue();
            if (x.contains(node.getName()))
                xPaths.addAll(node.getPaths());
        }

        Iterator it = xPaths.iterator();
        while (it.hasNext()) {
            Integer nodeId =  (Integer)it.next();
            UNode node = gClone.get(nodeId);

            if (!xPaths.containsAll(node.getPaths())) {
                xPaths.addAll(node.getPaths());
                it = xPaths.iterator();
            }
        }

        // Check if new X intersects with Y
        for (String varName: y) {
            for ( Integer nodeId : xPaths )
                if (gClone.get(nodeId).getName().equals(varName))
                    return false;
        }
        return true;
    }

    // Checks d-separation
    public static boolean dSeparated(HashMap<Integer, DAGNode> g, ArrayList<String> x, ArrayList<String> z, ArrayList<String> y) {
        System.out.println("Checking D-separation...");;
        ArrayList<DAGNode> union = new ArrayList<>();
        HashSet<String> allVarNames = new HashSet<>();
        allVarNames.addAll(x);
        allVarNames.addAll(y);
        allVarNames.addAll(z);

        for (Map.Entry pair : g.entrySet()) {
            DAGNode node = (DAGNode) pair.getValue();
            if (allVarNames.contains(node.getName())) {
                union.add(node);
            }
        }

        HashMap<Integer, DAGNode> ancestralGraph = getAncestralSubGraph(g, union) ;
        HashMap<Integer, UNode> moralGraphAG = genMoralGraph(ancestralGraph);
        return uSeparated(moralGraphAG, x, z, y);
    }
}
