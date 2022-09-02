package srm.rpt;

import java.util.EventObject;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.form.ClientActions;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IClientViewProxy;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.FilterContainerInitEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.FilterContainerSearchClickArgs;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.report.ReportList;
import kd.bos.report.events.SortAndFilterEvent;
import kd.bos.report.filter.ReportFilter;
import kd.bos.report.plugin.AbstractReportFormPlugin;

public class SrReportPlugin extends AbstractReportFormPlugin implements HyperLinkClickListener {
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		Toolbar toobar = getControl("toolbarap");
		toobar.addItemClickListener(this);
		ReportList control = getView().getControl("reportlistap");
		control.addHyperClickListener(this);
	}
	
	@Override
	public void initDefaultQueryParam(ReportQueryParam queryParam) {
		super.initDefaultQueryParam(queryParam);
		//获取报表的当前界面参数
		FormShowParameter showParameter = this.getView().getFormShowParameter();
		showParameter.getCustomParams();
		//过滤字段赋默认值，filterContainerInit对报表过滤控件不生效
//		this.getModel().setValue("kded_billnofilter", "pay-20210201");
		//页面加载触发一次查询
		ReportFilter reportFilter = this.getControl("reportfilterap");
		reportFilter.search();
	}

	@Override
	protected void filterContainerInit(FilterContainerInitEvent e, ReportQueryParam queryParam) {
		super.filterContainerInit(e, queryParam);
	}
	
	@Override
	public void filterContainerSearchClick(FilterContainerSearchClickArgs args) {
		super.filterContainerSearchClick(args);
	}
	
	@Override
	protected ReportQueryParam getQueryParam() {
		return super.getQueryParam();
	}
	
	@Override
	public void beforeQuery(ReportQueryParam queryParam) {
		super.beforeQuery(queryParam);
		queryParam.getFilter().getFilterItems();
	}
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		//reportlistap 是报表列表控件的标识，其控件原型继承AbstactGrid，与分录类似
		ReportList reportList = this.getControl("reportlistap");
//		//获取报表列表选中行索引
//		int[] rows = reportList.getEntryState().getSelectedRows();
//		//通过ReportModel和行索引获取选中报表行数据
//		for(int row : rows) {
//			reportList.getReportModel().getRowData(row);
//		}
		
	}
	
	@Override
	public void processRowData(String gridPK, DynamicObjectCollection rowData, ReportQueryParam queryParam) {
		super.processRowData(gridPK, rowData, queryParam);
	}

	@Override
	public void hyperLinkClick(HyperLinkClickEvent var1) {
		System.out.println();
	}
}
