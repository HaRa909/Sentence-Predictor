/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * This file runs the text reader
 * and the menu for interacting with
 * the sentenceanalyzer, it will write files
 * and is expandable with more options 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;


public class Main
{

    //word to index bindings
	static HashMap<String,Integer> bindings = new HashMap();
	static HashMap<Integer,String> bindings2 = new HashMap();

    //matrix to write to and temporary matrix that is a duplicate of this one
    static ArrayList<ArrayList<Double>> matrix;
    static ArrayList<ArrayList<Double>> temp;

    static int largestSentencelength = -1;
    
    //amount of unique words
    static int wordIndex;
    
    //amount of puncutation marks
    static int punctuationCount;
    static int sentencePauseIndex = -1;
    static String punctuations = "";

    static SentenceAnalyzer data;
    static MarkovChain Mchain;
    
    

    /**
     * The following variables, including the method Punctuation()
     * will be user modifiable.
     */

    static final double EPSILON = 1e-10;

    //spaces between elements in the matrix, 20 spaces between
    //the beginning of one element and another, recommended for it to be
    //the string input into the decimalformat variable's length + 1
    static int totalSpaces = 20;

    //decimal formatting desired
    static DecimalFormat decimalFormat = new DecimalFormat("0.00000000000000000");  

    //file to read from
    static String toAnalyze = "randtext.txt";
    
    //if you want the printed matrices to have number labels
    //on the columns and rows, set this to true
    static boolean labelMatrixIndices = true;
    
    



    /**
     * Hashmap instantiated with whatever punctuation is desired and their bindings. 
     * 
     * This program is coded  for four of these split the punctuation array into a 
     * section of sentence enders and  sentence pausers. 
     * 
     * Label the first sentencepauser index via sentencePauseIndex variable.
     */
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
    



    public static void main(String[] args)
    {
        Punctuation();
        //wordindex will be the index where we start binding words to numbers
        wordIndex = bindings2.size();
        matrixInit();
        matrixProcessing();

        //turns this frequency matrix into a Markov chain
		Mchain = new MarkovChain(matrix);

        //We will analyze the Markov chain in terms of words
        data = new SentenceAnalyzer(Mchain,bindings,bindings2,largestSentencelength,punctuationCount,sentencePauseIndex);


    
        short choice = -1;
        Scanner sc = new Scanner(System.in);
        String menuText = """
                          The following are valid commands:
                          0 : quit
                          1 : predict next word
                          2 : predict next N words
                          3 : sentence autocomplete
                          4 : write stationary distribution
                          5 : enter temp matrix menu
                          6 : write matrix
                          7 : check reducability and periodicity of chain
                          8 : print binding table
                          """;
        
        System.out.print(menuText);

        do{
            String input = sc.nextLine();
            if(input.equals(""))
            {
                System.out.print(menuText);
            }
            else
            {
                try {
                    choice = Short.parseShort(input);
                    System.out.println();
                    switch(choice)
                    {
                        case 0 -> {}
                        case 1 -> predictNextWord();
                        case 2 -> predictnextNwords();
                        case 3 -> completeSentence();
                        case 4 -> {
                            stationarydistcalculation(Mchain, null, 
                            "Matrix/StationaryDistributions/stationarydistribution");
                            System.out.println("Check the Matrix/StationaryDistributions folder");
                            }
                        case 5 -> tempMatrixMenu();
                        case 6 -> {
                            writemarkovchain(matrix, "Matrix/");
                            System.out.println("Check the Matrix folder");
                        }
                        case 7 -> Mchain.checkIrreducibilityAndPeriodicity();
                        case 8 -> bindingTable();
                        default -> System.out.println("Invalid choice!");
                    }
                } 
                catch (NumberFormatException e) {
                    System.out.println("Not a number!");
                }

            }
        }while(choice != 0);
        System.out.println("Program ended successfully!"); 
    }
    
