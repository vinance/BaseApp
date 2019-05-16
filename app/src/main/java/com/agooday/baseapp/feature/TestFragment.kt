package com.agooday.baseapp.feature


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agooday.baseapp.R
import com.agooday.baseapp.base.BaseFragment
import com.agooday.baseapp.data.TestModel
import kotlinx.android.synthetic.main.fragment_test.*


class TestFragment : BaseFragment() {
    val listData = mutableListOf<TestModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_test
    }

    override fun onViewCreated(view: View?) {



        recycler_view.adapter = FastAdapter(
            listData,
            arrayListOf(R.layout.item_test, R.layout.item_header),
            object : FastAdapter.OnBindView {
                override fun onBindView(type: Int, i: Int, holder: RecyclerView.ViewHolder) {
                    if (type == FastAdapter.DATA_TYPE) {
                        holder.itemView.findViewById<TextView>(R.id.title).text = listData[i].title
                    } else {
                        holder.itemView.findViewById<TextView>(R.id.header).text =
                            listData[i].header
                    }

                }

                override fun getViewType(position: Int): Int {
                    return if (listData[position].title.isEmpty()) FastAdapter.HEADER_TYPE else FastAdapter.DATA_TYPE
                }

            })

        for(i in 0..100){
            if(i%10 == 0){
                listData.add(TestModel("","header "+(i/10)))
            }else{
                listData.add(TestModel("Title","header"))
            }
        }
        recycler_view?.adapter?.notifyDataSetChanged()


    }

}
