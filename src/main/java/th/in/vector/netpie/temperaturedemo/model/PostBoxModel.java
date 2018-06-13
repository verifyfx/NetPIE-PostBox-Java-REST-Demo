package th.in.vector.netpie.temperaturedemo.model;

public class PostBoxModel {
	String msg;
	long ts;
	String tag;
	
	public PostBoxModel(String msg, long ts, String tag) {
		super();
		this.msg = msg;
		this.ts = ts;
		this.tag = tag;
	}
	public String getMsg() {
		return msg;
	}
	public long getTs() {
		return ts;
	}
	public String getTag() {
		return tag;
	}
}
