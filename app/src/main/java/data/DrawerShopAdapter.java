package data;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.RolandAssoh.stopgalere.ci.R;

import model.MenuModel;


public class DrawerShopAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<MenuModel> mDrawerItems;
	private LayoutInflater mInflater;
    private String mPackageName;

	public DrawerShopAdapter(Context context, String pkName, List<MenuModel> data) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDrawerItems = data;
		mContext = context;
        mPackageName = pkName;
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDrawerItems.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_view_item_navigation_drawer_shop, parent,
					false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.list_item_navigation_drawer_shop_icon);
			holder.title = (TextView) convertView.findViewById(R.id.list_item_navigation_drawer_shop_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MenuModel item = mDrawerItems.get(position);
		holder.icon.setBackgroundResource(mContext.getResources().getIdentifier(item.getIconRes(), "drawable", mPackageName));
		holder.title.setText(item.getText());

		return convertView;
	}

	private static class ViewHolder {
		public ImageView icon;
		public/* Roboto */TextView title;
	}
}
