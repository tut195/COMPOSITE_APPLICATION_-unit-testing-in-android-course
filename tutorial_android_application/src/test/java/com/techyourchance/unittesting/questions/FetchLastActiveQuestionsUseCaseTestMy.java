package com.techyourchance.unittesting.questions;

import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FetchLastActiveQuestionsUseCaseTestMy {


  FetchLastActiveQuestionsUseCase SUT;
  private EndpointTd mEndpointTd;

  @Mock
  FetchLastActiveQuestionsUseCase.Listener listener1;
  @Mock
  FetchLastActiveQuestionsUseCase.Listener listener2;

  @Captor
  ArgumentCaptor<List<Question>> mQuestionsCaptor;

  /**
   * Сдесь будем использовать Test double;
   */

  @Before
  public void setup() throws Exception {
    mEndpointTd = new EndpointTd();
    SUT = new FetchLastActiveQuestionsUseCase(mEndpointTd);

  }

  // succesfull - listeners notified of success with correct data

  @Test
  public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithCorrectData() {
//    // In Java this is not posible to describe this class. Потому используем аннотацию @Captor!!
//    ArgumentCaptor<List<Question>> ac = ArgumentCaptor.forClass(List<Question.class>);
//    // Arrange
    success();
    SUT.registerListener(listener1);
    SUT.registerListener(listener2);
    // Act
    SUT.fetchLastActiveQuestionsAndNotify();
    // Assert
    verify(listener1).onLastActiveQuestionsFetched(mQuestionsCaptor.capture());
    verify(listener2).onLastActiveQuestionsFetched(mQuestionsCaptor.capture());

    List<List<Question>> questionList = mQuestionsCaptor.getAllValues();
    MatcherAssert.assertThat(questionList.get(0), CoreMatchers.is(getExpectedQuestions()));
    MatcherAssert.assertThat(questionList.get(1), CoreMatchers.is(getExpectedQuestions()));
  }


  // failure - listeners notified of success with correct data
  @Test
  public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
    // Arrange
    failure();
    SUT.registerListener(listener1);
    SUT.registerListener(listener2);
    // Act
    SUT.fetchLastActiveQuestionsAndNotify();
    // Assert

    verify(listener1).onLastActiveQuestionsFetchFailed();
    verify(listener2).onLastActiveQuestionsFetchFailed();

  }

  private List<Question> getExpectedQuestions() {
    List<Question> questions = new LinkedList<>();
    questions.add(new Question("id1", "title1"));
    questions.add(new Question("id2", "title2"));

    return questions;
  }

  // Helper Class

  private void success() {
    // currently no operations
  }

  private void failure() {
    mEndpointTd.mFailure = true;
  }

  private static class EndpointTd extends FetchLastActiveQuestionsEndpoint {

    public boolean mFailure;

    public EndpointTd() {
      super(null);
    }

    @Override
    public void fetchLastActiveQuestions(Listener listener) {

      if (mFailure) {
        listener.onQuestionsFetchFailed();
      } else {
        List<QuestionSchema> questionSchemas = new LinkedList<>();
        questionSchemas.add(new QuestionSchema("title1", "id1", "body1"));
        questionSchemas.add(new QuestionSchema("title2", "id2", "body2"));
        listener.onQuestionsFetched(questionSchemas);
      }
    }
  }
}