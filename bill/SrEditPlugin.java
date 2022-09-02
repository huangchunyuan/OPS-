package srm.bill;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.operate.Donothing;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.ext.ssc.plugin.BillFormShowCustomerPlugin;
import kd.bos.form.ClientProperties;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.FormShowParameter;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.MessageBoxResult;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import kd.bos.form.container.Tab;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.control.events.TabSelectEvent;
import kd.bos.form.control.events.TabSelectListener;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.events.PreOpenFormEventArgs;
import kd.bos.form.field.BasedataEdit;
import kd.bos.form.field.FieldEdit;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.list.BillList;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.monitor.log.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.report.ReportShowParameter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.coderule.CodeRuleServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.util.io.IO.err;
import srm.utils.DataEntityUtil;

public class SrEditPlugin extends AbstractBillPlugIn implements TabSelectListener, BeforeF7SelectListener, HyperLinkClickListener {
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		Button confirmbtn = this.getControl("kded_confirmbtn");
		confirmbtn.addClickListener(this);
//		RichTextEditor richtexteditorap = this.getControl("kded_richtexteditorap");
		Tab tab = this.getControl("kded_tabap");
		tab.addTabSelectListener(this);
		BasedataEdit module = this.getControl("kded_module");
		module.addBeforeF7SelectListener(this);
		//查看控件的实际类型
		BillList billList = this.getView().getControl("kded_billlistap");
		billList.addHyperClickListener(this);
		
		
	}
	@Override
	public void afterLoadData(EventObject e) {
		super.afterLoadData(e);
		FormShowParameter parameter = this.getView().getFormShowParameter();
		Object param = parameter.getCustomParam("onlyview");
		if(param != null) {
			this.getView().setVisible(false, "tbmain");
		}
	}
	@Override
	public void afterBindData(EventObject e) {
		RichTextEditor richtexteditorap = this.getControl("kded_richtexteditorap");
		String solutiontext = (String) this.getModel().getValue("kded_solutiontext");
		if (!solutiontext.isEmpty()) {
			richtexteditorap.setText(solutiontext);
		}
		super.afterBindData(e);
		changeFieldStyle();
		long userId = RequestContext.get().getCurrUserId();
		DynamicObject engineer = (DynamicObject) this.getModel().getValue("kded_curhandler");
		DynamicObject srstatus = (DynamicObject) this.getModel().getValue("kded_srstatus");
		//L1处理中时只有支持工程师可以提供解决方案、挂起、升级.挂起中，只有支持工程师可以看见解挂，其他不可操作
		if("B".equals(srstatus.getString("number")) && Long.compare(userId, engineer.getDynamicObject("kded_engineeruser").getLong("id"))==0) {
			this.getView().setVisible(true, "kded_suspend","kded_resolve","kded_levelup");
			this.getView().setVisible(false, "kded_unsuspend");
		}else if ("D".equals(srstatus.getString("number")) && Long.compare(userId, engineer.getDynamicObject("kded_engineeruser").getLong("id"))==0) {
			this.getView().setVisible(true, "kded_unsuspend");
			this.getView().setVisible(false, "kded_suspend","kded_resolve","kded_levelup");
		}else {
		this.getView().setVisible(false, "kded_suspend","kded_resolve","kded_levelup","kded_unsuspend");
		}
		DynamicObject[] srstatus1 = BusinessDataServiceHelper.load("kded_demo_srstatus", "id,number", null);
	}
	@Override
	public void beforeClick(BeforeClickEvent evt) {
		Button btn = (Button) evt.getSource();
		if (btn.getKey().equals("kded_confirmbtn")) {
			String solutionsummary = (String) this.getModel().getValue("kded_solutionsummary");
			RichTextEditor richtexteditorap = this.getControl("kded_richtexteditorap");
			String richtextValue = richtexteditorap.getText();
			String errorString="";
			if (solutionsummary.isEmpty()) {
				errorString+="方案摘要;";
			}
			if (richtextValue.isEmpty()) {
				errorString+="解决方案详情;";
			}
			if (!errorString.isEmpty()) {
				this.getView().showErrorNotification(errorString+"不能为空");
				evt.setCancel(true);
			}
		}
		
		super.beforeClick(evt);
	}
	@Override
	public void click(EventObject evt) {
		Button btn = (Button) evt.getSource();
		if (btn.getKey().equals("kded_confirmbtn")) {
			RichTextEditor richtexteditorap = this.getControl("kded_richtexteditorap");
			String richtextValue = richtexteditorap.getText();
			this.getModel().setValue("kded_solutiontext", richtextValue);
			QFilter qFilter = new QFilter("number", QCP.equals, "E");
			DynamicObject srStatus = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", qFilter.toArray());
			if(srStatus == null) {
				throw new KDException("提交解决方案失败，请重试！");
			} else {
				DynamicObject srstatusObj = BusinessDataServiceHelper.loadSingle(srStatus.getLong("id"),"kded_demo_srstatus");
				this.getModel().setValue("kded_srstatus", srstatusObj);
				Date submitTime = new Date();
				this.getModel().setValue("kded_rootcauseon", submitTime);
				DynamicObject dataEntity = this.getModel().getDataEntity(true);
				OperationResult result = OperationServiceHelper.executeOperate("save", "kded_demo_srbill", new DynamicObject[] {dataEntity}, OperateOption.create());
				if (!result.isSuccess()) {
					this.getView().showErrorNotification("提交解决方案失败");
				}else {
//					((IBillModel)this.getModel()).push(dataEntity);
				this.getView().updateView();
				}
			}
		}
		super.click(evt);
	}
	@Override
	public void afterDoOperation(AfterDoOperationEventArgs args) {
		String operateKey = args.getOperateKey();
		if (operateKey.equals("save")) {
//			HashMap map = new HashMap();
//			map.put("method", "setLocalStorage");
//			HashMap arg = new HashMap();
//			arg.put("key", "myLocalStorage");
//			arg.put("value", "陈来珍-LocalStorage存储测试");
//			map.put("args", arg);
//			this.getView().executeClientCommand("callYZJApi", map);
			//刷新工单状态
//			this.getView().updateView("kded_srstatus");
		}
		super.afterDoOperation(args);
	}
	private void changeFieldStyle() {
		DynamicObject crStatus = (DynamicObject) this.getModel().getValue("kded_srstatus");
		if (crStatus==null) {
			return;
		}
		String status = crStatus.getString("number");
		String color = "#666666";
		switch(status) {
			case "A":
				color = "#99d92b";
				break;
			case "G":
				color = "#ffaa56";
				break;
			default:
				break;
		}
		Map<String, Object> props = new HashMap<>();
		Map<String, Object> item = new HashMap<>();
		item.put(ClientProperties.ForeColor, color);
		props.put(ClientProperties.Item, item);
		this.getView().updateControlMetadata("kded_srstatus", props);
	}

	@Override
	public void beforeClosed(BeforeClosedEvent e) {
		DynamicObject srstatus = (DynamicObject) this.getModel().getValue("kded_srstatus");
		if (srstatus!=null&&!srstatus.getString("number").equals("A")) {
			e.setCheckDataChange(false);
		}
		super.beforeClosed(e);
	}
	@Override
	public void tabSelected(TabSelectEvent arg0) {
		String tabKey = arg0.getTabKey();
		switch(tabKey) {
			//工作日志页签
			case "kded_worknoteap":
//				showWorkNoteTab();
				BillList billlistap = (BillList)this.getControl("kded_billlistap");
				QFilter billnoFilter = new QFilter("kded_srbillno", QCP.equals, this.getModel().getValue("billno"));
				billlistap.setFilter(billnoFilter);
				this.getView().updateView("kded_billlistap");
				break;
			//解决方案页签
			case "kded_solutionap":
				FieldEdit control = this.getControl("kded_solutionsummary");
				control.setFocus(true);
				break;
			default:
				break;
		}
	}
	private void showWorkNoteTab() {
		ListShowParameter listShowParameter = new ListShowParameter();
		listShowParameter.setFormId("bos_list");
		listShowParameter.setBillFormId("kded_demo_worknote");
//		listShowParameter.setShowTitle(true);
		listShowParameter.setShowFilter(false);
		listShowParameter.getOpenStyle().setShowType(ShowType.InContainer);
		listShowParameter.getOpenStyle().setTargetKey("kded_worknoteap");
		//自定义参数
//		Map<String, Object> params = new HashMap<>();
//		params.put("fromsr", this.getModel().getValue("billno"));
//		listShowParameter.setCustomParams(params);
		//设置运维工单的日志标签页初始时只能看到当前单据的
		ListFilterParameter listFilterParameter = new ListFilterParameter();
		QFilter billnoFilter = new QFilter("kded_srbillno", QCP.equals, this.getModel().getValue("billno"));
		listFilterParameter.setFilter(billnoFilter);
		listShowParameter.setListFilterParameter(listFilterParameter);
		StyleCss css = new StyleCss();
		css.setHeight("500px");
		listShowParameter.getOpenStyle().setInlineStyleCss(css);
		this.getView().showForm(listShowParameter);
	}
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		String itemKey = evt.getItemKey();
		switch(itemKey) {
		case "kded_resolve":
			activateSolutionTab();
			break;
		default:
			break;
		}
	}
	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		Object source = args.getSource();
		if(source instanceof Donothing) {
			Donothing op = (Donothing) source;
			String operateKey = op.getOperateKey();
			switch(operateKey) {
			case "suspend":
				FormShowParameter parameter = DataEntityUtil.getSuspendParameter(this);
				this.getView().showForm(parameter);
				break;
			case "unsuspend":
				DataEntityUtil.showUnsuspendConfirm(this.getView());
				break;
			default:
				break;
			}
		}
	}
	private void activateSolutionTab() {
//		ReportShowParameter reportShowParameter = new ReportShowParameter();
//		reportShowParameter.setFormId("kded_testrpt");
//		reportShowParameter.setCaption("showForm测试");
//		reportShowParameter.getOpenStyle().setShowType(ShowType.Modal);
//		StyleCss styleCss = new StyleCss();
//		styleCss.setWidth("800");
//		styleCss.setHeight("600");
//		reportShowParameter.getOpenStyle().setInlineStyleCss(styleCss);
//		this.getView().showForm(reportShowParameter);

		Tab tab = this.getControl("kded_tabap");
		tab.activeTab("kded_solutionap");
	}
	
	@Override
	public void closedCallBack(ClosedCallBackEvent e) {
		super.closedCallBack(e);
		String actionId = e.getActionId();
		switch(actionId) {
		case "suspendconfirm":
			doSuspendConfirm(e);
			this.getView().updateView();
			break;
		}
	}
	private void doSuspendConfirm(ClosedCallBackEvent e) {
		@SuppressWarnings("unchecked")
		Map<String, Object> returnData = (Map<String, Object>) e.getReturnData();
		if(returnData != null) {
			DynamicObject dynamicObject = (DynamicObject) returnData.get("dynamicObject");
			Date suspendTo = dynamicObject.getDate("kded_suspendtotime");
			//设置挂起时间和预计挂起结束时间
			this.getModel().setValue("kded_suspendtime", new Date());
			this.getModel().setValue("kded_suspendtotime", suspendTo);
			DynamicObject object = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", new QFilter("number", QCP.equals, "D").toArray());
			this.getModel().setValue("kded_suspendstatus", this.getModel().getValue("kded_srstatus"));
			this.getModel().setValue("kded_srstatus", object.get("id"));
			SaveServiceHelper.saveOperate("kded_demo_srbill", new DynamicObject[] {this.getModel().getDataEntity(true)}, OperateOption.create());
			
			//生成工作日志
			DynamicObject workNote = BusinessDataServiceHelper.newDynamicObject("kded_demo_worknote");
			//日志编号
			String number = CodeRuleServiceHelper.getNumber("kded_demo_worknote", workNote, "");
			workNote.set("billno", number);
			//创建时间
			workNote.set("kded_createtime", new Date());
			//运维单号
			workNote.set("kded_srbillno", this.getModel().getValue("billno"));
			workNote.set("kded_srstatus", this.getModel().getValue("kded_srstatus"));
			//动作
			workNote.set("kded_operation", "挂起");
			//处理人
			QFilter filter = new QFilter("kded_engineeruser", QCP.equals, RequestContext.getOrCreate().getUserId());
			DynamicObject engineer = QueryServiceHelper.queryOne("kded_demo_engineer", "id", filter.toArray());
			if(engineer != null) {
				workNote.set("kded_handler", engineer.get("id"));
			}
			//日志描述
			String convertStr = DataEntityUtil.ConvertToString(dynamicObject, new String[] {"kded_attachment"});
			workNote.set("kded_description", convertStr);
			//日志附件
			@SuppressWarnings("unchecked")
			List<DynamicObject> attachments = (List<DynamicObject>) returnData.get("attachments");
			//获取目标单（工作日志）的附件字段，构造kded_attachment对象并绑定动态表单返回的bd_attachment对象
			DynamicObjectCollection attachmentCol = (DynamicObjectCollection) workNote.get("kded_attachment");
			DynamicObjectType objectType = attachmentCol.getDynamicObjectType();
			
			for(DynamicObject att : attachments) {
				DynamicObject attachment = new DynamicObject(objectType);
				attachment.set("fbasedataid", att);
				attachmentCol.add(attachment);
			}
			SaveServiceHelper.save(new DynamicObject[] {workNote});
		}
	}
	
	
	@Override
	public void afterCreateNewData(EventObject e) {
	
		QFilter filter = new QFilter("kded_engineeruser", QCP.equals, UserServiceHelper.getCurrentUserId());
		DynamicObject engineer = QueryServiceHelper.queryOne("kded_demo_engineer", "id", filter.toArray());
		this.getModel().setValue("kded_reporter", engineer.get("id"));
	}
	@Override
	public void preOpenForm(PreOpenFormEventArgs e) {
		QFilter filter = new QFilter("kded_engineeruser", QCP.equals, UserServiceHelper.getCurrentUserId());
		DynamicObject engineer = QueryServiceHelper.queryOne("kded_demo_engineer", "id", filter.toArray());
		if (engineer==null) {
			e.setCancelMessage("该用户不是工程师");
			e.setCancel(true);
		}
		super.preOpenForm(e);
	}
	
	/**
     * 选择框回调函数
     * @param
     */
    @Override
    public void confirmCallBack(MessageBoxClosedEvent e) {
    	String backId = e.getCallBackId();
    	switch(backId) {
    	case "unsuspend":
    		if (MessageBoxResult.Yes.equals(e.getResult())) {
    			
    			Date now = new Date();
    			Instant end = now.toInstant();
    			Instant begin = this.getModel().getDataEntity().getDate("kded_suspendtime").toInstant();
    			long minutes = Duration.between(end, begin).toMinutes();
    			//挂起时长（h）
    			BigDecimal suspendHour = new BigDecimal(minutes/60.0).setScale(1, RoundingMode.HALF_UP);
    			BigDecimal oldHour = (BigDecimal) this.getModel().getValue("kded_suspendhour");
    			this.getModel().setValue("kded_suspendhour", oldHour.add(suspendHour));
    			
    			//确定解挂更新工单状态：L1/L2处理中，解挂时间，挂起时长
				this.getModel().setValue("kded_unsuspendtime", new Date());
    			this.getModel().setValue("kded_srstatus", this.getModel().getValue("kded_suspendstatus"));
    			this.getModel().setValue("kded_suspendstatus", null);
    			SaveServiceHelper.saveOperate("kded_demo_srbill", new DynamicObject[] {this.getModel().getDataEntity(true)}, OperateOption.create());
    		}
    		break;
    	}
    }

	@Override
	public void beforeF7Select(BeforeF7SelectEvent arg0) {
		String pname = arg0.getProperty().getName();
		switch(pname) {
		case "kded_module":
			doModuleFilter(arg0);
			break;
		}
	}
	
	private void doModuleFilter(BeforeF7SelectEvent e) {
		DynamicObject product = (DynamicObject) this.getModel().getValue("kded_product");
		if(product ==null) {
			this.getView().showTipNotification("请先选择产品！");
			e.setCancel(true);
		} else {
			//根据模块所属产品=产品过滤
			List<QFilter> customQFilters = new ArrayList<>();
			QFilter filter = new QFilter("kded_product.fbasedataid", QCP.in, product.getPkValue());
			customQFilters.add(filter);
			e.setCustomQFilters(customQFilters);
		}
	}
	@Override
	public void hyperLinkClick(HyperLinkClickEvent arg0) {
		// TODO Auto-generated method stub
		BillList billList = this.getView().getControl("kded_billlistap");
		// 获取列名
					String fieldName = arg0.getFieldName();		
					if (fieldName.equals("billno")) {
						// 取消系统自动打开本单据的处理
						//arg0.setCancel(true);
						// 获取当前选中行记录,注意:得到的结果是记录的主键ID
						ListSelectedRow currow = billList.getCurrentSelectedRowInfo();
						BillShowParameter showParameter = new BillShowParameter();				
						showParameter.setFormId("kded_demo_worknote");
						showParameter.setPkId(currow.getPrimaryKeyValue());
						showParameter.setCaption(currow.getBillNo());
						showParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
						showParameter.setStatus(OperationStatus.VIEW);
						this.getView().showForm(showParameter);				
					}
	}
}
