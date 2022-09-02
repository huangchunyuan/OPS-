package srm.bill;

import java.util.EventObject;
import java.util.Map;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.IFormView;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;

public class EngineerBillPlugin extends AbstractBillPlugIn {
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
	}
	
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		super.propertyChanged(e);
		String pname = e.getProperty().getName();
		switch(pname) {
			case "kded_engineeruser":
				doBringDeptComp(e);
				break;
		}
	}
	
	private void doBringDeptComp(PropertyChangedArgs e) {
		ChangeData[] changeSet = e.getChangeSet();
		DynamicObject engineer = (DynamicObject) changeSet[0].getNewValue();
		if(engineer != null) {
//			QFilter filter = new QFilter("id", QCP.equals, engineer.getLong("id"));
//			filter.and("entryentity.ispartjob", QCP.equals, false);
//			DynamicObject org = QueryServiceHelper.queryOne("bos_user", "id,entryentity.dpt", filter.toArray());
//			Long deptId = org.getLong("entryentity.dpt");klllllssddd
			Long userMainOrgId = UserServiceHelper.getUserMainOrgId(engineer.getLong("id"));
			this.getModel().setValue("kded_belongdept", userMainOrgId);
			Map<String, Object> companyByOrg = OrgUnitServiceHelper.getCompanyByAdminOrg(userMainOrgId);
			this.getModel().setValue("kded_belongcompany", companyByOrg.get("id"));
		}
	}
}
