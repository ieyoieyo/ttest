package bwt.yfbhj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bwt.yfbhj.R;
import bwt.yfbhj.itemVo.RightMenuItemVo;

public class RightMenuAdapter extends BaseAdapter {
    private ArrayList<RightMenuItemVo> items;
    private Context context;

    public RightMenuAdapter(Context context){
        this.context = context;
    }

    public ArrayList<RightMenuItemVo> getItems() {
        return items;
    }

    public void setItems(ArrayList<RightMenuItemVo> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public RightMenuItemVo getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = new View(context);
        grid = inflater.inflate(R.layout.right_menu_item,null);
        ImageView icon = (ImageView) grid.findViewById(R.id.icon);
        TextView text = (TextView) grid.findViewById(R.id.text);

        RightMenuItemVo vo = items.get(position);
        switch(vo.getType()){
//            case RightMenuItemVo.TYPE_BACK:
//                icon.setImageResource(R.mipmap.back_on);
//                text.setText(context.getResources().getString(R.string.menu_back));
//                break;
//            case RightMenuItemVo.TYPE_NEXT:
//                icon.setImageResource(R.mipmap.next_on);
//                text.setText(context.getResources().getString(R.string.menu_next));
//                break;
            case RightMenuItemVo.TYPE_REFRESH:
                icon.setImageResource(R.mipmap.refresh_on);
                text.setText(context.getResources().getString(R.string.menu_refresh));
                break;
            case RightMenuItemVo.TYPE_CLEAR:
                icon.setImageResource(R.mipmap.clear_on);
                text.setText(context.getResources().getString(R.string.menu_clear));
                break;
//            case RightMenuItemVo.TYPE_ABOUT:
//                icon.setImageResource(R.mipmap.back_on);
//                text.setText(context.getResources().getString(R.string.menu_about));
//                break;
        }

        return grid;
    }
}
