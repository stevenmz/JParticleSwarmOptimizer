/*
 * The MIT License
 *
 * Copyright 2014 Steven Alan Magaña-Zook <smaganazook ~~at~~ live dot com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package stevenmz.pso.test;

import java.util.Random;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import stevenmz.pso.ParticleSwarmOptimizer;

/**
 * This test class exercises the ParticleSwarmOptimizer class
 *
 * @author Steven Alan Magaña-Zook <smaganazook ~~at~~ live dot com>
 */
public class PSOTests {

    public PSOTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void PolynomialMinimizationTest() {
        final int NUMBER_OF_PARTICLES = 10;
        final int NUMBER_OF_ITERATIONS = 100;

        ParticleSwarmOptimizer optimizer = new ParticleSwarmOptimizer(new ParticleSwarmOptimizer.IParticleFunctionEvaluator() {

            @Override
            public double Evaluate(Number... potentialSolution) {
                return 3 + Math.pow(potentialSolution[0].intValue(), 2) + Math.pow(potentialSolution[1].intValue(), 2);
            }

            @Override
            public Number[] getRandomSolution() {
                Random r = new Random();
                Number[] solution = new Number[2];
                solution[0] = r.nextInt();
                solution[1] = r.nextInt();
                return solution;
            }
        }, NUMBER_OF_PARTICLES, NUMBER_OF_ITERATIONS, ParticleSwarmOptimizer.OptimizationType.MINIMIZE);
        ParticleSwarmOptimizer.Particle optimalPartical = optimizer.optimize();
        final Number[] bestSolution = optimalPartical.getLocalBestSolution();
        Assert.assertEquals(0, bestSolution[0].intValue());
        Assert.assertEquals(0, bestSolution[1].intValue());
    }
}
