package com.zhy.dialtong.fragment.contacts;


import java.io.InputStream;
import java.util.List;

import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.R;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class GridviewAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<ContactBean> list;
	private Context ctx;
	
	public GridviewAdapter(Context context, List<ContactBean> list) {
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list; 
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ViewHolder
	{
		public QuickContactBadge imgViewFlag;
		public TextView txtViewTitle;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		
		if(convertView==null)
		{
			view = new ViewHolder();
			convertView = inflater.inflate(R.layout.gridview_row, null);
			
			view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
			view.imgViewFlag = (QuickContactBadge) convertView.findViewById(R.id.photo_view);
			
			convertView.setTag(view);
		}
		else
		{
			view = (ViewHolder) convertView.getTag();
		}
		
		ContactBean cb = list.get(position);
		String name = cb.getDisplayName();
		view.txtViewTitle.setText(name);
		
		view.imgViewFlag.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));
		if(0 == cb.getPhotoId()){
			view.imgViewFlag.setImageResource(R.drawable.btn_head);
		}else{
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cb.getContactId());
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri); 
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			view.imgViewFlag.setImageBitmap(contactPhoto);
		}
		
		return convertView;
	}

}
