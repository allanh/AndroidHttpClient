package com.fuhu.test.smarthub.middleware.contract;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fuhu.test.smarthub.middleware.PostOffice;
import com.fuhu.test.smarthub.middleware.componet.ICommand;
import com.fuhu.test.smarthub.middleware.componet.IMailItem;
import com.fuhu.test.smarthub.middleware.componet.IPostOfficeProxy;
import com.fuhu.test.smarthub.middleware.componet.MailTask;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostOfficeProxy implements IPostOfficeProxy {
	
	public static String ACTION_DATAUPDATE = "ACTION_DATAUPDATE";
    private static PostOfficeProxy instance = new PostOfficeProxy();
    private Map<ICommand, MailTask> mMailTaskList;
    
    private PostOfficeProxy(){
         this.mMailTaskList = new HashMap<ICommand, MailTask>();
    }

    public static PostOfficeProxy getInstance(){
        return instance; 
    }
    
    @Override
    public void onMailItemUpdate(ICommand myCommand, IMailItem queryItem, List<IMailItem> result, Object... parameters) {
    	Intent intent = new Intent();
	    Bundle bundle = new Bundle();
	    bundle.putSerializable("mailTask", (Serializable) mMailTaskList.get(myCommand));
	    bundle.putSerializable("queryItem",(Serializable) queryItem);
	    bundle.putSerializable("result", (Serializable) result);
	    bundle.putSerializable("parameters", (Serializable) parameters);
	    
	    intent.setAction(PostOfficeProxy.ACTION_DATAUPDATE);
	    intent.putExtras(bundle);
	    MailBox.getInstance().receiveMail(intent);
//        MailBox.getInstance().receiveMail(mMailTaskList.get(myCommand), queryItem, result, parameters);
    }

    @Override
    public void onMailRequest(Context mContext, IMailItem queryItem, MailTask mMailTask, Object... parameter) {
        mMailTaskList.put(mMailTask.getCommand(), mMailTask);
        PostOffice mPostOffice = PostOffice.lookup(mMailTask.getCommand().getID());
        mPostOffice.doAction(mContext, queryItem, this, parameter);
    }

    @Override
    public void onMailDeliver(ICommand mCommand, boolean isForceDelivery, Object... parameter) {
       
    }

    public void onMailRequest(ICommand mCommand, Context mContext, IMailItem queryItem, Object... parameter) {
        PostOffice mPostOffice = PostOffice.lookup(mCommand.getID());
        mPostOffice.doAction(mContext, queryItem, this, parameter);
    }
}