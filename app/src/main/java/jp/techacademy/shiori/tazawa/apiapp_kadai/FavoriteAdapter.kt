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

class FavoriteAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // お気に入り登録したShopを格納
    private val items_all = mutableListOf<FavoriteShop>()
    private val items = items_all.filter { !it.default_flag }.toMutableList() // it.default_flag == falseと同じ

    // お気に入り画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null

    // Itemを押したときのメソッド
    var onClickItem: ((String, String, String, String, String) -> Unit)? = null

    // 更新用のメソッド
    fun refresh(list: List<FavoriteShop>){
        items.apply {
            clear() // itemsを空にする
            addAll(list) // itemsにlistを全て追加する
            //Log.d("kotlintest",list)
            Log.d("kotlintest_refresh", list.filter{ !it.default_flag }.toString())
        }

        notifyDataSetChanged() // recyclerViewを再描画させる
    }

    // お気に入り画面用のViewHolderオブジェクトを生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            // ViewTypeがVIEW_TYPE_EMMTY（つまり、お気に入り登録が0件）の場合
            VIEW_TYPE_EMPTY -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite_empty, parent, false))
            // 上記以外（つまり、1件以上のお気に入りが登録されている場合）
            else -> FavoriteItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
        }
    }

    // お気に入りが1件もない時に、「お気に入りはありません」を出す
    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    // onCreateViewHolderの第二引数はここで決める。
    // 条件によってViewTypeを返すようにすると、1つのRecycleViewで様々なViewがあるものが作れる
    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    // ViewHolderのバインド
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavoriteItemViewHolder){
            updateFavoriteItemViewHolder(holder, position)
        }
    }

    // ViewHolder内のUI部品に値などをセット
    private fun updateFavoriteItemViewHolder(holder: FavoriteItemViewHolder, position: Int){
        val data = items[position]
        holder.apply {
            rootView.apply {

                setOnClickListener {
                    onClickItem?.invoke(data.url, data.id, data.name, data.address, data.imageUrl)
                }
            }
            nameTextView.text = data.name
            addressTextView.text = data.address
            // Picassoというライブラリを使ってImageVIewに画像をはめ込む
            Picasso.get().load(data.imageUrl).into(imageView)
            favoriteImageView.setOnClickListener {
                Log.d("kotlintest_before",data.default_flag.toString())
                onClickDeleteFavorite?.invoke(data)
                Log.d("kotlintest_after",data.default_flag.toString())
                notifyItemChanged(position)
            }
        }
    }

    // お気に入りが登録されているときのリスト
    class FavoriteItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView: CardView = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
    }

    // お気に入り登録がまだ行われていないとき
    class EmptyViewHolder(view: View): RecyclerView.ViewHolder(view)

    companion object{
        // Viewの種類を表現する定数、こちらはお気に入りのお店
        private const val VIEW_TYPE_ITEM = 0

        // Viewの種類を表現する定数、こちらはお気に入りが1件もない時
        private const val VIEW_TYPE_EMPTY = 1
    }


}