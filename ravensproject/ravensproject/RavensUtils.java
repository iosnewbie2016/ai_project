package ravensproject;

import java.util.HashMap;

public class RavensUtils {
	public static final String ANGLE = "angle";
	public static final String ABOVE = "above";
	public static final String INSIDE = "inside";
	public static final String SHAPE = "shape";
	public static final String FILL = "fill";
	public static final String SIZE = "size";
	
	public static final int SHAPE_WEIGHT = 5;
	public static final int FILL_WEIGHT = 3;
	public static final int SIZE_WEIGHT = 3;
	public static final int OTHER_WEIGHT = 1;
	
	
	
	public RavensPair<Integer, HashMap<String,String>> DiffRavensObjects(RavensObject fig1, RavensObject fig2){
		HashMap<String, String> diffs = new HashMap<>();
		int simScore = 0;
		HashMap<String, String> fig1Atrib = fig1.getAttributes();
		HashMap<String, String> fig2Atrib = fig2.getAttributes();
		for (String aAttribType : fig1Atrib.keySet()){
			if(fig2Atrib.containsKey(aAttribType)){
				//if the shape was rotated or reflected, we need to know the difference in angle, no jus tthe second value.
				if(aAttribType.equals(ANGLE)){
					int angleChange = Math.abs(Integer.parseInt(fig1Atrib.get(aAttribType)) - Integer.parseInt(fig2Atrib.get(aAttribType)));
					
					System.out.println("Angle diff:" + angleChange);
					if(angleChange > 0){
						diffs.put(ANGLE, angleChange+"");
					} else{
						simScore += OTHER_WEIGHT;
					}
				}
				else if(aAttribType.equals(ABOVE)){
					
					//we dont really care who it's above when we diff it, just taht it's still above something
					int numAAbove = 0;
					int numBAbove = 0;
					numAAbove = fig1Atrib.get(aAttribType).split(",").length;
					numBAbove = fig2Atrib.get(aAttribType).split(",").length;
					if(numAAbove != numBAbove){
						diffs.put(ABOVE, numBAbove + "");
					}	else{
						simScore += OTHER_WEIGHT;
					}
				}
				else if(aAttribType.equals(INSIDE)){
					
					//we dont really care who it's above when we diff it, just taht it's still above something
					int numAAbove = 0;
					int numBAbove = 0;
					numAAbove = fig1Atrib.get(aAttribType).split(",").length;
					numBAbove = fig2Atrib.get(aAttribType).split(",").length;
					if (numAAbove != numBAbove) {
						diffs.put(INSIDE, numBAbove + "");
					} else {
						simScore += OTHER_WEIGHT;
					}
				} else if (fig2Atrib.get(aAttribType).compareTo(
						fig1Atrib.get(aAttribType)) != 0) {
					diffs.put(aAttribType, fig2Atrib.get(aAttribType));

				} else {
					// We keep track of differences, but we should rate based on
					// similarity.
					simScore+= AttributeValue(aAttribType);
				}
			} else {
				//figure 1 has extra attribute
				diffs.put(aAttribType, fig1Atrib.get(aAttribType));
				simScore -= OTHER_WEIGHT;
			}
		}
		
		for(String key : fig2Atrib.keySet()){
			if(!fig1Atrib.containsKey(key)){
				//fig 2 has a key that fig 1 doesnt, it's a diff!!!
				diffs.put(key, fig2Atrib.get(key));
				simScore -= OTHER_WEIGHT;
				
			}
		}
		return new RavensPair<Integer, HashMap<String,String>>(simScore, diffs) ;	
	}
	
	
	
