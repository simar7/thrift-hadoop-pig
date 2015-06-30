/**
 * ECE 454/750: Distributed Computing
 * <p/>
 * Code written by Wojciech Golab, University of Waterloo, 2015
 * Addendum Author(s):
 * 1) Simarpreet Singh s244sing@uwaterloo.ca
 * Wrote:
 *  a) Adjacency Matrix Graph representation
 *  b) EnumerateTriangles AMG implementation
 *  c) Trace calculator with Strassen
 * <p/>
 */

package ece454750s15a2;

import java.io.*;
import java.io.IOError;
import java.io.IOException;
import java.lang.*;
import java.lang.Integer;
import java.lang.Iterable;
import java.lang.RuntimeException;
import java.lang.System;
import java.lang.Thread;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;
    //private final CopyOnWriteArraySet<Triangle> triangleCopyOnWriteArraySetFoundList;
    private final ConcurrentHashMap<Triangle, Integer> triangleFoundConcurrentHashMap;
    private final int numThreads;
    private int iteratorChunkSize;

    private ArrayList<Triangle> triangleArrayList;

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.numCores = numCores;
        this.triangleFoundConcurrentHashMap = new ConcurrentHashMap<Triangle, Integer>();
        this.numThreads = numCores;      // 1 thread per core since these are CPU bound.
        this.iteratorChunkSize = 0;      // Just a placeholder, real value set in enumerateTriangles()
        this.triangleArrayList = new ArrayList<Triangle>();
    }

    public class AdjListGraph {
        private int V;
        private int E;
        private ArrayList<HashSet<Integer>> adjList;

        public AdjListGraph(int V, int E) {
            if (V < 0) throw new RuntimeException("Number of vertices cannot be negative.");
            this.V = V;
            this.E = 0;
            this.adjList = new ArrayList<HashSet<Integer>>();
            for (int i = 0; i < V; ++i) {
                this.adjList.add(new HashSet<Integer>());
            }
        }

        public AdjListGraph(AdjListGraph adjListOld) {
            this.V = adjListOld.getNumVertices();
            this.E = adjListOld.getTotalNumEdges();
            this.adjList = new ArrayList<HashSet<Integer>>(adjListOld.adjList);
        }

        public void addEdge(int i, int j) {
            if (this.adjList.get(i).add(j) && this.adjList.get(j).add(i))
                this.E += 1;
        }

        public void removeEdge(int i, int j) {
            if (this.adjList.get(i).remove(j) && this.adjList.get(j).remove(i))
                this.E -= 1;
        }

        public void removeVertex(int i) {
            this.adjList.remove(i);
            this.V -= 1;
        }

        public boolean hasEdge(int i, int j) {
            return (this.adjList.get(i).contains(j) || this.adjList.get(j).contains(i));
        }

        public int getNumVertices() {
            return this.V;
        }

        public int getTotalNumEdges() {
            return this.E;
        }

        public int getRelativeEdges(int i) {
            return this.adjList.get(i).size();
        }

        public HashSet<Integer> get(int i) {
            return this.adjList.get(i);
        }

    }

    public List<String> getGroupMembers() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("s244sing");
        ret.add("cpinn");
        return ret;
    }

    public Triangle checkOrder(int vertexA, int vertexB, int vertexC) {
        Triangle t = new Triangle(vertexA, vertexA, vertexC);

        if (vertexA < vertexB) {
            if (vertexB < vertexC)
                t = new Triangle(vertexA, vertexB, vertexC);
            else
                t = new Triangle(vertexA, vertexC, vertexB);
        }
        else if (vertexB < vertexA) {
            if (vertexA < vertexC)
                t = new Triangle(vertexB, vertexA, vertexC);
            else
                t = new Triangle(vertexB, vertexC, vertexA);
        }
        else if (vertexC < vertexA) {
            if (vertexA < vertexB)
                t = new Triangle(vertexC, vertexA, vertexB);
            else
                t = new Triangle(vertexC, vertexB, vertexA);
        }
        return t;
    }


    public void updateTriangleFoundList (int Vertex, int vertexA, int vertexB, int vertexC) {
        if(numCores == 1) {
            this.triangleArrayList.add(this.checkOrder(vertexA, vertexB, vertexC));
        }
        else {
            synchronized (triangleFoundConcurrentHashMap) {
                // FIXME: Need better logic for keys in the HashMap.
                //Random rand = new Random();
                triangleFoundConcurrentHashMap.put(this.checkOrder(vertexA, vertexB, vertexC), Vertex);
            }
        }
    }

    public void setIteratorChunkSize(int size) {
        this.iteratorChunkSize = size;
    }

    public int getIteratorChunkSize() {
        return this.iteratorChunkSize;
    }

    public void triangleWorker(AdjListGraph adjacencyListOrig, int startRange, int endRange) {
        int triangleCounter = 0;
        int numEdges = 0;
        int numVertices = 0;

        AdjListGraph adjacencyList = new AdjListGraph(adjacencyListOrig);

        numVertices = adjacencyList.getNumVertices();
        numEdges = adjacencyList.getTotalNumEdges();

        System.out.println("numVertices = " + numVertices);
        System.out.println("numEdges    = " + numEdges);

        int numEdges_A = 0;
        int numEdges_B = 0;
        int vertex_A = 0;
        int vertex_B = 0;
        Iterator<Integer> iteratorA;
        Iterator<Integer> iteratorB;
        HashSet<Integer> vertex;

        System.out.println("Thread #" + Thread.currentThread().getId() + ": startRange = " + startRange + " endRange = " + endRange);

        for (int vertex_index = startRange; vertex_index < endRange; vertex_index += 1) {
            vertex = adjacencyList.get(vertex_index);
            numEdges = vertex.size();
            iteratorA = vertex.iterator();
            while (iteratorA.hasNext()) {
                vertex_A = iteratorA.next();
                numEdges_A = adjacencyList.getRelativeEdges(vertex_A);
                if (numEdges_A > 1) {
                    iteratorB = vertex.iterator();
                    while (iteratorB.hasNext()) {
                        vertex_B = iteratorB.next();
                        numEdges_B = adjacencyList.getRelativeEdges(vertex_B);
                        if (numEdges_B > 1 && adjacencyList.hasEdge(vertex_A, vertex_B)) {
                            triangleCounter += 1;
                            if (numCores == 1)
                                this.updateTriangleFoundList(vertex_index, vertex_index, vertex_A, vertex_B);
                            else
                                this.updateTriangleFoundList(vertex_index, vertex_index, vertex_A, vertex_B);
                        }
                    }
                }
                iteratorA.remove();
            }
            vertex.clear();
        }

    }

    public List<Triangle> enumerateTriangles() throws IOException {
        final AdjListGraph adjacencyList = getAdjacencyList(input);

        // start the clock
        long startTime = System.currentTimeMillis();

        if (numCores == 1) {
            triangleWorker(adjacencyList, 0, adjacencyList.getNumVertices());
        } else {
            ExecutorService pool = Executors.newFixedThreadPool(this.numThreads);

            this.setIteratorChunkSize(adjacencyList.getNumVertices() / this.numThreads);

            int offset = 0;
            for (int i = 0; i < this.numThreads; ++i) {
                final int startRange = i > 0 ? offset + 1 : 0;
                final int endRange = offset + this.getIteratorChunkSize();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        triangleWorker(adjacencyList, startRange, endRange);
                    }
                });
                offset += this.getIteratorChunkSize();
            }

            //triangleWorker(adjacencyList, adjacencyList.getNumVertices() - this.getIteratorChunkSize(), adjacencyList.getNumVertices());

            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.DAYS);
            } catch (java.lang.InterruptedException ignore) {

            }
        }

        // stop the clock
        long endTime = System.currentTimeMillis();

        long diffTime = endTime - startTime;
        System.out.println("Actual computation took: " + diffTime + "ms");


        if (numCores == 1) {
            System.out.println("Total triangles = " + this.triangleArrayList.size());
            return this.triangleArrayList;
        }
        else {
            System.out.println("Total triangles = " + this.triangleFoundConcurrentHashMap.size());
            return new ArrayList<Triangle>(this.triangleFoundConcurrentHashMap.keySet());
        }
    }

    public AdjListGraph getAdjacencyList(byte[] data) throws IOException {
        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();
        if (!strLine.contains("vertices") || !strLine.contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }
        String parts[] = strLine.split(", ");
        int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
        int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

        AdjListGraph adjList = new AdjListGraph(numVertices, numEdges);

        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");
            int current_vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                for (String edge : parts) {
                    adjList.addEdge(current_vertex, Integer.parseInt(edge));
                }
            }
        }
        br.close();
        return adjList;
    }
}
