package automation.test.testapp2.view.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import automation.test.testapp2.R;
import automation.test.testapp2.view.adapter.RecyclerViewDataAdapter;
import automation.test.testapp2.model.SectionDataModel;
import automation.test.testapp2.model.SingleItemModel;
import automation.test.testapp2.recycler.TestRecyclerView2;

/**
 * Created by DELL on 7/18/2018.
 */

public class CommonFragment extends Fragment {
    private ArrayList<SectionDataModel> allSampleData;

    int color;

    public CommonFragment() {
    }

    @SuppressLint("ValidFragment")
    public CommonFragment(int color) {
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dummy_fragment, container, false);

        allSampleData = new ArrayList<>();

        createDummyData();

        TestRecyclerView2 recyclerView = (TestRecyclerView2) view.findViewById(R.id.dummyfrag_scrollableview);
        recyclerView.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(allSampleData, this.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


//        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
//        frameLayout.setBackgroundColor(color);
//
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        DessertAdapter adapter = new DessertAdapter(getContext());
//        recyclerView.setAdapter(adapter);

        return view;
    }
    private void createDummyData() {
        for (int i = 1; i <= 20; i++) {
            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle("Section " + i);
            ArrayList<SingleItemModel> singleItemModels = new ArrayList<>();
            for (int j = 1; j <= 20; j++) {
                singleItemModels.add(new SingleItemModel("Item " + j, "URL " + j));
            }
            dm.setAllItemInSection(singleItemModels);
            allSampleData.add(dm);
        }

    }
}
