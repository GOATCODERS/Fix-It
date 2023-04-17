 package com.example.fixit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "username";
    private static final String ARG_PARAM2 = "param2";

      // TODO: Rename and change types of parameters
//    private String mUsername;
//    private String mParam2;

    private TextView name;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView address;
    private TextView contact;
    String mEmail;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    private FirebaseUser user;

//     ConnectionThread checkConnection = new ConnectionThread();

    public ProfileFragment() {
        // Required empty public constructor
    }

     public TextView getName() {
         return name;
     }

     public void setName(String name) {
         this.name.setText(name);
     }

     public TextView getEmail() {
         return email;
     }

     public void setEmail(TextView email) {
         this.email = email;
     }

     public TextView getUsername() {
         return username;
     }

     public void setUsername(TextView username) {
         this.username = username;
     }

     public TextView getPassword() {
         return password;
     }

     public void setPassword(TextView password) {
         this.password = password;
     }

     public TextView getAddress() {
         return address;
     }

     public void setAddress(TextView address) {
         this.address = address;
     }

     public TextView getContact() {
         return contact;
     }

     public void setContact(TextView contact) {
         this.contact = contact;
     }


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, username);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mUsername = getArguments().getString(ARG_PARAM1);
//
////            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentProfile = inflater.inflate(R.layout.fragment_profile, container, false);

        name = fragmentProfile.findViewById(R.id.profile_name);
        email = (TextView) fragmentProfile.findViewById(R.id.profile_email);
        username = (TextView) fragmentProfile.findViewById(R.id.profile_username);
        password = (TextView) fragmentProfile.findViewById(R.id.profile_password_redirect);
        address = (TextView) fragmentProfile.findViewById(R.id.profile_address_redirect);
        contact = (TextView) fragmentProfile.findViewById(R.id.profile_contact);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mEmail = user.getEmail();

        init();


        return fragmentProfile;
    }

     public void init()
     {

         System.out.println("current user mEmail = "+ mEmail);
         database = FirebaseDatabase.getInstance();
         reference = database.getReference("Users");

         Query checkDatabase = reference.orderByChild("email").equalTo(mEmail);

         checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot)
             {
                 if(snapshot.exists())
                 {
                     String mUsername = snapshot.child("username").getValue(String.class);

                     System.out.println("current user mUsername = "+ mUsername);

                     String strName, strEmail, strPassword, strContact, strAddress;
                     strName = snapshot.child(mUsername).child("name").getValue(String.class);
                     strEmail = snapshot.child(mUsername).child("email").getValue(String.class);
                     strContact = snapshot.child(mUsername).child("contact").getValue(String.class);
                     strAddress = snapshot.child(mUsername).child("address").getValue(String.class);

                     System.out.println("profile fragment is strName "+strName);
                     setName(strName);
                     email.setText(strEmail);
                     System.out.println("profile fragment is email "+email.getText().toString());
                     username.setText("@" + mUsername);

                     if (strContact != null) {
                         contact.setText(strContact);
                     }
                     if (strAddress != null) {
                         address.setText(strAddress);
                     }
                 }else {
                     System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++                         onDataChange failed" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++" +
                             "\n +++++++++++++++++++++++++++++++++++++++++++++++++++");
                 }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

     }
}