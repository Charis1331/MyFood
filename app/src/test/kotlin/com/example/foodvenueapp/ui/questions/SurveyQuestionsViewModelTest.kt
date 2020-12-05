//package com.example.foodvenueapp.ui.questions
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import com.example.foodvenueapp.FakeRepository
//import com.example.foodvenueapp.MainCoroutineRule
//import com.example.foodvenueapp.domain.model.FoodVenue
//import com.example.foodvenueapp.domain.model.UNKNOWN_ERROR_CODE
//import com.google.common.truth.Truth.assertThat
//import junit.framework.Assert.fail
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runBlockingTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.util.concurrent.TimeoutException
//
//@ExperimentalCoroutinesApi
//class SurveyQuestionsViewModelTest {
//
//    private lateinit var SUT: SurveyQuestionsViewModel
//
//    private lateinit var repository: FakeRepository
//
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()
//
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    @Before
//    fun setUp() {
//        repository = FakeRepository()
//        val question1 = FoodVenue(1, "question1")
//        val question2 = FoodVenue(2, "question2")
//        val question3 = FoodVenue(3, "question3")
//        repository.addQuestions(question1, question2, question3)
//
//        SUT = SurveyQuestionsViewModel(repository)
//    }
//
//    @Test
//    fun fetchQuestions_validResponse() = mainCoroutineRule.runBlockingTest {
//        SUT.fetchQuestions()
//
//        SUT.questions.observeForTesting {
//            assertThat(SUT.questions.getOrAwaitValue()).hasSize(3)
//        }
//    }
//
//    @Test
//    fun fetchFailed_fetchQuestions_errorResponseWithUnknownErrorCode() =
//        mainCoroutineRule.runBlockingTest {
//            repository.shouldReturnError = true
//
//            SUT.fetchQuestions()
//
//            SUT.errors.observeForTesting {
//                assertThat(SUT.errors.getOrAwaitValue()).isEqualTo(UNKNOWN_ERROR_CODE)
//            }
//        }
//
//    @Test
//    fun invalidUrl_fetchQuestions_errorResponseWithNotFoundErrorCode() =
//        mainCoroutineRule.runBlockingTest {
//            repository.run {
//                shouldReturnError = true
//                errorCode = 404
//            }
//
//            SUT.fetchQuestions()
//
//            SUT.errors.observeForTesting {
//                assertThat(SUT.errors.getOrAwaitValue()).isEqualTo(404)
//            }
//        }
//
//    @Test
//    fun noNetwork_fetchQuestions_networkErrorResponse() =
//        mainCoroutineRule.runBlockingTest {
//            repository.run {
//                shouldReturnError = false
//                shouldReturnNetworkError = true
//            }
//
//            SUT.fetchQuestions()
//
//            SUT.networkLost.observeForTesting {
//                assertThat(SUT.networkLost.getOrAwaitValue()).isEqualTo(true)
//            }
//        }
//
//    @Test
//    fun error_fetchQuestions_questionsListIsNotAltered() =
//        mainCoroutineRule.runBlockingTest {
//            repository.run {
//                shouldReturnError = true
//                shouldReturnNetworkError = false
//            }
//
//            SUT.fetchQuestions()
//
//            try {
//                SUT.questions.getOrAwaitValue()
//                fail("This should throw TimeoutException")
//            } catch (e: TimeoutException) {
//                assertThat(e.message).isEqualTo("LiveData value was never set.")
//            }
//        }
//
//    @Test
//    fun networkError_fetchQuestions_questionsListIsNotAltered() =
//        mainCoroutineRule.runBlockingTest {
//            repository.run {
//                shouldReturnError = false
//                shouldReturnNetworkError = true
//            }
//
//            SUT.fetchQuestions()
//
//            try {
//                SUT.questions.getOrAwaitValue()
//                fail("This should throw TimeoutException")
//            } catch (e: TimeoutException) {
//                assertThat(e.message).isEqualTo("LiveData value was never set.")
//            }
//        }
//}