package model;

public class RecordInfo {
	
	private String type;
	private String name;
	private Object value;
	
	public RecordInfo(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if(value != null) {
			return "Info -> Type: " + type + ", Name: " + name + " Value: " + value;
		}else {
			return "Info -> Type: " + type + ", Name: " + name ;
		}
		
	}
	
	
}
