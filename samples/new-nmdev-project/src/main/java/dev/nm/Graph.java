/*
 * Copyright (c) NM LTD.
 * https://nm.dev/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dev.nm;

import dev.nm.graph.DiGraph;
import dev.nm.graph.WeightedArc;
import dev.nm.graph.algorithm.shortestpath.Dijkstra;
import dev.nm.graph.type.SimpleArc;
import dev.nm.graph.type.SparseDAGraph;

/**
 *
 * @author haksunli
 */
public class Graph {

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.fun1();
    }

    public void fun1() {
        System.out.println("fun1");
        DiGraph<Integer, WeightedArc<Integer>> G
                = new SparseDAGraph<>();

        G.addEdge(new SimpleArc<>(1, 2, 7.));
        G.addEdge(new SimpleArc<>(1, 6, 14.));
        G.addEdge(new SimpleArc<>(1, 3, 9.));
        G.addEdge(new SimpleArc<>(2, 4, 15.));
        G.addEdge(new SimpleArc<>(2, 3, 10.));
        G.addEdge(new SimpleArc<>(3, 4, 11.));
        G.addEdge(new SimpleArc<>(3, 6, 2.));
        G.addEdge(new SimpleArc<>(4, 5, 6.));
        G.addEdge(new SimpleArc<>(6, 5, 9.));

        Dijkstra<Integer> instance = new Dijkstra<>(G, 1);
        double dist = instance.distance(2);
        System.out.println("distance = " + dist);
        dist = instance.distance(3);
        System.out.println("distance = " + dist);
        dist = instance.distance(6);
        System.out.println("distance = " + dist);
        dist = instance.distance(4);
        System.out.println("distance = " + dist);
        dist = instance.distance(5);
        System.out.println("distance = " + dist);
    }

    public void fun2() {
        System.out.println("fun2");

        DiGraph<Integer, SimpleArc<Integer>> G
                = new SparseDAGraph<>();
        G.addEdge(new SimpleArc<>(1, 2, 7.));
        G.addEdge(new SimpleArc<>(1, 6, 14.));
        G.addEdge(new SimpleArc<>(1, 3, 9.));
        G.addEdge(new SimpleArc<>(2, 4, 15.));
        G.addEdge(new SimpleArc<>(2, 3, 10.));
        G.addEdge(new SimpleArc<>(3, 4, 11.));
        G.addEdge(new SimpleArc<>(3, 6, 2.));
        G.addEdge(new SimpleArc<>(4, 5, 6.));
        G.addEdge(new SimpleArc<>(6, 5, 9.));
        G.addEdge(new SimpleArc<>(99, 100, 99.));//disconnected from 1

        Dijkstra<Integer> instance1 = new Dijkstra<>(G, 1);

        Dijkstra<Integer> instance2 = new Dijkstra<>(G, 99);

        Dijkstra<Integer> instance3 = new Dijkstra<>(G, 100);
    }
}
