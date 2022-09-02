package srm.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.dataentity.metadata.clr.DataEntityPropertyCollection;
import kd.bos.entity.property.ComboProp;
import kd.bos.form.CloseCallBack;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.ShowType;
import kd.bos.form.plugin.IFormPlugin;

public class DataEntityUtil {
	public static String ConvertToString(DynamicObject dataEntity, String[] excluded) {
		Map<String,String> content = new HashMap<>();
		DataEntityPropertyCollection properties = dataEntity.getDataEntityType().getProperties();
		for(IDataEntityProperty p : properties) {
			Object obj = dataEntity.get(p);
			//为空或者被排除的不处理
			if(obj == null || (excluded != null && Arrays.asList(excluded).contains(p.getName()))) {
				continue;
			} else if(p instanceof ComboProp) {
				ComboProp cb = (ComboProp) p;
				content.put(p.getDisplayName().getLocaleValue(), cb.getItemByName(obj.toString()));
			} else {
				content.put(p.getDisplayName().getLocaleValue(), obj.toString());
			}
		}
		return JSON.toJSONString(content);
	}
	
	public static FormShowParameter getSuspendParameter(IFormPlugin obj) {
		FormShowParameter parameter = new FormShowParameter();
		parameter.setFormId("kded_demo_suspenddialog");
		parameter.getOpenStyle().setShowType(ShowType.Modal);
		parameter.setCloseCallBack(new CloseCallBack(obj, "suspendconfirm"));
		return parameter;
	}
	
	public static void showUnsuspendConfirm(IFormView view) {
		view.showConfirm("确定解挂吗？", MessageBoxOptions.OKCancel, new ConfirmCallBackListener("unsuspend"));
	}
}
