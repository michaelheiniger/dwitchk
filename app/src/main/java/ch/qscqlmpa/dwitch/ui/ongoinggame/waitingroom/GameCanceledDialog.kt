package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ch.qscqlmpa.dwitch.R

class GameCanceledDialog : DialogFragment() {

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////        return super.onCreateDialog(savedInstanceState)
////        if (arguments != null) {
////            if (arguments?.getBoolean("notAlertDialog")!!) {
////                return super.onCreateDialog(savedInstanceState)
////            }
////        }
//        val builder = AlertDialog.Builder(activity)
//        builder.setTitle("Alert Dialog")
//        builder.setMessage("Hello! I am Alert Dialog")
//        builder.setPositiveButton("Cool", object: DialogInterface.OnClickListener {
//            override fun onClick(dialog:DialogInterface, which:Int) {
//                dialog.
//            }
//        })
//        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
//            override fun onClick(dialog:DialogInterface, which:Int) {
//                dismiss()
//            }
//        })
//        return builder.create()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_canceled_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnDone = view.findViewById<Button>(R.id.btnDone)
        btnDone.setOnClickListener {
            val dialogListener = targetFragment as DialogListener
            dialogListener.onDoneClicked()
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var setFullScreen = false
        if (arguments != null) {
            setFullScreen = requireNotNull(arguments?.getBoolean("fullScreen"))
        }
        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    interface DialogListener {
        fun onDoneClicked()
    }

    //    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.game_canceled_fragment, container, false)
//    }
//
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameCanceledFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(targetFragment: Fragment): DialogFragment {
//                GameCanceledDialog().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
//                    }
//                }
            val dialogFragment = GameCanceledDialog()
            dialogFragment.setTargetFragment(targetFragment, 1)
            return dialogFragment
        }
    }
}
