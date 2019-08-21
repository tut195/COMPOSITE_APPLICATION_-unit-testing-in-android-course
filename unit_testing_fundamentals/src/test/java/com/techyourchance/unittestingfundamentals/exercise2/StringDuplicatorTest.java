package com.techyourchance.unittestingfundamentals.exercise2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StringDuplicatorTest {

  StringDuplicator SUT;

  @Before
  public void setup() throws Exception {
    SUT = new StringDuplicator();
  }

  @Test
  public void duplicate_emptyString_emptyStringReturned() throws Exception {
    String result = SUT.duplicate("");
    assertThat(result, is(""));
  }

  @Test
  public void duplicate_singleCharacter_duplicatedStringReturned() throws Exception {
    String result = SUT.duplicate("a");
    assertThat(result, is("aa"));
  }

  @Test
  public void duplicate_longString_duplicatedStringReturned() throws Exception {
    String result = SUT.duplicate("Vasiliy Zukanov");
    assertThat(result, is("Vasiliy ZukanovVasiliy Zukanov"));
  }
}