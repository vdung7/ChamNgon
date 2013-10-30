package model;

/**
 * @author Van_Thi
 *
 */
public class Content {
	private int id;
	private String name;
	public Content(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Content(){
		this(0,"");
	}
	public int getId() {
		return id;
	}
	public void setId(int cid) {
		this.id = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Content [cid=" + id + ", ten=" + name + "]";
	}
	
	
}	
