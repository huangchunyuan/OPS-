package srm.rpt;

import kd.bos.report.events.TreeReportListEvent;
import kd.bos.report.plugin.AbstractReportFormPlugin;

public class TestTreeReportPlugin extends AbstractReportFormPlugin {
	@Override
	public void setTreeReportList(TreeReportListEvent event) {
		super.setTreeReportList(event);
		event.setTreeReportList(true);
	}
}
