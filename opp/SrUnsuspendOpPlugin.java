package srm.opp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class SrUnsuspendOpPlugin extends AbstractOperationServicePlugIn {
	@Override
	public void onPreparePropertys(PreparePropertysEventArgs e) {
		super.onPreparePropertys(e);
	}
	
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		super.onAddValidators(e);
//		e.addValidator(new srSuspendValidator());
	}
	
	@Override
	public void beginOperationTransaction(BeginOperationTransactionArgs e) {
		super.beginOperationTransaction(e);
		DynamicObject[] entities = e.getDataEntities();
		for(DynamicObject entity : entities) {
			Object pkId = entity.getPkValue();
			DynamicObject siBill = BusinessDataServiceHelper.loadSingle(pkId, "kded_demo_srbill");
			Date now = new Date();
			Instant end = now.toInstant();
			Instant begin = siBill.getDate("kded_suspendtime").toInstant();
			long minutes = Duration.between(end, begin).toMinutes();
			//挂起时长（h）
			BigDecimal suspendHour = new BigDecimal(minutes/60.0).setScale(1, RoundingMode.HALF_UP);
			BigDecimal oldHour = siBill.getBigDecimal("kded_suspendhour");
			siBill.set("kded_suspendhour", oldHour.add(suspendHour));
			
			//确定解挂更新工单状态：L1/L2处理中，解挂时间，挂起时长
			siBill.set("kded_unsuspendtime", new Date());
			siBill.set("kded_srstatus", siBill.get("kded_suspendstatus"));
			siBill.set("kded_suspendstatus", null);
			SaveServiceHelper.saveOperate("kded_demo_srbill", new DynamicObject[] {siBill}, OperateOption.create());
		}
	}
}
class srSuspendValidator extends AbstractValidator {

	@Override
	public void validate() {
		for(ExtendedDataEntity rowDataEntity : this.getDataEntities()){
			DynamicObject dataEntity = rowDataEntity.getDataEntity();
			String number = dataEntity.getString("kded_srstatus.number");
			if (!"D".equals(number)){
				// 校验不通过，输出一条错误提示
//				this.addErrorMessage(rowDataEntity, 
//						String.format("只有状态为挂起中的工单才可解挂"));
			}
		}
	}
	
}