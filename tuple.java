/**
 * Name: Muhammad Rasul
 * Email: rasulhashir@gmail.com
 *
 * This class is meant to be used for priority queue in
 * modified dijkstra's algorithm. It will store the amount
 * of nodes it took to get to this node, and the amount of
 * edgeweight total it takes to get to this node.
 */

public class tuple
{
    public int currnode;
    public double totalpathcost;
    public int currpathlength;

    public tuple(int currnode, double totalpathcost, int currpathlength)
    {
        this.currnode = currnode;
        this.totalpathcost = totalpathcost;
        this.currpathlength = currpathlength;
    }
}