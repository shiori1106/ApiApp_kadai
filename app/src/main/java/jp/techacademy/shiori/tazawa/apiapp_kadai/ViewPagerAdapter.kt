package jp.techacademy.shiori.tazawa.apiapp_kadai

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    // プロパティ
    // タブの名前を格納したList
    val titleIds = listOf(R.string.tab_title_api, R.string.tab_title_favorite)
    // ページの中身（1ページ目がApiFragment、2ページ目がFavoriteFragment）
    val fragments = listOf(ApiFragment(), FavoriteFragment())

    // メソッド
    // ViewPager2が何ページあるのかを返す
    override fun getItemCount(): Int {
        return fragments.size

    }

    // 引数で受け取ったpositionのページのFragmentを返す
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}