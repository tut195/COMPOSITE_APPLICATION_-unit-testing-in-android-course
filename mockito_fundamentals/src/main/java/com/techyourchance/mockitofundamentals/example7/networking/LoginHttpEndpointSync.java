package com.techyourchance.mockitofundamentals.example7.networking;

public interface LoginHttpEndpointSync {

    /**
     * Log in using provided credentials
     * @return the aggregated result of login operation
     * @throws NetworkErrorException if login attempt failed due to network error
     */

    /**
     * Тестирование этого метода в интерфейсе позволяет получить данные переданные в метод loginSync при помощи ArgumentCaptora
     *
     *
     * @param username
     * @param password
     * @return
     * @throws NetworkErrorException
     */

    EndpointResult loginSync(String username, String password) throws NetworkErrorException;

    enum EndpointResultStatus {
        SUCCESS,
        AUTH_ERROR,
        SERVER_ERROR,
        GENERAL_ERROR
    }

    class EndpointResult {
        private final EndpointResultStatus mStatus;
        private final String mAuthToken;

        public EndpointResult(EndpointResultStatus status, String authToken) {
            mStatus = status;
            mAuthToken = authToken;
        }

        public EndpointResultStatus getStatus() {
            return mStatus;
        }

        public String getAuthToken() {
            return mAuthToken;
        }
    }
}
