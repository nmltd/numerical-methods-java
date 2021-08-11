/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nm.nmj;

import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;

/**
 *
 * @author haksunli
 */
public class HelloNM {

    public static void main(String[] args) {
        System.out.println("Hello NM!");

        Vector v = new DenseVector(1., 2., 3.);
        System.out.println(v);
    }
}
