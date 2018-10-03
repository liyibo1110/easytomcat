package ex15.pyrmont.digestertest;

import java.io.File;

import org.apache.commons.digester.Digester;

public class Test02 {

	public static void main(String[] args) {
		String path = System.getProperty("user.dir") + File.separator +"etc";
		File file = new File(path, "employee2.xml");
		//开始定制解析xml规则
		Digester digester = new Digester();
		digester.addObjectCreate("employee", "ex15.pyrmont.digestertest.Employee");
		digester.addSetProperties("employee");	//调用set方法赋值
		
		digester.addObjectCreate("employee/office", "ex15.pyrmont.digestertest.Office");
		digester.addSetProperties("employee/office");	//调用set方法赋值
		digester.addSetNext("employee/office", "addOffice");
		
		digester.addObjectCreate("employee/office/address", "ex15.pyrmont.digestertest.Address");
		digester.addSetProperties("employee/office/address");
		digester.addSetNext("employee/office/address", "setAddress");
		
		//开始解析
		try {
			Employee employee = (Employee)digester.parse(file);
			System.out.println("-------------------------------------------------");
			for(Office office : employee.getOffices()) {
				Address address = office.getAddress();
				System.out.println(office.getDescription());
				System.out.println("Address : " + address.getStreetNumber() + " " + address.getStreetName());
				System.out.println("--------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
