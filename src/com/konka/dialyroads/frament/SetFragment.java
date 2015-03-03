package com.konka.dialyroads.frament;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konka.dialyroads.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetFragment extends Fragment {
	private FragmentTabHost mTabHost;
	private final static String TAG_TAB_1 = "base";
	private final static String TAG_TAB_2 = "image";
	private final static String TAG_TAB_3 = "video";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webfileindex, container, false);
		mTabHost = (FragmentTabHost) view.findViewById(R.id.fragmentTabHost);

		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabFrameLayout);

		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_1).setIndicator("", getActivity().getResources().getDrawable(R.drawable.ic_tab_common_setting)), SetBaseFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_2).setIndicator("", getActivity().getResources().getDrawable(R.drawable.ic_tab_camera_setting)), SetImageFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_3).setIndicator("", getActivity().getResources().getDrawable(R.drawable.ic_tab_video_setting)), SetVideoFragment.class, null);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTabHost = null;
	}
}
