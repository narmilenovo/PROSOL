package com.example.user_management.utils;

public class Constants {


    private Constants() {
    }

    public static final String SWG_REMOVE_PRIVILEGES_ROLE_OPERATION = "Remove privileges from role";
    public static final String SWG_REMOVE_ROLE_USER_OPERATION = "Remove roles from user";
    public static final String SWG_ROLE_REMOVE_PRIVILEGES_MESSAGE = "Privileges Removed successfully !!";
    public static final String SWG_USER_REMOVE_ROLE_MESSAGE = "Roles Removed successfully !!";
    public static final String SWG_ASSIGN_PRIVILEGES_ROLE_OPERATION = "Assign privileges to role";
    public static final String SWG_ASSIGN_ROLE_USER_OPERATION = "Assign role to User";
    public static final String SWG_ROLE_ASSIGN_PRIVILEGES_MESSAGE = "Privileges added successfully !!";
    public static final String SWG_USER_ASSIGN_ROLE_MESSAGE = "Roles added successfully !!";

    public static final String REPOSITORY_PACKAGE = "com.example.user_management.repository.";

    public static final long TOKEN_LIFETIME_SECONDS = 24 * 60 * 60;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "authorities";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String JWT_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public static final long ACCESS_TOKEN_EXP = 86400000;
    public static final long REFRESH_TOKEN_EXP = 604800000;

    public static final String JWT_ILLEGAL_ARGUMENT_MESSAGE = "An error occurred during getting username from token";
    public static final String JWT_EXPIRED_MESSAGE = "The token is expired and not valid anymore";
    public static final String JWT_SIGNATURE_MESSAGE = "Authentication Failed. Username or Password not valid.";
    public static final String UNAUTHORIZED_MESSAGE = "You are not authorized to view the resource";
    public static final String FORBIDDEN_MESSAGE = "You don't have the right to access to this resource";
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found.";
    public static final String INVALID_DATA_MESSAGE = "One or many parameters in the request's body are invalid";

    public static final String MESSAGE_KEY = "message";
    public static final String DATA_KEY = "message";
    public static final String INVALID_TOKEN_MESSAGE = "The token is invalid!";
    public static final String TOKEN_EXPIRED_MESSAGE = "You token has been expired!";
    public static final String ALREADY_CONFIRMED_MESSAGE = "User Already been confirmed!";
    public static final String ACCOUNT_DEACTIVATED_MESSAGE = "Your account has been deactivated!";
    public static final String ACCOUNT_NOT_CONFIRMED_MESSAGE = "Your account isn't confirmed yet!";
    public static final String ACCOUNT_CONFIRMED_MESSAGE = "Your account confirmed successfully!";
    public static final String PASSWORD_LINK_SENT_MESSAGE = "A password reset link has been sent to your email box!";
    public static final String RESET_PASSWORD_SUCCESS_MESSAGE = "Your password has been reset successfully!";
    public static final String VALIDATE_TOKEN_SUCCESS_MESSAGE = "valid";
    public static final String TOKEN_NOT_FOUND_MESSAGE = "You token has been expired!";
    public static final String PASSWORD_NOT_MATCH_MESSAGE = "The current password don't match!";
    public static final String USER_PICTURE_NO_ACTION_MESSAGE = "Unknown action!";
    public static final String NO_ROLE_FOUND_WITH_ID_MESSAGE = "Role not found with this id !!";
    public static final String NO_ROLE_FOUND_WITH_NAME_MESSAGE = "Role not found with this Name !!";
    public static final String NO_USER_FOUND_WITH_ID_MESSAGE = "No user found with this id!";
    public static final String NO_USER_FOUND_WITH_EMAIL_MESSAGE = "No user found with this email!";

