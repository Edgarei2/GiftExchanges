package com.taiqiwen.im

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.base_framework.ui.divider.HorizontalDividerItemDecoration
import com.taiqiwen.im_api.model.Group

class GroupDetailFragment : Fragment() {

    private lateinit var group: Group
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GroupChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            group = it.getSerializable(KEY_GROUP_INFO) as Group
        }
        viewModel = ViewModelProvider(requireActivity()).get(GroupChatViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.detail)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = GroupDetailAdapter(group, viewModel)
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(context)
                    .paint(Paint().apply {
                        strokeWidth = 5f
                        color = Color.BLUE
                        isAntiAlias = true
                        pathEffect = DashPathEffect(floatArrayOf(25.0f, 25.0f), 0F)
                    })
                    .build()
            )
        }
    }

    companion object {

        private const val KEY_GROUP_INFO = "key_group_info"

        @JvmStatic
        fun newInstance(group: Group) =
            GroupDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_GROUP_INFO, group)
                }
            }
    }
}