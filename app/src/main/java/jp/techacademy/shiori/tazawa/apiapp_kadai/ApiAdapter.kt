package jp.techacademy.shiori.tazawa.apiapp_kadai

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ApiAdapter(private val context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // プロパティ
    // 取得したJsonデータを解析し、Shop型オブジェクトとして生成したものを格納するリスト
    private val items = mutableListOf<Shop>()

    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド）
    var onClickAddFavorite: ((Shop) -> Unit)? = null

    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド）
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    // Itemを押したときのメソッド
    //var onClickItem: ((String, Boolean) -> Unit)? = null
    var onClickItem: ((String, String, String, String, String) -> Unit)? = null

    fun refresh(list: List<Shop>) {
        update(list, false)
    }

    fun add(list: List<Shop>) {
        update(list, true)
    }

    // 表示リスト更新時に呼び出すメソッド
    fun update(list: List<Shop>, isAdd: Boolean) {
        items.apply {

            if (!isAdd) { // 追加のときは、Clearしない
                clear() // itemsを空にする

            }
            addAll(list) // itemsにlistを全て追加する
        }
        notifyDataSetChanged() // recycleViewを再描画させる
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // ViewHolderを継承したApiItmViewHolderオブジェクトを生成し戻す
        return ApiItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false)
        )
    }

    // ViewHolderを継承したApiItemViewHolderクラスの定義
    class ApiItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // レイアウトファイルからidがrootViewのCardViewオブジェクトを取得し、代入
        val rootView: CardView = view.findViewById(R.id.rootView)

        // レイアウトファイルからidがnameTextViewのTextViewオブジェクトを取得し、代入
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)

        // レイアウトファイルからidがImageViewオブジェクトを取得し、代入
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // レイアウトファイルからidがfavoriteImageViewのImageViewオブジェクトを取得し、代入
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)

        // レイアウトファイルからidがaddressTextViewのTextViewオブジェクトを取得し、代入
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
    }

    // 表示させる（格納されている）要素数を返すメソッド
    override fun getItemCount(): Int {
        return items.size
    }

    // 第１引数にonCreateViewHolderで作られたViewHolderが、第２引数に何番目の表示かが渡される
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApiItemViewHolder) {
            // 生成されたViewHolderがApiItemViewHolderだったら...
            updateApiItemViewHolder(holder, position)
        } // {
        // 別のViewHolderをバインドさせることが可能となる
        //}
    }

    private fun updateApiItemViewHolder(holder: ApiItemViewHolder, position: Int) {
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入
        val data = items[position]

        // お気に入り状態を取得
        val isFavorite = FavoriteShop.findBy(data.id) != null
        holder.apply {
            rootView.apply {

                setOnClickListener {
                    onClickItem?.invoke(if (data.couponUrls.sp.isNotEmpty()) data.couponUrls.sp else data.couponUrls.pc, data.id, data.name, data.address, data.logoImage)
                }


                // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
                nameTextView.text = data.name
                // addressTextViewのtextプロパティに代入されたオブジェクトのaddressプロパティを代入
                addressTextView.text = data.address
                // Picassoライブラリを使い、imageViewにdata.LogoImageのurlの画像を読み込ませる
                Picasso.get().load(data.logoImage).into(imageView)
                // 白抜きの星マークの画像を指定
                favoriteImageView.apply {
                    // Picassoというライブラリを使ってImageViewに画面をはめ込む
                    setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
                    setOnClickListener {
                        if (isFavorite) {
                            onClickDeleteFavorite?.invoke(data)

                        } else {
                            onClickAddFavorite?.invoke(data)

                        }
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }
}