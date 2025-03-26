
/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * This file is used to run certain Markov
 * chain operations. It turns a matrix into a
 * Markov chain and has some functions to analyze it.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class MarkovChain {

    public ArrayList<ArrayList<Double>> matrix;
    static final double EPSILON = 1e-10;
    static final double TOLERANCE = 1e-3;

    //used for SCC algorithm as a global index variable
    int index;

    //the stationary distributions we have of the chain
    public ArrayList<ArrayList<Double>> Stationarydists;

    //informationr egarding periodicity and irreducibiliy
    boolean irreducible = false;
    boolean aperiodic = false;

    //tells calculation status of certain important attributes of Markov Chain
    boolean calculatedirredicibility = false;
    boolean calculatedaperiodicity = false;
    boolean calculatedstationaries = false;

    //two ways of checking SCCs via Tarjan's algorithm
    int scccount = -1;
    public ArrayList<ArrayList<Integer>> sccs;

    /**
     * constructor for the markov chain. it will turn the matrix into a
     * transition matrix and set up other things for the object.
     *
     * @param frequencies is the matrix we want to turn into a chain
     */
    public MarkovChain(ArrayList<ArrayList<Double>> frequencies) {
        Stationarydists = new ArrayList();

        matrix = frequencies;

        for (int i = 0; i < frequencies.size(); i++) {
            int sum = 0;
            for (int j = 0; j < frequencies.size(); j++) {
                sum += frequencies.get(i).get(j);
            }

            for (int j = 0; j < frequencies.size(); j++) {
                double freq = frequencies.get(i).get(j);
                if (freq != 0.0) {
                    matrix.get(i).set(j, freq / sum);

                } else {
                    matrix.get(i).set(j, 0.0);
                }

            }

            if (sum == 0) {
                matrix.get(i).set(i, 1.0);
            }

        }

    }

    /**
     * secondary constructor to be used if the user already know their matrix is
     * a Markov chain.
     */
    public MarkovChain() {
        Stationarydists = new ArrayList();
    }

    /**
     * This is to normalize a Markov chain if there are certain edits done to it
     * that mess up its stochastic properties.
     */
    public void renormalize() {
        for (int i = 0; i < matrix.size(); i++) {
            double sum = 0;
            for (int j = 0; j < matrix.size(); j++) {
                sum += matrix.get(i).get(j);
            }

            for (int j = 0; j < matrix.size(); j++) {
                double freq = matrix.get(i).get(j);
                if (freq > EPSILON) {
                    matrix.get(i).set(j, freq / sum);

                } else {
                    matrix.get(i).set(j, 0.0);
                }

            }

            if (sum == 0) {
                matrix.get(i).set(i, 1.0);
            }

        }

    }

    /**
     * Turns the states input into transient states. This is for analyzing the
     * importance of certain states on the matrix.
     *
     * @param statestoremove is a hashset containing all the states we want to
     * remove
     */
    public void removestates(HashSet<Integer> statestoremove) {
        //remove the influence of states put in the list
        //we remove what goes in and what goes out of this state, isolating it entirely

        for (Integer remove : statestoremove) {
            
            //check every row that goes into the statetoremove
            //this state will become transient now that there is nothing going into it
            for (int i = 0; i < matrix.size(); i++) {
                double todivide = matrix.get(i).get(remove);
                //weird edge case, if it relies on the state we are removing entirely, we set it to itself as an absorbing state
                if (todivide == 1) {
                    matrix.get(i).set(remove, 0.0);
                    matrix.get(i).set(i, 1.0);
                } //otherwise if it actually goes into the state, then we will look at it
                else if (todivide > EPSILON) {
                    matrix.get(i).set(remove, 0.0);
                    int divisor = 0;
                    for (int j = 0; j < matrix.size(); j++) {
                        if (matrix.get(i).get(j) > EPSILON) {
                            divisor++;
                        }
                    }
                    for (int j = 0; j < matrix.size(); j++) {
                        if (matrix.get(i).get(j) > EPSILON) {
                            matrix.get(i).set(j, matrix.get(i).get(j) + (todivide / divisor));
                        }
                    }
                }

            }
        }

    }

    /**
     * A method that will check irreducibility and periodicity of the matrix, it
     * will either calculate for it if it has to, or it will reference a
     * calculation already made.
     */
    public void checkIrreducibilityAndPeriodicity() {
        if (calculatedirredicibility == false) {
            Checkforirreducibility();
        }
        if (calculatedaperiodicity == false) {
            aperiodicitycheck();
        }

        String irred = "reducible";
        String prdc = "periodic";
        if (irreducible) {
            irred = "irreducible";
        }
        if (aperiodic) {
            prdc = "aperiodic";
        }

        System.out.println("The chain is " + irred + " and " + prdc + ".");

    }

    /**
     * Finds the eigenvectors associated with eigenvalue 1 and lists those as
     * the stationarydistributions. We will figure out if these are limiting
     * distributions or not via the checkIrreducibilityAndPeriodicity(), since
     * this tells us if the matrix is Ergodic or not.
     */
    public void calculate_Alllimitingdists() {
        calculatedstationaries = true;

        //most important step, the matrix we are running QR algorithm on must be transposed
        //to obtain the stationary distribution
        ArrayList<ArrayList<Double>> matrixTranspose = Matrix.transpose(matrix);

        matrixpair eigenvectorsandvalues = Matrix.QRalgorithm(matrixTranspose);

        ArrayList<ArrayList<Double>> eigenvalues = eigenvectorsandvalues.m1;
        ArrayList<ArrayList<Double>> eigenvectors = eigenvectorsandvalues.m2;

        for (int i = 0; i < eigenvalues.size(); i++) {

            if (Math.abs(1 - eigenvalues.get(i).get(i)) < TOLERANCE) {
                
                boolean Negatives = false;
                //we are adding to this as a column vector
                ArrayList<Double> eigenvector = new ArrayList<>(eigenvectors.size());
                for (int j = 0; j < eigenvectors.size(); j++) {
                    //we go down the rows, not the columns since it is in here as a row vector

                    //there is one more thing to check if we are at this point, we must make sure that
                    //there are no negative entries that are higher than our zero threshold, otherwise
                    //it makes no sense since there are no negative probabilities

                    if(eigenvectors.get(j).get(i) < 0 && eigenvectors.get(j).get(i) < (EPSILON * -1))
                    {
                        Negatives = true;
                        break;
                    }

                    eigenvector.add(eigenvectors.get(j).get(i));
                }

                if(Negatives == false)
                {
                    Stationarydists.add(eigenvector);
                }
            }
        }
        normalizationofstationaries();
        System.out.println("Calculations are complete, found the stationary distribution(s)");

    }

    /**
     * Checks to see if the matrix is irreducible or not by running Tarjan's SCC
     * algorithm on it. If there is more than one SCC, this is reducible
     */
    public void Checkforirreducibility() {
        scccount++;
        calculatedirredicibility = true;

        if (calculatedstationaries) {
            if (Stationarydists.size() > 1) {
                irreducible = false;
                return;
            }
        }
        sccalgorithm();
        if (sccs.size() == 1) {
            irreducible = true;
        }
    }

    /**
     * Instantiates the algorithm by checking each vertex's connectability to
     * other vertices
     */
    public void sccalgorithm() {

        sccs = new ArrayList<>();
        ArrayList<vertex> V = new ArrayList(matrix.size());
        Stack<vertex> S = new Stack();
        index = 0;
        for (int i = 0; i < matrix.size(); i++) {
            V.add(new vertex(i));
        }

        for (int i = 0; i < V.size(); i++) {
            if (V.get(i).index() == -1) {

                strongconnect(i, V, index, S);
            }
        }

    }

    /**
     * the recursive method that checks reachability of different vertices
     * through checking index and comparing it with lowlink, or the smallest
     * index reachable by the vertex
     *
     * @param vertex the node we are checking reachability for
     * @param V the vertex list
     * @param index the current node we are looking at's order of being reached
     * @param S the stack of vertices that were seen but not necessarily
     * processed
     */
    public void strongconnect(int vertex, ArrayList<vertex> V, int index, Stack<vertex> S) {
        V.get(vertex).setindex(index);
        V.get(vertex).setlowlink(index);
        index++;
        S.push(V.get(vertex));
        V.get(vertex).setonstack(true);

        for (int j = 0; j < matrix.size(); j++) {
            if (Math.abs(matrix.get(vertex).get(j)) > EPSILON) {

                if (V.get(j).index() == -1) {
                    strongconnect(j, V, index, S);
                    V.get(vertex).setlowlink(Math.min(V.get(vertex).lowlink(), V.get(j).lowlink()));
                } else if (V.get(j).onstack()) {
                    V.get(vertex).setlowlink(Math.min(V.get(vertex).lowlink(), V.get(j).index()));
                }
            }
        }

        if (V.get(vertex).lowlink() == V.get(vertex).index()) {
            int wname;
            ArrayList<Integer> newscc = new ArrayList();
            do {
                vertex w = S.pop();
                w.setonstack(false);
                wname = w.vertname();
                newscc.add(wname);

            } while (vertex != wname);

            sccs.add(newscc);
            scccount++;

        }
    }

    /**
     * checks aperiodicity by multiplying the matrix against itself unttil the
     * return hopes from a state to itself are received for every state. The
     * return hops tell about the way the graph will cycle when multiplied
     * against itself, if the gcd is 1, that means we don't have a period,
     * otherwise we do.
     */
    public void aperiodicitycheck() {

        ArrayList<ArrayList<Double>> scratch1 = new ArrayList(matrix.size());
        ArrayList<ArrayList<Double>> scratch2 = new ArrayList(matrix.size());

        for (int i = 0; i < matrix.size(); i++) {
            ArrayList<Double> r1 = new ArrayList(matrix.size());
            ArrayList<Double> r2 = new ArrayList(matrix.size());
            for (int j = 0; j < matrix.size(); j++) {

                r1.add(matrix.get(i).get(j));
                r2.add(0.0);
            }
            scratch1.add(r1);
            scratch2.add(r2);
        }

        ArrayList<Integer> representatives = new ArrayList(sccs.size());

        Stack<Integer> toremove = new Stack();

        HashMap<Integer, Integer> bindingdatabase = new HashMap(sccs.size());

        for (int i = 0; i < sccs.size(); i++) {
            representatives.add(sccs.get(i).get(0));
        }

        int powerofmatrix = 0;

        while (!representatives.isEmpty()) {

            powerofmatrix++;

            for (int i = 0; i < representatives.size(); i++) {

                if (scratch1.get(representatives.get(i)).get(representatives.get(i)) > EPSILON) {

                    if (!bindingdatabase.containsKey(representatives.get(i))) {
                        bindingdatabase.put(representatives.get(i), powerofmatrix);
                    } else {
                        if (gcd(bindingdatabase.get(representatives.get(i)), powerofmatrix) > 1) {

                            aperiodic = false;
                            return;
                        } else {

                            toremove.push(representatives.get(i));
                        }

                    }

                }

            }

            while (!toremove.isEmpty()) {

                bindingdatabase.remove(toremove.peek());
                representatives.remove(toremove.peek());
                toremove.pop();
            }

            ArrayList<ArrayList<Double>> temp = scratch2;
            scratch2 = scratch1;
            scratch1 = temp;
            scratch1 = Matrix.multiplication(matrix, scratch2, scratch1);

        }

        aperiodic = true;

    }

    /**
     * turns the eigenvectors into vectors such that when you add up all the
     * elements, you get 1. This is so it is a proper stationary distribution
     */
    public void normalizationofstationaries() {
        for (ArrayList<Double> vector : Stationarydists) {
            double sum = 0.0;
            for (Double val : vector) {
                sum += val;
            }

            for (int i = 0; i < vector.size(); i++) {
                vector.set(i, vector.get(i) / sum);
            }
        }

    }

    /**
     * The euclidean GCD algorithm used by the aperiodicitycheck() method to
     * check each state's period.
     */
    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

}
