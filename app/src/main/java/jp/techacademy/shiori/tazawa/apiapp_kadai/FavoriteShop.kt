package jp.techacademy.shiori.tazawa.apiapp_kadai

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FavoriteShop: RealmObject() {
    @PrimaryKey
    var id: String = ""
    var address: String = ""
    var imageUrl: String = ""
    var name: String = ""
    var url: String = ""
    // 論理削除用に追加
    var default_flag = false


    companion object{
        // お気に入りのShopを全件取得
        fun findAll(): List<FavoriteShop> =
            Realm.getDefaultInstance().use{ realm ->
                realm.where(FavoriteShop::class.java)
                    .findAll().let{
                        realm.copyFromRealm(it)
                    }
            }

        // お気に入りされているShopをidで検索して返す
        // お気に入りに登録されていなければnullで返す

        // 論理削除用
        fun findBy(id: String): FavoriteShop? =
                Realm.getDefaultInstance().use{ realm ->
                    realm.where(FavoriteShop::class.java)
                            .equalTo(FavoriteShop::id.name, id)
                            .equalTo(FavoriteShop::default_flag.name, false)
                            .findFirst()?.let{
                                realm.copyFromRealm(it)
                            }
                }


        // 物理削除用
        /*fun findBy(id: String): FavoriteShop? =
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.let{
                        realm.copyFromRealm(it)
                    }
            }*/

        // お気に入り追加

        // 論理削除用
        fun insert(favoriteShop: FavoriteShop) {

            // idがお気に入りに登録されていない場合は登録
            if (findBy(favoriteShop.id) == null) {
                Realm.getDefaultInstance().executeTransaction {
                    it.insertOrUpdate(favoriteShop)
                    Log.d("kotlintest","nullだったので登録したよ")
                }

            } else {
                // idがお気に入りに登録されている場合は、default_flagをfalseに
                Realm.getDefaultInstance().use { realm ->
                    realm.where(FavoriteShop::class.java)
                            .equalTo(FavoriteShop::id.name, favoriteShop.id)
                            .findFirst()?.also { addShop ->
                                realm.executeTransaction {
                                    addShop.default_flag = false
                                }
                            }
                }
                Log.d("kolintest","trueからfalseにしたよ")
            }
        }


        // 物理削除用
        /*fun insert(favoriteShop: FavoriteShop) =
            Realm.getDefaultInstance().executeTransaction{
                it.insertOrUpdate(favoriteShop)
            }*/

        // idでお気に入りから削除

        // 論理削除用（デフォルトフラグをtrueに変更）
        fun delete(id: String) =
                Realm.getDefaultInstance().use{ realm ->
                    realm.where(FavoriteShop::class.java)
                            .equalTo(FavoriteShop::id.name, id)
                            .findFirst()?.also{ deleteShop ->
                                realm.executeTransaction {
                                    deleteShop.default_flag = true
                                }
                            }
                }


        // 物理削除用
        /*fun delete(id: String) =
            Realm.getDefaultInstance().use{ realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.also{ deleteShop ->
                        realm.executeTransaction{
                            deleteShop.deleteFromRealm()
                        }
                    }
            }*/

    }
}
