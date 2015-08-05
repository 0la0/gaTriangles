package gaTriangles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GenerationsNormal {
	
	public List<PopulationNormal> populations = new ArrayList<PopulationNormal>();

	public GenerationsNormal () {}
	
	public GenerationsNormal (JSONObject parentObj) {
		JSONArray jsonGenerations = (JSONArray) parentObj.get("data");
		
		this.populations = (List<PopulationNormal>) jsonGenerations.stream().map(jsonGeneration -> {
			JSONArray population = (JSONArray) jsonGeneration;
			PopulationNormal pop = new PopulationNormal();
			
			pop.individuals = (List<IndividualNormal>) population.stream().map(jsonInd -> {
				//----
				JSONArray jsonGenome = (JSONArray) jsonInd;
				IndividualNormal ind = new IndividualNormal();
				
				ind.genome = (List<Double>) jsonGenome.stream().map(gene -> {
					return (Double) gene ;
				}).collect(Collectors.toList());
				return ind;
				//----
			}).collect(Collectors.toList());
			
			return pop;
			
		}).collect(Collectors.toList());
		
	}
	
}
