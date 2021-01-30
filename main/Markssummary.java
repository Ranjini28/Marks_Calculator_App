package com.example.studentmarkscalculator;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.DecimalFormat;


public class StudentsSummaryFragment  {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.smc_students_summary, container, false);
    }

    
    @Override
    public void onStart() {
        super.onStart();

        Cursor marks = ((StudentMarksCalculatorActivity) getActivity()).getDbHelper().fetchAverageMarks();

        TextView labMarkField = (TextView) getActivity().findViewById(R.id.averageLabMark);
        TextView midtermMarkField = (TextView) getActivity().findViewById(R.id.averageMidtermMark);
        TextView finalExamMarkField = (TextView) getActivity().findViewById(R.id.averageFinalExamMark);
        TextView overallMarkField = (TextView) getActivity().findViewById(R.id.averageOverallMark);

        double labMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_LAB));
        double midtermMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_MIDTERM));
        double finalExamMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_FINAL_EXAM));
        double overallMark = labMark + midtermMark + finalExamMark;

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        labMarkField.setText(numberFormat.format(labMark));
        midtermMarkField.setText(numberFormat.format(midtermMark));
        finalExamMarkField.setText(numberFormat.format(finalExamMark));
        overallMarkField.setText(numberFormat.format(overallMark));

    }

}