	public int AttributeValue(String attribute){
		switch (attribute) {
		case SHAPE:
			return SHAPE_WEIGHT;
		case FILL:
			return FILL_WEIGHT;
		case SIZE:
			return SIZE_WEIGHT;
		default:
			return OTHER_WEIGHT;

		}
	}
	public HashMap<String, String> FindPartners(
			HashMap<String, RavensObject> objA,
			HashMap<String, RavensObject> objB) {

		HashMap<String, String> partners = new HashMap<>();
		
		for(String aName : objA.keySet()){
			HashMap<String, Integer> potentialMatch = new HashMap<>();
			//record all the diffs
			for(String bName : objB.keySet()){
				RavensPair<Integer , HashMap<String, String >> result = DiffRavensObjects(objA.get(aName), objB.get(bName));
				HashMap<String,String> diffs = result.getR();
				potentialMatch.put(bName,result.getL() );
				
			}
			//find the least differences.
			//We will use the first one found in most cases, not the best method, so it will lower our confidence
			int mostSim = -1000;
			String potentialPotential = "";
			for(String potent : potentialMatch.keySet()){
				if(potentialMatch.get(potent) > mostSim){
					potentialPotential = potent;
					mostSim = potentialMatch.get(potent);
				}
			}
			if(partners.values().contains(potentialPotential)){
				//someone has already been paired with this one
				//is there a second potential with same amount of diffs?
				//Hashmaps arn't gurenteed ordered, so we're pushing our luck, it will have to do for now.
				int count = 0;
				for( int val : potentialMatch.values()){
					if(val == mostSim){
						count++;
					}
				}
				if (count >1){
					
					//we have multiple matches! pick the second one.
					for(String newMatch : potentialMatch.keySet()){
						if(newMatch.compareTo(potentialPotential) != 0 && potentialMatch.get(newMatch) == mostSim){
							partners.put(aName, newMatch);
							System.out.println("*Partners [ " + aName + "," + newMatch + "]");
							break;
						}
					}
				} else{
					partners.put(aName, "none");
					System.out.println("Partners [ " + aName + ", removed]");
				}
			}else 
			{//no matches already
				partners.put(aName, potentialPotential);
				System.out.println("Partners [ " + aName + "," + potentialPotential + "]");
			}
			
			
			
			
			
			
		}
		//if any figures from B were not matched, we can guess that they were added and dont have a partner.
		for (String bObj : objB.keySet()){
			if(!partners.values().contains(bObj)){
				System.out.println("Partners [ " + bObj + ",added]");
				partners.put(bObj, "added");
			}
		}
		return partners;
	}
	
	public RavensPair<Integer, HashMap<String,String>> NewDiffRavensObjects(RavensObject fig1, RavensObject fig2){
		HashMap<String, String> diffs = new HashMap<>();
		int simScore = 0;
		HashMap<String, String> fig1Atrib = fig1.getAttributes();
		HashMap<String, String> fig2Atrib = fig2.getAttributes();
		for (String aAttribType : fig1Atrib.keySet()){
			if(fig2Atrib.containsKey(aAttribType)){
				//if the shape was rotated or reflected, we need to know the difference in angle, no jus tthe second value.
				if(aAttribType.equals(ANGLE)){
					int angleChange = Math.abs(Integer.parseInt(fig1Atrib.get(aAttribType)) - Integer.parseInt(fig2Atrib.get(aAttribType)));
					
					System.out.println("Angle diff:" + angleChange);
					if(angleChange > 0){
						diffs.put(ANGLE, angleChange+"");
					} else{
						simScore += OTHER_WEIGHT;
					}
				}
				else if(aAttribType.equals(ABOVE)){
					
					//we dont really care who it's above when we diff it, just taht it's still above something
					int numAAbove = 0;
					int numBAbove = 0;
					numAAbove = fig1Atrib.get(aAttribType).split(",").length;
					numBAbove = fig2Atrib.get(aAttribType).split(",").length;
					if(numAAbove != numBAbove){
						diffs.put(ABOVE, numBAbove + "");
					}	else{
						simScore += OTHER_WEIGHT;
					}
				}
				else if(aAttribType.equals(INSIDE)){
					
					//we dont really care who it's above when we diff it, just taht it's still above something
					int numAAbove = 0;
					int numBAbove = 0;
					numAAbove = fig1Atrib.get(aAttribType).split(",").length;
					numBAbove = fig2Atrib.get(aAttribType).split(",").length;
					if (numAAbove != numBAbove) {
						diffs.put(INSIDE, numBAbove + "");
					} else {
						simScore += OTHER_WEIGHT;
					}
				} else if (fig2Atrib.get(aAttribType).compareTo(
						fig1Atrib.get(aAttribType)) != 0) {
					diffs.put(aAttribType, fig2Atrib.get(aAttribType));

				} else {
					// We keep track of differences, but we should rate based on
					// similarity.
					simScore+= AttributeValue(aAttribType);
				}
			} else {
				//figure 1 has extra attribute
				diffs.put(aAttribType, fig1Atrib.get(aAttribType));
				simScore -= OTHER_WEIGHT;
			}
		}
		
		for(String key : fig2Atrib.keySet()){
			if(!fig1Atrib.containsKey(key)){
				//fig 2 has a key that fig 1 doesnt, it's a diff!!!
				diffs.put(key, fig2Atrib.get(key));
				simScore -= OTHER_WEIGHT;
				
			}
		}
		return new RavensPair<Integer, HashMap<String,String>>(simScore, diffs) ;	
	}
}

