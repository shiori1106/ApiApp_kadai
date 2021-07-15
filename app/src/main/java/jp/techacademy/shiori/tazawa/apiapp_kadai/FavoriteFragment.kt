package jp.techacademy.shiori.tazawa.apiapp_kadai

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_api.*

class FavoriteFragment: Fragment() {

    // プロパティ
    private val favoriteAdapter by lazy {FavoriteAdapter(requireContext())}

    // FavoriteFragment -> MainActivityに削除を通知する
    private var fragmentCallback: FragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback){
            fragmentCallback = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_favorite.xmlが反映されたViewを作成して、returnする
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // FavoriteAdapterのお気に入り削除用のメソッドの追加を行う
        favoriteAdapter.apply {
            // Adapterの処理をそのままActivityに通知
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            // Itemをクリックしたとき
            /*onClickItem = { url: String, favorite: Boolean ->
                fragmentCallback?.onClickItem(url, favorite)
            }*/
            /*onClickItem = { url: String, id: String ->
                fragmentCallback?.onClickItem(url, id)
            }*/
            onClickItem = { url: String, id: String, name: String, address: String, image_url: String ->
                fragmentCallback?.onClickItem(url, id, name, address, image_url)
            }
        }

        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = favoriteAdapter
            // 一列ずつ表示
            layoutManager = LinearLayoutManager(requireContext())
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()

    }

    fun updateData(){
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }

    // 画面が戻った時に更新する
    override fun onResume() {
        super.onResume()
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }

}