    public static final String SWG_AUTH_TAG_NAME = "Authentication";
    public static final String SWG_AUTH_TAG_DESCRIPTION = "Operations pertaining to login,refresh,logout,account information";
    public static final String SWG_AUTH_REGISTER_OPERATION = "Register a new user in the system";
    public static final String SWG_AUTH_BULK_OPERATION = "Bulk upload in the system";
    public static final String SWG_AUTH_REGISTER_MESSAGE = "User registered successfully!";
    public static final String SWG_AUTH_REGISTER_ERROR = "Failed to register the user";
    public static final String SWG_AUTH_LOGIN_OPERATION = "Authenticate an user";
    public static final String SWG_REFRESH_TOKEN_OPERATION = "Token refresh";
    public static final String SWG_AUTH_LOGOUT_OPERATION = "User Logout ";
    public static final String SWG_TOKEN_VALIDATION_OPERATION = "Valid Token Checking ";
    public static final String SWG_AUTH_LOGIN_MESSAGE = "Authenticated successfully!";
    public static final String SWG_AUTH_LOGOUT_MESSAGE = "User successfully Logged Out!";
    public static final String SWG_TOKEN__MESSAGE = "Token is validated Successfully !!!";
    public static final String SWG_AUTH_LOGIN_ERROR = "Bad credentials | The account is deactivated | The account isn't confirmed yet";
    public static final String SWG_AUTH_CONFIRM_ACCOUNT_OPERATION = "Confirm the account of an user";
    public static final String SWG_AUTH_CONFIRM_ACCOUNT_MESSAGE = "Account confirmed successfully!";
    public static final String SWG_AUTH_CONFIRM_ACCOUNT_ERROR = "The token is invalid | The token has been expired";

    public static final String SWG_RES_PWD_TAG_NAME = "Password Reset";
    public static final String SWG_RES_PWD_TAG_DESCRIPTION = "Operations pertaining to user's reset password process";
    public static final String SWG_RES_PWD_FORGOT_OPERATION = "Request a link to reset the password";
    public static final String SWG_RES_PWD_FORGOT_MESSAGE = "Reset link sent to the mail box successfully!";
    public static final String SWG_RES_PWD_FORGOT_ERROR = "No user found with the email provided";
    public static final String SWG_RES_PWD_RESET_OPERATION = "Change the user password through a reset token";
    public static final String SWG_RES_PWD_RESET_MESSAGE = "The action completed successfully!";
    public static final String SWG_RES_PWD_RESET_ERROR = "The token is invalid or has expired";

    public static final String SWG_TOKEN_TAG_NAME = "Token";
    public static final String SWG_TOKEN_TAG_DESCRIPTION = "Token validation and refresh";
    public static final String SWG_TOKEN_VALIDATE_OPERATION = "Validate a token";
    public static final String SWG_TOKEN_VALIDATE_MESSAGE = "The token is valid";
    public static final String SWG_TOKEN_VALIDATE_ERROR = "Invalid token | The token has expired";
    public static final String SWG_TOKEN_REFRESH_OPERATION = "Refresh token by generating new one";
    public static final String SWG_TOKEN_REFRESH_MESSAGE = "New access token generated successfully";
    public static final String SWG_TOKEN_REFRESH_ERROR = "Invalid token | The token is unallocated";

    public static final String SWG_USER_TAG_NAME = "Users";
    public static final String SWG_USER_TAG_DESCRIPTION = "Users manipulation";
    public static final String SWG_USER_LIST_OPERATION = "Get all users";
    public static final String SWG_USER_LIST_MESSAGE = "List retrieved successfully!";
    public static final String SWG_USER_LOGGED_OPERATION = "Get the authenticated user";
    public static final String SWG_USER_LOGGED_MESSAGE = "User retrieved successfully!";
    public static final String SWG_USER_ITEM_OPERATION = "Get one user";
    public static final String SWG_USER_ITEM_MESSAGE = "Item retrieved successfully!";
    public static final String SWG_USER_UPDATE_OPERATION = "Update a user";
    public static final String SWG_USER_UPDATE_STATUS_OPERATION = "Update a user status";
    public static final String SWG_USER_UPDATE_BATCH_STATUS_OPERATION = "Update a batchUser status";
    public static final String SWG_USER_UPDATE_MESSAGE = "User updated successfully!";
    public static final String SWG_USER_UPDATE_PWD_OPERATION = "Update user password";
    public static final String SWG_USER_UPDATE_PWD_MESSAGE = "The password updated successfully!";
    public static final String SWG_USER_UPDATE_PWD_ERROR = "The current password is invalid";
    public static final String SWG_USER_DELETE_OPERATION = "Delete a user";
    public static final String SWG_USER_BATCH_DELETE_OPERATION = "Delete a batchUser";
    public static final String SWG_USER_DELETE_MESSAGE = "User deleted successfully!";
    public static final String SWG_USER_PICTURE_OPERATION = "Change or delete user picture";
    public static final String SWG_USER_PICTURE_MESSAGE = "The picture updated/deleted successfully!";
    public static final String SWG_USER_PICTURE_ERROR = "An IOException occurred!";

