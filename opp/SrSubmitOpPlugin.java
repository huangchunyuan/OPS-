package srm.opp;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.eye.api.log.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class SrSubmitOpPlugin extends AbstractOperationServicePlugIn {
	@Override
	public void onPreparePropertys(PreparePropertysEventArgs e) {
		super.onPreparePropertys(e);
		e.getFieldKeys().add("kded_srstatus");
	}
	
	@Override
	public void beginOperationTransaction(BeginOperationTransactionArgs e) {
		super.beginOperationTransaction(e);
		DynamicObject[] dataEntities = e.getDataEntities();
		for(DynamicObject data : dataEntities) {
			QFilter qFilter = new QFilter("number", QCP.equals, "B");
			DynamicObject srStatus = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", qFilter.toArray());
			if(srStatus == null) {
				throw new KDException("问题单提交失败，请重试！");
			} else {
				DynamicObject loadSingle = BusinessDataServiceHelper.loadSingle(srStatus.getLong("id"),"kded_demo_srstatus");
				data.set("kded_srstatus", loadSingle);
			}
		}
	}
}
