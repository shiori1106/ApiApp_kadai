package jp.techacademy.shiori.tazawa.apiapp_kadai

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiFragment: Fragment() {

    // プロパティ
    // RecyclerViewの表示のために必要となるAdapterクラス
    private val apiAdapter by lazy {ApiAdapter(requireContext())}
    // 別のスレッドで動作させるために作成したもの
    // （描画はMainスレッドで行い、API通信の処理は別スレッドで行う必要があるため）
    private val handler = Handler(Looper.getMainLooper())

    // Fragment -> ActivityにFavoriteの変更を通知する
    private var fragmentCallback: FragmentCallback? = null

    private var page = 0

    // Apiでデータを読み込み中ですフラグ。
    // 追加ページの追加ページの読み込みの時にこれがないと、
    // 連続して読み込んでしまうので、それの制御のため。
    private var isLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback){
            fragmentCallback = context
        }
    }

    // メソッド
    // Fragmentで使うレイアウトファイルを確定させるメソッド
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_api, container, false) // fragment_api.xmlが反映されたViewを生成して、returnする
    }

    // 初期化を行うメソッド
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ここから初期化処理を行う
        // ApiAdapterのお気に入り追加、削除用のメソッドの追加を行う
        apiAdapter.apply{

            onClickAddFavorite = {
                // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onAddFavorite(it)
            }
            onClickDeleteFavorite = {
                // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            // Itemをクリックしたとき
            onClickItem = { url: String, id: String, name: String, address: String, image_url: String ->
                fragmentCallback?.onClickItem(url, id, name, address, image_url)
            }
        }

        // RecycleViewの初期化
        recyclerView.apply {
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示

            // Scrollを検知するListenerを実装する。
            // これによって、RecyclerViewの下端に近づいた時に次のページを読み込んで、下に付け足す
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                // dx はx軸方向の変化量(横) dy はy軸方向の変化量(縦)
                // ここではRecyclerViewは縦方向なので、dyだけ考慮する
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy == 0){ // 縦方向の変化量(スクロール量)が0の時は動いていないので何も処理はしない
                        return
                    }

                    // RecyclerViewの現在の表示アイテム数
                    val totalCount = apiAdapter.itemCount

                    // RecyclerViewの現在見えている最後のViewHolderのposition
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                    // totalCountとlastVisibleItemから全体のアイテム数のうちどこまでが見えているかがわかる
                    // (例:totalCountが20、lastVisibleItemが15の時は、現在のスクロール位置から下に5件見えていないアイテムがある)
                    // 一番下にスクロールした時に次の20件を表示する等の実装が可能になる。
                    // ユーザビリティを考えると、一番下にスクロールしてから追加した場合、一度スクロールが止まるので、ユーザーは気付きにくい
                    // ここでは、一番下から5番目を表示した時に追加読み込みする様に実装する

                    // 読み込み中でない、かつ、現在のスクロール位置から下に5件見えていないアイテムがある
                    if (!isLoading && lastVisibleItem >= totalCount - 6) {
                        updateData(true)
                    }
                }
            })

        }

        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }

        updateData()
    }

    // お気に入りが削除されたときの処理（Activityからコールされる）
    fun updateView(){
        // RecyclerViewのAdapterに対して再描画のリクエストをする
        recyclerView.adapter?.notifyDataSetChanged()
    }

    // API通信を行い、データを取得するメソッド
    //private fun updateData(isAdd: Boolean = false){
    private fun updateData(isAdd: Boolean = false, keyword_kensaku: String = "ランチ"){// 初期値はランチ
        if (isLoading){
            return
        } else {
            isLoading = true
        }
        if (isAdd){
            page ++
        } else {
            page = 0
        }
        val start = page * COUNT + 1

        // URLを作成
        /*var keyword_kensaku = "ランチ"

        if (editTextKeyword.text.toString() != null) {
            keyword_kensaku = editTextKeyword.text.toString()
        }*/

        val url = StringBuilder()
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key)) // Apiを使うためのApikey
            .append("&start=").append(1) // 何件目からデータを取得するか
            .append("&count=").append(COUNT) // 1回で20件取得する
            //.append("&keyword=").append(getString(R.string.api_keyword)) // お店の検索ワード。
            .append("&keyword=").append(keyword_kensaku) // 入力した検索ワードを適用
            .append("&format=json") // ここで利用しているAPIは戻りの形をxmlかjsonか選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
            .toString()


        // Http通信を行う本体
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object: Callback {
            // Error時の処理
            override fun onFailure(call: Call, e: IOException){

                e.printStackTrace()
                handler.post{
                    updateRecyclerView(listOf())
                }
            }
            // 成功時の処理
            override fun onResponse(call: Call, response: Response) {
                var list = listOf<Shop>()

                response.body?.string()?.also {
                    // JsonデータからApiResponseへ変換を行う
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post {
                    updateRecyclerView(list)
                }
            }
        })
    }

    // 画面が戻った時に更新する
    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updateRecyclerView(list: List<Shop>){
        apiAdapter.refresh(list)
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }

    companion object{
        private const val COUNT = 20 // 1回のAPIで取得する件数
    }
}