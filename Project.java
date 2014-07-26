import java.util.Vector;
public class Project {
	private int prjId;
	private int userId;
	private String prjName;
	private int resultFlag;
	private String resultAddr;
	private String trainDataAddr;
	private String trainLabelsAddr;
	private String testDataAddr;
	private String testLabelsAddr;
	private String trainDataType;
	private String trainLabelsType;
	private String testDataType;
	private String testLabelsType;
	private int layerNum;
	private Vector<Param> global_info;
	private Vector<LayerInfo> layers;
	String paramFileInfo;
	public Project(int id, int userid, String name){
		prjId = id;
		userId = userid;
		prjName = name;
		resultFlag = -1;
		resultAddr = null;
		paramFileInfo = "";
		layers = new Vector<LayerInfo>();
		trainDataAddr = "";
		trainLabelsAddr = "";
		testDataAddr = "";
		testLabelsAddr = "";
		global_info = new Vector<Param>();
	}
	public Project(int id){
		prjId = id;
		resultFlag = -1;
		resultAddr = null;
		paramFileInfo = "";
		trainDataAddr = "";
		trainLabelsAddr = "";
		testDataAddr = "";
		testLabelsAddr = "";
		layers = new Vector<LayerInfo>();
		global_info = new Vector<Param>();
	}
	
	public void setResult(int flag, String addr){
		resultFlag = flag;
		resultAddr = addr;
	}
	public int getPrjId(){
		return prjId;
	}
	public int getUserId(){
		return userId;
	}
	public String getPrjName(){
		return prjName;
	}
	public int getLayerNum(){
		return layerNum;
	}
	public String getTrainDataAddr(){
		return trainDataAddr;
	}
	public String getTrainLabelsAddr()	{
		return trainLabelsAddr;
	}
	public String getTestDataAddr(){
		return testDataAddr;
	}
	public String getTestLabelsAddr(){
		return testLabelsAddr;
	}
	public void addLayer(LayerInfo layer){
		if(layers != null){
			layers.add(layer);
			layerNum ++;
		}
		else{
			layers = new Vector<LayerInfo>();
			layers.add(layer);
			layerNum ++;
		}
	}
	public Vector<LayerInfo> getLayers(){
		return layers;
		
	}
	public Vector<Param> getGlobalInfo(){
		return global_info;
	}
	public LayerInfo getLayer(int j){
		return layers.get(j);
		
	}
	public void addGlobalInfo(Param p){
		global_info.add(p);
	}
	public void setTrainDataAddr(String addr, String fileType){
		trainDataAddr = addr;
		trainDataType = fileType;
	}
	public void setTrainLabelsAddr(String addr, String fileType){
		trainLabelsAddr = addr;
		trainLabelsType = fileType;
	}
	public void setTestDataAddr(String addr, String fileType){
		testDataAddr = addr;
		testDataType = fileType;
	}
	public void setTestLabelsAddr(String addr, String fileType){
		testLabelsAddr = addr;
		testLabelsType = fileType;
	}
	
	public void fillParamFile(){
		paramFileInfo = "Layer_num:"+layerNum+"\n";
		
		for(int i = 0;i < global_info.size();i++){
			global_info.get(i).fillParamInfo();
			paramFileInfo += global_info.get(i).paramInfo;
		}
		
		for(int i = 0;i < layerNum; i++){
			
			layers.get(i).fillLayerInfo();
			paramFileInfo += layers.get(i).layerInfo;
		}
		if(trainDataAddr != ""){
			paramFileInfo += "trainData:" + trainDataAddr.replaceAll(":", "%")  + ","+ trainDataType +  "\n";
		}
		if(trainLabelsAddr != "")
			paramFileInfo += "trainLabels:" + trainLabelsAddr.replaceAll(":", "%") + ","+ trainLabelsType + "\n";
		if(testDataAddr != "")
			paramFileInfo += "testData:" + testDataAddr.replaceAll(":", "%") + ","+ testDataType + "\n";
		if(testLabelsAddr != "")
			paramFileInfo += "testLabels:" + testLabelsAddr.replaceAll(":", "%") + ","+ testLabelsType + "\n";
	}
	public int hashCode(){
		return ((Integer)prjId).hashCode();
	}
}
