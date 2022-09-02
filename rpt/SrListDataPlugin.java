package srm.rpt;

import java.util.ArrayList;
import java.util.List;

import kd.bos.algo.Algo;
import kd.bos.algo.DataSet;
import kd.bos.algo.DataType;
import kd.bos.algo.Field;
import kd.bos.algo.JoinDataSet;
import kd.bos.algo.JoinType;
import kd.bos.algo.Row;
import kd.bos.algo.RowMeta;
import kd.bos.algo.RowUtil;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.log.utils.LogOperactionListutils;
import kd.bos.orm.ORM;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
//运维工单报表
public class SrListDataPlugin extends AbstractReportListDataPlugin {

	@Override
	public DataSet query(ReportQueryParam queryParam, Object var2) throws Throwable {
		 String selectedField = "kded_product,kded_srstatus";
		 String[] srstatus= new String[]{"A","B","C","D","E","F","G"};
		 String[] fields= new String[]{"kded_srstatusa","kded_srstatusb","kded_srstatusc","kded_srstatusd","kded_srstatuse","kded_srstatusf","kded_srstatusg"};
		 QFilter filter=null;
		 DataSet result = null;
		 ArrayList<String> selectFiles = new ArrayList<String>();
		 selectFiles.add("kded_product");
		 for (int i=0;i<srstatus.length;i++) {
			 filter=new QFilter("kded_srstatus.number", QCP.equals, srstatus[i]);
			 DataSet ds = QueryServiceHelper.queryDataSet(getClass().getName(), "kded_demo_srbill", selectedField, new QFilter[] {filter}, null);
			 if (ds==null) {
				continue;
			}
			 DataSet groupDS = ds.groupBy(new String[] {"kded_product"}).count().finish().select(new String[] {"kded_product","count as "+fields[i]});
			 selectFiles.add(fields[i]);
			 for (Row row : groupDS) {
				RowMeta rowMeta = groupDS.getRowMeta();
			}
			 if (result!=null) {
				result.fullJoin(groupDS).on("kded_product", "kded_product").select((String[]) selectFiles.toArray(new String[0])).finish();
			}else {
				result=groupDS;
			}
		}
//		 DataSet groupDS = ds.groupBy(new String[] {"kded_product","kded_srstatus"}).count().finish();
//		 DataSet productDS = groupDS.copy().select("kded_product");
//		 for(String number:srstatus) {
//		 DataSet tempDataSet = copy.filter("kded_srstatus.number=number");
//		 productDS.join(tempDataSet);
//		 }
		 //productDS.select(new String[] {"kded_product","kded_srstatus","count as kded_count"})
//		 DataSet result = productDS.join(srstatusDS,JoinType.LEFT).on("kded_product", "kded_product").select(new String[] {"kded_product","kded_srstatus","count as kded_count"}).finish();
		 
        return result;
	}
	
}
