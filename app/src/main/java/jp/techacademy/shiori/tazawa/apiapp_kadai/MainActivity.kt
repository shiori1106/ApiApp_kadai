package jp.techacademy.shiori.tazawa.apiapp_kadai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentCallback {

    // プロパティ
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    // メソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewPager2の初期化
        viewPager2.apply{

            // プロパティに値を設定
            adapter = viewPagerAdapter // ViewPager2でページングするものを決定
            orientation = ViewPager2.ORIENTATION_HORIZONTAL // スワイプの向き横（ORIENTATION_VERTICAL を指定すれば縦スワイプで実装可能）
            offscreenPageLimit = viewPagerAdapter.itemCount // ViewPager2で保持する画面数
        }

        // TabLayoutの初期化
        // TabLayoutとViewPagew2を紐づける
        // TabLayoutのTextを指定する
        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()

    }

    // favoriteを追加
    /*override fun onClickItem(url: String, favorite: Boolean) {
        WebViewActivity.start(this, url, favorite)
    }*/
    override fun onClickItem(url: String, id: String,name: String, address: String, image_url: String) {
        WebViewActivity.start(this, url, id, name, address, image_url)
    }

    // Favoriteに追加するときのメソッド（Fragment -> Activityへ通知する）
    override fun onAddFavorite(shop: Shop){
        FavoriteShop.insert(FavoriteShop().apply{
            id = shop.id
            name = shop.name
            address = shop.address
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    // Favoriteから削除したときのメソッド（Fragment -> Activityへ通知する）
    override fun onDeleteFavorite(id: String){
        showConfirmDeleteFavoriteDialog(id)

    }

    private fun showConfirmDeleteFavoriteDialog(id: String){
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok){_,_ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel){_,_ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id: String){
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    companion object{
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }
}