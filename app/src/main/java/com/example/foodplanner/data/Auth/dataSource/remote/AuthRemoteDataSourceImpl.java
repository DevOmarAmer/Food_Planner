package com.example.foodplanner.data.Auth.dataSource.remote;

import androidx.credentials.GetCredentialRequest;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {

    private final FirebaseAuth firebaseAuth;

    public AuthRemoteDataSourceImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public AuthRemoteDataSourceImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void login(String email, String password, AuthResultCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String displayName = user.getDisplayName() != null
                                    ? user.getDisplayName()
                                    : "User";
                            String photoUrl = user.getPhotoUrl() != null
                                    ? user.getPhotoUrl().toString()
                                    : null;
                            callback.onSuccess(user.getUid(), user.getEmail(), displayName, photoUrl);
                        } else {
                            callback.onError("Login failed: User is null");
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        callback.onError(errorMessage);
                    }
                });
    }

    @Override
    public void signUp(String name, String email, String password, AuthResultCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.trim())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        callback.onSuccess(user.getUid(), user.getEmail(), name.trim(), null);
                                    });
                        } else {
                            callback.onError("Sign up failed: User is null");
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Sign up failed";
                        callback.onError(errorMessage);
                    }
                });
    }

    @Override
    public void signInWithGoogle(String idToken, AuthResultCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String displayName = user.getDisplayName() != null
                                    ? user.getDisplayName()
                                    : "User";
                            String photoUrl = user.getPhotoUrl() != null
                                    ? user.getPhotoUrl().toString()
                                    : null;
                            callback.onSuccess(user.getUid(), user.getEmail(), displayName, photoUrl);
                        } else {
                            callback.onError("Google sign-in failed: User is null");
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Google sign-in failed";
                        callback.onError(errorMessage);
                    }
                });
    }

    @Override
    public GetCredentialRequest buildGoogleSignInRequest(String webClientId) {
        GetSignInWithGoogleOption option = new GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce(generateNonce())
                .build();

        return new GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build();
    }

    @Override
    public boolean isAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public String getCurrentUserEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    @Override
    public String getCurrentUserDisplayName() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            return user.getDisplayName();
        }
        return "User";
    }

    /**
     * Generate a secure nonce for Google Sign-In
     */
    private String generateNonce() {
        try {
            String rawNonce = UUID.randomUUID().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawNonce.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString();
        }
    }
}
