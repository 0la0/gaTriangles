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
	
	//private FitnessDistance fitnessFunction;
	private int numPopulations = 4;
	private int populationSize = 8;
	private int numDimensions = 8;
	private ArrayList<TempPsoPopulation> populations = new ArrayList<TempPsoPopulation>();
	private ArrayList<Population> activePopulations = new ArrayList<Population>();
	private int size = 1000;
	private ImageFromDesc imgDisplay;
	
	public PsoToFile () {
		
		Position size = new Position(new int[]{
			this.size, this.size, this.size, 
			this.size, this.size, this.size,
			this.size, this.size
		});
		
		//---POPULATION SETUP---//
		for (int i = 0; i < this.numPopulations; i++) {
			PsoConfigOptions options = new PsoConfigOptions();
			options.c1 = 0.006f;
			options.c2 = 0.001f;
			options.speedLimit = 25;
			FitnessDistance fitnessFunction = new FitnessDistance();
			fitnessFunction.setGoal(this.generateNewGoal());
			//this.fitnessFunction.setGoal(this.generateNewGoal());
			Population p = new Population(size, this.populationSize, fitnessFunction, options);
			options.population = p;
			this.activePopulations.add(p);
		}
		
		this.runAlgorithm();
		this.showImageAndDialog();
	}
	
	//TODO: play around with the coefficients!
	public void runAlgorithm () {
		
		for (int i = 0; i < 4000; i++) {
			
			this.populations.add(new TempPsoPopulation(this.activePopulations));
			
			this.activePopulations.stream().forEach(population -> {
				population.update();
				
				if (population.getDimensionFitness(3) < 30) {
					int[] newGoalState = this.generateNewGoal();
					population.resetGoal(newGoalState);
					population.scatter();
				}
			});
			
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
