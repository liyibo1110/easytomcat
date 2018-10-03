package ex15.pyrmont.digestertest;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class EmployeeRuleSet extends RuleSetBase {

	@Override
	public void addRuleInstances(Digester digester) {
		digester.addObjectCreate("employee", "ex15.pyrmont.digestertest.Employee");
		digester.addSetProperties("employee");	//调用set方法赋值
		
		digester.addObjectCreate("employee/office", "ex15.pyrmont.digestertest.Office");
		digester.addSetProperties("employee/office");	//调用set方法赋值
		digester.addSetNext("employee/office", "addOffice");
		
		digester.addObjectCreate("employee/office/address", "ex15.pyrmont.digestertest.Address");
		digester.addSetProperties("employee/office/address");
		digester.addSetNext("employee/office/address", "setAddress");
	}

}
