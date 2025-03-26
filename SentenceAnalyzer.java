
/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * This file is used for analysis of the Markov chain
 * mainly in the context of using words as states.
 * The methods are mostly designed around trying
 * to predict sequences of states.
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

public class SentenceAnalyzer {

    //uses a markov chain as the subject of analysis
    public MarkovChain mc;
    static final double EPSILON = 1e-10;

    //acts as a limit to how large a sentence can be
    public int largestSentencelength;

    //used as the comparator for the priority queue
    //in implementation of dijkstra's algorithm. 
    //Checks the path cost value of the tuple object,
    //prioritizing higher cost
    Comparator<tuple> weightcomparison = (t1, t2) -> {
        if (t1.totalpathcost > t2.totalpathcost) {
            return -1;
        } else {
            return 1;
        }

    };

    //tells the analyzer which states are sentence enders
    //and sentence pausers.
    int punctuationCount;
    int sentencePauseIndex;

    //the binding tables, telling us what state means what
    public HashMap<String, Integer> bindings;
    public HashMap<Integer, String> bindings2;

    /**
     * Constructor of the SentenceAnalyzer object, this compiles all the data
     * from the markov chain and word information.
     */
    SentenceAnalyzer(MarkovChain mc, HashMap<String, Integer> bindings, HashMap<Integer, String> bindings2,
            int largestSentencelength, int punctuationCount, int sentencePauseIndex) {
        this.largestSentencelength = largestSentencelength;
        this.mc = mc;
        this.bindings = bindings;
        this.bindings2 = bindings2;
        this.punctuationCount = punctuationCount;
        this.sentencePauseIndex = sentencePauseIndex;
    }

