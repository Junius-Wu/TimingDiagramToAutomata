package UML;

public class UppaalLocation {
	Boolean init=false;
	public Boolean getInit() {
		return init;
	}
	public void setInit(Boolean init) {
		this.init = init;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	String lineEAID;
	public String getLineEAID() {
		return lineEAID;
	}
	public void setLineEAID(String lineEAID) {
		this.lineEAID = lineEAID;
	}
	String Name;
	int id;
	int Type=0;   //0Ϊ���� 1Ϊ����
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	String Invariant;
	public String getInvariant() {
		return Invariant;
	}
	public void setInvariant(String invariant) {
		Invariant = invariant;
	}
}
