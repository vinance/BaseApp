package com.agooday.baseapp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.agooday.baseapp.MainViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment: Fragment(){
    lateinit var  compositeDisposable : CompositeDisposable
    var rootView: View?=null
    private var isViewCreated = false
    lateinit var mainViewModel : MainViewModel
    abstract fun getLayoutId():Int
    abstract fun onViewCreated(view: View?)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.let {
            mainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        compositeDisposable = CompositeDisposable()

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(rootView == null){
            rootView =  inflater.inflate(getLayoutId(), container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isViewCreated) onViewCreated(view)
        isViewCreated = true
    }


    override fun onResume() {
        super.onResume()
        this.tag?.let {
            mainViewModel.showNavigationBottomEvent.value = it.contains("ROOT")
            mainViewModel.showBackButtonEvent.value = !it.contains("ROOT")
        }
    }


    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}