    /**
     * This is the menu for the temporary matrix option.
     * It creates a duplicate Markov chain the user can do whatever
     * they want with without messing up the original data.
     */
    public static void tempMatrixMenu()
    {
        temp = Matrix.duplicatematrix(matrix);
        MarkovChain MchainDupe = new MarkovChain();
        MchainDupe.matrix = temp;



        System.out.println("Working with copy of Markov chain");
        short choice = -1;
        Scanner sc = new Scanner(System.in);
        String menuText = """
                          The following are valid commands for tempMatrix:
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

                          """;
        
        System.out.print(menuText);
        HashSet<Integer> statestoremove = new HashSet();
        
        do{
            String input = sc.nextLine();
            if(input.equals(""))
            {
                System.out.print(menuText);
            }
            else
            {
                try {
                    choice = Short.parseShort(input);
                    System.out.println();
                    switch(choice)
                    {
                        case 0 -> {}
                        case 1 -> predictNextWord();
                        case 2 -> predictnextNwords();
                        case 3 -> completeSentence();
                        case 4 -> {
                            stationarydistcalculation(MchainDupe, statestoremove, 
                            "TempMatrix/StationaryDistributions/stationarydistribution");
                            System.out.println("Check the TempMatrix/StationaryDistributions folder");
                            }
                        case 5 -> {
                            writemarkovchain(temp, "TempMatrix/");
                            System.out.println("Check the TempMatrix folder");
                        }
                        case 6 -> MchainDupe.checkIrreducibilityAndPeriodicity();
                        case 7 -> smoothing(MchainDupe);
                        case 8 -> RemoveStates(MchainDupe,statestoremove);
                        case 9 -> bindingTable();
                        default -> System.out.println("Invalid choice!");
                    }
                } 
                catch (NumberFormatException e) {
                    System.out.println("Not a number!");
                }

            }
        }while(choice != 0);
        temp = null;
        System.out.println("Successfully left temporary matrix menu"); 
    }


    
    

