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
 *  d) Multithreading support (yeehaw)
 *  e) Graph traversal algorithm (additional only)
 *  f) Custom implementation of HashedTriangle
 *  g) In order addition
 * <p/>
 */

package ece454750s15a2;

import java.io.*;
import java.io.IOError;
import java.io.IOException;
import java.lang.*;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Iterable;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;
    private final ConcurrentHashMap<String, Triangle> tfchamp = new ConcurrentHashMap<String, Triangle>();
    private final ConcurrentHashMap<Integer, HashSet<Integer>> adjListGlobal = new ConcurrentHashMap<Integer, HashSet<Integer>>();
    private final int numThreads;
    private int iteratorChunkSize;
    private int totalVertices;
    private int totalEdges;

    private ArrayList<Triangle> triangleArrayList;

    public class HashedTriangle {
        private final int x;
        private final int y;
        private final int z;

        public HashedTriangle(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public HashedTriangle(Triangle triangle) {
            Scanner scanner = new Scanner(triangle.toString()).useDelimiter(" ");
            this.x = scanner.nextInt();
            this.y = scanner.nextInt();
            this.z = scanner.nextInt();
        }

        public Triangle convertToTriangleType(HashedTriangle hashedTriangle) {
            return new Triangle(hashedTriangle.x, hashedTriangle.y, hashedTriangle.z);
        }

        @Override public String toString() {
            return String.format(this.x + " " + this.y + " " + this.z);
        }

        @Override public int hashCode() {
            return (this.x) + (this.y) + (this.z);
        }

        @Override public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final HashedTriangle otherhashedTriangle = (HashedTriangle) obj;
            if (this.x == otherhashedTriangle.x || this.x == otherhashedTriangle.y || this.z == otherhashedTriangle.z)
                if(this.y == otherhashedTriangle.y || this.y == otherhashedTriangle.x || this.y == otherhashedTriangle.z)
                    if(this.z == otherhashedTriangle.z || this.z == otherhashedTriangle.x || this.z == otherhashedTriangle.y)
                        return true;
                    else
                        return false;
                else
                    return false;
            else
                return false;
            // if all else fails...
            //return true;
        }
    }

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.totalEdges = 0;
        this.totalVertices = 0;
        this.numCores = numCores;
        this.numThreads = numCores;      // 1 thread per core since these are CPU bound.
        this.iteratorChunkSize = 0;      // Just a placeholder, real value set in enumerateTriangles()
        this.triangleArrayList = new ArrayList<Triangle>();
    }

    public class AdjListGraph {
        private int V;
        private int E;
        private int Node;
        private ConcurrentHashMap<Integer, HashSet<Integer>> adjList;

        public AdjListGraph(int V, int E) {
            if (V < 0) throw new RuntimeException("Number of vertices cannot be negative.");
            this.V = V;
            this.E = 0;
            this.adjList = new ConcurrentHashMap<Integer, HashSet<Integer>>();
            for (int i = 0; i < V; ++i) {
                this.adjList.put(V, new HashSet<Integer>());
            }
        }

        public AdjListGraph(AdjListGraph adjListOld) {
            this.V = adjListOld.getNumVertices();
            this.E = adjListOld.getTotalNumEdges();
            this.adjList = new ConcurrentHashMap<Integer, HashSet<Integer>>(adjListOld.adjList);
        }

        public AdjListGraph(ConcurrentHashMap<Integer, HashSet<Integer>> adjListHM, int totalVertices, int totalEdges) {
            this.V = totalVertices;
            this.E = totalEdges;
            this.adjList = new ConcurrentHashMap<Integer, HashSet<Integer>>(adjListHM);
        }

        public void addEdge(int i, String[] edges) {
            HashSet<Integer> edgesSet = new HashSet<Integer>();

            for (String edge : edges) {
                if(Integer.parseInt(edge) > i)
                    edgesSet.add(Integer.parseInt(edge));
            }

            if (this.adjList.put(i, edgesSet) == null)
                this.E += 1;
        }

        public void removeVertex(int i) {
            this.adjList.remove(i);
            this.V -= 1;
        }

        public boolean hasEdge(int i, int j) {
            return (this.adjList.get(i) != null || this.adjList.get(j) != null);
        }

        public int getNumVertices() {
            return this.V;
        }

        public int getTotalNumEdges() {
            return this.E;
        }

        public void setNumVertices(int numVertices) {
            this.V = numVertices;
        }

        public void setTotalEdges(int numEdges) {
            this.E = numEdges;
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
        Triangle t = new Triangle(vertexA, vertexB, vertexC);

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
            this.triangleArrayList.add(new Triangle(vertexA, vertexB, vertexC));
    }

    public void showTriangleFoundList() {
        System.out.println("triangleFoundList = " + this.triangleArrayList.toString());
    }

    public void setIteratorChunkSize(int size) {
        this.iteratorChunkSize = size;
    }

    public int getIteratorChunkSize() {
        return this.iteratorChunkSize;
    }

    public ArrayList<Integer> checkIntersection(HashSet<Integer> h1, HashSet<Integer> h2) {
        ArrayList<Integer> intersectArrayList = new ArrayList<Integer>();

        for (Integer vertex : h1) {
            if (h2.contains(vertex)) {
                intersectArrayList.add(vertex);
            }
        }

        return intersectArrayList;
    }

    public void cleanUpAdjLists(AdjListGraph adjListGraph, int vertexA, int vertexB, int vertexC) {
        //adjListGraph.adjList.get(vertexA).remove(vertexB);
        //adjListGraph.adjList.get(vertexA).remove(vertexC);

        adjListGraph.adjList.get(vertexB).remove(vertexA);
        //adjListGraph.adjList.get(vertexB).remove(vertexC);

        adjListGraph.adjList.get(vertexC).remove(vertexA);
        adjListGraph.adjList.get(vertexC).remove(vertexB);
    }

    public class triangleWorkerParallel implements Runnable {
        private AdjListGraph adjListGraph;
        private int startRange = 0;
        private int endRange = 0;
        private int startingVertex = 0;

        public triangleWorkerParallel(AdjListGraph adjacencyList, int startRange, int endRange, int startingVertex) {
            this.adjListGraph = adjacencyList;
            this.startRange = startRange;
            this.endRange = endRange;
            this.startingVertex = startingVertex;
        }

        public Triangle checkOrder(int vertexA, int vertexB, int vertexC) {
            Triangle t = new Triangle(vertexA, vertexB, vertexC);

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

        public void cleanUpAdjListsParallel(AdjListGraph adjListGraph, int vertexA, int vertexB, int vertexC) {
            //adjListGraph.adjList.get(vertexA).remove(vertexB);
            //adjListGraph.adjList.get(vertexA).remove(vertexC);

            adjListGraph.adjList.get(vertexB).remove(vertexA);
            //adjListGraph.adjList.get(vertexB).remove(vertexC);

            adjListGraph.adjList.get(vertexC).remove(vertexA);
            adjListGraph.adjList.get(vertexC).remove(vertexB);
        }

        public void updateTriangleFoundListParallel (int Vertex, int vertexA, int vertexB, int vertexC) {
                tfchamp.putIfAbsent(vertexA + " " + vertexB + " " + vertexC + " " + Vertex, new Triangle(vertexA, vertexB, vertexC));
        }

        public void run() {
            int triangleCounter = 0;
            int numEdges = 0;
            int numVertices = 0;

            numVertices = this.adjListGraph.getNumVertices();
            numEdges = this.adjListGraph.getTotalNumEdges();

            //System.out.println("numVertices = " + numVertices);
            //System.out.println("numEdges    = " + numEdges);


            int trianglesFound = 0;

            //System.out.println("Thread #" + Thread.currentThread().getId() + " sees adjListGlobal = " + this.adjListGraph.adjList.toString());

            System.out.println("[triangleWorkerParallel] Thread #" + Thread.currentThread().getId() + ": startRange = " + startingVertex + " endRange = " + TriangleCountImpl.this.totalVertices);

            // naive++ triangle counting algorithm
            for (int i = startingVertex; i < TriangleCountImpl.this.totalVertices; i += numCores) {
                if (this.adjListGraph.adjList.get(i) != null) {
                    HashSet<Integer> n1 = new HashSet<Integer>(this.adjListGraph.adjList.get(i));
                    if (n1.size() >= 2) {
                        for (int j : n1) {
                            if (i < j) {
                                ArrayList<Integer> intersectionArrayList = new ArrayList<Integer>(checkIntersection(this.adjListGraph.adjList.get(i), this.adjListGraph.adjList.get(j)));
                                if (intersectionArrayList.size() != 0) {
                                    for (Integer l : intersectionArrayList) {
                                        if (j < l) {
                                            trianglesFound += 1;
                                            this.updateTriangleFoundListParallel(i, i, j, l);
                                            //this.cleanUpAdjListsParallel(this.adjListGraph, i, j, l);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //showTriangleFoundList();
            System.out.println("Thread #" + Thread.currentThread().getId() + " found = " + trianglesFound + " triangles");

        }
    }

    public void triangleWorker(AdjListGraph adjacencyList, int startRange, int endRange) {
        int triangleCounter = 0;
        int numEdges = 0;
        int numVertices = 0;

        numVertices = adjacencyList.getNumVertices();
        numEdges = adjacencyList.getTotalNumEdges();

        System.out.println("Thread #" + Thread.currentThread().getId() + ": startRange = " + startRange + " endRange = " + endRange);

        //System.out.println(adjacencyList.adjList.toString());

        // naive++ triangle counting algorithm
        for (int i = startRange; i < endRange; i += 1) {
            if (adjacencyList.adjList.get(i) != null) {
                ArrayList<Integer> n1 = new ArrayList<Integer>(adjacencyList.adjList.get(i));
                if (n1.size() >= 2) {
                    for (int j : n1) {
                        if (i < j) {
                            ArrayList<Integer> intersectionArrayList = new ArrayList<Integer>(checkIntersection(adjacencyList.adjList.get(i), adjacencyList.adjList.get(j)));
                            if (intersectionArrayList.size() != 0) {
                                for (Integer l : intersectionArrayList) {
                                    if (j < l) {
                                        this.updateTriangleFoundList(i, i, j, l);
                                        //this.cleanUpAdjLists(adjacencyList, i, j, l);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int readFirstLineAndGiveVertices() {
        InputStream istream = new ByteArrayInputStream(this.input);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));

        int numVerticesfromFile = 0;

        try {
            String strLine = br.readLine();
            String parts[] = strLine.split(", ");
            numVerticesfromFile = Integer.parseInt(parts[0].split(" ")[0]);

            // File error handling.
            if (!strLine.contains("vertices") || !strLine.contains("edges")) {
                System.err.println("Invalid graph file format. Offending line: " + strLine);
                System.exit(-1);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numVerticesfromFile;
    }

    public List<Triangle> enumerateTriangles() throws IOException {

        // start the clock
        long startTime = System.currentTimeMillis();

        final AdjListGraph adjacencyList;
        AdjListGraph adjListreadParallel;

        if (numCores == 1) {
            adjacencyList = getAdjacencyList(input);

            // stop the clock
            long endTimeInputRead = System.currentTimeMillis();

            // report the running time of the computation
            long diffTimeRead = endTimeInputRead - startTime;

            System.out.println("Done reading input, now counting..   [" + diffTimeRead + "ms]");

            triangleWorker(adjacencyList, 0, adjacencyList.getNumVertices());
        }
        else {
            ExecutorService readpool = Executors.newFixedThreadPool(this.numThreads);

            int readChunkSize = readFirstLineAndGiveVertices() / this.numThreads;

            int offset = 0;
            for (int i = 0; i < this.numThreads; ++i) {
                final int startRange = i > 0 ? offset : 0;
                final int endRange = offset + readChunkSize;
                readpool.submit(new getAdjacencyListParallel(input, startRange, endRange, i));
                offset += readChunkSize;
            }

            readpool.shutdown();

            try {
                readpool.awaitTermination(1, TimeUnit.DAYS);
            } catch (java.lang.InterruptedException ignore) {
                System.out.println("Read Pool Interrupted!");
            }


            // stop the clock
            long endTimeInputRead = System.currentTimeMillis();

            // report the running time of the computation
            long diffTimeRead = endTimeInputRead - startTime;

            System.out.println("adjListGlobal has = " + adjListGlobal.size() + " entries.");
            System.out.println("Done reading input, now counting..   [" + diffTimeRead + "ms]");

            // since we are done now, return the input back to the requester.
            adjListreadParallel = new AdjListGraph(adjListGlobal, TriangleCountImpl.this.totalVertices, TriangleCountImpl.this.totalEdges);

            //System.out.println("Size of global shared adjListGraph = " + adjListreadParallel.adjList.size());
            //System.out.print("adjListGlobal = " + adjListGlobal.toString());

            ExecutorService pool = Executors.newFixedThreadPool(this.numThreads);

            //this.setIteratorChunkSize(adjacencyList.getNumVertices() / this.numThreads);

            //int offset = 0;
            for (int i = 0; i < this.numThreads; ++i) {
                //final int startRange = i > 0 ? offset + 1 : 0;
                //final int endRange = offset + this.getIteratorChunkSize();
                pool.submit(new triangleWorkerParallel(adjListreadParallel, i, 0, i));
                //offset += this.getIteratorChunkSize();
            }

            pool.shutdown();

            try {
                pool.awaitTermination(1, TimeUnit.DAYS);
            } catch (java.lang.InterruptedException ignore) {
                System.out.println("Pool Interrupted!");
            }

        }

        if (numCores == 1) {
            System.out.println("Total triangles = " + this.triangleArrayList.size());
            return this.triangleArrayList;
        }
        else {
            List<Triangle> triangleList = new ArrayList<Triangle>(tfchamp.values());
            System.out.println("Total triangles = " + triangleList.size());
            return new ArrayList<Triangle>(triangleList);
        }

    }

    public class getAdjacencyListParallel implements Runnable {
        private int startRange = 0;
        private int endRange = 0;
        private int totalEdgesFound = 0;
        private int startingVertex = 0;
        private byte[] data;

        public getAdjacencyListParallel(byte[] input, int startRange, int endRange, int startingVertex) {
            this.startRange = startRange;
            this.endRange = endRange;
            this.startingVertex = startingVertex;
            this.data = input;
        }

        public void addEdgeParallel(int i, String[] edges) {
            HashSet<Integer> edgesSet = new HashSet<Integer>();

            for (String edge : edges) {
                if(Integer.parseInt(edge) > i) {
                    edgesSet.add(Integer.parseInt(edge));
                    this.totalEdgesFound += 1;
                }
            }

            adjListGlobal.putIfAbsent(i, edgesSet);
        }

        public int getTotalEdgesFound() {
            return this.totalEdgesFound;
        }

        public void run() {

            InputStream istream = new ByteArrayInputStream(this.data);
            BufferedReader br = new BufferedReader(new InputStreamReader(istream));
            try {
                // Skip the first line.
                String strLine = br.readLine();

                // File error handling.
                if (!strLine.contains("vertices") || !strLine.contains("edges")) {
                    System.err.println("Invalid graph file format. Offending line: " + strLine);
                    System.exit(-1);
                }
                String parts[] = strLine.split(", ");
                int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
                int numEdges = Integer.parseInt(parts[1].split(" ")[0]);

                TriangleCountImpl.this.totalEdges = numEdges;
                TriangleCountImpl.this.totalVertices = numVertices;

                // Skip over to the right start line per thread.
                for (int i = 0; i < startRange + 1; i++)
                    strLine = br.readLine();

                //System.out.println("Thread #" + Thread.currentThread().getId() + " first line = " + strLine);

                System.out.println("[getAdjacencyListParallel] Thread #" + Thread.currentThread().getId() + " startRange = " + startRange + " endRange = " + endRange);

                int totalEntriesAdded = 0;

                for (int i = this.startRange; (i < endRange && strLine != null && !strLine.equals("")); ++i) {
                    parts = strLine.split(": ");
                    int current_vertex = Integer.parseInt(parts[0]);
                    if (parts.length > 1) {
                        parts = parts[1].split(" +");
                        totalEntriesAdded += 1;
                        //adjList.addEdge(current_vertex, parts);
                        this.addEdgeParallel(current_vertex, parts);
                        if (numVertices < 10)
                            System.out.println("Thread #" + Thread.currentThread().getId() + " Adding: Vertex = " + current_vertex + " neighbors = " + Arrays.toString(parts));
                    }

                    strLine = br.readLine();
                }

                //System.out.println("Size of global shared adjListGraph = " + adjListGlobal.size());
                System.out.println("[getAdjacencyListParallel] Thread #" + Thread.currentThread().getId() + " added = " + totalEntriesAdded + " entries");

                br.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
            //return adjList;
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
        this.totalVertices = numVertices;
        this.totalEdges = numEdges;


        AdjListGraph adjList = new AdjListGraph(numVertices, numEdges);

        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");
            int current_vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                adjList.addEdge(current_vertex, parts);
            }
        }
        br.close();
        return adjList;
    }
}
