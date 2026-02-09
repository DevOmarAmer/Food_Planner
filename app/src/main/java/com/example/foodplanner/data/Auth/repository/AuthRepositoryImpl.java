package com.example.foodplanner.data.Auth.repository;

import androidx.credentials.GetCredentialRequest;

import com.example.foodplanner.data.Auth.dataSource.local.AuthLocalDataSource;
import com.example.foodplanner.data.Auth.dataSource.remote.AuthRemoteDataSource;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthRemoteDataSource remoteDataSource;
    private final AuthLocalDataSource localDataSource;

    public AuthRepositoryImpl(AuthRemoteDataSource remoteDataSource,
            AuthLocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        remoteDataSource.login(email, password, new AuthRemoteDataSource.AuthResultCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName, String photoUrl) {
                localDataSource.saveUserSession(userId, email, displayName, photoUrl);
                callback.onSuccess(userId, email, displayName, photoUrl);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    @Override
    public void signUp(String name, String email, String password, AuthCallback callback) {
        remoteDataSource.signUp(name, email, password, new AuthRemoteDataSource.AuthResultCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName, String photoUrl) {
                localDataSource.saveUserSession(userId, email, displayName, photoUrl);
                callback.onSuccess(userId, email, displayName, photoUrl);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    @Override
    public void signInWithGoogle(String idToken, AuthCallback callback) {
        remoteDataSource.signInWithGoogle(idToken, new AuthRemoteDataSource.AuthResultCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName, String photoUrl) {
                localDataSource.saveUserSession(userId, email, displayName, photoUrl);
                callback.onSuccess(userId, email, displayName, photoUrl);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    @Override
    public void continueAsGuest() {
        localDataSource.setGuestMode();
    }

    @Override
    public GetCredentialRequest buildGoogleSignInRequest(String webClientId) {
        return remoteDataSource.buildGoogleSignInRequest(webClientId);
    }

    @Override
    public void saveUserSession(String userId, String email, String displayName, String imageUrl) {
        localDataSource.saveUserSession(userId, email, displayName, imageUrl);
    }

    @Override
    public boolean isUserLoggedIn() {
        return remoteDataSource.isAuthenticated();
    }

    @Override
    public boolean isGuestMode() {
        return localDataSource.isGuestMode();
    }

    @Override
    public void logout() {
        remoteDataSource.signOut();
        localDataSource.clearUserData();
    }
}
