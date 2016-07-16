import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
public class Graph {

	private Map<String,HashMap<String,Long>> adjacencyMap=null;
	Long maxTimeStamp;
	
	Graph(){
		adjacencyMap=new HashMap<String,HashMap<String,Long>>();
		maxTimeStamp=Long.MIN_VALUE;

	}
	void addEdge(String actor,String target, Long timeInSeconds){
		if(adjacencyMap.size()==0)
			maxTimeStamp=timeInSeconds;
		else{
			//don't add edge if the new timestamp is out of the 60second window
			if(timeInSeconds-maxTimeStamp< -60)
				return;

		}
		/** remove all the entries that are in the graph, that are now out of the 60 second window period 
		after the addition of new payment */
		removeIfOutOfWindowPeriod(timeInSeconds);
		//add the mapping from actor-target and from target-actor since it is an undirected graph
		addEdgeToMap(actor, target, timeInSeconds);
		addEdgeToMap(target, actor, timeInSeconds);
		//update the maxTimeStamp
		if(timeInSeconds>maxTimeStamp)
			maxTimeStamp=timeInSeconds;
			
	}
	
	void addEdgeToMap(String actor,String target,Long timeInSeconds){
		
		/** if the Node is already present in the main map then just 
		add the edge to the adjacency list of that map */
		if(adjacencyMap.containsKey(actor)){
			adjacencyMap.get(actor).put(target,timeInSeconds);

		}
		//else first create the Node in the main list and then add the adjacency list
		else{
			/** made and assumption that if there is an existing edge from actor-target even if the new created time is less than the existing one.
			if it is within the 60 minute window just overwrite it with the created time of the new payment */
			HashMap<String,Long> actorAdjacencyList=new HashMap<String,Long>();
			actorAdjacencyList.put(target, timeInSeconds);
			adjacencyMap.put(actor, actorAdjacencyList);
		}
		
	}
	
	/**
	 * function to remove the edge between actor and target
	 * @param actor
	 * @param target
	 */
	void removeEdge(String actor,String target){
		if(adjacencyMap.containsKey(actor)){
			if(adjacencyMap.get(actor).size()==0){
				adjacencyMap.remove(actor);
			}
			adjacencyMap.get(actor).remove(target);
		}


	}
	/**
	 * Compares and removes all the payments in the graph which are out of the 60 window period 
	 * when compared to the new payment
	 * @param newTimeStamp
	 */

	public void removeIfOutOfWindowPeriod(Long newTimeStamp) {
		Map<String, String> edgesToBeRemoved = new HashMap<String, String>();
		Iterator<String> it = adjacencyMap.keySet().iterator();
		while (it.hasNext()) {
			String actor = it.next();
			Iterator<Entry<String, Long>> it2 = adjacencyMap.get(actor).entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry<String, Long> mapEntry = it2.next();
				String target = mapEntry.getKey();
				Long timestamp = mapEntry.getValue();
				if ((newTimeStamp - timestamp > 60)) {
					edgesToBeRemoved.put(actor, target);
				}
			}
			/**
			 * remove all the payments which were out of the 60 seconds window.
			 */
			Iterator<Entry<String, String>> it3 = edgesToBeRemoved.entrySet().iterator();
			while (it3.hasNext()) {
				Map.Entry<String, String> mapEntry = it3.next();
				adjacencyMap.get(mapEntry.getKey()).remove(mapEntry.getValue());
				adjacencyMap.get(mapEntry.getValue()).remove(mapEntry.getKey());
			}

		}

	}

	/**
	 * calculates the median of the number of edges of the nodes in the graph
	 * @return median value
	 */
	float getMedian(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		for(HashMap<String,Long> h: adjacencyMap.values()){
			if(h.size()!=0)
				list.add(new Integer(h.size()));
		}
		Collections.sort(list);
		int list_length=list.size();
		//taken the mid value if the length is odd otherwise take the average of the two middle values
		return (list_length%2==0? (((float)(list.get((list_length/2)-1)+list.get(list_length/2)))/2):list.get((int)Math.ceil((list_length/2))));
	}
}
