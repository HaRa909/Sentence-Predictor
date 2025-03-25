# Sentence-Predictor
Basic word and sentence prediction program designed to read off a text file the user specifies and performs some basic analysis on it. The main focus of this project is to analyze this information in the context of Markov chains. Matrices in this program are treated as arraylists of arraylists with doubles as the elements of them. The following descriptions will contain summaries of the 4 most important files and their purposes.

# Matrix.java
A class used for static methods, them designed to obtain information from Matrices or do matrix math. The main purpose for this file is to implement QR algorithm, using factorization via Householder reflectors as a means of obtaining eigenvalues and eigenvectors in a numerically stable manner. Many of the methods and functions in this file are designed to best balance optimization through combination of many steps of matrix math and numerical stability in matrix analysis.

**QR Alrogithm** - A simple and numerically stable way of obtaining real eigenvalues and the eigenvectors associated with them.

**QR Decompisition via Householder Reflector** - A complicated but numerically stable way of obtaining the QR decomposition of a matrix.

# MarkovChain.java
A class used for the MarkovChain object, it heavily relies upon `Matrix.java` to check for the periodicity and eigenvectors of the matrix. 

**Periodicity** - Periodicity is a term used in the context of Markov chains as the amount of hops it takes for a state in a graph to return to itself. If any state is periodic, meaning there is a pattern in how many hops it takes to return to itself, then the graph is periodic. If there isn't a pattern in the hop count for any of the states to return to themselves, this means the Markov chain is aperiodic. 

**Eigenvectors** - In a Markov chain, the eigenvectors that are most important are the ones associated with eigenvalue 1, this is because this is called a stationary distribution of the graph, which tells you the long term behavior of the Markov chain. It will tell the one analyzing it about the likliest state to be on in the graph regardless of the starting point and after essentially an infinite amount of time. 

Two important parts local to the `MarkovChain.java` file itself are the checks for irreducibility and the state removing method.

**Irreducibility** - Irreducibility is an analog to strongly connected components on a graph, or a way of describing reachability from a node to any other node. If node `A` can reach  node `B`, and node `B` can reach node `A`, `A` and `B` are in the same strongly connected component, or the same communication class in Markov chain terminology. An irreducible Markov chain is one where every single state is in the same communication class. Tarjan's SCC algorithm is how these strongly connected components are determined.

**State removal** - A way to analyze the Markov chain's behavior if certain states are turned transient. This can be important in analyzing how the Markov chain's stationary distribution changes if certain states are turned transient. The method of turning states transient in this program are simply by removing ingress states but maintaining egress states, meaning that state will only ever be exited, and in the stationary distribution this will be seen as a `0%` probability of being in this state.

# SentenceAnalyzer.java
A class used to perform analysis on the Markov chain. It predicts the next word, next N words, or tries to complete a sentence. The program uses a modified version of Dijkstra's shortest weighted path algorithm on a probability graph. Since the probabiltiies must be multiplied against each other whenever a new edge is traversed, we use `Log(probabilityA * probabilityB) = Log(probabilityA) + Log(probabilityB)` as this is a more numerically stable way. This results in negative Dijkstra's since probabilities are all less than 1, however, since we are maximizing positive values, there is no issue that comes from this.

# Main.java
A class used to combine everything together. It will read the file containing the sentences, build the frequency matrix and have it converted into a Markov chain, it handles all the file writing and user input/output. Upon reading the file all words are converted to lowercase since capitalization doesn't tell anything too important. There is a basic CLI implemented with the following options:
```
0 : quit
1 : predict next word
2 : predict next N words
3 : sentence autocomplete
4 : write stationary distribution
5 : enter temp matrix menu
6 : write matrix
7 : check reducability and periodicity of chain
8 : print binding table
```
As seen with the commands above, there is temporary matrix menu command, which has the following:
```
0 : return to Main menu
1 : predict next word
2 : predict next N words
3 : sentence autocomplete
4 : obtain stationary distribution
5 : print matrix
6 : check reducability and periodicity of chain
7 : smooth matrix
8 : remove states
9 : print index to word binding table
```
All of these commands interact with the `MarkovChain.java` or `SentenceAnalyzer.java` in a way that prevents the program from crashing. There are also some user changable attributes in this file, which will be listed below.

**`Punctuation()`** - A method used to set what the user desires their punctuation to be. The two punctuations this program is designed around are sentence ending and sentence pausing punctuation.
```
public static void Punctuation()
    {
        String[] punctuation = {".","!","?",","};
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < punctuation.length; i++)
        {
            sb.append(punctuation[i]);
            bindings.put(punctuation[i],i);
            bindings2.put(i, punctuation[i]);
        }
        punctuationCount = punctuation.length;
        punctuations = sb.toString();

        //from the third index onwards there are sentence pausers here, since comma pauses
        sentencePauseIndex = 3;
    }
```
`punctuation`is a string Array used to tell the program which strings should be treated as punctuation, this is because punctuation is treated differently than other strings, as they are important to predicting sentences and not adding to sentence length since they aren't words. The user must put the punctuation used to end sentences before punctuation used to pause sentences, and then mark the index where the sentence pausers start from, this is so the program knows that these punctuation marks don't end a sentence.

**`static boolean labelMatrixIndices`** - For the print matrix command, if set to true, it will display the row and column numbers on the matrix, starting from zero, this is useful if the matrix is extremely large and it is difficult to keep count of the columns. To see what words and states these indices are associated with, use `8` to print the binding table, telling the user which index is associated with which words/states.

**`static DecimalFormat decimalFormat`** - a variable used to determine the amount of precision the user wants in their outputs for stationary distributions and matrices.

**`static int totalSpaces`** - a variable associated with decimalformat used to set spacing when the `labelMatrixIndices` option is set to true, it is meant to center the index description within the amount of spaces allocated. It is recommended that this is equal to the size of the string used for the `Decimalformat` object's instantiation plus 1.

**`static String toAnalyze`** - variable used to set the file the data is being read from, make sure that this file is in the same directory as all the other java files for this program.





