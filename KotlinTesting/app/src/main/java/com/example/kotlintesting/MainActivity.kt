package com.example.kotlintesting

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource
import com.example.kotlintesting.DBUtils.DBHelper
import com.example.kotlintesting.model.DateList
import com.example.kotlintesting.model.ForecastService
import com.example.kotlintesting.model.JsonRootBean
import com.example.kotlintesting.unitTestUtils.SimpleIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mIdlingResource = SimpleIdlingResource()
        getTheDataFromServer()

    }


    fun getTheDataFromServer() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://samples.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        val forecastService = retrofit.create<ForecastService>(
            ForecastService::class.java!!
        )

        val info = forecastService.getInfo()
        mIdlingResource!!.setIdleState(false)
        info.enqueue(object : Callback<JsonRootBean> {

            override fun onResponse(call: Call<JsonRootBean>?, response: Response<JsonRootBean>?) {
                if (response == null) {

                    Log.e("info", "Server return empty")

                } else {
                    if (response.isSuccessful) {

                        Log.e("info", "Connection to server is successful")
                        updateUI(response)

                    } else {

                        Log.e("info", "Server return error")

                    }

                }

            }

            override fun onFailure(call: Call<JsonRootBean>?, t: Throwable?) {
                Log.e("info", "Connection to server failed")
            }

        })

    }


    fun updateUI(response: Response<JsonRootBean>) {

        val responseInfo = response.body()

        if (responseInfo.list.isNotEmpty()) {

            val mRecyclerView = findViewById<RecyclerView>(R.id.forecast_list)


            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL

            // layoutManager
            mRecyclerView.layoutManager = layoutManager

            // itemDecoration
            val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            mRecyclerView.addItemDecoration(itemDecoration)

            // animation
            mRecyclerView.itemAnimator = DefaultItemAnimator()

            // setAdapter
            val adapter = KotlinRecycleAdapter(this, responseInfo.list)
            mRecyclerView.adapter = adapter


            // itemClick
            adapter!!.setOnKotlinItemClickListener(object :
                KotlinRecycleAdapter.IKotlinItemClickListener {
                override fun onItemClickListener(position: Int) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("" + responseInfo.list!![position].dt_txt)
                    builder.setMessage("" + responseInfo.list!![position].weather[0].description + "\n" + responseInfo.list!![position].weather[0].main)
                    builder.setPositiveButton("OK", null)
                    val alert = builder.create()
                    alert.show()

                }
            })

            insertDataToDB(responseInfo.list)
        }

        mIdlingResource!!.setIdleState(true)


    }


    class KotlinRecycleAdapter : RecyclerView.Adapter<KotlinRecycleAdapter.MyHolder> {
        private var list: MutableList<DateList>? = null
        private var context: Context? = null
        private var itemClickListener: IKotlinItemClickListener? = null


        constructor (mContext: Context, list: MutableList<DateList>?) {
            this.context = mContext
            this.list = list
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            var view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int = list?.size!!

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder?.date_text?.text = list!![position].dt_txt
            holder?.des_text?.text = list!![position].weather[0].description
            holder?.main_text?.text = list!![position].weather[0].main

            holder.itemView.setOnClickListener {
                itemClickListener!!.onItemClickListener(position)
            }

        }

        class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
            var date_text: TextView = itemView!!.findViewById(R.id.date_text)
            var main_text: TextView = itemView!!.findViewById(R.id.main_text)
            var des_text: TextView = itemView!!.findViewById(R.id.des_text)
        }

        fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        interface IKotlinItemClickListener {
            fun onItemClickListener(position: Int)
        }


    }

    private var mIdlingResource: SimpleIdlingResource? = null
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource? {
        if (mIdlingResource == null) {
            mIdlingResource =
                SimpleIdlingResource()
        }
        return mIdlingResource
    }


    private var db: SQLiteDatabase? = null
    fun insertDataToDB(list: MutableList<DateList>) {
        db = DBHelper(applicationContext, "mydb", null, 1).writableDatabase
        val cv = ContentValues()
        for (element in list) {
            cv.put("wdate", "" + element.dt_txt)
            db!!.insert("forecast", null, cv)
        }
        Log.e("mytab", "-->have insert")


        val cursor = db!!.query(
            "forecast",
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val wdate = cursor.getString(cursor.getColumnIndex("wdate"))
            Log.e("mytab", "---->$wdate")
        }
    }


}
