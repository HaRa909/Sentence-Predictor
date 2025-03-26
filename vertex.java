/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 * 
 * This file creates the vertex object that is
 * meant to be used in Tarjan's SCC algorithm.
 * This is used to check if the markov chain is
 * irreducible or not.
 */
public class vertex
{
    int vertname;
    int index;
    int lowlink;
    boolean onstack;

    public vertex(int vertname)
    {
        this.vertname = vertname;
        this.index = -1;
        this.lowlink = 0;
        this.onstack = false;
    }

    public void setindex(int index)
    {
        this.index = index;
    }
    public void setlowlink(int lowlink)
    {
        this.lowlink = lowlink; 
    }
    public void setonstack(boolean onstack)
    {
        this.onstack = onstack;
    }
    public int vertname()
    {
        return vertname;
    }

    public boolean onstack()
    {
        return onstack;
    }
    public int index()
    {
        return index;
    }
    public int lowlink()
    {
        return lowlink;   
    }

}