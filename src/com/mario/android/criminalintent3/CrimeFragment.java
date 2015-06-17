package com.mario.android.criminalintent3;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {

	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO=1;
	private static final int REQUEST_CONTACT = 2;
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_TIME = "time";
	private static final String DIALOG_IMAGE = "image";
	public static final String EXTRA_CRIME_ID = "com.mario.android.criminalintent3.crime_id";
	private static final String TAG = "CrimeFragment";
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
//	private Button mTimeButton;
	private ImageView mPhotoView;
	private ImageButton mPhotoButton;
	private Button mSuspectButton;
	private CheckBox mSolvedCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crime_list_item_context, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(mCrime);
		case android.R.id.home:
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void updateDate() {
		mDateButton.setText(DateFormat.format("EEEE-MM-dd-yyyy",
				mCrime.getDate()));
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mCrime.setTitle(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment
						.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});

//		mTimeButton = (Button) v.findViewById(R.id.crime_time);
//		mTimeButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				FragmentManager fm = getActivity().getSupportFragmentManager();
//				TimePickerFragment time = new TimePickerFragment();
//				time.show(fm, DIALOG_TIME);
//			}
//		});

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mCrime.setSolved(isChecked);
					}
				});

		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});
		
		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if(p == null){
					return;
				}
				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});

		// 检查相机是否可用
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
				|| Camera.getNumberOfCameras() > 0;
				if(! hasACamera){
					mPhotoButton.setEnabled(false);
				}
				
		Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});
		
		mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
				startActivity(i);
			}
		});
		if(mCrime.getSuspect() != null){
			mSuspectButton.setText(mCrime.getSuspect());
		}
		return v;
	}
	
	private void showPhoto(){
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		
		if(p  != null){
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		}else if(requestCode == REQUEST_PHOTO){
			String filename = data.getStringExtra(CrimeCameraFragment.EATRA_PHOTO_FILENAME);
			if(filename != null){
//				Log.i(TAG, "filename:" + filename);
				Photo p = new Photo(filename);
				mCrime.setPhoto(p);
//				Log.i(TAG, "Crime: " + mCrime.getTitle()+ " has a photo");
				showPhoto();
			}
		}else if(requestCode == REQUEST_CONTACT){
			Uri contactUri = data.getData();
			String[] queryFields = new String[]{
					ContactsContract.Contacts.DISPLAY_NAME
			};
			
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
			
			if(c.getCount() == 0){
				c.close();
				return;
			}
			
			c.moveToFirst();
			String suspect = c.getString(0);
			mCrime.setSuspect(suspect);
			mSuspectButton.setText(suspect);
			c.close();
		}

	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
	
	private String getCrimeReport(){
		String solvedString = null;
		if(mCrime.isSolved()){
			solvedString = getString(R.string.crime_report_solved);
		}else{
			solvedString = getString(R.string.crime_report_unsolved);
		}
		
		String dateFormat = "EEE,MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		
		String suspect = mCrime.getSuspect();
		if(suspect == null){
			suspect = getString(R.string.crime_report_no_suspect);
		}else{
			suspect = getString(R.string.crime_report_subject,suspect);
		}
		
		String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
		
		return report;
	}
}
