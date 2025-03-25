# Sentence-Predictor
Basic word and sentence prediction program designed to read off a text file the user specifies and perform some basic analysis on it. The main focus of this project is to analyze this information in the context of Markov chains. Matrices in this program are treated as arraylists of arraylists with doubles as the elements of them. The following descriptions will contain summaries of the 4 most important files and their purposes.

# Matrix.java
A class used for static methods, them designed to obtain information from Matrices or do matrix math. The main purpose for this file is to implement QR algorithm, using factorization via Householder reflectors as a means of obtaining eigenvalues and eigenvectors in a numerically stable manner. Many of the methods and functions in this file are designed to best balance optimization through combination of many steps of matrix math and numerical stability in matrix analysis.

# MarkovChain.java
A class used for the MarkovChain object, it heavily relies upon `Matrix.java` to check for the periodicity and eigenvectors of the matrix. 

**Periodicity** - Periodicity is a term used in the context of Markov chains as the amount of hops it takes for a state in a graph to return to itself. If any state is periodic, meaning there is a pattern in how many hops it takes to return to itself, then the graph is periodic. If there isn't a pattern in the hop count for any of the states to return to themselves, this means the Markov chain is aperiodic. 

**Importance of Eigenvectors** - In a Markov chain, the eigenvectors that are most important are the ones associated with eigenvalue 1, this is because this is called a stationary distribution of the graph, which tells you the long term behavior of the Markov chain. It will tell the one analyzing it about the likliest state to be on in the graph regardless of the starting point and after essentially an infinite amount of time. 

The 

