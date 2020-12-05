package com.example.foodvenueapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foodvenueapp.MainCoroutineRule
import com.example.foodvenueapp.getOrAwaitValue
import com.example.foodvenueapp.observeForTesting
import com.example.foodvenueapp.ui.FakeLocationProvider
import com.example.foodvenueapp.ui.USER_POSITION
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class MainViewModelTest {

    private lateinit var SUT: MainViewModel

    private val locationProvider = FakeLocationProvider()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        SUT = MainViewModel(locationProvider)
    }

    @Test
    fun getLocation_successfullyFetched_userLocationHasValue_and_locationErrorIsFalse() =
        mainCoroutineRule.runBlockingTest {
            locationProvider.returnSuccessful = true
            SUT.getLocation()

            SUT.userLocation.observeForTesting {
                assertThat(SUT.userLocation.getOrAwaitValue()).isEqualTo(USER_POSITION)
            }
            SUT.locationRetrievalError.observeForTesting {
                assertThat(SUT.locationRetrievalError.getOrAwaitValue()).isEqualTo(false)
            }
        }

    @Test
    fun getLocation_providerError_locationErrorIsTrue_rejectPermissionIsFalse_rejectSettingsIsFalse() =
        mainCoroutineRule.runBlockingTest {
            locationProvider.returnProviderError = true
            SUT.getLocation()

            SUT.locationRetrievalError.observeForTesting {
                assertThat(SUT.locationRetrievalError.getOrAwaitValue()).isEqualTo(true)
            }
            SUT.rejectedLocationSettings.observeForTesting {
                assertThat(SUT.rejectedPermissions.getOrAwaitValue()).isEqualTo(false)
            }
            SUT.rejectedPermissions.observeForTesting {
                assertThat(SUT.rejectedLocationSettings.getOrAwaitValue()).isEqualTo(false)
            }
        }

    @Test
    fun getLocation_permissionError_rejectPermissionIsTrue_locationRetrievalErrorIsFalse_rejectSettingsIsFalse() =
        mainCoroutineRule.runBlockingTest {
            locationProvider.returnPermissionRejection = true
            SUT.getLocation()

            SUT.rejectedPermissions.observeForTesting {
                assertThat(SUT.rejectedPermissions.getOrAwaitValue()).isEqualTo(true)
            }
            SUT.locationRetrievalError.observeForTesting {
                assertThat(SUT.locationRetrievalError.getOrAwaitValue()).isEqualTo(false)
            }
            SUT.rejectedLocationSettings.observeForTesting {
                assertThat(SUT.rejectedLocationSettings.getOrAwaitValue()).isEqualTo(false)
            }
        }

        @Test
    fun getLocation_locationSettingsError_rejectSettingsIsTrue_rejectPermissionIsFalse_locationRetrievalErrorIsFalse() =
        mainCoroutineRule.runBlockingTest {
            locationProvider.returnLocationSettingsRejection = true
            SUT.getLocation()

            SUT.rejectedLocationSettings.observeForTesting {
                assertThat(SUT.rejectedLocationSettings.getOrAwaitValue()).isEqualTo(true)
            }
            SUT.rejectedPermissions.observeForTesting {
                assertThat(SUT.rejectedPermissions.getOrAwaitValue()).isEqualTo(false)
            }
            SUT.locationRetrievalError.observeForTesting {
                assertThat(SUT.locationRetrievalError.getOrAwaitValue()).isEqualTo(false)
            }
        }
}