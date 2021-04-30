package org.d3if2081.hitungbmi.ui.histori

import androidx.lifecycle.ViewModel
import org.d3if2081.hitungbmi.db.BmiDao

class HistoriViewModel(db: BmiDao) : ViewModel() {
    val data = db.getLastBmi()
}