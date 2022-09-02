package srm.wf;

import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.db.tx.TX;
import kd.bos.db.tx.TXHandle;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;

public class RdeAuditWfPlugin implements IWorkflowPlugin {

	@Override
	public void notify(AgentExecution execution) {
		
		List<Long> currentApprover = execution.getCurrentApprover();
		if(!currentApprover.isEmpty()) {
			//查询rde工程师
			QFilter filter = new QFilter("kded_engineeruser", QCP.equals, currentApprover.get(0));
			DynamicObject engineer = QueryServiceHelper.queryOne("kded_demo_engineer", "id", filter.toArray());
			if(engineer != null) {
				//开启事务
				TXHandle tx = TX.required(this.getClass().getName());
				try {
					//更新任务单的任务审批人
					String entityNumber = execution.getEntityNumber();
					String key = execution.getBusinessKey();
					DynamicObject taskBill = BusinessDataServiceHelper.loadSingle(key, entityNumber);
					taskBill.set("kded_taskauditor", engineer.getLong("id"));
					taskBill.set("billstatus", "C");
					
					//更新运维工单的工单状态
					filter = new QFilter("billno", QCP.equals, taskBill.getString("kded_srbillno"));
					DynamicObject srBill = BusinessDataServiceHelper.loadSingle("kded_demo_srbill", "id,billno,kded_srstatus", filter.toArray());
					filter = new QFilter("number", QCP.equals, "F");
					DynamicObject srStatus = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", "id", filter.toArray());
					if(srBill != null && srStatus != null) {
						srBill.set("kded_srstatus", srStatus.getLong("id"));
					}
					//保存任务单和运维工单
					SaveServiceHelper.save(new DynamicObject[] {taskBill});
					SaveServiceHelper.save(new DynamicObject[] {srBill});
					
				} catch(Throwable e) {
					tx.setRollback(true);
					throw e;
				}
			}
		}
		
	}
}
