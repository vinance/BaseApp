package com.agooday.baseapp.feature


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agooday.baseapp.R
import com.agooday.baseapp.base.BaseFragment
import com.agooday.baseapp.data.TestModel
import kotlinx.android.synthetic.main.fragment_test2.*


class Test2Fragment : BaseFragment() {
    val listData = mutableListOf<TestModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_test2
    }

    override fun onViewCreated(view: View?) {



        recycler_view.adapter = FastAdapter(
            listData,
            arrayListOf(R.layout.item_test),
            object : FastAdapter.OnBindView {
                override fun onBindView(type: Int, i: Int, holder: RecyclerView.ViewHolder) {
                    holder.itemView.findViewById<TextView>(R.id.title).text = listData[i].title

                }

                override fun getViewType(position: Int): Int {
                    return FastAdapter.DATA_TYPE
                }

            })

        for(i in 0..100){
            listData.add(TestModel("Title "+i,"header "+i))
        }
        recycler_view?.adapter?.notifyDataSetChanged()


    }

}
