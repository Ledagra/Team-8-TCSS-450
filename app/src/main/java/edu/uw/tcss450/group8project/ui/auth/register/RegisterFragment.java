package edu.uw.tcss450.group8project.ui.auth.register;

import static edu.uw.tcss450.group8project.utils.PasswordValidator.*;
import static edu.uw.tcss450.group8project.utils.PasswordValidator.checkClientPredicate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group8project.R;
import edu.uw.tcss450.group8project.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.group8project.utils.PasswordValidator;


/**
 * Fragment for registering new user into database.
 */
public final class RegisterFragment extends Fragment {
    /** Minimum length for a password. */
    private static final int MIN_PASSWORD_LENGTH = 8;
    /** Binding object for fragment_register.xml. */

    private FragmentRegisterBinding binding;
    /** RegisterViewModel object for this fragment. */

    private RegisterViewModel mRegisterModel;
    /** PasswordValidator object for the name input. */

    private PasswordValidator mNameValidator = checkPwdLength(1);
    /** PasswordValidator object for the email input. */

    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));
    /** PasswordValidator object for the password input. */

    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editPassword2
                    .getText().toString()))
                    .and(checkPwdLength(MIN_PASSWORD_LENGTH))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));
    /** Constructor for RegisterFragment. */
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonRegister.setOnClickListener(this::attemptRegister);
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }
    /**
     * Method that attempts registration when button pressed.
     *
     * @param button button press input
     */
    private void attemptRegister(final View button) {
        validateFirst();
    }

    /**
     * Method that validates firstname.
     */
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editFirst
                        .getText().toString().trim()),
                this::validateLast,
                result -> binding.editFirst
                        .setError("Please enter a first name."));
    }

    /**
     * Method that validates lastname.
     */
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editLast
                        .getText().toString().trim()),
                this::validateUsername,
                result -> binding.editLast
                        .setError("Please enter a last name."));
    }

    /**
     * Method that validates username.
     */
    private void validateUsername() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editUser
                        .getText().toString().trim()),
                this::validateEmail,
                result -> binding.editUser
                        .setError("Please enter a username."));
    }

    /**
     * Method that validates email.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail
                        .getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> binding.editEmail
                        .setError("Please enter a valid Email address."));
    }
    /**
     * Method that validates passwords match.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.editPassword2
                                .getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(binding.editPassword1
                        .getText().toString().trim()),
                this::validatePassword,
                result -> binding.editPassword1
                        .setError("Passwords must match."));
    }
    /**
     * Method that validates password.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword1
                        .getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword1
                        .setError("Please enter a valid Password."));
    }
    /**
     * Method that verifies authentication with server.
     */
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                binding.editFirst.getText().toString(),
                binding.editLast.getText().toString(),
                binding.editUser.getText().toString(),
                binding.editEmail.getText().toString(),
                binding.editPassword1.getText().toString());
    }

    /**
     * Method that navigates to account verification page.
     */
    private void navigateToAccountVerification() {
        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            alert.setCancelable(false);
            alert.setTitle(getActivity().getString(R.string.account_verification_title));
            alert.setMessage(getActivity().getString(R.string.account_verification_message));
            alert.setButton(DialogInterface.BUTTON_NEUTRAL
                    , getActivity().getString(R.string.action_sign_in_short)
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
                            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();

                    directions.setEmail(binding.editEmail.getText().toString());
                    directions.setPassword(binding.editPassword1.getText().toString());

                    Navigation.findNavController(getView()).navigate(directions);
                }
            });
            alert.show();




//        Navigation.findNavController(getView())
//                .navigate(RegisterFragmentDirections
//                        .actionRegisterFragmentToAccountVerificationFragment(email, jwt, username));
    }

    /**
     * An observer on the HTTP Response from the web server.
     * This observer should be attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editEmail.setError(
                            "Error authenticating: "
                                    + response.getJSONObject("data")
                                    .getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON parse error", e.getMessage());
                }
            } else {
                navigateToAccountVerification();

            }
        } else {
            Log.d("JSON response", "No response");
        }

    }
}
