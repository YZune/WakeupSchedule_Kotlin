package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

@Dao
interface TableDao {
    @Insert
    suspend fun insertTable(tableBean: TableBean): Long

    @Update
    suspend fun updateTable(tableBean: TableBean)

    @Query("select max(id) from tablebean")
    suspend fun getLastId(): Int?

    @Transaction
    suspend fun changeDefaultTable(oldId: Int, newId: Int) {
        resetOldDefaultTable(oldId)
        setNewDefaultTable(newId)
    }

    @Query("update tablebean set type = 0 where id = :oldId")
    suspend fun resetOldDefaultTable(oldId: Int)

    @Query("update tablebean set type = 1 where id = :newId")
    suspend fun setNewDefaultTable(newId: Int)

    @Query("select * from tablebean where id = :tableId")
    suspend fun getTableById(tableId: Int): TableBean?

    @Query("select * from tablebean where id = :tableId")
    fun getTableByIdSync(tableId: Int): TableBean?

    @Query("select id from tablebean where type = 1")
    suspend fun getDefaultTableId(): Int

    @Query("select * from tablebean where type = 1")
    suspend fun getDefaultTable(): TableBean

    @Query("select * from tablebean where type = 1")
    fun getDefaultTableSync(): TableBean

    @Query("select id, tableName, background, maxWeek, nodes, type from tablebean")
    fun getTableSelectListLiveData(): LiveData<List<TableSelectBean>>

    @Query("select id, tableName, background, maxWeek, nodes, type from tablebean")
    suspend fun getTableSelectList(): List<TableSelectBean>

    @Query("delete from tablebean where id = :id")
    suspend fun deleteTable(id: Int)

    @Query("delete from coursebasebean where tableId = :id")
    suspend fun clearTable(id: Int)
}