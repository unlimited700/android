package com.apnavaidya;

/**
 * Created by neeraj on 10/4/16.
 */
public class SigInResponse {

        private Long userId;
        private String userName;
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

}
