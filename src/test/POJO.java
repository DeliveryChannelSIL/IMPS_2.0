package test;
public class POJO
{
	private String[] ParamateresNames;

	private String[] ParamateresValues;

	public String[] getParamateresNames ()
	{
		return ParamateresNames;
	}
	public void setParamateresNames (String[] ParamateresNames)
	{
		this.ParamateresNames = ParamateresNames;
	}
	public String[] getParamateresValues ()
	{
		return ParamateresValues;
	}

	public void setParamateresValues (String[] ParamateresValues)
	{
		this.ParamateresValues = ParamateresValues;
	}

	@Override
	public String toString()
	{
		return "ClassPojo [ParamateresNames = "+ParamateresNames+", ParamateresValues = "+ParamateresValues+"]";
	}
	public static void main(String[] args) {
		
	}
}