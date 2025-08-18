import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import javax.inject.Inject

@CacheableTask
abstract class GCloudFirebaseTest @Inject constructor(
    execOperations: ExecOperations,
) : DefaultTask(), ExecOperations by execOperations {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val applicationAPK: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val testAPK: ConfigurableFileCollection

    @get:Input
    @get:Option(option = "device", description = "The target the device to run tests on")
    abstract val device: Property<String>

    @get:Internal // This is just the name for the run in FTL, no need to be part of the caching inputs
    @get:Option(option = "client-details", description = "The details for the client running the tests")
    abstract val clientDetails: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "firebase-rerun", description = "Number of times to rerun failed tests")
    abstract val firebaseRerun: Property<Int>

    @get:Internal // we don't care where we store the results, just that they are stored and we can download them locally
    @get:Option(option = "results-bucket", description = "The GCS bucket to store results")
    abstract val resultsBucket: Property<String>

    @get:Internal
    @get:Option(option = "results-bucket-dir", description = "The directory in the GCS bucket to store results")
    abstract val resultsBucketDir: Property<String>

    @get:Optional
    @get:OutputDirectory
    @get:Option(option = "results-local-dir", description = "The local directory to store results")
    abstract val resultsLocalDir: DirectoryProperty

    init {
        with(project) {
            resultsBucketDir.convention(this@GCloudFirebaseTest.name)
            resultsLocalDir.convention(layout.dir(provider { temporaryDir }))
        }
    }

    @TaskAction
    fun runTests() {
        listOfNotNull(
            runCatching { runTestLab() }.exceptionOrNull(),
            runCatching { retrieveResults() }.exceptionOrNull(),
        ).reduceOrNull { acc, it -> acc.addSuppressed(it); acc }?.let { throw it }
    }

    private fun runTestLab() = exec {
        commandLine(buildList {
            add("gcloud")
            add("beta")
            add("firebase")
            add("test")
            add("android")
            add("run")
            add("--type")
            add("instrumentation")
            add("--app")
            add(applicationAPK.singleAPK.absolutePath)
            add("--test")
            add(testAPK.singleAPK.absolutePath)
            add("--device")
            add(device.get())
            clientDetails.orNull?.let {
                add("--client-details")
                add(it)
            }
            resultsBucketDir.orNull?.let {
                add("--results-dir")
                add(it)
            }
            add("--no-performance-metrics")
            firebaseRerun.orNull?.let {
                add("--num-flaky-test-attempts")
                add(it)
            }
            timeout.orNull?.let {
                add("--timeout")
                add("${it.toMinutes()}m")
            }
        })
    }

    private fun retrieveResults() = exec {
        val resultsDir = resultsLocalDir.get().asFile.apply { deleteRecursively(); mkdirs() }

        commandLine(
            "gsutil", "-m", "rsync",
            "-x", ".*(?<!\\.xml)$",
            "-r", "gs://${resultsBucket.get()}/${resultsBucketDir.get()}/",
            resultsDir.absolutePath,
        )
    }

    private val ConfigurableFileCollection.singleAPK
        get() = asFileTree.matching { include("**.apk") }.singleFile

}
