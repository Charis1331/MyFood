package com.example.foodvenueapp.ui.venues

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foodvenueapp.FakeRepository
import com.example.foodvenueapp.MainCoroutineRule
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.getOrAwaitValue
import com.example.foodvenueapp.observeForTesting
import com.example.foodvenueapp.ui.FakeDataManipulator
import com.example.foodvenueapp.ui.USER_POSITION
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class VenuesViewModelTest {

    private lateinit var SUT: VenuesViewModel

    private val repo = FakeRepository()
    private val dataManipulator = FakeDataManipulator()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        SUT = VenuesViewModel(repo, dataManipulator)
    }

    @Test
    fun getVenues_successfulResponse_venuesHasValue_networkErrorIsFalse() =
        mainCoroutineRule.runBlockingTest {
            repo.returnSuccess = true
            SUT.getVenues(USER_POSITION, PositionOfInterest(USER_POSITION, 0f))

            SUT.venues.observeForTesting {
                Truth.assertThat(SUT.venues.getOrAwaitValue()).isNotEmpty()
            }
            SUT.networkError.observeForTesting {
                Truth.assertThat(SUT.networkError.getOrAwaitValue()).isFalse()
            }
        }

    @Test
    fun getVenues_networkError_networkErrorIsTrue() =
        mainCoroutineRule.runBlockingTest {
            repo.returnNetworkError = true
            SUT.getVenues(USER_POSITION, PositionOfInterest(USER_POSITION, 0f))

            SUT.networkError.observeForTesting {
                Truth.assertThat(SUT.networkError.getOrAwaitValue()).isTrue()
            }
        }

    @Test
    fun getVenues_genericError_networkErrorIsTrue() =
        mainCoroutineRule.runBlockingTest {
            repo.returnGenericError = true
            SUT.getVenues(USER_POSITION, PositionOfInterest(USER_POSITION, 0f))

            SUT.genericError.observeForTesting {
                Truth.assertThat(SUT.genericError.getOrAwaitValue()).isEqualTo(repo.errorCode)
            }
        }
}