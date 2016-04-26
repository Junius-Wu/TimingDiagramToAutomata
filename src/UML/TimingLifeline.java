/**
 * 
 */
package UML;

import java.util.ArrayList;

/**
 * @author bojan
 *
 */
public class TimingLifeline {
 public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLifelineID() {
		return LifelineID;
	}

	public void setLifelineID(String lifelineID) {
		LifelineID = lifelineID;
	}

	
String name;
 String LifelineID;
ArrayList<lineInfo>  xrefs;
public ArrayList<lineInfo> getXrefs() {
	return xrefs;
}

public void setXrefs(ArrayList<lineInfo> xrefs) {
	this.xrefs = xrefs;
}


}
