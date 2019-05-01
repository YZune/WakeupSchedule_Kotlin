package androidx.fragment.app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import com.google.android.material.card.MaterialCardView
import com.suda.yzune.wakeupschedule.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.dip
import kotlin.coroutines.CoroutineContext

abstract class BaseDialogFragment : DialogFragment(), CoroutineScope {

    private lateinit var job: Job

    @get:LayoutRes
    protected abstract val layoutId: Int

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(dip(280), ViewGroup.LayoutParams.WRAP_CONTENT)
        val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
        val cardView = root.find<MaterialCardView>(R.id.base_card_view)
        LayoutInflater.from(context).inflate(layoutId, cardView, true)
        return root
    }

    override fun show(manager: FragmentManager, tag: String) {
        mDismissed = false
        mShownByMe = true
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}