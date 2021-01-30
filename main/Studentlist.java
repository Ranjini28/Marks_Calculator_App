package com.example.studentmarkscalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.studentmarkscalculator.integration.R;

public class StudentsListFragment extends ListFragment implements View.OnClickListener{
    
    private StudentMarksCalculatorActivity parentActivity;

  
    private StudentRecordsDbAdapter dbHelper;

   
    private int listItemLayout;

  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       
        listItemLayout = R.layout.smc_student_records_list_item;
    }

 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smc_student_records, container, false);

        Button addStudentRecordButton = (Button) v.findViewById(R.id.addStudentRecordButton);
        addStudentRecordButton.setOnClickListener(this);

        Button studentsSummaryButton = (Button) v.findViewById(R.id.summaryButton);
        studentsSummaryButton.setOnClickListener(this);

        Button infoButton = (Button) v.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(this);

        return v;
    }

  
    @Override
    public void onStart() {
        super.onStart();

        dbHelper = ((StudentMarksCalculatorActivity) getActivity()).getDbHelper();
        displayListView();

        if (getFragmentManager().findFragmentById(R.id.details_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = ((StudentMarksCalculatorActivity)activity);
    }

  
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ViewGroup listItem = ((ViewGroup)v);
        String firstName = ((TextView)listItem.findViewById(R.id.firstName)).getText().toString();
        String lastName = ((TextView)listItem.findViewById(R.id.lastName)).getText().toString();

        parentActivity.onStudentSelected(id, firstName, lastName);

        getListView().setItemChecked(position, true);
    }

  
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addStudentRecordButton:
                openAddStudentDialog();
                break;
            case R.id.summaryButton:
                ((StudentMarksCalculatorActivity) getActivity()).openSummary();
                break;
            case R.id.infoButton:
                openInfoDialog();
        }
    }

   
    private void openAddStudentDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View studentEntryView = factory.inflate(R.layout.smc_student_entry, null);

        final EditText studentNumberField = (EditText) studentEntryView.findViewById(R.id.studentNumber);
        final EditText studentFirstNameField = (EditText) studentEntryView.findViewById(R.id.firstName);
        final EditText studentLastNameField = (EditText) studentEntryView.findViewById(R.id.lastName);

        new AlertDialog.Builder(getActivity())
                .setTitle("Enter Student Info:")
                .setView(studentEntryView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) throws NumberFormatException, SQLiteConstraintException {
                String studentNumber = studentNumberField.getText().toString();
                String studentFirstName = studentFirstNameField.getText().toString();
                String studentLastName = studentLastNameField.getText().toString();

                if (studentNumber.length() > 0
                        && studentFirstName.length() > 0
                        && studentLastName.length() > 0
                        ) { 

                    try {
                        dbHelper.insertStudent(
                                Integer.parseInt(studentNumberField.getText().toString()),
                                studentFirstNameField.getText().toString(),
                                studentLastNameField.getText().toString()
                        );

                        dbHelper.insertMarks(
                                Integer.valueOf(studentNumberField.getText().toString()),
                                null,
                                null,
                                null
                        );
                    } catch (NumberFormatException e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Invalid input; student not added")
                                .setMessage(e.getMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    } catch (SQLiteConstraintException e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Invalid input; student not added")
                                .setMessage("That student number is already in use!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                    displayListView();
                } else { //if there are empty entries
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Invalid input; student not added")
                            .setMessage("Student number, first name and last name cannot be empty!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                      
                    }
                }).show();

    }

    private void openInfoDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Info")
                .setMessage("Welcome to the student marks calculator by Mathew Poff! Here you " +
                        "can see a list of students whose marks can be recorded." +
                        "\n\n-Click on a student's list entry to view and edit their marks " +
                        "(bonus points are allowed) or remove their record." +
                        "\n\n-Click on 'Add Student' to add a new student record." +
                        "\n\n-Click on 'Summary' to see the average marks for all students in " +
                        "the database (blank marks are not counted).")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllStudents();

       
        String[] columns = new String[] {
                StudentRecordsDbAdapter.STUDENT_ID,
                StudentRecordsDbAdapter.STUDENT_FIRSTNAME,
                StudentRecordsDbAdapter.STUDENT_LASTNAME
        };

      
        int[] to = new int[] {
                R.id.studentNumber,
                R.id.firstName,
                R.id.lastName
        };

        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                getActivity(), listItemLayout,
                cursor,
                columns,
                to,
                0);

        setListAdapter(dataAdapter);

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchStudentById(Long.valueOf(constraint.toString()));
            }
        });
    }
}
