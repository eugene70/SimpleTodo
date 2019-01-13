package eugene.todo.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class TodoVo implements Serializable {

	private static final long serialVersionUID = 1967346566312437928L;

	String id;
	List<String> refs = new ArrayList<String>();
	List<String> subs = new ArrayList<String>();
	
	@NotEmpty
	String name;
	String creDt;
	String modDt;
	boolean complete;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getRefs() {
		return refs;
	}

	public void setRefs(List<String> refs) {
		this.refs = refs;
	}

	public void addRef(String ref) {
		if (ref == null || ref.length() == 0) {
			return;
		}
		if (this.refs == null) {
			this.refs = new ArrayList<String>();
		}
		this.refs.add(ref);
	}
	
	public void removeRef(String ref) {
		if (this.refs == null) {
			this.refs = new ArrayList<String>();
		}
		this.refs.remove(ref);
	}
	
	public List<String> getSubs() {
		return subs;
	}

	public void setSubs(List<String> subs) {
		this.subs = subs;
	}

	public void addSub(String sub) {
		if (sub == null || sub.length() == 0) {
			return;
		}
		if (this.subs == null) {
			this.subs = new ArrayList<String>();
		}
		this.subs.add(sub);
	}
	
	public void removeSub(String sub) {
		if (this.subs == null) {
			this.subs = new ArrayList<String>();
		}
		this.subs.remove(sub);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreDt() {
		return creDt;
	}

	public void setCreDt(String creDt) {
		this.creDt = creDt;
	}

	public String getModDt() {
		return modDt;
	}

	public void setModDt(String modDt) {
		this.modDt = modDt;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean isComplete) {
		this.complete = isComplete;
	}

	@Override
	public String toString() {
		return "TodoVo [id=" + id + ", refs=" + refs + ", subs=" + subs + ", name=" + name + ", creDt=" + creDt
				+ ", modDt=" + modDt + ", complete=" + complete + "]";
	}


}
