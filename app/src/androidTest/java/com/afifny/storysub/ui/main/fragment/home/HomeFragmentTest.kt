package com.afifny.storysub.ui.main.fragment.home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.afifny.storysub.R
import com.afifny.storysub.data.remote.retrofit.ApiConfig
import com.afifny.storysub.util.JsonConverter
import com.afifny.storysub.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.API_MOCK = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStory_Success() {
        val bundle = Bundle()
        launchFragmentInContainer<HomeFragment>(bundle, R.style.Theme_StoryApp)
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withText("Lorem Ipsum")).check(matches(isDisplayed()))
    }

    @Test
    fun getStory_Empty() {
        val bundle = Bundle()
        launchFragmentInContainer<HomeFragment>(bundle, R.style.Theme_StoryApp)
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response_empty.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.view_error)).check(matches(isDisplayed()))
    }
}