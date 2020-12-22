package edu.wgu.cristianreyes.bundles;

import java.util.ListResourceBundle;

public class LoginResources extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return contents;
        }

        private Object[][] contents = {
                {"function", "Log In"},
                {"username","Username"},
                {"password", "Password"},
                {"invalidLogin", "Login credentials invalid, please check username and password."},
                {"loginClientError", "Login client unavailable. If problem persists, contact IT."}
        };
}

