package com.fuhu.test.smarthub.middleware;

import android.annotation.SuppressLint;
import android.content.Context;

import com.fuhu.test.smarthub.middleware.componet.AMailItem;
import com.fuhu.test.smarthub.middleware.componet.ICommand;
import com.fuhu.test.smarthub.middleware.componet.IMailItem;
import com.fuhu.test.smarthub.middleware.componet.IPostOfficeProxy;
import com.fuhu.test.smarthub.middleware.componet.ISchedulingActionProxy;
import com.fuhu.test.smarthub.middleware.contract.ErrorCodeHandler;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public enum SmartHubCommand implements ICommand, Serializable{
	ReqSendToIFTTT(1,1),
	ReqSendToAWS(2,2)
	;
	
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer, SmartHubCommand> lookupTable  = new HashMap<Integer, SmartHubCommand>();

	static{	
		 for(SmartHubCommand tmp: SmartHubCommand.values()){
			 lookupTable.put(tmp._id, tmp);
		 }
	}
	
	public static SmartHubCommand lookup(final int id){
		return lookupTable.get(id);
	}
	
	private int _id=-1;
	private int _Content=-1;

	private SmartHubCommand(final int id, final int Content){
		_id=id;
		set_Content(Content);
	}
	
	public int get_Content() {
		return _Content;
	}

	public void set_Content(int _Content) {
		this._Content = _Content;
	}

	@Override
	public int getID() {
		return _id;
	}
	
	@Override
	public String getAddress() {
		return String.valueOf(_id);
	}

	@Override
	public Object doAction(Context mContext, IMailItem queryItem, IPostOfficeProxy mPostOfficeProxy, Object... obj) {
		return null;
	}

	@Override
	public Object doNextAction(IMailItem queryItem, IPostOfficeProxy mPostOfficeProxy, Object... obj) {
		return null;
	}

	@Override
	public JSONObject genJson(final AMailItem queryItem) {
		return null;
	}

	@Override
	public void onCommandComplete(IPostOfficeProxy mPostOfficeProxy, ISchedulingActionProxy mISchedulingActionProxy, IMailItem queryITem, List<IMailItem> result, Object... parameters) {
	}

	@Override
	public void onCommandFailed(final Context mContext, IPostOfficeProxy mPostOfficeProxy, IMailItem queryITem, ErrorCodeHandler errorCode) {
	}
}