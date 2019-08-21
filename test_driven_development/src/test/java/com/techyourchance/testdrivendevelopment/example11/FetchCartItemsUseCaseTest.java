package com.techyourchance.testdrivendevelopment.example11;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.example11.networking.CartItemSchema;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint.Callback;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class FetchCartItemsUseCaseTest {

    // region constants ----------------------------------------------------------------------------
    public static final int LIMIT = 10;
    public static final int PRICE = 5;
    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String ID = "id";
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    @Mock GetCartItemsHttpEndpoint mGetCartItemsHttpEndpointMock;
    @Mock FetchCartItemsUseCase.Listener mListenerMock1;
    @Mock FetchCartItemsUseCase.Listener mListenerMock2;

    @Captor ArgumentCaptor<List<CartItem>> mAcListCartItem;
    // endregion helper fields ---------------------------------------------------------------------

    FetchCartItemsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchCartItemsUseCase(mGetCartItemsHttpEndpointMock);
        success();
    }

    private List<CartItemSchema> getCartItemSchemes() {
        List<CartItemSchema> schemas = new ArrayList<>();
        schemas.add(new CartItemSchema(ID, TITLE, DESCRIPTION, PRICE));
        return schemas;
    }

    // Correct card limit passed to endpoint

    @Test
    public void fetchCartItems_correctLimitPassedToEndpoint() throws Exception {
        // Arrange
        ArgumentCaptor<Integer> acInt = ArgumentCaptor.forClass(Integer.class);
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        // Assert
        verify(mGetCartItemsHttpEndpointMock).getCartItems(acInt.capture(), any(Callback.class));
        assertThat(acInt.getValue(), is(LIMIT));
    }

    @Test
    public void fetchCartItems_success_observersNotifiedWithCorrectData() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(LIMIT);
        // Assert что оба слушателя будут вызваны
        verify(mListenerMock1).onCartItemsFetched(mAcListCartItem.capture());// передаём argument captures
        verify(mListenerMock2).onCartItemsFetched(mAcListCartItem.capture());

        // Получаем значени
        List<List<CartItem>> captures = mAcListCartItem.getAllValues();

        List<CartItem> capture1 = captures.get(0);
        List<CartItem> capture2 = captures.get(1);
        //Проверяем утверждения!!!
        assertThat(capture1, is(getCartItems()));
        assertThat(capture2, is(getCartItems()));
    }

    @Test
    public void fetchCartItems_success_unsubscribedObserversNotNotified() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(LIMIT);
        // Assert
        verify(mListenerMock1).onCartItemsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    @Test
    public void fetchCartItems_generalError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        generaError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(LIMIT);
        // Assert
        verify(mListenerMock1).onFetchCartItemsFailed();
        verify(mListenerMock2).onFetchCartItemsFailed();
    }

    @Test
    public void fetchCartItems_networkError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        networkError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(LIMIT);
        // Assert
        verify(mListenerMock1).onFetchCartItemsFailed();
        verify(mListenerMock2).onFetchCartItemsFailed();
    }

    // region helper methods -----------------------------------------------------------------------

    private List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(ID, TITLE, DESCRIPTION, PRICE));
        return cartItems;
    }

    /**
     * В данном методе вызывается метод doAnswer - позводяет перехватить ответ??
     * Принимает на вход new Answer(), у которого есть метод answer, который имеет
     * на входе параметра invokation - соответвенно через него можни получить и воздействовать на
     * "содержит информацию от вызоме mock"
     */


    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();

                // Вот эти аргументы и есть то, что мы вызываем в ниже вызванном методе!
                Callback callback = (Callback) args[1];
                callback.onGetCartItemsSucceeded(getCartItemSchemes()); // созданный замоканый ответ!
                return null;
            }
        }).when(mGetCartItemsHttpEndpointMock).getCartItems(anyInt(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetCartItemsFailed(GetCartItemsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetCartItemsHttpEndpointMock).getCartItems(anyInt(), any(Callback.class));
    }

    private void generaError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetCartItemsFailed(GetCartItemsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetCartItemsHttpEndpointMock).getCartItems(anyInt(), any(Callback.class));
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    // endregion helper classes --------------------------------------------------------------------

}