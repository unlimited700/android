package com.apnavaidya;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by neeraj on 25/12/15.
 */
public class RemediesFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List expandableListTitle;   // async will fill this
    static HashMap expandableListDetail=new HashMap();;
    private int lastExpandedPosition = -1;
    Bundle arg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.remedies_layout,container, false);
        expandableListView = (ExpandableListView)rootview.findViewById(R.id.expandableListViewRemedy);
        showView();
        return rootview;
    }
    void setArgumentss(Bundle bundle)
    {
        Log.i("info","came here");
        arg=bundle;
        if (bundle.getSerializable("remedy") != null) {
            Log.i("info","finally got it ");
            expandableListDetail=(HashMap) bundle.getSerializable("remedy");
        }
        else {
            Log.i("INFO", "no hashmap got");
            return;
        }
    }

    void showView() {
      /*  Bundle bundle = arg;
        if(bundle==null) {
            Log.i("INFO", "got null bundle");
            return;}
        if (bundle.getSerializable("hasmap") != null) {
            expandableListDetail = (HashMap) bundle.getSerializable("hasmap");
        }
        */
        Log.i("INFO","successsssssssssssssssssssssssssss");
        expandableListTitle = new ArrayList(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListAdapter(getActivity().getApplicationContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
               /* Toast.makeText(getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        /*expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });*/

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)), Toast.LENGTH_SHORT
                )
                        .show();*/

                return false;
            }
        });



    }


}

