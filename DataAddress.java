public class DataAddress {
	int nameId;//Ψһ��ʶ��param
	String data_type; //��ʾ����ʲô���͵�����
	String file_type;//��ʾ�ļ��ĺ�׺����
	String file_addr;//��ʾ�ļ���ַ
	String paramInfo;
	public DataAddress(String dataType, String addr ,String fileType){
		paramInfo = "";
		data_type = dataType;
		file_type = fileType;
		file_addr = addr;
	}
	public void fillParamInfo(){
		paramInfo = data_type + ":" + file_addr.replaceAll(":", "%") + ","+ file_type + "\n";
	}
	public void printInfo(){
		
		System.out.print(paramInfo);
	}
}
