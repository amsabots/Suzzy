package com.example.suzzy.BottomSheets;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.MainFrags.MoreFrag;
import com.example.suzzy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import static com.example.suzzy.MainFrags.CartFrag.SUB_TOTALCOST;

public class Payment extends BottomSheetDialogFragment implements View.OnClickListener {
    ImageView toggle_explanation;
    LinearLayout toggle_container;
    boolean isExplanationVisible = false;
    TextView change_name, change_location, payable_amount, txt11, tgd, tgm,
            checkout_subtotal, location, name;
    List<TextView> textViews;
    String total, mname, mlocation, payment_method, muser;
    long mcount, total_amount;
    private static final String TAG = "Payment";

    public static Payment newInstace() {
        return new Payment();
    }

    ProgressDialog progressDialog;
    MaterialButton submit;
    RadioGroup radioGroup;
   RadioButton radioButton;
   CoordinatorLayout msnackbar;
   List<String> Item_Ids;
   public interface SendDataBackToHostingActivity{
       void dataAttached(String data);
   }
SendDataBackToHostingActivity mCallback;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_bottom_sheet, container, false);
        initViews(view);
        underLineViews();
        total = getArguments().getString(SUB_TOTALCOST);
        checkout_subtotal.setText("Ksh "+total);
        progressDialog = new ProgressDialog(getContext());
        getUserInfo();
        msnackbar = view.findViewById(R.id.cartfrag_snackbar);
        payment_method = "Payment on Delivery";
        muser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Item_Ids = new ArrayList<>();
        return view;
    }

    private void underLineViews() {
        textViews.add(payable_amount);
        textViews.add(change_name);
        textViews.add(txt11);
        textViews.add(tgd);
        textViews.add(tgm);
        textViews.add(change_location);
        underline(textViews);
        submit.setOnClickListener(this);
        change_name.setOnClickListener(this);
        change_location.setOnClickListener(this);
    }

    private void initViews(View view) {
        textViews = new ArrayList<>();
        toggle_explanation = view.findViewById(R.id.toggle_explanations);
        toggle_container = view.findViewById(R.id.checkout_toggle_explanation_view);
        toggle_explanation.setOnClickListener(this);
        payable_amount = view.findViewById(R.id.payable_amount);
        txt11 = view.findViewById(R.id.textView11);
        tgd = view.findViewById(R.id.toggle_on_delivery);
        tgm = view.findViewById(R.id.toggle_mpesa);
        change_location = view.findViewById(R.id.checkout_change_location);
        change_name = view.findViewById(R.id.checkout_change_name);
        name = view.findViewById(R.id.checkoutname);
        location = view.findViewById(R.id.checkoutlocation);
        checkout_subtotal = view.findViewById(R.id.checkout_subtotal);
        submit = view.findViewById(R.id.checkout_commit_changes);
        radioGroup = view.findViewById(R.id.checkout_radiogroup);
        getRadioselectedListener(view);

    }



    private void getUserInfo() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait......");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(dataSnapshot.child("name").exists()){
                    name.setText(dataSnapshot.child("name").getValue().toString());

                }
                if(dataSnapshot.child("location").exists()){
                    DataSnapshot locate = dataSnapshot.child("location");
                    location.setText(locate.child("residence").getValue().toString() + ", " +
                            locate.child("city").getValue().toString());
                    mname = dataSnapshot.child("name").getValue().toString();
                    mlocation = locate.child("residence").getValue().toString() + ", " +
                            locate.child("city").getValue().toString();
                }

                mcount = dataSnapshot.child("Cart").getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Check your Internet, An error occured during execution",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void underline(List<TextView> textViews) {
        for (int i = 0; i < textViews.size(); i++) {
            General.underLineTextview(textViews.get(i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_explanations:
                toggle_container.setVisibility(isExplanationVisible ? View.GONE : View.VISIBLE);
                toggle_explanation.setImageDrawable(isExplanationVisible?getResources()
                        .getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp):getResources()
                        .getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                isExplanationVisible = !isExplanationVisible;
                break;
            case R.id.checkout_commit_changes:
                commitOrder();
                break;
            case R.id.checkout_change_location:
                startActivity(new Intent(getContext(), MoreFrag.class));
                break;
            case R.id.checkout_change_name:
                startActivity(new Intent(getContext(), MoreFrag.class));
                break;
        }
    }

    private void commitOrder() {
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.i(TAG, "commitOrder: PAYMENT METHOD: "+payment_method);
        Map<String, Object> params = new HashMap<>();
        params.put("time", ServerValue.TIMESTAMP);
        params.put("name", mname);
        params.put("location", mlocation);
        params.put("status", "Pending confirmation");
        params.put("packaging", false);
        params.put("number", mcount);
        params.put("amount",Long.parseLong(total));
        params.put("message", "awaiting order and packaging receipt confirmation");
        params.put("payment_method", payment_method);
        params.put("Customer",muser);
        params.put("delete", true);
       final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
       final String uniqID = database.push().getKey();
               database.child("Orders").child(uniqID).updateChildren(params)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        database.child("Users")
                                .child(muser).child("Cart")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot items:dataSnapshot.getChildren()
                                             ) {
                                           Item_Ids.add(items.child("id").getValue().toString());
                                        }
                                        uploadItems(Item_Ids, uniqID, database);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }else{
                            Snackbar.make(msnackbar, "Failed, please try again later", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             progressDialog.dismiss();
                Toast.makeText(getContext(), "Please check your network and Try again",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadItems(List<String> item_ids, String id, DatabaseReference mref) {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Action running.......");
        progressDialog.show();
        for (int i = 0; i < item_ids.size(); i++){
            Map<String, Object> item = new HashMap<>();
            item.put("id", item_ids.get(i));
            mref.child("Orders").child(id).child("items").push()
                    .updateChildren(item)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                              //Toast.makeText(getContext(), "Almost done...", Toast.LENGTH_SHORT).show();
                            }else{
                                Snackbar.make(msnackbar, "Failed, Please try Again", BaseTransientBottomBar.LENGTH_LONG)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                commitOrder();
                                            }
                                        }).show();
                            }
                        }
                    });
            if(i == item_ids.size() -1 ){
                progressDialog.dismiss();
                mCallback.dataAttached("payment_bottom_sheet");
            }
        }
    }

    private void getRadioselectedListener(final View view) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
              radioButton = view.findViewById(checkedId);
              payment_method = radioButton.getText().toString();
            }
        });
    }
//    public class Items{
//        String message, Customer, name, location, status, payment_method;
//        long time, number
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       try{
       mCallback = (SendDataBackToHostingActivity)context;
       }catch (ClassCastException e){
           throw  new ClassCastException("must implement the Listener");
       }
    }
}