    /**
     * simple method printing the index and the word associated with it
     */
    public void indextostringbinding() {
        System.out.println("Binding database, Integer to String");
        System.out.println();
        for (Map.Entry<Integer, String> entry : bindings2.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * simple method printing the word and the index associated with it
     */
    public void stringtoindexbinding() {
        System.out.println("Binding database, String to Integer");
        System.out.println();
        for (Map.Entry<String, Integer> entry : bindings.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * linear scan that checks the highest probability transition in the matrix,
     * starting from the row inserted as the parameter, it then prints the state
     * after getting it from the binding table
     *
     * @param lastword the state we are reading from
     */
    public void predictNextWord(int lastword) {
        int likliest = 0;
        double likliestrobability = -1;
        for (int i = 0; i < mc.matrix.size(); i++) {
            if (mc.matrix.get(lastword).get(i) > likliestrobability) {
                likliest = i;
                likliestrobability = mc.matrix.get(lastword).get(i);

            }
        }
        if (Math.abs(likliestrobability) < EPSILON) {
            System.out.println("We don't have the data to determine the next word");
            return;
        }
        System.out.println(bindings2.get(likliest));
        System.out.println();
    }

    /**
     * Uses a modifed version of dijkstra's using log as a means of numerical
     * stability to calculate the highest probability pathway using N words, if
     * there isn't enough data or the N is too large, it will simply tell the
     * user that it couldn't predict that many words, and it will sy how many it
     * could. Punctuation does not count as words.
     *
     * @param N the amount of words to predict
     * @param lastword word we want to start predicting N words from
     */
    public void predictnextNwords(int N, int lastword) {

        PriorityQueue<tuple> pq = new PriorityQueue<>(weightcomparison);
        HashSet<Integer> discoveredvertices = new HashSet();
        HashMap<Integer, Double> shortestpaths = new HashMap();
        HashMap<Integer, Integer> parents = new HashMap();

        tuple start = new tuple(lastword, 0.0, 0);
        pq.add(start);
        int lastnode = -1;
        int longestpath = 0;

        while (!pq.isEmpty()) {
            tuple currvert = pq.poll();

            //if we have an N length path, since we are popping it from the queue, it is
            //the best one, so from here we finish
            if (currvert.currpathlength == N) {
                lastnode = currvert.currnode;
                break;
            }
            //This is for failure to reach N length path
            if (currvert.currpathlength > longestpath) {
                longestpath = currvert.currpathlength;
            }

            //if vertex not discovered and the currentpathlength is less than N, then we
            //do path checking
            if (!discoveredvertices.contains(currvert.currnode) && currvert.currpathlength < N) {
                discoveredvertices.add(currvert.currnode);
                for (int i = 0; i < mc.matrix.size(); i++) {
                    //checks to see that vertex wasn't visited nor is the entry around 0
                    if (!discoveredvertices.contains(i) && mc.matrix.get(currvert.currnode).get(i) > EPSILON) {
                        //if shortest path map doesn't have an entry for this node, just put it in it's the 
                        //best one we have since it's the only one we have so far
                        if (!shortestpaths.containsKey(i)) {
                            shortestpaths.put(i, (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i))));

                            //doesn't add to path length if this is a punctuation 
                            if (i < punctuationCount) {
                                pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength));
                            } else {
                                pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength + 1));
                            }
                            parents.put(i, currvert.currnode);
                        } //otherwise do some path comparison
                        else if (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i)) > shortestpaths.get(i)) {
                            shortestpaths.put(i, (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i))));
                            if (i < punctuationCount) {
                                pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength));
                            } else {
                                pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength + 1));
                            }
                            parents.put(i, currvert.currnode);

                        }
                    }
                }

            }
        }
        //if the lastnode is -1, meaning that we couldn't find a path of length N, we tell the user
        if (lastnode == -1) {
            System.out.println("We could not find a good prediction of the next " + N + " words.");
            System.out.println("We could only estimate the next " + longestpath + " words.");
            System.out.println("Autocompletion failed");
            return;
        }

        //using the prev pointers to go up the path and write down the
        //path taken, or the words used and writes them all down
        Stack<Integer> path = new Stack();

        while (lastnode != lastword) {
            path.push(lastnode);
            lastnode = parents.get(lastnode);
        }
        while (!path.isEmpty()) {
            if (path.peek() < punctuationCount) {
                System.out.print(bindings2.get(path.pop()));
            } else {
                System.out.print(" " + bindings2.get(path.pop()));
            }

        }

    }

    /**
     * Extremely similar to the predictnextNwords() method, the only difference
     * is that the early exit condition is if a sentence is completed
     *
     * @param lastword the last word in the incomplete sentence
     * @param sizeofcurrsentence the amount of words the sentence already has
     */
    public void AutocompleteSentence(int lastword, int sizeofcurrsentence) {

        PriorityQueue<tuple> pq = new PriorityQueue<>(weightcomparison);
        HashSet<Integer> discoveredvertices = new HashSet();
        HashMap<Integer, Double> shortestpaths = new HashMap();
        HashMap<Integer, Integer> parents = new HashMap();

        tuple start = new tuple(lastword, 0.0, sizeofcurrsentence);
        pq.add(start);
        int lastnode = -1;

        while (!pq.isEmpty()) {
            tuple currvert = pq.poll();
            //if the currnode is a sentence ender, we are done
            if (currvert.currnode < sentencePauseIndex) {
                lastnode = currvert.currnode;
                break;
            }

            //largest sentence length + the amount of punctuation that isn't sentence enders is the max sentence length allowed
            if (!discoveredvertices.contains(currvert.currnode) && currvert.currpathlength < largestSentencelength + (punctuationCount - sentencePauseIndex)) {
                discoveredvertices.add(currvert.currnode);
                for (int i = 0; i < mc.matrix.size(); i++) {
                    if (mc.matrix.get(currvert.currnode).get(i) > EPSILON && !discoveredvertices.contains(i)) {
                        if (!shortestpaths.containsKey(i)) {
                            shortestpaths.put(i, (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i))));
                            pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength + 1));
                            parents.put(i, currvert.currnode);
                        } else if (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i)) > shortestpaths.get(i)) {
                            shortestpaths.put(i, (currvert.totalpathcost + Math.log(mc.matrix.get(currvert.currnode).get(i))));
                            pq.add(new tuple(i, shortestpaths.get(i), currvert.currpathlength + 1));
                            parents.put(i, currvert.currnode);

                        }
                    }
                }

            }

        }
        if (lastnode == -1) {
            System.out.println("Autocompletion failed, couldn't complete sentence");
            return;
        }

        //rebuilding path 
        Stack<Integer> path = new Stack();

        while (lastnode != lastword) {
            path.push(lastnode);
            lastnode = parents.get(lastnode);
        }
        while (!path.isEmpty()) {
            if (path.peek() < punctuationCount) {
                System.out.print(bindings2.get(path.pop()));
            } else {
                System.out.print(" " + bindings2.get(path.pop()));
            }
        }

    }

}
