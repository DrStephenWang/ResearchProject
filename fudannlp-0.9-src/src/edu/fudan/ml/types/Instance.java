package edu.fudan.ml.types;
public class Instance {
	protected Object data;
	protected Object target;
	protected Object clause;
	private Object source;
	private Object tempData;
	public Instance()	{
	}
	public Instance(Object data) {
		this(data, null, null);
	}
	public Instance(Object data, Object target) {
		this(data, target, null);
	}
	public Instance(Object data, Object target, Object clause) {
		this.data = data;
		this.target = target;
		this.clause = clause;
	}
	public Object getTarget() {
		if (target == null)
			return data;
		return this.target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public void setClasue(String s) {
		this.clause = s;
	}
	public String getClasue() {
		return (String) this.clause;
	}
	public Object getSource() {
		return this.source;
	}
	public void setSource(Object source) {
		this.source = source;
	}
	public void setTempData(Object tempData) {
		this.tempData = tempData;
	}
	public Object getTempData() {
		return tempData;
	}
}
