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
    var default_flag: Boolean = false


    companion object {
        // お気に入りのShopを全件取得
        fun findAll(): List<FavoriteShop> =
                Realm.getDefaultInstance().use { realm ->
                    realm.where(FavoriteShop::class.java)
                            .findAll().let {
                                realm.copyFromRealm(it)
                            }
                }

        // -- 検索用 --

        // お気に入り登録されているShopをidで検索して返す
        // お気に入りに登録されていなければnullで返す

        // 論理削除用
        fun findBy(id: String): FavoriteShop? {
            var realm = Realm.getDefaultInstance()
            val target = realm.where(FavoriteShop::class.java)
                        .equalTo(FavoriteShop::id.name, id)
                        .equalTo(FavoriteShop::default_flag.name, false)
                        .findFirst()

            return(target?.let {
                realm.copyFromRealm(it)
            })
            Log.d("kolintest","findbyが呼び出された")
            realm.close()
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

        // 物理削除の書き換え版
        /*fun findBy(id: String): FavoriteShop? {
            var realm = Realm.getDefaultInstance()
            val target = realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()

            return(target?.let {
                realm.copyFromRealm(it)
            })
        }*/


        // -- お気に入り追加 --

        // 論理削除用
        fun insert(favoriteShop: FavoriteShop) {

            // idがお気に入りに登録されていない場合は登録
            Log.d("kotlintest_追加",favoriteShop.id.toString())
            Log.d("kotlintest_追加",favoriteShop.default_flag.toString())
            // isFavorite = findBy(favoriteShop.id) != null //???
            //Log.d("kotelintest_追加",isFavorite.toString())

            if (findBy(favoriteShop.id) == null) {
            //if (isFavorite) {

                // 一時的にデータ取得用
                var realm = Realm.getDefaultInstance()
                realm.where(FavoriteShop::class.java)
                        .findAll().forEach {
                            Log.d("kotlintest_data_追加","${it.id} + ${it.name} + ${it.default_flag}")
                        }




                Realm.getDefaultInstance().executeTransaction {
                    it.insertOrUpdate(favoriteShop)
                    Log.d("kotlintest_追加","nullだったので登録したよ")
                    Log.d("kotlintest_追加",favoriteShop.default_flag.toString())
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
                Log.d("kolintest_追加","trueからfalseにしたよ")
            }
        }


        // 物理削除用
        /*fun insert(favoriteShop: FavoriteShop) =
            Realm.getDefaultInstance().executeTransaction{
                it.insertOrUpdate(favoriteShop)
            }*/

        // 物理削除用の書き換え版
        /*fun insert(favoriteShop: FavoriteShop){
            var realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.insertOrUpdate(favoriteShop)
            }
        }*/


        // -- idでお気に入りから削除 --

        // 論理削除用（デフォルトフラグをtrueに変更）
        fun delete(id: String) {
            var realm = Realm.getDefaultInstance()
            val target = realm.where(FavoriteShop::class.java)
                        .equalTo(FavoriteShop::id.name, id)
                        .findFirst()

            Log.d("kotlintest_削除", target?.default_flag.toString())

            realm.executeTransaction {
                target?.default_flag = true
            }

            realm.where(FavoriteShop::class.java)
                    .findAll().forEach {
                        Log.d("kotlintest_data_削除","${it.id} + ${it.name} + ${it.default_flag}")
                    }

            realm.close()

            Log.d("kotlintest_削除",target?.id.toString())
            Log.d("kotlintest_削除",target?.default_flag.toString())
            Log.d("kotlintest_削除","削除したのでtrueにしたよ")
        }



        // 物理削除用の書き換え版
        /*fun delete(id: String) {
            var realm = Realm.getDefaultInstance()
            val target = realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()

            realm.executeTransaction {
                target?.deleteFromRealm()
            }
        }*/


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
