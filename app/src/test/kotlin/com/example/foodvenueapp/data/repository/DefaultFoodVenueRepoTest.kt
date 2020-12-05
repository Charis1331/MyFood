package com.example.foodvenueapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foodvenueapp.MainCoroutineRule
import com.example.foodvenueapp.data.source.FOOD_VENUE_LIST
import com.example.foodvenueapp.data.source.FakeDataSource
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class DefaultFoodVenueRepoTest {

    private val dataSource = FakeDataSource(FOOD_VENUE_LIST)

    private lateinit var SUT: DefaultFoodVenueRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        SUT = DefaultFoodVenueRepository(dataSource)
    }

    @Test
    fun getVenues_emptyRepository_successfulResultWrapper() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource()
        val repo = DefaultFoodVenueRepository(emptySource)

        val fakeLocation = LatLng(0.0, 0.0)
        val fakePoI = PositionOfInterest(fakeLocation, 1f)
        val result = repo.getVenues(fakeLocation, fakePoI).firstOrNull()

        assertTrue(result is ResultWrapper.Success)
        assertTrue((result as ResultWrapper.Success).data!!.isEmpty())
    }

    @Test
    fun getVenues_nonEmptyRemoteDataSource_filledVenuesList() = mainCoroutineRule.runBlockingTest {
        val fakeLocation = LatLng(0.0, 0.0)
        val fakePoI = PositionOfInterest(fakeLocation, 1f)
        val result = SUT.getVenues(fakeLocation, fakePoI).first() as ResultWrapper.Success

        val venuesListSize = result.data!!.size
        assertTrue(venuesListSize == 2)
    }

    @Test
    fun getVenues_nullRepository_emptyErrorResult() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource(null)
        val repo = DefaultFoodVenueRepository(emptySource)

        val fakeLocation = LatLng(0.0, 0.0)
        val fakePoI = PositionOfInterest(fakeLocation, 1f)
        val result = repo.getVenues(fakeLocation, fakePoI).first()
        assertTrue(result is ResultWrapper.Error)
    }
}