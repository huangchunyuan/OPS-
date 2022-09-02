package srm.form;

import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.Donothing;
import kd.bos.form.control.AttachmentPanel;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.field.DateTimeEdit;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.attachment.AttachmentFieldServiceHelper;
import srm.utils.DataEntityUtil;

public class SuspendFormPlugin extends AbstractFormPlugin{
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
	}
	
	@Override
	public void beforeClick(BeforeClickEvent evt) {
		super.beforeClick(evt);
	}
	
	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		Object source = args.getSource();
		if(source instanceof Donothing) {
			
			DataEntityUtil.ConvertToString(this.getModel().getDataEntity(true), new String[] {"kded_attachment"});
			
			Donothing op = (Donothing) source;
			String key = op.getOperateKey();
			if("suspendconfirm".equals(key)) {
				doSuspendConfirm(args);
			}
		}
	}
	
	private void doSuspendConfirm(BeforeDoOperationEventArgs e) {
		Date suspendTo = (Date) this.getModel().getValue("kded_suspendtotime");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 4);
		Date after4hour = c.getTime();
		if(!suspendTo.after(after4hour)) {
			this.getView().showTipNotification("计划挂起时长不能小于4小时，请重新选择计划挂起结束时间");
			e.setCancel(true);
		} else {
			DynamicObject dataEntity = this.getModel().getDataEntity();
			Map<String, Object> data = new HashMap<String, Object>();
			//获取动态表单数据包，附件面板没有包含在内
			data.put("dynamicObject", dataEntity);
			//获取附件面板附件数据并通过AttachmentFieldServiceHelper转成bd_attachment集合便于上传到目标单附件字段
			AttachmentPanel attachmentPanel = this.getView().getControl("kded_attachmentpanelap");
			List<Map<String, Object>> attachmentData = attachmentPanel.getAttachmentData();
			List<DynamicObject> attachments = AttachmentFieldServiceHelper.saveAttachments("kded_worknote", this.getView().getPageId(), attachmentData);
			data.put("attachments", attachments);
			this.getView().returnDataToParent(data);
			this.getView().close();
		}
	}
	
	@Override
	public void beforeClosed(BeforeClosedEvent e) {
		super.beforeClosed(e);
	}

	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		DateTimeEdit edit = this.getControl("kded_suspendtotime");
		edit.setMinDate(new Date());
		
	}
	
}
