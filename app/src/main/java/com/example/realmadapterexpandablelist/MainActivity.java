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
 * Last modified 4/23/17 11:40 PM
 */

package com.example.realmadapterexpandablelist;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.example.realmadapterexpandablelist.databinding.ActivityMainBinding;
import com.example.realmadapterexpandablelist.model.ProductGroup;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.widget.AbsListView.CHOICE_MODE_NONE;
import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

public class MainActivity extends AppCompatActivity {
    private Realm mRealm;
    private ActivityMainBinding mMainBinding;
    private RealmResults<ProductGroup> mGroups;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Random mRnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainBinding.list.setEmptyView(mMainBinding.emptyView);
        mRealm = Realm.getDefaultInstance();

        mGroups = mRealm.where(ProductGroup.class).isNull("parent").findAll().sort("id");

        /* Fill data */
        final int dummyGroupCount = 5;
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < dummyGroupCount; i++) {
                    ProductGroup group = realm.createObject(ProductGroup.class); // Create managed objects directly
                    group.id = getNextId();
                    group.name = "Group #" + mGroups.size();
                        /* Add subgroups for the group */
                    for (int j = 0; j < dummyGroupCount; j++) {
                        ProductGroup subGroup = realm.createObject(ProductGroup.class); // Create managed objects directly
                        subGroup.id = getNextId();
                        subGroup.name = "SubGroup #" + (group.subGroups.size() + 1);
                        subGroup.parent = group;
                        group.subGroups.add(subGroup);
                    }
                }
            }
        });

        mMainBinding.list.setAdapter(new ProductGroupExpandableAdapter(getApplicationContext(),
                mGroups));

        mMainBinding.list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ((ProductGroupExpandableAdapter) parent.getExpandableListAdapter()).setSelectedGroup(
                        (ProductGroup) parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
                Log.d(TAG, "onChildClick: ");
                return false;
            }
        });

        mMainBinding.list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ((ProductGroupExpandableAdapter) parent.getExpandableListAdapter()).setSelectedGroup(
                        (ProductGroup) parent.getExpandableListAdapter().getGroup(groupPosition));
                Log.d(TAG, "onGroupClick: ");
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }

    public void onAddGroupButtonClick(View view) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ProductGroup group = realm.createObject(ProductGroup.class); // Create managed objects directly
                group.id = getNextId();
                group.name = "Group #" + mGroups.size();
            }
        });
    }

    public void onEditButtonClick(View view) {
        final ProductGroup item =
                ((ProductGroupExpandableAdapter) mMainBinding.list.getExpandableListAdapter())
                        .getSelectedGroup();
        if (item != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    item.name = item.name + "+";
                }
            });
        }
    }

    public void onDelButtonClick(View view) {
        final ProductGroupExpandableAdapter adapter =
                (ProductGroupExpandableAdapter) mMainBinding.list.getExpandableListAdapter();
        final ProductGroup item = adapter.getSelectedGroup();
        if (item != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    item.deleteFromRealm();
                    //adapter.setSelectedGroup(null);
                }
            });
        }
    }

    /**
     * Generates random id value based on size of ProductGroup list
     *
     * @return
     */
    private int getNextId() {
        final int randomRangeMultiplier = 5;
        if (mRnd == null) {
            mRnd = new Random();
        }

        return mRnd.nextInt((mRealm.where(ProductGroup.class).findAll().size() + 1)
                * randomRangeMultiplier);
    }

    public void onAddSubgroupButtonClick(View view) {
        final ProductGroup item =
                ((ProductGroupExpandableAdapter) mMainBinding.list.getExpandableListAdapter())
                        .getSelectedGroup();
        if (item != null) {
            final ProductGroup parent;
            if (item.parent != null) {
                parent = item.parent;
            } else {
                parent = item;
            }
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ProductGroup group = realm.createObject(ProductGroup.class); // Create managed objects directly
                    group.id = getNextId();
                    group.name = "Subgroup #" + parent.subGroups.size();
                    group.parent = parent;
                    parent.subGroups.add(group);
                }
            });
        }

    }

    public void onSelectGroupButtonClick(View view) {
        final ProductGroupExpandableAdapter adapter =
                (ProductGroupExpandableAdapter) mMainBinding.list.getExpandableListAdapter();

        final int index = 1;
        adapter.setSelectedGroup(index);
        mMainBinding.list.setSelectedGroup(index);
    }

    public void onSelectChildButtonClick(View view) {
        final ProductGroupExpandableAdapter adapter =
                (ProductGroupExpandableAdapter) mMainBinding.list.getExpandableListAdapter();

        final int indexG = 2;
        final int indexCh = 2;
        mMainBinding.list.expandGroup(indexG);
        adapter.setSelectedGroup(indexG, indexCh);
        mMainBinding.list.setSelectedChild(indexG, indexCh, true);
    }
}
