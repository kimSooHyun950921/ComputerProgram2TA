
public class Client {
	private String ip;
	private int id;
	private String name;
	private int session;
	private int problem;
	public Client(String ip) {
		super();
		this.ip = ip;
		this.id = -1;
		this.name = null;
		this.session = -1;
		this.problem = -1;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSession() {
		return session;
	}
	public void setSession(int session) {
		this.session = session;
	}
	public int getProblem() {
		return problem;
	}
	public void setProblem(int problem) {
		this.problem = problem;
	}
	
	
	
	

}
