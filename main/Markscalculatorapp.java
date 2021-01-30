package com.example.studentmarkscalculator;
import android.os.Bundle;
import com.example.studentmarkscalculator.NavigationActionBar;
import com.example.studentmarkscalculator.integration.R;


public class StudentMarksCalculatorActivity extends NavigationActionBar {
    private StudentRecordsDbAdapter dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smc_student_marks_calculator);

        dbHelper = new StudentRecordsDbAdapter(this);
        dbHelper.open();

       
        if (findViewById(R.id.fragment_container) != null) {

           
            if (savedInstanceState != null) {
                return;
            }

            StudentsListFragment firstFragment = new StudentsListFragment();

           
            firstFragment.setArguments(getIntent().getExtras());


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

   
    public void onStudentSelected(long id, String firstName, String lastName) {
       
        StudentDetailsFragment detailsFrag = (StudentDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        if (detailsFrag != null) {
           
            detailsFrag.updateDetailsView(id, firstName, lastName);

        } else {
            
            StudentDetailsFragment newFragment = new StudentDetailsFragment();
            Bundle args = new Bundle();
            args.putLong(StudentDetailsFragment.ARG_STUDENT_ID, id);
            args.putString(StudentDetailsFragment.ARG_STUDENT_FIRSTNAME, firstName);
            args.putString(StudentDetailsFragment.ARG_STUDENT_LASTNAME, lastName);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

           
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

          
            transaction.commit();
        }
    }

 
    public void openSummary() {
       
        StudentDetailsFragment detailsFrag = (StudentDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        if (detailsFrag != null) {
          
        } else {
           
            StudentsSummaryFragment newFragment = new StudentsSummaryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

          
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

      
            transaction.commit();
        }
    }

   
    public StudentRecordsDbAdapter getDbHelper() {return dbHelper;}
}
