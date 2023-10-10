package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sustain.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CategoryAdapter extends ArrayAdapter<String>
{
    private Context context;
    private List<String> categories;

    public CategoryAdapter(@NonNull Context context, List<String> categories)
    {
        super(context, R.layout.li_category, categories);
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent)
    {
        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.li_category, parent, false);
        }

        TextView text = view.findViewById(R.id.tvCategory);

        String cat = categories.get(position);
        text.setText(cat);

        if(cat.equals("Recycling"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_recycle), null, null, null);
        }
        else if(cat.equals("Food & Drink"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_food), null, null, null);
        }
        else if(cat.equals("Transport"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.icon_transport), null, null, null);
        }
        else if(cat.equals("Water"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_water), null, null, null);
        }
        else if(cat.equals("Energy"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_energy), null, null, null);
        }
        else if(cat.equals("Exercise"))
        {
            text.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_exercise), null, null, null);
        }
        else
        {
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        return view;
    }
}
