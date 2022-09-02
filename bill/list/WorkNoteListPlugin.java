package srm.bill.list;

import java.util.ArrayList;
import java.util.Map;

import kd.bos.entity.datamodel.events.LoadDataEventArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.ListShowParameter;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;

public class WorkNoteListPlugin extends AbstractListPlugin {
	@Override
	public void loadData(LoadDataEventArgs e) {
		super.loadData(e);
	}
	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);
		ListShowParameter listShowParameter = (ListShowParameter) this.getView().getFormShowParameter();
		Map<String, Object> params = listShowParameter.getCustomParams();
		String fromSr = (String) params.get("fromsr");
		if(fromSr != null) {
			ArrayList<QFilter> list = new ArrayList<QFilter>();
			QFilter filter = new QFilter("kded_srbillno", QCP.equals, fromSr);
			list.add(filter);
			e.setCustomQFilters(list);
		}
	}
}
