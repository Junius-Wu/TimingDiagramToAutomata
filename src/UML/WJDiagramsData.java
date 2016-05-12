package UML;

import java.util.ArrayList;


public class WJDiagramsData{
	ArrayList <String> ids = new ArrayList<String>();
	
	ArrayList<TimingLifeline> lifelines = new ArrayList<TimingLifeline>();
	ArrayList<connector> connectors = new ArrayList<connector>();

	public String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDiagramID() {
		return diagramID;
	}
	public void setDiagramID(String diagramID) {
		this.diagramID = diagramID;
	}
	public String diagramID;
	
	public ArrayList<String> getIds() {
		return ids;
	}
	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
	public ArrayList<TimingLifeline> getLifelines() {
		return lifelines;
	}
	public void setLifelines(ArrayList<TimingLifeline> lifelines) {
		this.lifelines = lifelines;
	}
	public ArrayList<connector> getConnectors() {
		return connectors;
	}
	public void setConnectors(ArrayList<connector> connectors) {
		this.connectors = connectors;
	}
	
}