    /**
     * The matrix is initialized to the amount of unique
     * words in the file, their frequencies are all counted up
     * to save memory we create the array, matrices, hashmap
     * such that the entirety of allocated space is utilized
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void matrixInit()
    {
        try (BufferedReader br = new BufferedReader(new FileReader(toAnalyze))) 
		{
			String line;
            
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                line = line.trim();
                //put the punctuation here that you want to ignore
                String[] words = line.split("[\\s" + punctuations + "]+");

				for(int j = 0; j < words.length; ++j)
				{
                    // Looks at the words to see if we saw them before and if it isn't just a newline
					if(!bindings.containsKey(words[j]) && !words[j].equals(""))
					{
						bindings2.put(wordIndex,words[j]);
                        bindings.put(words[j],wordIndex);
						++wordIndex;
					}
				}
            }

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

        matrix = new ArrayList<>(wordIndex);


		for(int j = 0; j < wordIndex; ++j)
		{
			ArrayList<Double> row = new ArrayList<>(wordIndex);
			for(int k = 0; k < wordIndex; ++k)
			{
				row.add(0.0);
			}
			matrix.add(row);
		}
        
    }

    /**
     * Putting all the information we previously got into the matrix.
     * Words are bound to nodes, the file is then read again
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void matrixProcessing()
    {
        String prev = null;
        int wordsincurrsentence = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(toAnalyze)))
		{
			String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String[] clauses = line.split("(?<=[" + punctuations + "])"); // Split line into clauses
				for(int j = 0; j < clauses.length; ++j)
				{
                    clauses[j] = clauses[j].trim();
                    if(clauses[j].equals(""))
                    {
                        continue;
                    }
                    
                    String lastChar = "" + clauses[j].charAt(clauses[j].length() - 1);
                    boolean lastCharAPunctuation = false;
                    
                    //punctuation count is the index where we are no longer a punctuation mark
                    if(bindings.containsKey(lastChar) && bindings.get(lastChar) < punctuationCount)
                    {
                        clauses[j] = clauses[j].replace(lastChar,"");
                        //weird case where a punctuation is followed by a punctuation
                        if(clauses[j].equals(""))
                        {
                            matrix.get(bindings.get(prev)).set(bindings.get(lastChar),
                            matrix.get(bindings.get(prev)).get(bindings.get(lastChar)) + 1);   
                            prev = lastChar;
                            continue;
                        }
                        lastCharAPunctuation = true;
                    }
                        String words[] = clauses[j].split("[\\s]");
                        int k = 0;

                        if(prev ==  null)
                        { 
                            prev = words[k];
                            ++k;
                        }

                        while(k < words.length)
                        {
                            matrix.get(bindings.get(prev)).set(bindings.get(words[k]),
                            matrix.get(bindings.get(prev)).get(bindings.get(words[k])) + 1);   
                            prev = words[k];
                            ++k;
                        }
                        wordsincurrsentence += words.length;

                        if(lastCharAPunctuation)
                        {
                            //if the punctuation mark is less than the index where the sentence pausers start,
                            //this means it is a sentence ender and we are done
                            if(bindings.get("" + lastChar.charAt(0)) < sentencePauseIndex)
                            {
                                if(wordsincurrsentence > largestSentencelength)
                                {
                                    largestSentencelength = wordsincurrsentence;
                                }
                                wordsincurrsentence = 0;
                            }
                 
                            matrix.get(bindings.get(prev)).set(bindings.get(lastChar),matrix.get(bindings.get(prev)).get(bindings.get(lastChar)) + 1);
                            prev = lastChar;
                        }
                    
					}
				
					
			}
        }
		catch(IOException e)
		{
			e.printStackTrace();
		}

    }

  
    public static void predictNextWord()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Type a sentence you want us to predict the next word for");
        String input = sc.nextLine();
        input = input.toLowerCase();
		String[] entries = input.split(" ");
        if(!bindings.containsKey(entries[entries.length - 1]))
        {
            System.out.println("The last word you typed is not in our system");
        }
        else
        {
            data.predictNextWord(bindings.get(entries[entries.length - 1]));
        }
        
    }

    /**
     * predicts the next N words based off the words input.
     * It will not necessarily stop the moment a sentence is
     * completed.
     */
    public static void predictnextNwords()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tell us the amount of words you want us to predict");
		String input = sc.nextLine();
        int N = Integer.parseInt(input);
        System.out.println("Type your words");
        input = sc.nextLine();
        input = input.toLowerCase();
		String[] entries = input.split(" ");
        if(!bindings.containsKey(entries[entries.length - 1]))
        {
            System.out.println("The last word you typed is not in our system");
        }
        else
        {
            data.predictnextNwords(N,bindings.get(entries[entries.length - 1]));
        }
    }

    /**
     * completes the sentence based off the last word input.
     */
    public static void completeSentence()
    {
       Scanner sc = new Scanner(System.in);
       System.out.println();
       System.out.println("Type an incomplete sentence for us to autocomplete");
       String input = sc.nextLine();
       input = input.toLowerCase();
       String[] entries = input.split(" ");
       if(!bindings.containsKey(entries[entries.length - 1]))
       {
           System.out.println("The last word you typed is not in our system");
       }
        else
        {
            data.AutocompleteSentence(bindings.get(entries[entries.length - 1]), entries.length);
        }
       
       System.out.println();
    }

    
    /**
     * Calls for Markov chain to remove states input into the hashset.
     * This will take care of type safety as well
     * @param Mchain the markov chain we want to directly modify and 
     * change specified states to transient
     * @param statestoremove a hashset consisting of the integer values
     * of the states we want to remove
     */
    public static void RemoveStates(MarkovChain Mchain, HashSet<Integer> statestoremove)
    {
        System.out.println("Write down the words/punctuation you want to get rid of in the chain");
        System.out.println("Separate them with spaces");

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        input = input.toLowerCase();
        String[] words = input.split("[\\s]");
        for(String word : words)
        {
            if(!bindings.containsKey(word))
            {
                System.out.println(word + " was not found in the source file");
                return;
            }
            else
            {
                statestoremove.add(bindings.get(word));
            }
        }
        Mchain.removestates(statestoremove);
        System.out.println("States specified were successfully converted into transient states");



    }

    
       /**
        * finds the smallest probability in the row
        * adds 1/(matrixsize)th of that probability to everything
        * @param Mchain the markov chain we want to directly edit
        * and have it such that all nodes can communicate with
        * each other.
        */
       public static void smoothing(MarkovChain Mchain)
       {
            ArrayList<ArrayList<Double>> matrixIn = Mchain.matrix;
            for(int k = 0; k < matrixIn.size(); ++k)
            {
                double smoothingVal = 1;
                for(int j = 0; j < matrixIn.size(); ++j)
                {
                    if(matrixIn.get(k).get(j) > EPSILON && matrixIn.get(k).get(j) < smoothingVal)
                    {
                        smoothingVal = matrixIn.get(k).get(j);
                    }
                }
                smoothingVal = smoothingVal / Math.pow(matrixIn.size(),2);
                for(int j = 0; j < matrixIn.size(); ++j)
                {
                    matrixIn.get(k).set(j,matrixIn.get(k).get(j) + smoothingVal);
                }
            }

            Mchain.renormalize();
            System.out.println("Chain was successfully smoothed");
       }





    

    /**
     * calculates the stationary distribution and ignores the states
     * that we don't want, prints the rest of them. Will not do a calculation
     * again if it already did one. Prints as many files as we have
     * valid stationary distributions to print.
     * @param Mchain the markov chain we want the stationary distributions of
     * @param statestoremove the states we want to ignore in the chain
     * @param directory folder we want to put the file in
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void stationarydistcalculation(MarkovChain Mchain, 
    HashSet<Integer> statestoremove, String directory)
    {

        if(Mchain.calculatedstationaries == true)
        {
            return;
        }
    
        Mchain.calculate_Alllimitingdists();
       
       ArrayList<ArrayList<Double>> stationarydists = Mchain.Stationarydists;

       if(statestoremove != null)
       {
        for(int i = 0; i < stationarydists.size(); i++)
        {
            ArrayList<Double> possibleremoval = stationarydists.get(i);
            for(Integer remove : statestoremove)
            {
                if(Math.abs(1 - possibleremoval.get(remove)) < EPSILON)
                {
                    stationarydists.remove(possibleremoval);
                    i--;
                    statestoremove.remove(remove);
                    break;
                }
            }
        }

       }
       
       
       
       for(int j = 0; j < stationarydists.size(); j++)
       {
        //we want the lowest priority on top so we can get rid of it first
           Comparator<indexvalue> mostimportant = (val1, val2) -> {
               if (val1.value > val2.value) {
                   return 1;
               } else {
                   return -1;
               }
           };
        
        //if the eigenvector is too large, this will provide a summary of the highest probability
        //words found in the stationary distribution
        PriorityQueue<indexvalue> toptenmostimportantwords = new PriorityQueue<>(mostimportant);
        //writes each eigenvector to a new file so it is easier to read
        String outputdiststo = directory + j + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputdiststo))) {
            
        {
            writer.write("stationarydistribution : " + j);
            writer.write("\n\n\n");
            for(int k = 0; k < stationarydists.get(j).size(); k++)
            {
                writer.write("(" + k + "): " + bindings2.get(k) + ": " +  decimalFormat.format(Math.abs(stationarydists.get(j).get(k))));
                writer.write("\n");
                indexvalue tempInd = new indexvalue(k,stationarydists.get(j).get(k));
                toptenmostimportantwords.add(tempInd);

                //Our priority queue holds the node of the least priority at the top
                //so we just pop the lowest priority off if the size exceeds 10
                if(toptenmostimportantwords.size() > 10)
                {
                    toptenmostimportantwords.poll();
                }
            }

        }

        Stack<Integer> Temp = new Stack();
        
        while(!toptenmostimportantwords.isEmpty())
        {
            Temp.add(toptenmostimportantwords.poll().index);
        }

        writer.write("\n\n\n");
        writer.write("--------------------------------------------------------------------------------------");
        writer.write("\nMost important states and their probabilities in this stationary distribution are : ");
        writer.write("\n\n\n");
        while(!Temp.empty())
        {
            writer.write("(" + Temp.peek() + ") : " + bindings2.get(Temp.peek()) + " : " + stationarydists.get(j).get(Temp.pop()) + "\n");   

        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
       }
}

    /**
     * prints out the binding table to a file directory
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void bindingTable()
{
     try (BufferedWriter writer = new BufferedWriter(new FileWriter("Matrix/Binding_Table.txt"))) {  
        {
            writer.write("Matrix index to word bindings :");
            writer.write("\n\n\n");
            for(int k = 0; k < bindings2.size(); k++)
            {
                writer.write("(" + k + "): " + bindings2.get(k));
                writer.write("\n");
            }

        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
}



    /**
     * writes markov chain to a file directory listed in directory
     * @param matrix we want to save to a file
     * @param directory folder we want to put the matrix in
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void writemarkovchain(ArrayList<ArrayList<Double>> matrix, String directory)
{
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "outputMarkovChain.txt"))) {
            
        {
            
            /**
             * Centers label within the size of each matrix double entry
             * the default is 20 spaces between each entry in the matrix
             * due to the precision, this can be changed via totalSpaces
             * which is the measure of the distance between the start of 
             * one matrix entry to the next one
             */
            if(labelMatrixIndices)
            {
                writer.write(" ".repeat(totalSpaces / 2));
                for(int i = 0; i < matrix.size(); i++)
            {
                
                String in = "(" + i + ")";
                String formattedResult = String.format("%" + (totalSpaces - (totalSpaces - in.length()) / 2) + "s", in)
                         .concat(" ".repeat((totalSpaces - in.length()) / 2));
                writer.write(formattedResult);
            }
            writer.write("\n");
            }
            
            
           
            for(int i = 0; i < matrix.size(); i++)
            {
                //prints out a label for each row, or the row index in the markov
                //chain data is being printed for for ease of reading
                if(labelMatrixIndices)
                {
                    String in = "(" + i + ")";
                    writer.write(in + " ".repeat(totalSpaces/2 - in.length()));
                }

                //prints the actual probabilities of each node in an organized manner
                for(int j = 0; j < matrix.get(0).size(); j++)
                {
                    writer.write(decimalFormat.format(matrix.get(i).get(j)) + " ");
                }
                writer.write("\n");
            }

        }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
}


}




