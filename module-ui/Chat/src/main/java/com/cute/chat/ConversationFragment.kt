package com.cute.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.analysis.Analysis
import com.cute.baselogic.storage.UserDataStore
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelFragment
import com.cute.basic.util.StatusUtils
import com.cute.chat.adapter.ConversationAdapter
import com.cute.chat.adapter.ConversationHeaderAdapter
import com.cute.chat.databinding.FragmentConversationBinding
import com.cute.chat.intent.ConversationIntent
import com.cute.chat.state.ConversationState
import com.cute.im.IMCore
import com.cute.im.bean.Conversation
import com.cute.im.listener.ConversationListener
import com.cute.im.service.ConversationService
import com.cute.im.service.MessageService
import com.cute.im.service.MsgServiceObserver
import com.cute.uibase.dialog.AppAlertDialog
import com.cute.uibase.route.RoutePage
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = RoutePage.CHAT.CHAT_CONVERSATION)
class ConversationFragment :
    BaseModelFragment<FragmentConversationBinding, ConversationViewModel>(), ConversationListener {

    private var headerAdapter: ConversationHeaderAdapter? = null
    private var conversationAdapter: ConversationAdapter? = null

    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentConversationBinding {

        return FragmentConversationBinding.inflate(layout, container, false)
    }

    override fun initView() {
        viewBinding.srlLayout.apply {
            setOnRefreshListener {
                viewModel.processIntent(ConversationIntent.LoadData("${this.context.userDataStore.getUid()}"))
            }

            setOnLoadMoreListener {
                val last = conversationAdapter?.lastConversation()
                if (last != null) {
                    viewModel.processIntent(ConversationIntent.LoadMoreData(last))
                } else {
                    lifecycleScope.launch {
                        viewModel.setState(ConversationState.LoadMoreConversationData(true, null))
                    }
                }
            }
        }

        viewBinding.rvHeader.apply {
            headerAdapter = context?.let { ConversationHeaderAdapter(it) }
            this.adapter = headerAdapter
        }

        viewBinding.rvConversation.apply {
            conversationAdapter = context?.let { ConversationAdapter(it, lifecycleScope) }
            conversationAdapter?.setLongClickListener { i, name, conversation ->
                showDeleteConversationDialog(name, conversation)
            }
            this.adapter = conversationAdapter
        }

        viewModel.observerState {
            when (it) {
                is ConversationState.HeaderData -> {
                    headerAdapter?.setData(it.data)
                    context?.apply {
                        userDataStore.saveOfficialAccount(it.officialAccount)
                    }
                }

                is ConversationState.ConversationData -> {
                    if (!it.data.isNullOrEmpty()) {
                        conversationAdapter?.setData(it.data.toMutableList())
                    }
                    viewBinding.srlLayout.finishRefresh(500)
                    conversationAdapter?.showFooter(it.isBottom)
                    viewBinding.srlLayout.setEnableLoadMore(!it.isBottom)
                }

                is ConversationState.LoadMoreConversationData -> {
                    if (!it.data.isNullOrEmpty()) {
                        conversationAdapter?.addData(it.data.toMutableList())
                    }
                    viewBinding.srlLayout.finishLoadMore(500)
                    conversationAdapter?.showFooter(it.isBottom)
                    viewBinding.srlLayout.setEnableLoadMore(!it.isBottom)
                }

            }
        }
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, true)
        context?.apply {
            viewModel.processIntent(ConversationIntent.GetOfficialAccount("${this.userDataStore.getUid()}"))
        }
    }

    override fun firstShowUserVisible() {
        viewBinding.srlLayout.autoRefresh()
    }

    override fun onResume() {
        super.onResume()
        UserBehavior.setRootPage("im_msg_list")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, false)
    }

    override fun onConversationChange(conversation: Conversation) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (headerAdapter?.isHeaderInfo(conversation) == true) {
                headerAdapter?.receiveConversation(conversation)
            } else {
                conversationAdapter?.receiveConversation(conversation)
            }
        }
    }

    override fun onConversationDelete(list: MutableList<Conversation>) {
        lifecycleScope.launch(Dispatchers.Main) {
            conversationAdapter?.removeConversation(list)
        }
    }

    private fun showDeleteConversationDialog(name: String, conversation: Conversation) {
        if (context == null) return
        val title =
            if (name.isEmpty()) requireContext().getString(com.cute.uibase.R.string.str_hint) else requireContext().getString(
                com.cute.uibase.R.string.str_conversation_with_value, name
            )
        val alertDialog =
            AppAlertDialog.Builder()
                .setTitle(title)
                .setContent(requireContext().getString(com.cute.uibase.R.string.str_delete_conversation_tip))
                .setBtnPositiveContent(requireContext().getString(com.cute.uibase.R.string.str_confirm))
                .setBtnNegativeContent(requireContext().getString(com.cute.uibase.R.string.str_cancel))
                .create(requireContext())
        alertDialog.setPositiveListener {
            lifecycleScope.launch {
                IMCore.getService(ConversationService::class.java)
                    .deleteConversation(conversation.channel)
                IMCore.getService(MessageService::class.java)
                    .deleteMessageByChannel(conversation.channel)
            }

        }
    }
}