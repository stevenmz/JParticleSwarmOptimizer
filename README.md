JParticleSwarmOptimizer
=======================

##About

This project implements Particle Swarm Optimization (PSO) in Java following the theory laid out on the [Swarm Intelligence site](http://www.swarmintelligence.org/index.php). PSO is a handy optimization technique when you are trying to find optimal values of a high dimensional problem-space with an error function that can be computed discreetly but perhaps is not differentiable.

###Further reading on PSO

* [Swarm Intelligence site](http://www.swarmintelligence.org/index.php)
* [Wikipedia Article](http://en.wikipedia.org/wiki/Particle_swarm_optimization)
* [Video visualizing what the particles do during optimization](http://vimeo.com/17407010)

##How to use this code
This project follows the maven convention for java projects, and has only one external dependency (JUnit). As such, it is simple to get and start using this in your own project.

1. Clone this repository: git clone https://github.com/stevenmz/JParticleSwarmOptimizer.git
2. Open this maven project in the IDE of your choice (Netbeans, Eclipse, etc.) or browse to this project's root directory and build it there (mvn install)
3. This project currently has one implementation class ParticleSwarmOptimizer and one unit test class PSOTests that you can use as-is or modify to fit your needs.


###Example Use: Minimize the function f(a,b,c) = 1234 + a^2 + b^2 + c^2
```java
// set the hyper-parameters used by the particle swarm optimization algorithm.
// Experiment with different values to see the rate of execution and accuracy
final int NUMBER_OF_PARTICLES = 10; 
final int NUMBER_OF_ITERATIONS = 100;

// Define the function we want the algorithm to optimize
final ParticleSwarmOptimizer.IParticleFunctionEvaluator functionToMinimize = new ParticleSwarmOptimizer.IParticleFunctionEvaluator() {
    /**
     * This method performs the evaluation of the function we want to optimize
     * Minimize: f(a,b,c) = 1234 + a^2 + b^2 + c^2
     */
	@Override
    public double Evaluate(Number... potentialSolution) {
        return 1234
                + Math.pow(potentialSolution[0].intValue(), 2)
                + Math.pow(potentialSolution[1].intValue(), 2)
                + Math.pow(potentialSolution[2].intValue(), 2);
    }

    /**
     * This method is used to initialize particles to random locations 
     * throughout the problem space when the swarm begins the optimization routine. 
     */
    @Override
    public Number[] getRandomSolution() {
        Random r = new Random();
        Number[] solution = new Number[3];
        solution[0] = r.nextInt();
        solution[1] = r.nextInt();
        solution[2] = r.nextInt();
        return solution;
    }
};

ParticleSwarmOptimizer optimizer = new ParticleSwarmOptimizer(
        functionToMinimize,
        NUMBER_OF_PARTICLES,
        NUMBER_OF_ITERATIONS,
        ParticleSwarmOptimizer.OptimizationType.MINIMIZE);

// Perform the optimization
ParticleSwarmOptimizer.Particle optimalPartical = optimizer.optimize();

// Retrieve the solution computed by the algorithm
final Number[] bestSolution = optimalPartical.getLocalBestSolution();
```