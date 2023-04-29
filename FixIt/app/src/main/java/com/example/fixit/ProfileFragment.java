package com.example.fixit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "username";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mUsername;
//    private String mParam2;

    private TextView name;
    private TextView email;
    private TextView age;
    private TextView password;
    private TextView address;
    private TextView contact;
    private ProgressBar progressBar;
    private ImageButton profileImage;
    private Bitmap bitmap;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    StorageReference storage;
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

    public void setEmail(String email) {
        this.email.setText(email);
    }


    public TextView getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address.setText(address);
    }

    public TextView getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.setText(contact);
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
        requireActivity().setTitle("My Profile");
        name = fragmentProfile.findViewById(R.id.profile_name);
        email = (TextView) fragmentProfile.findViewById(R.id.profile_email);
        password = (TextView) fragmentProfile.findViewById(R.id.profile_password_redirect);
        address = (TextView) fragmentProfile.findViewById(R.id.profile_address_redirect);
        contact = (TextView) fragmentProfile.findViewById(R.id.profile_contact);
        age = (TextView) fragmentProfile.findViewById(R.id.profile_age);
        progressBar = fragmentProfile.findViewById(R.id.progressBar);
        profileImage = fragmentProfile.findViewById(R.id.profile_image);
        progressBar.setVisibility(View.VISIBLE);

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), UploadProfilePictureActivity.class);
                startActivity(intent);
            }
        });
        fragmentProfile.findViewById(R.id.btnEditProfile).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        init();

        return fragmentProfile;
    }

    public void init() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String uid = user.getUid();

        System.out.println("current user mUsername = " + uid);

        DatabaseReference checkDatabase = reference.child(uid);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strName, strEmail, strPassword, strContact, strAge, strProfilePath;
                User userData = snapshot.getValue(User.class);

                if (userData != null) {
                    strContact = userData.getContact();
                    strName = userData.getName();
                    strEmail = userData.getEmail();
                    strAge = userData.getAge();
                    strProfilePath = userData.getProfilePic();

                    setName(strName);
                    setEmail(strEmail);


                    if (strContact != null) {
                        setContact(strContact);
                    }
                    if (strAge != null) {
                        age.setText(strAge);
                    }
                    if(strProfilePath!=null){
                        storage = FirebaseStorage.getInstance().getReference("User_dp/"+strProfilePath);

                        try
                        {
                            File localFile = File.createTempFile("tempFile", ".png");
                            storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);

                                    profileImage.setImageBitmap(newBitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to load file image", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //profileImage=




                } else {
                    Toast.makeText(getContext(), "User data not available at the moment", Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}