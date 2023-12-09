package com.ravi.listmate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private AppCompatEditText username, password, password2;
    private AppCompatButton login, signup, reset, googleLogin, facebookLogin;
    private FirebaseAuth mAuth;
    private AppCompatCheckBox newUser;
    private AppCompatTextView forgotPassword;
    private AppCompatImageView viewPassword, viewPassword2;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> activityResultLauncherGoogleLogin;
    private CallbackManager mCallbackManager;
    private static boolean isFacebookLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
//            redirectToUserProfile();
            LoginManager.getInstance().logOut();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        setViewObjects();
        setNonViewObjects();

        mCallbackManager = CallbackManager.Factory.create();
        viewPassword.setImageResource(R.drawable.ic_password_visibility_off);
        viewPassword2.setImageResource(R.drawable.ic_password_visibility_off);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        activityResultLauncherGoogleLogin = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                            AuthCredential credentials = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                            mAuth.signInWithCredential(credentials)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            redirectToUserProfile();
                                        } else {
                                            Toast.makeText(MainActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            googleSignInClient.revokeAccess().addOnCompleteListener(this, task12 -> {
                                // Cache cleared, you can proceed with other actions
                            });
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        newUser.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                login.setVisibility(View.GONE);
                signup.setVisibility(View.VISIBLE);
                password2.setVisibility(View.VISIBLE);
                forgotPassword.setVisibility(View.GONE);
                reset.setVisibility(View.GONE);
                viewPassword2.setVisibility(View.VISIBLE);
            } else {
                login.setVisibility(View.VISIBLE);
                signup.setVisibility(View.GONE);
                password2.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.VISIBLE);
                reset.setVisibility(View.VISIBLE);
                viewPassword2.setVisibility(View.GONE);
            }
        });

        login.setOnClickListener(view -> loginWithCredentials());

        signup.setOnClickListener(view -> signupWithCredentials());

        reset.setOnClickListener(view -> resetPassword());

        googleLogin.setOnClickListener(view -> signInWithGoogle());

        facebookLogin.setOnClickListener(view -> setFacebookLogin());

        viewPassword.setOnClickListener(view -> {
            if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                viewPassword.setImageResource(R.drawable.ic_password_visibility_off);
            } else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                viewPassword.setImageResource(R.drawable.ic_password_visibility_on);
            }
        });

        viewPassword2.setOnClickListener(view -> {
            if (password2.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                viewPassword2.setImageResource(R.drawable.ic_password_visibility_off);
            } else {
                password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                viewPassword2.setImageResource(R.drawable.ic_password_visibility_on);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                redirectToUserProfile();
            }
        }
    }

    private void setViewObjects() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password_2);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.singup);
        reset = findViewById(R.id.reset_password);
        googleLogin = findViewById(R.id.signup_google);
        facebookLogin = findViewById(R.id.signup_facebook);
        newUser = findViewById(R.id.new_user);
        forgotPassword = findViewById(R.id.forgot_password);
        viewPassword = findViewById(R.id.view_password);
        viewPassword2 = findViewById(R.id.view_password_2);
    }

    private void setNonViewObjects() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setFacebookLogin() {
        isFacebookLogin = true;
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFacebookLogin) {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            redirectToUserProfile();
                        }
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Account already exists with \nsame email address.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginWithCredentials() {
        String emailString = Objects.requireNonNull(username.getText()).toString().trim();
        String passwordString = Objects.requireNonNull(password.getText()).toString().trim();

        if (isValidEmail(emailString)) {
            username.setError("Invalid email address");
            return;
        }

        if (passwordString.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return;
        }

        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            redirectToUserProfile();
                        }
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidUserException e) {
                            username.setError("User does not exist");
                            username.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            username.setError("Invalid email or password");
                            username.requestFocus();
                        } catch (Exception e) {
                            username.setError("Invalid login credentials");
                            username.requestFocus();
                        }
                    }
                });
    }

    private void signupWithCredentials() {
        String emailString = Objects.requireNonNull(username.getText()).toString().trim();
        String passwordString = Objects.requireNonNull(password.getText()).toString().trim();
        String confirmPasswordString = Objects.requireNonNull(password2.getText()).toString().trim();

        if (isValidEmail(emailString)) {
            username.setError("Invalid email address");
            username.requestFocus();
            return;
        }

        if (passwordString.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        if (!passwordString.equals(confirmPasswordString)) {
            password2.setError("Password does not match");
            password2.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification();
                            redirectToUserProfile();
                        }
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            password.setError("Password is weak. Kindly use a combination of alphabets, numbers and special characters");
                            password.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            username.setError("Email is invalid or already in use");
                            username.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            username.setError("Email already exist");
                            username.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void resetPassword() {
        String emailString = Objects.requireNonNull(username.getText()).toString().trim();

        if (emailString.equals("")) {
            username.setError("Enter valid email");
            username.requestFocus();
            return;
        }

        if (isValidEmail(emailString)) {
            username.setError("Invalid email address");
            username.requestFocus();
            return;
        }
        checkIfEmailExists(emailString);
    }

    private void checkIfEmailExists(String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isEmailExists = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                        if (isEmailExists) {
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    showAlertDialogResetPassword();
                                } else {
                                    try {
                                        throw Objects.requireNonNull(task1.getException());
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        username.setError("User does not exist!");
                                        username.requestFocus();
                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            username.setError("User does not exist!");
                            username.requestFocus();
                        }
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        activityResultLauncherGoogleLogin.launch(intent);
    }

    private boolean isValidEmail(CharSequence target) {
        return TextUtils.isEmpty(target) || !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void redirectToUserProfile() {
        addUserToDatabase();
        Intent intent = new Intent(this, UserHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void addUserToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection(Collections.ROOT_COLLECTION).document(Objects.requireNonNull(user.getEmail())).set(new UserData(user.getDisplayName(), user.getEmail()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something went wrong! Try login again.", Toast.LENGTH_SHORT).show();
                            Log.i("Error", e.toString());
                        }
                    });
        }
    }

    private void showAlertDialogResetPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("A link to reset your password is sent to your registered email.");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            newUser.setChecked(false);
            username.setText("");
            password.setText("");
            password2.setText("");
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}