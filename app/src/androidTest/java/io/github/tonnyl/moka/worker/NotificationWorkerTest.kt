package io.github.tonnyl.moka.worker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.WorkManagerTestInitHelper
import io.github.tonnyl.moka.work.NotificationWorker
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class NotificationWorkerTest {

    @get:Rule
    var workManagerRule = WorkManagerTestRule()

    @Test
    @Throws(Exception::class)
    fun testPeriodicWork() {
        // Create request
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(15L, TimeUnit.MINUTES)
            .build()

        val workManager = WorkManager.getInstance(workManagerRule.testContext)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(workManagerRule.testContext)

        // Enqueue and wait for result.
        workManager.enqueue(request).result.get()

        // Tells the testing framework the period delay is met
        testDriver?.setPeriodDelayMet(request.id)

        assertThat(
            workManager.getWorkInfoById(request.id).get().state,
            `is`(WorkInfo.State.RUNNING)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testWithConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(constraints)
            .build()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(workManagerRule.testContext)
        // Enqueue and wait for result.
        workManagerRule.workManager.enqueue(request).result.get()
        testDriver?.setAllConstraintsMet(request.id)

        assertThat(
            workManagerRule.workManager.getWorkInfoById(request.id).get().outputData,
            `is`(Data.EMPTY)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testNotificationWorker() {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()

        workManagerRule.workManager.enqueue(request).result.get()

        val workInfo = workManagerRule.workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.RUNNING))
        assertThat(workInfo.outputData, `is`(Data.EMPTY))
    }

}