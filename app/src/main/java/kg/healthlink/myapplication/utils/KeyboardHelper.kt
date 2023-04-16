package kg.healthlink.myapplication.utils
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardHelper {

    fun showKeyboard(context: Context?, focusOn: View) {
        if (focusOn.requestFocus()) {
            val imm =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusOn, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(activity: Activity?) {
        val imm =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}