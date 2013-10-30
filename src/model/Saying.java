package model;


/**
 * @author Van_Thi
 *
 */
/**
 * @author Van_Thi
 *
 */
public class Saying {
	private int id;
	private String vietnamese;
	private String english;
	private String author;
	private int favourite;
	private int contentID;
	public Saying(int id, String vietnamese, String english, String author,
			int contentID, int favourite) {
		super();
		this.id = id;
		this.vietnamese = vietnamese;
		this.english = english;
		this.author = author;
		this.contentID = contentID;
		this.favourite = favourite;
	}
	
	public Saying(){
		this(0,"","","",0,0);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVietnamese() {
		return vietnamese;
	}
	public void setVietnamese(String vietnamese) {
		this.vietnamese = vietnamese;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getCid() {
		return contentID;
	}
	public void setCid(int cid) {
		this.contentID = cid;
	}
	
	public int getFavourite() {
		return favourite;
	}

	public void setFavourite(int favourite) {
		this.favourite = favourite;
	}

	@Override
	public String toString() {
		return "ChamNgon [chid=" + id + ", nDungViet=" + vietnamese
				+ ", nDungAnh=" + english + ", tacGia=" + author
				+ ", yeuthich=" + favourite + ", cid=" + contentID + "]";
	}

	
	
	
}
