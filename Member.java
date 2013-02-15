package outlineReader;
import java.util.ArrayList;


class Member {
	private String parent;
	private String name;
	private String alias;
	private ArrayList<String> UDA;
	private ArrayList<String> attributes = null;
	private String dataStorage;
	private String timeBalance;
	private String memberFormula;
	private String twoPassCalc;
	private String consolidation;
	private String varianceReporting;
	private String delimiter;
	
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String getVarianceReporting() {
		return (varianceReporting == null || varianceReporting.equals("")) ? "" : " " + varianceReporting;
	}

	public void setVarianceReporting(String varianceReporting) {
		varianceReporting = varianceReporting.replaceAll("\"", " ");
		varianceReporting = varianceReporting.equals("Expense") ? "E" : varianceReporting;
		varianceReporting = varianceReporting.trim();
		this.varianceReporting = varianceReporting;
	}

	String getConsolidation() {
		return (consolidation == null || consolidation.equals("")) ? "+" : consolidation;
	}

	public void setConsolidation(String consolidation) {
		consolidation = consolidation.replaceAll("\"", " ");
		consolidation = consolidation.trim();
		this.consolidation = consolidation.equals("") ? "+" : consolidation;
	}

	public String getTwoPassCalc() {
		return (twoPassCalc == null || twoPassCalc.equals("")) ? "" : " " + twoPassCalc;
	}

	public void setTwoPassCalc(String twoPassCalc) {
		twoPassCalc = twoPassCalc.replaceAll("\"", " ");
		twoPassCalc = twoPassCalc.equals("Y") ? "T" : twoPassCalc;
		twoPassCalc = twoPassCalc.trim();
		this.twoPassCalc = twoPassCalc;
	}

	String getMemberFormula() {
		return (memberFormula == null || memberFormula.equals("")) ? "" : " " + memberFormula;
	}

	public void setMemberFormula(String memberFormula) {
		this.memberFormula = memberFormula.trim();
	}

	public String getTimeBalance() {
		return (timeBalance == null || timeBalance.equals("")) ? "" : " " + timeBalance;
	}

	public void setTimeBalance(String timeBalance) {
		timeBalance = timeBalance.replaceAll("\"", " ");
		if (timeBalance.equals("Last")) {
			timeBalance = "L";
		} else if (timeBalance.equals("First")) {
			timeBalance = "F";
		} else if (timeBalance.equals("Average")) {
			timeBalance = "A";
		}
		timeBalance = timeBalance.trim();
		this.timeBalance = timeBalance;
	}

	public void setDataStorage(String dataStorage) {
		if (dataStorage.equals("DynamicCalc")) {
			dataStorage = "X";
		} else if (dataStorage.equals("DynamicCalcAndStore")) {
			dataStorage = "V";
		} else if (dataStorage.equals("LabelOnly")) {
			dataStorage = "O";
		} else if (dataStorage.equals("NeverShare")){
			dataStorage = "N";
		} else if (dataStorage.equals("ShareData")){
			dataStorage = "";
		}
		dataStorage = dataStorage.trim();
		this.dataStorage = dataStorage;
	}

	String getDataStorage() {
		return (dataStorage == null || dataStorage.equals("")) ? "" : " " + dataStorage;
	}

	public Member() {
		
	}
	
	public Member(String name, String delimiter) {
		this.name = name;
		this.delimiter = delimiter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias == null ? "" : alias;
	}

	public void setAlias(String alias) {
		this.alias = alias.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\"", " ").trim();
	}
	
	public void addUDA(String uda) {
        if (UDA == null) UDA = new ArrayList<String>();
        UDA.add(uda);
    }
	
	String getUDA() {
		if (this.UDA == null) return "";

		StringBuilder out = new StringBuilder();
		for (Object o : UDA)
		{
		  out.append(o.toString());
		  out.append(delimiter);
		}
		return out.toString();
	}
	
	public int countUDA() {
		return UDA == null ? 0 : UDA.size();
	}
	
   public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	String getAttributes() {
		if (this.attributes == null) return "";
		
		StringBuilder out = new StringBuilder();
		for (Object o : attributes)
		{
		  out.append(o.toString());
		  out.append(delimiter);
		}
		return out.toString().substring(0, out.toString().length()-1);
	}

	public void addAttribute(String attribute) {
        if (attributes == null) attributes = new ArrayList<String>();
        attributes.add(attribute);
	}
	
	public String toString() {
		String properties = getConsolidation() + getTimeBalance() + getDataStorage() + getTwoPassCalc() + getVarianceReporting();
		return this.parent
    			+ delimiter + name
    			+ delimiter + getAlias()
				+ delimiter + properties.trim()
				+ delimiter + getMemberFormula()
				+ delimiter + getAttributes()
				+ delimiter + getUDA();
	}

}
