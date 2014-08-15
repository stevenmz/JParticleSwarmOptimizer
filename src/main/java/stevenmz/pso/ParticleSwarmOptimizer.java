/*
 * The MIT License
 *
 * Copyright 2014 Steven Alan Magaña-Zook (smaganazook ~~at~~ live dot com).
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
package stevenmz.pso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class coordinates the optimization operations as defined here:
 * http://www.swarmintelligence.org/tutorials.php
 *
 * @author Steven Alan Magaña-Zook (smaganazook ~~at~~ live dot com)
 */
public class ParticleSwarmOptimizer {

    private final IParticleFunctionEvaluator functionToOptimize;
    private int learningFactorGlobal; // c2 in theory documentation
    private int learningFactorLocal; // c1 in theory documentation
    private final int numberOfIterations;
    private final int numberOfParticles;
    private final OptimizationType optimizationType;
    private List<Particle> particles;
    Particle prtclGlobalBestSolution;// gbest in theory documentation

    public ParticleSwarmOptimizer(IParticleFunctionEvaluator functionToOptimize, int numberOfParticles, int numberOfIterations, OptimizationType optimizationType) {
        if (numberOfIterations <= 0) {
            throw new IllegalArgumentException("numberOfIterations must be > 0");
        }

        if (numberOfParticles <= 0) {
            throw new IllegalArgumentException("numberOfParticles must be > 0");
        }

        if (functionToOptimize == null) {
            throw new IllegalArgumentException("functionToOptimize must be initialized");
        }

        this.functionToOptimize = functionToOptimize;
        this.numberOfIterations = numberOfIterations;
        this.numberOfParticles = numberOfParticles;
        this.optimizationType = optimizationType;

        learningFactorLocal = 2;
        learningFactorGlobal = 2;
    }

    /**
     * This method runs the particle swarm optimization algorithm.
     *
     * @return the optimal particle (position in the problem space)
     */
    public Particle optimize() {
        this.initializeSwarm();

        for (int iteration = 0; iteration < numberOfIterations; iteration++) {
            for (Particle particle : particles) {
                particle.calculateCurrentFitness();
            }

            // "Choose the particle with the best fitness value of all the particles as the gBest"
            if (optimizationType == OptimizationType.MAXIMIZE) {
                Particle max = Collections.max(particles);
                if (max.localBestFitness > prtclGlobalBestSolution.localBestFitness) {
                    prtclGlobalBestSolution.localBestFitness = max.localBestFitness;
                    prtclGlobalBestSolution.localBestSolution = Arrays.copyOf(max.localBestSolution, max.localBestSolution.length);
                }
            } else {
                Particle min = Collections.min(particles);
                if (min.localBestFitness < prtclGlobalBestSolution.localBestFitness) {
                    prtclGlobalBestSolution.localBestFitness = min.localBestFitness;
                    prtclGlobalBestSolution.localBestSolution = Arrays.copyOf(min.localBestSolution, min.localBestSolution.length);
                }
            }

            for (Particle particle : particles) {
                particle.updateVelocity(prtclGlobalBestSolution);
                particle.updatePosition();
            }
        }
        return prtclGlobalBestSolution;
    }

    private void initializeSwarm() {
        // Initialize all of the particles
        particles = new ArrayList<>(numberOfParticles);
        for (int i = 0; i < numberOfParticles; i++) {

            final Number[] randomInitialSolution = functionToOptimize.getRandomSolution();
            double initialBestSolution = functionToOptimize.Evaluate(randomInitialSolution);

            final Particle particle = new Particle(randomInitialSolution);
            particle.presentLocation = randomInitialSolution;
            particle.velocity = randomInitialSolution;
            particle.localBestFitness = initialBestSolution;

            particles.add(particle);
        }

        // Initialize the global best particle that particles will gravitate to
        final Number[] randomInitialGlobalSolution = functionToOptimize.getRandomSolution();
        final double initialGlobalBestSolution = functionToOptimize.Evaluate(randomInitialGlobalSolution);
        prtclGlobalBestSolution = new Particle(randomInitialGlobalSolution);
        prtclGlobalBestSolution.setLocalBestFitness(initialGlobalBestSolution);
    }

    /**
     * Should we be looking to maximize the function or minimize it. I prefer
     * defining it this way rather than having users modify their evaluation
     * functions to accommodate the optimizer (for example, by negating the
     * output of the function)
     */
    public enum OptimizationType {

        MAXIMIZE, MINIMIZE
    }

    public class Particle implements Comparable<Particle> {

        private double localBestFitness;
        private Number[] localBestSolution;// pbest in theory documentation
        private Number[] presentLocation;// persent (sp?) in theory documentation
        /**
         * A random number generator used to update the Particle's velocity
         */
        private final Random randomNumberGenerator;
        private Number[] velocity;

