package srm.bill;

import java.util.EventObject;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ClickListener;
import kd.bos.form.field.TextEdit;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;

public class TaskBillPlugin extends AbstractBillPlugIn implements ClickListener {
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		Label edit = this.getControl("kded_srbillnoval");
		edit.addClickListener(this);
	}

	@Override
	public void click(EventObject evt) {
		super.click(evt);
		Object source = evt.getSource();
		if(source instanceof Label) {
			Label edit = (Label) source;
			String key = edit.getKey();
			
			switch(key) {
			case "kded_srbillnoval":
				showSrBill();
				break;
			}
		}
	}

	private void showSrBill() {
		String srBillNo = (String) this.getModel().getValue("kded_srbillno");
		QFilter fiter = new QFilter("billno", QCP.equals, srBillNo);
		DynamicObject srBill = QueryServiceHelper.queryOne("kded_demo_srbill", "id", fiter.toArray());
		
		if(srBill != null) {
			BillShowParameter billParameter = new BillShowParameter();
			billParameter.setFormId("kded_demo_srbill");
			billParameter.setPkId(srBill.getLong("id"));
			billParameter.getOpenStyle().setShowType(ShowType.Modal);
			billParameter.setCustomParam("onlyview", "true");
			this.getView().showForm(billParameter);
		}
	}
	@Override
	public void afterBindData(EventObject e) {
		String srBillNo = this.getModel().getDataEntity().getString("kded_srbillno");
		Label label = this.getControl("kded_srbillnoval");
		label.setText(srBillNo);
		super.afterBindData(e);
	}
	@Override
	public void afterLoadData(EventObject e) {
		super.afterLoadData(e);
		String srBillNo = this.getModel().getDataEntity().getString("kded_srbillno");
		Label label = this.getControl("kded_srbillnoval");
		label.setText(srBillNo);
	}
}
