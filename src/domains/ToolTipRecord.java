package domains;

public interface ToolTipRecord {
	public default String getTooltip(){
		return this.toString();
	};
}