    public static final String SWG_ROLE_TAG_NAME = "Roles";
    public static final String SWG_ROLE_TAG_DESCRIPTION = "Roles manipulation";
    public static final String SWG_ROLE_CREATE_OPERATION = "Create a role";
    public static final String SWG_ROLE_CREATE_MESSAGE = "Role created successfully!";
    public static final String SWG_PRIVILEGE_CREATE_MESSAGE = "Privilege created successfully!";
    public static final String SWG_ROLE_LIST_OPERATION = "Get all roles";
    public static final String SWG_ROLE_TRUE_LIST_OPERATION = "Get all enabled roles";
    public static final String SWG_ROLE_LIST_MESSAGE = "List retrieved successfully!";
    public static final String SWG_ROLE_ITEM_OPERATION = "Get one role";
    public static final String SWG_ROLE_ITEM_MESSAGE = "Item retrieved successfully!";
    public static final String SWG_ROLE_UPDATE_OPERATION = "Update a role";
    public static final String SWG_ROLE_UPDATE_STATUS_OPERATION = "Update a role status";
    public static final String SWG_ROLE_UPDATE_BATCH_STATUS_OPERATION = "Update batchRole status";
    public static final String SWG_PRIVILEGE_UPDATE_OPERATION = "Update a privilege";
    public static final String SWG_ROLE_UPDATE_MESSAGE = "Role updated successfully!";
    public static final String SWG_PRIVILEGE_UPDATE_MESSAGE = "Privilege updated successfully!";
    public static final String SWG_ROLE_DELETE_OPERATION = "Delete a role";
    public static final String SWG_ROLE_DELETE_BATCH_OPERATION = "Delete a batchRole";
    public static final String SWG_PRIVILEGE_DELETE_OPERATION = "Delete a privilege";
    public static final String SWG_ROLE_DELETE_MESSAGE = "Role deleted successfully!";
    public static final String SWG_PRIVILEGE_DELETE_MESSAGE = "Privilege deleted successfully!";

    public static final String SWG_PRIVILEGE_TAG_NAME = "Privileges";
    public static final String SWG_PRIVILEGE_TAG_DESCRIPTION = "Privileges manipulation";
    public static final String SWG_PRIVILEGE_CREATE_OPERATION = "Create a privilege";
    public static final String SWG_PRIVILEGE_LIST_OPERATION = "Get all privileges";
    public static final String SWG_PRIVILEGE_LIST_MESSAGE = "List retrieved successfully!";
    public static final String SWG_PRIVILEGE_ITEM_OPERATION = "Get one privilege";
    public static final String SWG_PRIVILEGE_ITEM_MESSAGE = "Item retrieved successfully!";
    public static final String NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE = "Privilege not found with this Id";
    public static final String NO_PRIVILEGE_FOUND_WITH_NAME_MESSAGE = "Privilege not found with this Name";
    public static final String PRIVILEGE_FOUND_WITH_NAME_MESSAGE = "Privilege name already exist";
    public static final String ROLE_FOUND_WITH_NAME_MESSAGE = "Role name already exist";
    public static final String USER_FOUND_WITH_EMAIL_MESSAGE = "User email already exist";
}
