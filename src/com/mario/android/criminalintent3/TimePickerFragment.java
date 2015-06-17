package com.mario.android.criminalintent3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_time, null);
		TimePicker timePicker = (TimePicker) v
				.findViewById(R.id.dialog_date_timePicker);

		return new AlertDialog.Builder(getActivity()).setView(v)
				.setTitle("Crime of Time:")
				.setPositiveButton(android.R.string.ok, null).create();
	}
}
