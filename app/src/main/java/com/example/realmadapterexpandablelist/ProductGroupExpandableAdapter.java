/*
 * Copyright (c) 2017. Ruslan Primak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 4/24/17 12:59 AM
 */

package com.example.realmadapterexpandablelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.realmadapterexpandablelist.model.ProductGroup;

import org.jetbrains.annotations.NotNull;

import io.realm.OrderedRealmCollection;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ProductGroupExpandableAdapter extends BaseExpandableListAdapter {
    private OrderedRealmCollection<ProductGroup> mGroups;
    private LayoutInflater mInflater;
    private final RealmChangeListener<OrderedRealmCollection<ProductGroup>> mListener;
    private ProductGroup mSelected;

    public ProductGroupExpandableAdapter(Context context,
                                         @NotNull OrderedRealmCollection<ProductGroup> groups) {
        mGroups = groups;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mListener = new RealmChangeListener<OrderedRealmCollection<ProductGroup>>() {
            @Override
            public void onChange(OrderedRealmCollection<ProductGroup> results) {
                notifyDataSetChanged();
            }
        };

        addListener(groups);
    }

    private void addListener(@NonNull OrderedRealmCollection<ProductGroup> data) {
        if (data instanceof RealmResults) {
            RealmResults<ProductGroup> results = (RealmResults<ProductGroup>) data;
            //noinspection unchecked
            results.addChangeListener((RealmChangeListener) mListener);
        } else if (data instanceof RealmList) {
            RealmList<ProductGroup> list = (RealmList<ProductGroup>) data;
            //noinspection unchecked
            list.addChangeListener((RealmChangeListener) mListener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private void removeListener(@NonNull OrderedRealmCollection<ProductGroup> data) {
        if (data instanceof RealmResults) {
            RealmResults<ProductGroup> results = (RealmResults<ProductGroup>) data;
            //noinspection unchecked
            results.removeChangeListener((RealmChangeListener) mListener);
        } else if (data instanceof RealmList) {
            RealmList<ProductGroup> list = (RealmList<ProductGroup>) data;
            //noinspection unchecked
            list.removeChangeListener((RealmChangeListener) mListener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).subGroups.size();
    }

    @Override
    public ProductGroup getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public ProductGroup getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).subGroups.sort("id").get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return getItemView(groupPosition, 0, convertView, parent, false);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return getItemView(groupPosition, childPosition, convertView, parent, true);
    }

    private View getItemView(int groupPosition, int childPosition, View convertView,
                             ViewGroup parent, boolean isChild) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.exp_list_item, parent, false);
        }

        ProductGroup group;
        if (isChild) {
            group = getChild(groupPosition, childPosition);
        } else {
            group = getGroup(groupPosition);
        }

        if ((mSelected != null) && mSelected.isValid() && group.equals(mSelected)) {
            convertView.setBackgroundResource(android.R.color.holo_blue_bright);
        } else {
            convertView.setBackgroundResource(android.R.color.transparent);
        }

        TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
        textView1.setText(group.name);

        TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
        textView2.setText(String.valueOf(group.id));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setSelectedGroup(ProductGroup group) {
        mSelected = group;
        notifyDataSetChanged();
    }

    public void setSelectedGroup(int groupPosition) {
        mSelected = getGroup(groupPosition);
        notifyDataSetChanged();
    }

    public void setSelectedGroup(int groupPosition, int childPosition) {
        mSelected = getChild(groupPosition, childPosition);
        notifyDataSetChanged();
    }

    public ProductGroup getSelectedGroup() {
        return mSelected;
    }

}
