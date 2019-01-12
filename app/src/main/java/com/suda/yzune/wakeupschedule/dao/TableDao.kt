package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

@Dao
interface TableDao {
    @Insert
    fun insertTable(tableBean: TableBean)

    @Update
    fun updateTable(tableBean: TableBean)

    @Query("select max(id) from tablebean")
    fun getLastId(): LiveData<Int>

    @Query("select max(id) from tablebean")
    fun getLastIdInThread(): Int?

    @Query("update tablebean set type = 0 where id = :oldId")
    fun resetOldDefaultTable(oldId: Int)

    @Query("update tablebean set type = 1 where id = :newId")
    fun setNewDefaultTable(newId: Int)

    @Query("select * from tablebean where id = :tableId")
    fun getTableByIdInThread(tableId: Int): TableBean

    @Query("select * from tablebean where id = :tableId")
    fun getTableById(tableId: Int): LiveData<TableBean>

    @Query("select * from tablebean where type = 1")
    fun getDefaultTable(): LiveData<TableBean>

    @Query("select id from tablebean where type = 1")
    fun getDefaultTableId(): LiveData<Int>

    @Query("select * from tablebean where type = 1")
    fun getDefaultTableInThread(): TableBean

    @Query("select id, tableName, background, maxWeek, nodes, type from tablebean")
    fun getTableSelectList(): LiveData<List<TableSelectBean>>

    @Query("delete from tablebean where id = :id")
    fun deleteTable(id: Int)
}