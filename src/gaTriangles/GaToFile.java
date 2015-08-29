package gaTriangles;

import gaViz.breed.BreedStandard;
import gaViz.crossover.CrossoverGenenome;
import gaViz.fitness.FitnessCustomGoal;
import gaViz.main.BinaryStringHelper;
import gaViz.main.GaConfigOptions;
import gaViz.main.GaGenerator;
import gaViz.main.Population;
import gaViz.mutate.MutateStandard;
import gaViz.probability.ProbabilityStandard;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * Run the algorithm and write model and thumbnail to file
 */
public class GaToFile {

	private GaConfigOptions gaConfigOptions;
	private GaGenerator gaGenerator;
	private ImageFromDesc imgDisplay;
	private float mutateThresh = 0.0009f;
	private double fitnessUpperBound = 0.95;
	private double fitnessLowerBound = 0.87;
	private int goalChangeCnt = 0;
	
	public GaToFile () {
		//configure genetic algorithm
		this.gaConfigOptions = new GaConfigOptions();
		this.gaConfigOptions.numGenes = 8;
		this.gaConfigOptions.populationSize = 40;
		this.gaConfigOptions.numGenerations = 4000;
		this.gaConfigOptions.geneLength = 10;
		this.gaConfigOptions.mutateObj = new MutateStandard(mutateThresh);
		this.gaConfigOptions.fitnessObj = new FitnessCustomGoal(
				new Random().doubles(gaConfigOptions.numGenes).toArray());
		this.gaConfigOptions.crossoverObj = new CrossoverGenenome();
		this.gaConfigOptions.breederObj = new BreedStandard();
		this.gaConfigOptions.probabilityObj = new ProbabilityStandard();
 		
		//run algorithm and show results
 		this.gaGenerator = new GaGenerator(this.gaConfigOptions);
		this.runGeneticAlgorithm();
		this.showImageAndDialog();
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
	
	/**
	 * Main algorithm:
	 *  For a predetermined number of iterations, do:
	 *    -Step1: generate an arbitrary goal state
	 *    -Step2: when gene[0] makes a predetermined fitness level (fitnessUpperBound)
	 *            increase mutation until fitnessLowerBound is reached
	 *    -Step3: go to step1
	 */
	public void runGeneticAlgorithm () {
		int cnt = 0;

		boolean isDecreasing = true;
		ExponentialMovingAverage expAvg = new ExponentialMovingAverage(0.3);
		
		while (cnt++ < this.gaConfigOptions.numGenerations) {
			this.gaGenerator.createNewPopulation();
			Population latestPopulation = this.gaGenerator.getLatestPopulation();
			
			double geneFitness = ((FitnessCustomGoal) this.gaConfigOptions.fitnessObj).getGeneFitness(latestPopulation, 0);
			double ema = expAvg.getAvg(geneFitness);
			
			if (isDecreasing) {
				if (ema > this.fitnessUpperBound) {
					isDecreasing = false;
					System.out.println("start increase at index: " + cnt);
				}
			}
			else {
				if (ema < this.fitnessLowerBound) {
					System.out.println("generating new goal at index: " + cnt);
					this.goalChangeCnt++;
					generateNewGoal();
					expAvg.reset();
					mutateThresh = 0.0005f;
					this.gaConfigOptions.mutateObj = new MutateStandard(mutateThresh);
					this.gaConfigOptions.fitnessObj.calcFitness(latestPopulation);
					this.gaConfigOptions.probabilityObj.calc(latestPopulation);
					isDecreasing = true;
				}
				else {
					mutateThresh += 0.0001f;
					this.gaConfigOptions.mutateObj = new MutateStandard(mutateThresh);
				}
			}
			
		}
	}
	
	private double[] generateNewGoal () {
		double[] goal = new Random().doubles(gaConfigOptions.numGenes).toArray();
		this.gaConfigOptions.fitnessObj.setGoal(goal);
		return goal;
	}
	
	private void renderImage (JSONObject jsonPopulation) {
		String thumbDescPath = "./files/imageDescriptions/thumbs.json";
		JSONObject thumbData = FileHelper.getJsonFromFile(thumbDescPath);
		ImageFormat imgFormat = new ImageFormat(thumbData);
		
		GenerationsNormal gensNormal = new GenerationsNormal(jsonPopulation);
		this.imgDisplay = new ImageFromDesc(gensNormal, imgFormat);
	}
	
	private JSONObject getJsonRepresentation () {
		double maxVal = BinaryStringHelper.maxVal * 1.0;
		JSONArray populations = new JSONArray();
		
		gaGenerator.getGenerations().getPopulations().forEach(population -> {
			
			//TODO: map list to JSONArray
			JSONArray pop = new JSONArray();
			population.getIndividuals().forEach(individual -> {
				List<Double> genome = individual.getGenome().stream().map(gene -> {
					return (Double) (gene / maxVal);
				}).collect(Collectors.toList());
				
				JSONArray jsonGenome = new JSONArray();
				genome.forEach(gene -> jsonGenome.add(gene));
				
				pop.add(jsonGenome);
			});
			
			populations.add(pop);
		});
		
		//----ADD META DATA----//
		JSONObject metaData = new JSONObject();
		metaData.put("numGenes", this.gaConfigOptions.numGenes);
		metaData.put("populationSize", this.gaConfigOptions.populationSize);
		metaData.put("numGenerations", this.gaConfigOptions.numGenerations);
		metaData.put("geneLength", this.gaConfigOptions.geneLength);
		metaData.put("fitnessLowerBound", this.fitnessLowerBound);
		metaData.put("fitnessUpperBound", this.fitnessUpperBound);
		metaData.put("goalChangeCnt", this.goalChangeCnt);
		
		JSONObject popObj = new JSONObject();
		popObj.put("data", populations);
		popObj.put("meta", metaData);
		
		return popObj;
	}
	
	// wikipedia.org/wiki/Exponential_smoothing#The_exponential_moving_average
	private class ExponentialMovingAverage {
		
		private double a;
		private double st = 0;
		
		public ExponentialMovingAverage (double a) {
			this.a = a;
		}
		
		public double getAvg (double xt) {
			if (st == 0) st = xt;
			this.st = a * xt + (1 - a) * st;
			return st;
		}
		
		public void reset () {
			this.st = 0;
		}
		
	}
	
	public static void main (String[] args) {
		new GaToFile();
	}
	
}
