package com.example.MAD;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ProfileCompanySettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_company_setting, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = view.findViewById(R.id.btnBackCom);

        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });

        Button BtnDeact = view.findViewById(R.id.BtnDeactCom);
        BtnDeact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom dialog
                showDeactivateDialog();
            }
        });

        Button BtnLogOut = view.findViewById(R.id.BtnLogOutCom);
        BtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom dialog
                showLogOutDialog();
            }
        });

        return view;
    }

    // Method to show custom dialog
    private void showDeactivateDialog() {
        // Create a new dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));

        Button BtnConfirm = dialog.findViewById(R.id.BtnSave);
        BtnConfirm.setText("Deactivate");

        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        confirmMessage.setText("Permanently delete your account?\nYour account data won’t be recovered.");
        // Show the dialog
        dialog.show();

        // Set a button's functionality to dismiss the dialog
        Button BtnCancel = dialog.findViewById(R.id.BtnCancel);
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Dismiss the dialog when button is clicked
            }
        });
    }

    private void showLogOutDialog() {
        // Create a new dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));

        Button BtnConfirm = dialog.findViewById(R.id.BtnSave);
        BtnConfirm.setText("Log Out");

        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        confirmMessage.setText("Log out of PathSeeker ?");
        // Show the dialog
        dialog.show();

        // Set a button's functionality to dismiss the dialog
        Button BtnCancel = dialog.findViewById(R.id.BtnCancel);
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Dismiss the dialog when button is clicked
            }
        });

        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.pop_up_message);
                dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));

                TextView message = dialog.findViewById(R.id.text1);
                message.setText("You have been logged out\nThank you.");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();  // Dismiss the dialog after the delay
                    }
                }, 2000);
            }
        });
    }
}