        public Particle(Number[] presentLocation) {
            this.presentLocation = presentLocation;
            this.localBestSolution = presentLocation;
            randomNumberGenerator = new Random();
        }

        @Override
        public int compareTo(Particle otherParticle) {
            return (int) (this.localBestFitness - otherParticle.localBestFitness);
        }

        public Number[] getLocalBestSolution() {
            return localBestSolution;
        }

        @Override
        public String toString() {
            return "Particle{" + "localBestFitness=" + localBestFitness + ", localBestSolution=" + localBestSolution + '}';
        }

        /**
         * Calculates the fitness of the current solution and updates the
         * particle's local best if it is strictly better
         */
        private void calculateCurrentFitness() {
            double currentFitness = functionToOptimize.Evaluate(presentLocation);
            if (optimizationType == OptimizationType.MAXIMIZE) {
                if (currentFitness > localBestFitness) {
                    localBestFitness = currentFitness;
                    localBestSolution = presentLocation;
                }
            } else {
                if (currentFitness < localBestFitness) {
                    localBestFitness = currentFitness;
                    localBestSolution = presentLocation;
                }

            }
        }

        private void setLocalBestFitness(double localBestFitness) {
            this.localBestFitness = localBestFitness;
        }

        private void updatePosition() {
            for (int i = 0; i < presentLocation.length; i++) {
                presentLocation[i] = presentLocation[i].doubleValue() + velocity[i].doubleValue();
            }
        }

        /**
         * Implementation of equation a: v[] = v[] + c1 * rand() * (pbest[] -
         * present[]) + c2 * rand() * (gbest[] - present[])
         *
         * @param currentGlobalBestFitness
         */
        private void updateVelocity(Particle currentGlobalBestFitness) {
            double rand1 = randomNumberGenerator.nextDouble();
            double rand2 = randomNumberGenerator.nextDouble();

            double c1Rand1 = learningFactorLocal * rand1;
            double c2Rand2 = learningFactorGlobal * rand2;

            //(pbest[] - present[])
            Number[] pBestMinusPresent = new Number[localBestSolution.length];
            for (int i = 0; i < localBestSolution.length; i++) {
                // Double value should have the least truncation? Why cant Number subtract Number?
                pBestMinusPresent[i] = localBestSolution[i].doubleValue() - presentLocation[i].doubleValue();

            }

            //(gbest[] - present[])
            Number[] gBestMinusPresent = new Number[localBestSolution.length];
            for (int i = 0; i < localBestSolution.length; i++) {
                // Double value should have the least truncation? Why cant Number subtract Number?
                gBestMinusPresent[i] = currentGlobalBestFitness.localBestSolution[i].doubleValue() - presentLocation[i].doubleValue();

            }

            //c1 * rand() * (pbest[] - present[])
            for (int i = 0; i < pBestMinusPresent.length; i++) {
                pBestMinusPresent[i] = pBestMinusPresent[i].doubleValue() * c1Rand1;

            }

            //c2 * rand() * (gbest[] - present[])
            for (int i = 0; i < gBestMinusPresent.length; i++) {
                gBestMinusPresent[i] = gBestMinusPresent[i].doubleValue() * c2Rand2;

            }

            // c1 * rand() * (pbest[] - present[]) + c2 * rand() * (gbest[] - present[])
            Number[] sum = new Number[localBestSolution.length];
            for (int i = 0; i < gBestMinusPresent.length; i++) {
                sum[i] = pBestMinusPresent[i].doubleValue() + gBestMinusPresent[i].doubleValue();
            }

            // update solution v[] = v[] + sum
            for (int i = 0; i < gBestMinusPresent.length; i++) {
                velocity[i] = velocity[i].doubleValue() + sum[i].doubleValue();
            }
        }

    }

    /**
     * This interface provides a way to abstract the function used during the
     * particle swarm optimization. Motivations include: finding a way to not
     * hard-code the dimension of the input vectors, allowing the function to
     * take in different types of numbers such as
     * (int,long,double,int,...)unlike other implementations that may force you
     * to one type (typically a double which may be memory wasteful if your
     * types do not require that precision). I know there will be a hit to
     * performance for boxing the types but i hope it is minor and the
     * cost-benefit makes it worth it.
     *
     * @author Steven Alan Magaña-Zook <smaganazook ~~at~~ live dot com>
     */
    public interface IParticleFunctionEvaluator {

        /**
         * Evaluate the function to be optimized at a single point in the
         * problem space.
         *
         * @param potentialSolution the new location in the input space to run
         * on the function
         * @return The "fitness" of the passed in potential solution
         */
        double Evaluate(Number... potentialSolution);

        /**
         * This method is used during the swarm's initialization and also as a
         * way to indirectly let the optimizer know the dimension of the problem
         * space.
         *
         * @return a solution in the function's range (problem space) evaluated
         * as if a random vector in the problem's domain was passed to the
         * evaluate method.
         */
        Number[] getRandomSolution();
    }
}
