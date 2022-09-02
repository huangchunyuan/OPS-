package srm.bill.list;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.BadgeInfo;
import kd.bos.entity.datamodel.IListModel;
import kd.bos.entity.datamodel.ListField;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.filter.FilterParameter;
import kd.bos.entity.list.QueryBuilder;
import kd.bos.entity.list.QueryResult;
import kd.bos.entity.operate.Donothing;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.FormShowParameter;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.MessageBoxResult;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.BillList;
import kd.bos.list.IListView;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.list.query.PageRowCacheUtils;
import kd.bos.mvc.list.QueryBuilderFactory;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import srm.utils.DataEntityUtil;

public class SrListPlugin extends AbstractListPlugin {
	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);
		//CSE可见报告人为自己的问题单，PSE可见（当前处理人为自己或（L1处理中且模块负责人为自己））且问题状态非已取消或待受理的问题单，
		//RDE可见（当前处理人为自己或（L2处理中且模块负责人为自己）且问题状态非已取消或待受理的问题单
//		String userId = RequestContext.get().getUserId();
//		QFilter filter = new QFilter("kded_engineeruser", QCP.equals, userId);
//		DynamicObject engineer = QueryServiceHelper.queryOne("kded_demo_engineer", "id, kded_engineerrole", filter.toArray());
//		List<QFilter> customQFilters = new ArrayList<>();
//		if(engineer == null) {
//			this.getView().showTipNotification("您不是工程师！");
//		} else {
//			Long engineerId = engineer.getLong("id");
//			String role = engineer.getString("kded_engineerrole");
//			List<String> statusList = new ArrayList<>();
//			statusList.add("A");
//			statusList.add("G");
//			List<Long> moduleIds = new ArrayList<>();
//			
//			filter = new QFilter("kded_srstatus.number", QCP.not_in, statusList);
//			filter.and("kded_curhandler", QCP.equals, engineerId);
//			QFilter moduleFilter = null;
//			Map<Object, DynamicObject> fromCache = null;
//			
//			switch(role) {
//				case "CSE":
//					customQFilters.add(new QFilter("kded_reporter.kded_engineeruser", QCP.equals, userId));
//					break;
//				case "PSE":
//					moduleFilter = new QFilter("kded_pseengineer", QCP.equals, engineerId);
//					fromCache = BusinessDataServiceHelper.loadFromCache("kded_demo_module", "id", moduleFilter.toArray());
//					for(Object key : fromCache.keySet()) {
//						DynamicObject dynamicObject = fromCache.get(key);
//						moduleIds.add(dynamicObject.getLong("id"));
//					}
//					QFilter pseFilter = new QFilter("kded_srstatus.number", QCP.equals, "B");
//					pseFilter.and(new QFilter("kded_module", QCP.in, moduleIds));
//					filter.or(pseFilter);
//					customQFilters.add(filter);
//					break;
//				case "RDE":
//					moduleFilter = new QFilter("kded_rdeengineer", QCP.equals, engineerId);
//					fromCache = BusinessDataServiceHelper.loadFromCache("kded_demo_module", "id", moduleFilter.toArray());
//					for(Object key : fromCache.keySet()) {
//						DynamicObject dynamicObject = fromCache.get(key);
//						moduleIds.add(dynamicObject.getLong("id"));
//					}
//					QFilter rdeFilter = new QFilter("kded_srstatus.number", QCP.equals, "C");
//					rdeFilter.and(new QFilter("kded_module", QCP.in, moduleIds));
//					filter.or(rdeFilter);
//					customQFilters.add(filter);
//					break;
//				default:
//					break;
//			}
//		}
//		e.setCustomQFilters(customQFilters);
//演示案例，暂时不需要自行过滤
	}
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		//beforedooperate中校验
//		String itemKey = evt.getItemKey();
//		switch(itemKey) {
//		case "kded_unsuspend":
//			BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
//			ListSelectedRowCollection selectedRows = billList.getSelectedRows();
//			if (selectedRows.isEmpty()) {
//				this.getView().showErrorNotification("请先选择要解挂的工单");
//				return;
//			}
//			if(selectedRows.size() > 1) {
//				this.getView().showErrorNotification("不能选中多张工单进行解挂");
//				return;
//			}
//			
//			DynamicObjectCollection collection = billList.getSelectedRowDatas(billList.getListFields(), selectedRows).getCollection();
//			String number = collection.get(0).getDynamicObject("kded_srstatus").getString("number");
//			if("D".equals(number)) {
//				this.getView().showConfirm("确定解挂吗？", MessageBoxOptions.OKCancel, new ConfirmCallBackListener("unsuspend",this));
//			} else {
//				this.getView().showErrorNotification("只有状态为挂起中的工单才可解挂");
//			}
//			break;
//		default:
//			break;
//		}
	}
	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		Object source = args.getSource();
		if(source instanceof Donothing) {
			Donothing op = (Donothing) source;
			String operateKey = op.getOperateKey();
			switch(operateKey) {
			case "unsuspend":
				BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
				ListSelectedRowCollection selectedData = billList.getSelectedRows();
				if(selectedData.size()==0) {
					this.getView().showErrorNotification("请先选择要解挂的工单");
					args.setCancel(true);
				} else if(selectedData.size() > 1) {
					this.getView().showErrorNotification("不能选中多张工单进行解挂");
					args.setCancel(true);
				} else {
					QFilter filter = new QFilter("id", QCP.equals, selectedData.get(0).getPrimaryKeyValue());
					DynamicObject srBill = QueryServiceHelper.queryOne("kded_demo_srbill", "id,kded_srstatus.number", filter.toArray());
					if (srBill==null) {
						this.getView().showErrorNotification("单据不存在，可能已被删除");
						args.setCancel(true);
						return;
					}
					String number = srBill.getString("kded_srstatus.number");
					if("D".equals(number)) {
						DataEntityUtil.showUnsuspendConfirm(this.getView());
					}else {
						this.getView().showErrorNotification("只有状态为挂起中的工单才可解挂");
						args.setCancel(true);
					}
				}
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void afterBindData(EventObject e) {
		// 获取单据列表
		BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
		// 清除页面行缓存,否则拿到的数据不是最新的
		PageRowCacheUtils.clearPageRowcache(this.getView().getPageId(), AbstractListPlugin.BILLLISTID);

		// 查询行数据
		IListModel model = billList.getListModel();
		model.setFieldCotnrolRules(billList.getFieldControlRules());
		// 获取过滤参数
		FilterParameter generalFilterParameter = billList.generalFilterParameter();
		// 获取列表字段
		List<ListField> listFields = billList.getListFields();
		model.setFilterParameter(generalFilterParameter);
		model.setNeedKeyFields(true);
		model.setListFields(listFields);
		// 自定义过滤参数
		model.getProvider().getQFilters();
		// 构建查询这里可以设置分页查询记录数
		QueryBuilder queryBuilder = QueryBuilderFactory.createQueryBuilder(model.getProvider(), 0, 20, false, true);
		model.getProvider().setQueryBuilder(queryBuilder);
		// 获取单据列表数据
		DynamicObjectCollection dataCol = model.getProvider().getData(0, model.getBillDataCount());
		Toolbar toolbar = this.getView().getControl("toolbarap");
		int count = 0;
		for(DynamicObject data : dataCol) {
			DynamicObject srStatus = (DynamicObject) data.get("kded_srstatus");
			if(srStatus!=null&&"D".equals(srStatus.get("number"))) {
				count = count + 1;
			}
		}
		BadgeInfo info= new BadgeInfo();
		info.setColor("#ff0000");
		info.setCount(count);
		info.setShowZero(true);
		toolbar.setBadgeInfo("kded_unsuspend", info);
	}
	@Override
	public void confirmCallBack(MessageBoxClosedEvent e) {
		super.confirmCallBack(e);
		String backId = e.getCallBackId();
    	switch(backId) {
		case "unsuspend":
			BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
			Object pk = billList.getSelectedRows().get(0).getPrimaryKeyValue();
			if (MessageBoxResult.Yes.equals(e.getResult())) {
				OperationServiceHelper.executeOperate("unsuspend", "kded_demo_srbill", new Object[] {pk}, OperateOption.create());
			}
			break;
    	}
	}
	@Override
	public void afterDoOperation(AfterDoOperationEventArgs args) {
		super.afterDoOperation(args);
		String operateKey = args.getOperateKey();
		switch(operateKey) {
		case "unsuspend":
			if(args.getOperationResult().isSuccess()) {
				BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
				billList.refresh();
			}
			break;
		case "colsesrbill":
			BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
			Object pk = billList.getSelectedRows().get(0).getPrimaryKeyValue();
			QFilter qFilter = new QFilter("number", QCP.equals, "F");
			DynamicObject srStatus = BusinessDataServiceHelper.loadSingleFromCache("kded_demo_srstatus", qFilter.toArray());
			DynamicObject loadSingle = BusinessDataServiceHelper.loadSingle(pk,"kded_demo_srbill");
			loadSingle.set("kded_srstatus", srStatus.getLong("id"));
			SaveServiceHelper.save(new DynamicObject[] {loadSingle});
		default:
			break;
		}
		
	}
}
