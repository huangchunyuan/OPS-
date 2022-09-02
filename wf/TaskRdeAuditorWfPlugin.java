package srm.wf;

import java.util.ArrayList;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;

public class TaskRdeAuditorWfPlugin implements IWorkflowPlugin {

	@Override
	public List<Long> calcUserIds(AgentExecution execution) {
		List<Long> list = new ArrayList<>();
		String businessKey = execution.getBusinessKey();
		QFilter filter = new QFilter("id", QCP.equals, businessKey);
		DynamicObject object = QueryServiceHelper.queryOne("kded_demo_taskbill", "kded_module.kded_rdeengineer.kded_engineeruser as engineer", filter.toArray());
		if(object != null) {
			list.add(object.getLong("engineer"));
		}
		return list;
	}
}
