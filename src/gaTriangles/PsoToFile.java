package gaTriangles;

import gaViz.main.BinaryStringHelper;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pso.FitnessDistance;
import pso.Population;
import pso.Position;
import pso.PsoConfigOptions;

public class PsoToFile {
	
	private FitnessDistance fitnessFunction;
	private int numPopulations = 1;
	private int populationSize = 20;
	private int numDimensions = 7;
	private ArrayList<TempPsoPopulation> populations = new ArrayList<TempPsoPopulation>();
	private int size = 1000;
	private ImageFromDesc imgDisplay;
	
	public PsoToFile () {
		
		Position size = new Position(new int[]{
			this.size, this.size, this.size, 
			this.size, this.size, this.size,
			this.size, this.size, this.size
		});
		this.fitnessFunction = new FitnessDistance();
		this.fitnessFunction.setGoal(this.generateNewGoal());
		
		//---POPULATION SETUP---//
		//for (int i = 0; i < this.numPopulations; i++) {
			PsoConfigOptions options = new PsoConfigOptions();
			options.c1 = 0.006f;
			options.c2 = 0.001f;
			options.speedLimit = 25;
			Population p = new Population(size, this.populationSize, fitnessFunction, options);
			options.population = p;
			//this.opts.add(options);
			//this.populations.add(p);
		//}
		
		this.runAlgorithm(p);
		this.showImageAndDialog();
	}
	
	//TODO: play around with the coefficients!
	public void runAlgorithm (Population population) {
		//this.populations.add(population)
		
		for (int i = 0; i < 4000; i++) {
			population.update();
			TempPsoPopulation tpp = new TempPsoPopulation(population.getParticles());
			this.populations.add(tpp);
			
			if (this.fitnessFunction.getDimensionFitness(population, 0) < 30) {
				System.out.println("new goal at index " + i);
				this.fitnessFunction.setGoal(this.generateNewGoal());
				population.scatter();
			}
			
			if (Math.random() < 0.01) {
				population.scatter();
			}

		}
		
	
	}
	
	private int[] generateNewGoal () {
		int[] goalState = new int[this.numDimensions];
		for (int i = 0; i < this.numDimensions; i++) {
			goalState[i] = (int) Math.round(this.size * Math.random());
		}
		return goalState;
	}
	
	private JSONObject getJsonRepresentation () {
		double maxVal = BinaryStringHelper.maxVal * 1.0;
		JSONArray populations = new JSONArray();
		
		this.populations.forEach(population -> {
			
			//TODO: map list to JSONArray
			JSONArray pop = new JSONArray();
			population.getParticles().forEach(particle -> {	
				double[] position = Arrays.stream(particle.getPosition().getVector()).mapToDouble(scalar -> {
					double normalValue = scalar / (size * 1.0);
					return normalValue;
				}).toArray();
					
				JSONArray jsonPosition = new JSONArray();
				Arrays.stream(position).forEach(scalar -> jsonPosition.add(scalar));
				pop.add(jsonPosition);
			});
			
			populations.add(pop);
		});
		
		
		//----ADD META DATA----//
		JSONObject metaData = new JSONObject();
		metaData.put("algorithm", "PSO");
		metaData.put("numGenes", numDimensions);
		metaData.put("populationSize", populationSize);
		metaData.put("numGenerations", numPopulations);
	
		
		JSONObject popObj = new JSONObject();
		popObj.put("data", populations);
		popObj.put("meta", metaData);
		
		return popObj;
	}
	
	private void showImageAndDialog () {
		JSONObject jsonPopulations = this.getJsonRepresentation();
		this.renderImage(jsonPopulations);
		
		int userChoice = JOptionPane.showConfirmDialog(new JFrame(), "Save Model and Thumbnail?", "Do Not Save", JOptionPane.YES_NO_OPTION);
		if (userChoice == 0) {
			String fileName = FileHelper.printModelToFile(jsonPopulations);
			String thumbFilePath = "./files/thumbs/" + fileName;
			FileHelper.writeImage(imgDisplay.getImage(), thumbFilePath);
			System.out.println("model and thumbnail written: " + fileName);
		}
		System.exit(0);
	}
	
	private void renderImage (JSONObject jsonPopulation) {
		String thumbDescPath = "./files/imageDescriptions/thumbs.json";
		JSONObject thumbData = FileHelper.getJsonFromFile(thumbDescPath);
		ImageFormat imgFormat = new ImageFormat(thumbData);
		
		GenerationsNormal gensNormal = new GenerationsNormal(jsonPopulation);
		this.imgDisplay = new ImageFromDesc(gensNormal, imgFormat);
	}
	
	public static void main (String[] args) {
		new PsoToFile();
	}

}
