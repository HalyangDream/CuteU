package com.cute.mine

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.cute.basic.BaseListActivity
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.basic.util.StatusUtils
import com.cute.logic.http.response.list.BlackUser
import com.cute.mine.adapter.BlackUserAdapter
import com.cute.mine.databinding.ActivityBlackUserBinding
import com.cute.mine.intent.BlackUserIntent
import com.cute.mine.state.BlackUserState
import com.cute.mine.viewmodel.BlackUserViewModel
import com.cute.uibase.databinding.LayoutTitleBarBinding

class BlackUserActivity : BaseListActivity<ActivityBlackUserBinding, BlackUserViewModel>() {


    private lateinit var titleBarBinding: LayoutTitleBarBinding
    private lateinit var blackUserAdapter: BlackUserAdapter

    override fun initViewBinding(layout: LayoutInflater): ActivityBlackUserBinding {
        return ActivityBlackUserBinding.inflate(layout)
    }

    override fun initView() {
        titleBarBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        titleBarBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_black_user)
        titleBarBinding.ivNavBack.setOnClickListener {
            finish()
        }
        viewBinding.slLayout.setOnClickRefresh {
            viewModel.processIntent(BlackUserIntent.BlackList(ListIntent.Loading()))
        }
        viewBinding.srlLayout.apply {
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    viewModel.processIntent(BlackUserIntent.BlackList(ListIntent.Refresh()))
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    viewModel.processIntent(BlackUserIntent.BlackList(ListIntent.LoadMore()))
                }
            })
        }

        viewBinding.rvBlack.apply {
            layoutManager = LinearLayoutManager(context)
            blackUserAdapter = BlackUserAdapter(context)
            blackUserAdapter.setRemoveListener {
                viewModel.processIntent(BlackUserIntent.RemoveBlack(it))
            }
            adapter = blackUserAdapter
        }
        viewModel.observerState {

            when (it) {
                is BlackUserState.BlackListResult -> handleListIntent(it.state)

                is BlackUserState.RemoveBlackSuccess -> {
                    blackUserAdapter.remove(it.user)
                    emptyLayout(isEmpty = blackUserAdapter.getRealItemCount() == 0)
                }
            }
        }
        viewModel.processIntent(BlackUserIntent.BlackList(ListIntent.Loading()))
    }

    override fun <T> handleListIntent(state: ListState<T>) {
        when (state) {
            is ListState.RefreshSuccess -> {
                viewBinding.srlLayout.finishRefresh()
                val list = state.data as MutableList<BlackUser>?
                blackUserAdapter.setData(list)
            }

            is ListState.LoadingSuccess -> {
                val list = state.data as MutableList<BlackUser>?
                blackUserAdapter.setData(list)
            }

            is ListState.LoadMoreSuccess -> {
                viewBinding.srlLayout.finishLoadMore()
                blackUserAdapter.addData(state.data as MutableList<BlackUser>?)
            }

            else -> super.handleListIntent(state)
        }
    }

    override fun emptyLayout(isEmpty: Boolean) {
        viewBinding.slLayout.showEmptyView(isEmpty)
    }

    override fun netErrorLayout(isNetError: Boolean) {
        viewBinding.slLayout.showNetErrorView(isNetError)
    }

    override fun dataHasBottomLayout(isBottom: Boolean) {
        viewBinding.srlLayout.setEnableLoadMore(!isBottom)
        blackUserAdapter.showFooter(isBottom)
    }

    override fun loadingLayout(isLoading: Boolean) {
        viewBinding.slLayout.showLoadingView(isLoading)
    }
}