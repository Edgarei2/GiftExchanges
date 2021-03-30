package com.taiqiwen.profile

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beyondsw.lib.GiftServiceUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.ui.selectionsheet.SheetSelectionItem
import com.taiqiwen.base_framework.ui.selectionsheet.SheetSelection
import com.taiqiwen.profile.ui.AvatarLayout
import com.test.account_api.AccountServiceUtil


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var ivBackGround: SimpleDraweeView
    private lateinit var avatarLayout: AvatarLayout
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        val curUserId = AccountServiceUtil.getSerVice().getCurUser()?.userId
        AccountServiceUtil.getSerVice().refresh(curUserId) {
            viewModel.refreshUserStatus {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ivBackGround = view.findViewById(R.id.iv_bg)
        avatarLayout = view.findViewById(R.id.avatar)
        //val bitmap: Bitmap = BlurBitmapUtil.blurBitmap(context, BitmapFactory.decodeResource(resources, R.drawable.avatar), 3f)
        //ivBackGround.setImageBitmap(bitmap)
        //ivBackGround.setImageURI()
        viewModel.getUserAvatarUrl().observe(viewLifecycleOwner, Observer {
            ivBackGround.setImageURI(it)
            //avatarLayout.circleImageView.background = BitmapDrawable(resources, ImageUtil.getBitmapFromFresco(it))
            avatarLayout.setImage(it)
        })

        viewModel.getLoginStatus().observe(viewLifecycleOwner, Observer {isLoggedIn ->
            if (isLoggedIn) {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.GONE
                view.findViewById<View>(R.id.user_profile).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.VISIBLE
                view.findViewById<View>(R.id.user_profile).visibility = View.GONE
            }
        })

        viewModel.getUserName().observe(viewLifecycleOwner, Observer { userName ->
            view.findViewById<TextView>(R.id.name).text = userName
        })

        viewModel.getCredit().observe(viewLifecycleOwner, Observer { credit ->
            view.findViewById<TextView>(R.id.credit).text = credit.toString()
        })

        viewModel.getFriends().observe(viewLifecycleOwner, Observer { friends ->
            view.findViewById<TextView>(R.id.friends).text = friends?.size.toString()
        })

        viewModel.getCollection().observe(viewLifecycleOwner, Observer { collections ->
            view.findViewById<View>(R.id.collected_area).setOnClickListener {
                if (context == null || collections == null) {
                    return@setOnClickListener
                }
                SheetSelection.Builder(context!!)
                    .title("Custom Sheet Selection")
                    .items(
                        source = collections,
                        mapper = {
                            SheetSelectionItem(
                                key = "key_$it",
                                value = "Custom $it",
                                iconUrl = it.imageUrls?.get(0),
                                extraInfo = Bundle().apply {
                                    putSerializable("gift_detail", it)
                                }
                            )
                        }
                    )
                    .showDraggedIndicator(true)
                    .searchEnabled(true)
                    .searchNotFoundText("未找到相关结果")
                    .theme(R.style.Theme_Custom_SheetSelection)
                    .onItemClickListener { item, position ->
                        GiftServiceUtil.getSerVice().startGiftActivity(context!!, collections[position])
                    }
                    .show()
            }
        })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window: Window? = activity?.window
            window?.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}