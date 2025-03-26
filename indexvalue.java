/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * Stores the label for a graph index
 * and the double value associated with it.
 * This is to be used in a priority queue
 * using a double value associated with the index
 * to compare them.
 */

public class indexvalue
{
    public int index;
    public double value;

    public indexvalue(int index, double value)
    {
        this.index = index;
        this.value = value;
    }
}