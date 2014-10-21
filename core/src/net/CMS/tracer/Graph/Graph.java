package net.CMS.tracer.Graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.badlogic.gdx.utils.Array;

public class Graph {
	
	private int MAX_NODES = 200;
	private Array<Integer> nodes;
	private int adjacencyMatrix[][];
	public Graph(int maxNodes){
		MAX_NODES = maxNodes;
		nodes = new Array<Integer>();
		adjacencyMatrix = new int [MAX_NODES][MAX_NODES];

	}
	public void addNode(int n){
		if(!nodes.contains(n,true)) nodes.add(n);
		
	}
	public void removeNode(int n){
		if(nodes.contains(n,true)){
			for(int i: nodes){
				for(int j: nodes){
					if(i == n || j == n) removeEdge(i, j);
				}
			}
			nodes.removeValue(n,true);
		}
	}
	public int getEdge(int n1, int n2) {
		return adjacencyMatrix[n1][n2];
	}
	public void addEdge(int n1, int n2, int w){
		adjacencyMatrix[n1][n2] = w;
		adjacencyMatrix[n2][n1] = -w;
	}
	public void removeEdge(int n1, int n2){
		adjacencyMatrix[n1][n2] = 0;
		adjacencyMatrix[n2][n1] = 0;
	}
	public boolean hasEdge(int n1, int n2){
		return adjacencyMatrix[n1][n2] != 0;
	}
	public int getWeight(int n1, int n2){
		return adjacencyMatrix[n1][n2];
	}
	public Array<Integer> getLongestPath(){
		Array<Integer> lp;
		int tm = 0;
		for(int i = 0 ; i < nodes.size; i++){
		boolean v[] = new boolean[nodes.size];
		int cm = 0;
		for(int n = 0; n  < nodes.size; n++){
			if(hasEdge(i, n) && !v[n]){
				cm++;
				v[n] = true;
			}
		}
		
	}
		return null;
	}
	public Array<Array<Integer>> getConnectedGraphcs(){
		Array<Array<Integer>> gfs = new Array<Array<Integer>>();
		boolean taken[] = new boolean[nodes.size];
		for(int i = 0; i < nodes.size; i++){
			boolean[] cg = BFS(adjacencyMatrix, MAX_NODES, nodes.get(i));
			if(cg.length > 1){
				Array<Integer> g = new Array<Integer>();
				for(int j = 0 ; j < cg.length; j++){

					taken[j] = true;
					if(cg[j]){
						g.add(j);
					}
				}
				boolean found = false;
				for(Array<Integer> n: gfs){
					for(Integer r: g){
						if(n.contains(r, true))found = true;
					}
				
				}
				if(!found)
				gfs.add(g);
			}

		}
		return gfs;
	}
	 public static boolean[] BFS(int[][] adjacencyMatrix, int vertexCount, int givenVertex){
	      // Result array.
	      boolean[] mark = new boolean[vertexCount];

	      Queue<Integer> queue = new LinkedList<Integer>();
	      queue.add(givenVertex);
	      mark[givenVertex] = true;

	      while (!queue.isEmpty())
	      {
	        Integer current = queue.remove();

	        for (int i = 0; i < vertexCount; ++i)
	            if (adjacencyMatrix[current][i] != 0 && !mark[i])
	            {
	                mark[i] = true;
	                queue.add(i);
	            }
	      }

	      return mark;
	  }

}
