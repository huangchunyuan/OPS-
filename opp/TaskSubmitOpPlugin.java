package srm.opp;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.eye.api.log.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class TaskSubmitOpPlugin extends AbstractOperationServicePlugIn{
	@Override
		public void onPreparePropertys(PreparePropertysEventArgs e) {
		e.getFieldKeys().add("kded_srstatus");
		e.getFieldKeys().add("kded_srbillno");
		super.onPreparePropertys(e);
			
		}
	@Override
	public void beginOperationTransaction(BeginOperationTransactionArgs e) {
		for (DynamicObject dynamicObject : e.getDataEntities()) {
			Object srbillno = dynamicObject.get("kded_srbillno");
			QFilter qFilter = new QFilter("number", QCP.equals, "C");
			DynamicObject srStatus = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", qFilter.toArray());
			//loadSingle
			if(srStatus == null) {
				throw new KDException("问题单提交失败，请重试！");
			} else {
				DynamicObject loadSingle = BusinessDataServiceHelper.loadSingle(srStatus.getLong("id"),"kded_demo_srstatus");
				//需求任务单提交之后，运维工单的状态设为L2处理中
				DynamicObject srObj = BusinessDataServiceHelper.loadSingle("kded_demo_srbill", "kded_srstatus,kded_taskbillno", new QFilter[] {new QFilter("billno", QCP.equals, srbillno)});
				srObj.set("kded_srstatus", loadSingle);
				srObj.set("kded_taskbillno", dynamicObject.get("billno"));
				SaveServiceHelper.save(new DynamicObject[] {srObj});
			}
		}
		
		super.beginOperationTransaction(e);
	}
}

