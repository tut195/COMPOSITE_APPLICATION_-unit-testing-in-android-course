package com.techyourchance.testdoublesfundamentals.example4;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.techyourchance.testdoublesfundamentals.example4.LoginUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.example4.authtoken.AuthTokenCache;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.EventBusPoster;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.LoggedInEvent;
import com.techyourchance.testdoublesfundamentals.example4.networking.LoginHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import org.junit.Before;
import org.junit.Test;

public class LoginUseCaseSyncTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String AUTH_TOKEN = "authToken";

    LoginHttpEndpointSyncTd mLoginHttpEndpointSyncTd;
    AuthTokenCacheTd mAuthTokenCacheTd;
    EventBusPosterTd mEventBusPosterTd;

    LoginUseCaseSync SUT;

  /**
   * Если регистрация пользователя успешна - authToken должен быть сохрянт
   * Если нет - соответсвенно authToken не меняется
   * Если логин успешный, то должен вызваться loginEventPosted!
   * Если логин не успешен, - логин не постится
   *
   * @throws Exception
   */



  @Before
    public void setup() throws Exception {
        mLoginHttpEndpointSyncTd = new LoginHttpEndpointSyncTd();
        mAuthTokenCacheTd = new AuthTokenCacheTd();
        mEventBusPosterTd = new EventBusPosterTd();
        SUT = new LoginUseCaseSync(mLoginHttpEndpointSyncTd, mAuthTokenCacheTd, mEventBusPosterTd);
    }

    @Test
    public void loginSync_success_usernameAndPasswordPassedToEndpoint() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mLoginHttpEndpointSyncTd.mUsername, is(USERNAME));
        assertThat(mLoginHttpEndpointSyncTd.mPassword, is(PASSWORD));
    }

  /**
   * В данном тесте просиходит следующая логика. Когда SUT принимает на вход правильные
   * логин и пароль, происходит сохранение параметров. Соответвенно - создав глобальную переменную
   * класса mAuthTokenCache - можем проверить взаимодействие с этим классом - по доступу к кешированной
   * переменной
   *
   * @throws Exception
   */
  @Test
    public void loginSync_success_authTokenCached() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(AUTH_TOKEN));
    }

  /**
   * Соответвенно предидущего теста - внутри импленетации создаётся переменная mIsGeneralError
   * и меняя эту переменную извне мы имеев возможнть менять поведение LoginHttpEndpointSyncTd, а
   * через него соответвенно - будет запиисано в кеш или нет
   *
   * @throws Exception
   */

  @Test
    public void loginSync_generalError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(""));
    }

    @Test
    public void loginSync_authError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(""));
    }

    @Test
    public void loginSync_serverError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(""));
    }

    @Test
    public void loginSync_success_loggedInEventPosted() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mEvent, is(instanceOf(LoggedInEvent.class)));
    }

    @Test
    public void loginSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    @Test
    public void loginSync_authError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    @Test
    public void loginSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    @Test
    public void loginSync_success_successReturned() throws Exception {
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void loginSync_serverError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_authError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_generalError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_networkError_networkErrorReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // ---------------------------------------------------------------------------------------------
    // Helper classes
    // Ненобходимо убедитс, что параметры, username и password переданные в конструктор соответсвуют!
    // Потому в шаей имплементации этого класса

    private static class LoginHttpEndpointSyncTd implements LoginHttpEndpointSync {
        public String mUsername = "";
        private String mPassword = "";
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult loginSync(String username, String password) throws NetworkErrorException {
            mUsername = username;
            mPassword = password;
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "");
            }  else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, AUTH_TOKEN);
            }
        }
    }

    private static class AuthTokenCacheTd implements AuthTokenCache {

        String mAuthToken = "";

        @Override
        public void cacheAuthToken(String authToken) {
            mAuthToken = authToken;
        }

        @Override
        public String getAuthToken() {
            return mAuthToken;
        }
    }

    private static class EventBusPosterTd implements EventBusPoster {
        public Object mEvent;
        public int mInteractionsCount;

        @Override
        public void postEvent(Object event) {
            mInteractionsCount++;
            mEvent = event;
        }
    }
}