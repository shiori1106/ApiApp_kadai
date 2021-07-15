package jp.techacademy.shiori.tazawa.apiapp_kadai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    // WebViewで指定されたURLを読み込み表示している
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // 受け取ったお店の情報
        val id_web = intent.getStringExtra(KEY_ID)
        val name_web = intent.getStringExtra(KEY_NAME)
        val address_web = intent.getStringExtra(KEY_ADDRESS)
        val image_url_web = intent.getStringExtra(KEY_IMAGE_URL)
        val url_web = intent.getStringExtra(KEY_URL)

        // タップされたお店のお気に入り状況
        val isFavorite_Web = FavoriteShop.findBy(id_web!!) != null


        // 渡ってきたURLを読み込む
        // webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
        webView.loadUrl(url_web.toString())

        // お気に入りの状況に応じて★の表示画像を設定する
        favoriteImageView2.apply {
            // setImageResource(if (intent.getBooleanExtra(KEY_FAVORITE, false)) R.drawable.ic_star else R.drawable.ic_star_border)
            setImageResource(if (isFavorite_Web) R.drawable.ic_star else R.drawable.ic_star_border)

            setOnClickListener {
                if (isFavorite_Web) {

                    // お気に入りから削除
                    onDeleteFavorite(id_web)


                } else {

                    // お気に入りからに追加
                    onAddFavorite(id_web, name_web!!, address_web!!, image_url_web!!, url_web!!)

                }
            }
        }
    }

    // お気に入り追加
    private fun onAddFavorite(id_web: String, name_web: String, address_web: String, image_url_web: String, url_web: String){
        FavoriteShop.insert(FavoriteShop().apply{
            id = id_web
            name = name_web
            address = address_web
            imageUrl = image_url_web
            url = url_web
        })

        // web画面上のお気に入り状況の表示を変更（☆→★）
        favoriteImageView2.apply {
            setImageResource(R.drawable.ic_star)}

    }

    // お気に入り削除
    private fun onDeleteFavorite(id: String){
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

        // web画面上のお気に入り状況の表示を変更（★→☆）
        favoriteImageView2.apply {
            setImageResource(R.drawable.ic_star_border)}

         }



    companion object{
        private const val KEY_URL = "key_url"
        private const val KEY_ID = "key_id"
        private const val KEY_NAME = "key_name"
        private const val KEY_ADDRESS = "key_address"
        private const val KEY_IMAGE_URL = "key_image_url"

        // 遷移処理を行っている
        fun start(activity: Activity, url: String, id: String, name: String, address: String, image_url: String){
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(KEY_URL, url)
            intent.putExtra(KEY_ID, id)
            intent.putExtra(KEY_NAME, name)
            intent.putExtra(KEY_ADDRESS, address)
            intent.putExtra(KEY_IMAGE_URL, image_url)
            activity.startActivity(intent)
        }
    }
}