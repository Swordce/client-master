package com.lepao.ydcgkf.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * created by zwj on 2018/9/10 0010
 */
@Table(database = AppDatabase.class)
public class FingerDao extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;
    @Column
    public String fingerId;
    @Column
    public String fingerTemplate;
}
