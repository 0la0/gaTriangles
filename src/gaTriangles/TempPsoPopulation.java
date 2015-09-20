package gaTriangles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pso.Particle;
import pso.Population;
import pso.Position;
import pso.Velocity;

public class TempPsoPopulation {

	private ArrayList<Particle> particles;
	
//	public TempPsoPopulation (ArrayList<Particle> particles) {	
//		this.particles = (ArrayList<Particle>) particles.stream()
//				.map(particle -> {
//					Position pos = new Position(particle.getPosition().getVector());
//					Velocity vel = new Velocity(particle.getVelocity().getVector());
//					return new Particle(pos, vel);
//				})
//				.collect(Collectors.toList());	
//	}
	
	public TempPsoPopulation (List<Population> populations) {

		this.particles = (ArrayList<Particle>) populations.stream()
				.flatMap(population -> population.getParticles().stream())
				.map(particle -> {
					Position pos = new Position(particle.getPosition().getVector());
					Velocity vel = new Velocity(particle.getVelocity().getVector());
					return new Particle(pos, vel);
				})
				.collect(Collectors.toList());
	}
	
	public ArrayList<Particle> getParticles () {
		return this.particles;
	}
	
}
