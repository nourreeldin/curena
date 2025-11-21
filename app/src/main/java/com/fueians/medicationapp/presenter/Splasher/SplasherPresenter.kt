import com.fueians.medicationapp.view.interfaces.ISplashView
import kotlinx.coroutines.*

class SplashPresenter(private val view: ISplashView) {

    private val presenterScope = CoroutineScope(Dispatchers.Main)

    fun start() {
        presenterScope.launch {
            delay(3000L)
            view.showMainScreen()
        }
    }

    fun onDestroy() {
        presenterScope.cancel()
    }
}
