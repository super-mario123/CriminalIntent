package com.mario.android.criminalintent3;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;

	// private static final String TAG = "CrimeListFragment";

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);
		setRetainInstance(true);
		mSubtitleVisible = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.get(getActivity()).addCrime(crime);
			Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
			intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivityForResult(intent, 0);
			return true;
		case R.id.menu_item_show_subtitle:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
				mSubtitleVisible = true;
				item.setTitle(R.string.hide_subtitle);
			} else {
				getActivity().getActionBar().setSubtitle(null);
				mSubtitleVisible = false;
				item.setTitle(R.string.show_subtitle);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		Crime crime = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.empty_list, container, false);

		ListView listView = (ListView) v.findViewById(android.R.id.list);
		listView.setEmptyView(v.findViewById(android.R.id.empty));

		Button mAddCrimeButton = (Button) v.findViewById(R.id.add_crime_button);
		mAddCrimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent intent = new Intent(getActivity(),
						CrimePagerActivity.class);
				intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(intent, 0);
			}
		});
		// View v = super.onCreateView(inflater, container, savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			registerForContextMenu(listView);
		} else {
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.crime_list_item_context, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						CrimeLab crimeLab = CrimeLab.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								crimeLab.deleteCrime(adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						crimeLab.saveCrimes();
						return true;

					default:
						return false;
					}
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {

				}
			});
		}
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
		// Toast.makeText(getActivity(), c.getTitle() + "was clicked",
		// Toast.LENGTH_SHORT).show();
		// Log.d(TAG, c.getTitle()+"was clicked");
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(i);
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {

		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_crime, null);
			}
			Crime c = getItem(position);

			TextView titleTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.getTitle());
			TextView dateTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView
					.setText(DateFormat.format("EE-MM-dd,yyyy", c.getDate()));
			CheckBox solvedCheckBox = (CheckBox) convertView
					.findViewById(R.id.crime_list_item_slovedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());

			return convertView;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,
				menu);
	}
}
