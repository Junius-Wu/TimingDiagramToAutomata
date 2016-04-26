package UML;

import java.util.ArrayList;

public class UppaalTemPlate {
String Name;
public String getName() {
	return Name;
}
public void setName(String name) {
	Name = name;
}
String declaration;
ArrayList<UppaalLocation> locations=new ArrayList<UppaalLocation>();
public ArrayList<UppaalLocation> getLocations() {
	return locations;
}
public void setLocations(ArrayList<UppaalLocation> locations) {
	this.locations = locations;
}
public ArrayList<UppaalTransition> getTransitions() {
	return transitions;
}
public void setTransitions(ArrayList<UppaalTransition> transitions) {
	this.transitions = transitions;
}
ArrayList<UppaalTransition>transitions=new ArrayList<UppaalTransition>();
}
