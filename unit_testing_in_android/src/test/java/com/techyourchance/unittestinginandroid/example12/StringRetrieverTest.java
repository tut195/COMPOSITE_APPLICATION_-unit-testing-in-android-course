package com.techyourchance.unittestinginandroid.example12;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StringRetrieverTest {

    // region constants ----------------------------------------------------------------------------
    public static final int ID = 10;
    public static final String STRING = "string";
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    @Mock Context mContextMock;
    // endregion helper fields ---------------------------------------------------------------------

    StringRetriever SUT;

    @Before
    public void setup() throws Exception {
        SUT = new StringRetriever(mContextMock);
    }

    /**
     * В данном тесте мы не используем ArgumentCaptor.
     * Есть класс, внутрь которого передатся context. Соответсвенно - когда StringRetriever пытается получить значение стрирговой переменной,
     * то происходит обращение через контекс.
     * verify
     *
     *
     * @throws Exception
     */
    @Test
    public void getString_correctParameterPassedToContext() throws Exception {
        // Arrange

        ArgumentCaptor<Integer> ac = ArgumentCaptor.forClass(Integer.class);
        // Act
        SUT.getString(ID);
        // Assert
        verify(mContextMock).getString(ID); // проверяем, что происходит обращение
        verify(mContextMock).getString(ac.capture());

        Integer v = ac.getValue();
        assertThat(v, is(ID));

        verifyNoMoreInteractions(mContextMock);
    }

    @Test
    public void getString_correctResultReturned() throws Exception {
        // Arrange
        when(mContextMock.getString(ID)).thenReturn(STRING);
        // Act
        String result = SUT.getString(ID);
        // Assert
        assertThat(result, is(STRING));
    }

    // region helper methods -----------------------------------------------------------------------
    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    // endregion helper classes --------------------------------------------------------------------

}